package com.skintrack.app.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.usecase.UpdateCheckInStreak
import com.skintrack.app.ui.screen.timeline.ChartRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.skintrack.app.ui.screen.dashboard.DayCheckIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
    private val updateCheckInStreak: UpdateCheckInStreak,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _selectedTrendPeriod = MutableStateFlow(7)
    val selectedTrendPeriod: StateFlow<Int> = _selectedTrendPeriod.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.currentUser()
            val userId = user?.userId ?: "local-user"
            val username = user?.displayName ?: user?.email?.substringBefore('@') ?: "用户"

            val productCount = try {
                productRepository.countByUser(userId)
            } catch (_: Exception) {
                0
            }

            combine(
                skinRecordRepository.getRecordsByUser(userId),
                updateCheckInStreak.observeStreak(userId),
                _selectedTrendPeriod,
            ) { records, streak, period ->
                if (records.isEmpty()) {
                    DashboardUiState.Empty(username = username)
                } else {
                    val latestRecord = records.first() // already sorted desc
                    val today = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val latestDate = latestRecord.recordedAt
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val hasTakenPhotoToday = latestDate == today

                    // Calculate score change from previous record
                    val scoreChange = if (records.size >= 2) {
                        val current = latestRecord.overallScore ?: 0
                        val previous = records[1].overallScore ?: 0
                        (current - previous).toFloat()
                    } else {
                        0f
                    }

                    val scoredRecords = records
                        .filter { it.overallScore != null }
                        .sortedBy { it.recordedAt }

                    // Filter by period
                    val cutoffInstant = Clock.System.now()
                        .minus(period.days)
                    val filteredChartPoints = scoredRecords
                        .filter { it.recordedAt >= cutoffInstant }
                        .map { ChartRecord(date = it.recordedAt, overallScore = it.overallScore!!) }

                    // Calculate week check-ins
                    val tz = TimeZone.currentSystemDefault()
                    val todayDate = today
                    // Find Monday of this week
                    val daysFromMonday = (todayDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + 7) % 7
                    val monday = todayDate.minus(daysFromMonday, DateTimeUnit.DAY)
                    val weekLabels = listOf("一", "二", "三", "四", "五", "六", "日")
                    val recordDates = records.map {
                        it.recordedAt.toLocalDateTime(tz).date
                    }.toSet()

                    val weekCheckIns = (0..6).map { offset ->
                        val date = monday.plus(offset, DateTimeUnit.DAY)
                        DayCheckIn(
                            weekdayLabel = weekLabels[offset],
                            dayOfMonth = date.dayOfMonth,
                            isCompleted = date in recordDates,
                            isToday = date == todayDate,
                        )
                    }

                    DashboardUiState.Content(
                        username = username,
                        latestRecord = latestRecord,
                        scoreChange = scoreChange,
                        hasTakenPhotoToday = hasTakenPhotoToday,
                        chartPoints = filteredChartPoints,
                        allChartPoints = scoredRecords.map {
                            ChartRecord(date = it.recordedAt, overallScore = it.overallScore!!)
                        },
                        currentStreak = streak?.currentStreak ?: 0,
                        totalRecords = records.size,
                        weekCheckIns = weekCheckIns,
                        productCount = productCount,
                    )
                }
            }.collect { _uiState.value = it }
        }
    }

    fun onTrendPeriodChange(periodDays: Int) {
        _selectedTrendPeriod.update { periodDays }
    }
}

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Empty(
        val username: String = "用户",
    ) : DashboardUiState

    data class Content(
        val username: String,
        val latestRecord: SkinRecord,
        val scoreChange: Float,
        val hasTakenPhotoToday: Boolean,
        val chartPoints: List<ChartRecord>,
        val allChartPoints: List<ChartRecord>,
        val currentStreak: Int,
        val totalRecords: Int,
        val weekCheckIns: List<DayCheckIn> = emptyList(),
        val productCount: Int = 0,
    ) : DashboardUiState
}
