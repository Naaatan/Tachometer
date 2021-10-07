package nay.lib

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getTickValues() {
        val min = 0
        val max = 100

        val range = min..max
        val majorStep = (max - min) / 10
        val minerStep = majorStep / 4

        println("max=[$max] min=[$min] majorStep=[$majorStep] minerStep=[$minerStep]")

        val ticks = mutableListOf<Tick<Int>>(Tick.Major(min), Tick.Major(max))
        var majorSeek = 0
        var minorSeek = 0
        for (s in range) {
            if (majorSeek == majorStep) {
                if ((range.last - s) >= majorStep) {
                    ticks.add(Tick.Major(s))
                }

                majorSeek = 1
                minorSeek = 1
                continue
            } else majorSeek++

            if (minorSeek == minerStep) {
                if ((range.last - s) >= minerStep) {
                    ticks.add(Tick.Minor(s))
                }

                minorSeek = 1
            } else minorSeek++
        }

        val sortBy = ticks.sortedBy {
            when (it) {
                is Tick.Major -> it.value
                is Tick.Minor -> it.value
            }
        }
        sortBy.forEach {
            when (it) {
                is Tick.Major -> println("tick: -------------------Major: ${it.value}")
                is Tick.Minor -> println("tick: Minor: ${it.value}")
            }
        }
    }
}

sealed interface TickTest<T> {
    data class Major<T>(val value: T): TickTest<T>
    data class Minor<T>(val value: T): TickTest<T>
}