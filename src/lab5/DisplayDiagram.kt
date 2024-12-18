package lab5

private const val colorCalc = "\u001b[32m"
private const val colorRead = "\u001b[34m"
private const val colorWrite = "\u001b[31m"
private const val reset = "\u001b[0m"

fun printDiagram() {
    val processors = CPU.processors
    val banks = Memory.banks

    print("Tick|\t")
    repeat(processors.size) { print("P${it + 1}\t") }
    print("|\t")
    repeat(banks.size) { print("B${it + 1}\t") }
    println()

    val tickCount = processors.firstOrNull()?.ticks?.size ?: 0
    for (i in 0..<tickCount) {
        print("${i + 1}\t|\t")
        for (p in processors) {
            print(p.ticks[i].displayText)
        }
        print("|\t")
        for (b in banks) {
            print(b.ticks[i].displayText)
        }
        println()
    }
}

private val Tick.displayText: String
    get() = when (this) {
        is Tick.Calculate -> "$i"
        Tick.Free -> "X"
        is Tick.Read -> "R${if (fromArgs) "*$i" else "$i"}"
        is Tick.Write -> "W$i"
    }.let { if (it.length < 4) "$it\t" else it }.colored(printColor)

private val Tick.printColor: String
    get() = when (this) {
        is Tick.Calculate -> colorCalc
        is Tick.Read -> colorRead
        is Tick.Write -> colorWrite
        Tick.Free -> ""
    }

private fun String.colored(color: String) = "${color}$this$reset"