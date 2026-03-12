package com.skintrack.app.ui.screen.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SubscriptionPlan
import com.skintrack.app.domain.model.UserSubscription
import com.skintrack.app.domain.repository.SubscriptionRepository
import com.skintrack.app.platform.PaymentManager
import com.skintrack.app.platform.PaymentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class PaywallViewModel(
    private val paymentManager: PaymentManager,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    fun selectPlan(plan: SubscriptionPlan) {
        _uiState.update { it.copy(selectedPlan = plan, error = null) }
    }

    fun purchase() {
        val plan = _uiState.value.selectedPlan
        if (_uiState.value.isPurchasing) return

        _uiState.update { it.copy(isPurchasing = true, error = null) }

        viewModelScope.launch {
            val result = paymentManager.purchase(plan)
            handlePaymentResult(result, plan)
        }
    }

    fun restorePurchase() {
        if (_uiState.value.isPurchasing) return

        _uiState.update { it.copy(isPurchasing = true, error = null) }

        viewModelScope.launch {
            val result = paymentManager.restorePurchase()
            handlePaymentResult(result, SubscriptionPlan.MONTHLY)
        }
    }

    private suspend fun handlePaymentResult(result: PaymentResult, plan: SubscriptionPlan) {
        when (result) {
            is PaymentResult.Success -> {
                val now = Clock.System.now()
                val tz = TimeZone.currentSystemDefault()
                val period = when (plan) {
                    SubscriptionPlan.MONTHLY -> DatePeriod(months = 1)
                    SubscriptionPlan.YEARLY -> DatePeriod(years = 1)
                    SubscriptionPlan.FREE -> DatePeriod()
                }
                val expiryLocalDate = now.toLocalDateTime(tz).date.plus(period)
                val expiryDate = LocalDateTime(expiryLocalDate, LocalTime(0, 0)).toInstant(tz)

                subscriptionRepository.saveSubscription(
                    UserSubscription(
                        userId = "local-user",
                        plan = plan,
                        startDate = now,
                        expiryDate = expiryDate,
                        isActive = true,
                    )
                )
                _uiState.update { it.copy(isPurchasing = false, success = true) }
            }
            is PaymentResult.Error -> {
                _uiState.update { it.copy(isPurchasing = false, error = result.message) }
            }
            is PaymentResult.Cancelled -> {
                _uiState.update { it.copy(isPurchasing = false) }
            }
        }
    }
}

data class PaywallUiState(
    val selectedPlan: SubscriptionPlan = SubscriptionPlan.YEARLY,
    val isPurchasing: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)
