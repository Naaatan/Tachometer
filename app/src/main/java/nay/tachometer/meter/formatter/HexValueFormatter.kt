package nay.tachometer.meter.formatter

import nay.lib.TachometerValueFormatter
import java.util.*

class HexValueFormatter : TachometerValueFormatter {
    override fun onValue(value: Int): String {
       return value.toByteArray().toHexPrefixString()
    }

    private fun Int.toByteArray() = byteArrayOf(0x00, 0x00, 0x00, 0x00).let {
        it[0] = ((this and 0xFF000000.toInt()) shr 24).toByte()
        it[1] = ((this and 0x00FF0000) shr 16).toByte()
        it[2] = ((this and 0x0000FF00) shr 8).toByte()
        it[3] = (this and 0x000000FF).toByte()
        it
    }

    private fun ByteArray.toHexPrefixString(isReverse: Boolean = false, zeroPadding: Int = 2): String {
        val sHex =
            map { byte ->
                String
                    .format("%02x", byte)
                    .uppercase(Locale.ROOT)
            }
            .let { if (isReverse) it.asReversed() else it }
            .joinToString("")
            .dropWhile { it == '0' }
            .padStart(zeroPadding, '0')
            .let { if (it.isEmpty()) "0" else it }

        return "0x$sHex"
    }
}