package com.skintrack.app.data.remote

import kotlinx.coroutines.flow.StateFlow

expect class NetworkMonitor() {
    val isOnline: StateFlow<Boolean>
}
