package nay.tachometer.meter

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nay.tachometer.api.MeterApi

class MockSpeedMeter(
    private val delay: Long,
    private val min: Int,
    private val max: Int
) : MeterApi<Int> {

    override fun start(): Flow<Int> = flow {
        while (true) {
            emit((min..max).random())
            delay(delay)
        }
    }
}