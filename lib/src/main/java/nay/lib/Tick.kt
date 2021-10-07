package nay.lib

internal sealed interface Tick<T> {
    data class Major<T>(val value: T): Tick<T>
    data class Minor<T>(val value: T): Tick<T>
}