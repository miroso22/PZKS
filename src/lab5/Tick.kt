package lab5

sealed interface Tick {
    data object Free : Tick
    class Read(val i: Int, val fromArgs: Boolean = false) : Tick
    class Write(val i: Int) : Tick
    class Calculate(val i: Int) : Tick
}

val Tick.isBusy get() = this != Tick.Free
