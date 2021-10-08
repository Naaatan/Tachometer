package nay.tachometer.setting

import nay.tachometer.meter.formatter.HexValueFormatter

fun settingTachometer() =
    arrayOf<SettingTachometer>(
        SettingTachometer.HexFormat(1, HexValueFormatter())
    )

interface SettingTachometer {
    val id: Int

    data class HexFormat(override val id: Int, val formatter: HexValueFormatter) : SettingTachometer
}