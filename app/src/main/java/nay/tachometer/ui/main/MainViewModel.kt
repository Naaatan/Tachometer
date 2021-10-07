package nay.tachometer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nay.tachometer.api.MeterApi
import nay.tachometer.meter.MockSpeedMeter

class MainViewModel : ViewModel() {
    val min = 40
    val max = 180

    private val speedApi: MeterApi<Int> = MockSpeedMeter(delay = 1200L, min = 0, max = 250)

    private val mMeterValue = MutableStateFlow<Int>(min)
    val meterValue: StateFlow<Int> = mMeterValue

    init {
        speedApi.start().onEach {
            mMeterValue.value = it
        }.launchIn(viewModelScope)
    }
}