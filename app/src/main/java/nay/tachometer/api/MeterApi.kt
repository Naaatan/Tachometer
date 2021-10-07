package nay.tachometer.api

import kotlinx.coroutines.flow.Flow

interface MeterApi<T> {
    fun start(): Flow<T>
}