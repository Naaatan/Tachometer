package nay.tachometer.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {
    fun startMeter(): Flow<Int> = flow {
        while (true) {
            emit((0..100).random())
            delay(1000)
        }
    }
}