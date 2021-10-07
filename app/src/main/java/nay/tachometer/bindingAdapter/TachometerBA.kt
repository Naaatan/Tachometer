package nay.tachometer.bindingAdapter

import androidx.databinding.BindingAdapter
import nay.lib.Tachometer

object TachometerBA {

    @BindingAdapter(value = ["value", "animDuration"], requireAll = false)
    @JvmStatic
    fun setMeterValue(tachometer: Tachometer, value: Int?, animDuration: Long?) {
        val v = value ?: return
        val d = animDuration ?: 1000L

        tachometer.setMeterValue(v, d)
    }
}