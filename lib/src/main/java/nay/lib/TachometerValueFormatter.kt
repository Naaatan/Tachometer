package nay.lib

interface TachometerValueFormatter {
    fun onValue(value: Int): String
}