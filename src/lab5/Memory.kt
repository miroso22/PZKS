package lab5

private const val memoryBanksCount = 1

object Memory {
    val banks = List(memoryBanksCount) { MemoryBank() }

    fun reset(tickCount: Int) = banks.forEach { it.reset(tickCount) }

    fun addTick(time: Int, tick: Tick) {
        if (tick is Tick.Calculate) throw Exception("Trying to add a Calculate tick to memory")
        val bank = if (tick is Tick.Read && !tick.fromArgs) {
            banks.firstOrNull { it.hasResultFor(tick.i) }
                ?: throw Exception("Result for ${tick.i} is not stored in memory")
        } else {
            banks.firstOrNull { it.isFreeAt(time) }
                ?: throw Exception("No bank is free at $time")
        }
        bank.addTick(tick, time)
    }

    fun getBankForValue(i: Int) = banks.firstOrNull { it.hasResultFor(i) }
        ?: throw Exception("Result for $i is not stored in memory")

    fun isFreeAt(time: Int) = banks.any { it.isFreeAt(time) }
}

class MemoryBank {
    private val results = mutableListOf<Int>()
    val ticks = mutableListOf<Tick>()

    fun reset(tickCount: Int) {
        results.clear()
        ticks.clear()
        ticks.addAll(List(tickCount) { Tick.Free })
    }

    fun addTick(tick: Tick, time: Int) {
        ticks[time] = tick
        if (tick is Tick.Write)
            results.add(tick.i)
    }

    fun hasResultFor(i: Int) = i in results

    fun calculationTimeFor(i: Int) = ticks.indexOfFirst { it is Tick.Write && it.i == i }

    fun isFreeAt(time: Int) = ticks[time] == Tick.Free
}