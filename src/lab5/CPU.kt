package lab5

import kotlin.math.max

private const val processorsCount = 6

object CPU {
    val processors = List(processorsCount) { Processor() }
    private var maxTickCount = 0

    private val completionTime: Int
        get() = processors.maxOf { it.lastBusyTickTime } + 1

    private fun reset() {
        processors.forEach { it.reset(maxTickCount) }
        Memory.reset(maxTickCount)
    }

    fun submitTasks(tasks: List<Task>): CalculationResults {
        maxTickCount = tasks.sumOf { it.cost + 2 }

        reset()
        calculateSequential(tasks)
        val sequentialTime = completionTime

        reset()
        calculateParallel(tasks)
        val parallelTime = completionTime

        val speedup = sequentialTime.toFloat() / parallelTime
        val efficiency = speedup / processorsCount

        return CalculationResults(sequentialTime, parallelTime, speedup, efficiency)
    }

    private fun calculateSequential(tasks: List<Task>) {
        val processor = processors.first()
        for (task in tasks) {
            processor.submitTask(task)
        }
    }

    private fun calculateParallel(tasks: List<Task>) {
        for (task in tasks) {
            val processor = processors.minBy { it.completionTimeFor(task) }
            processor.submitTask(task)
        }
    }
}

class Processor {
    private val results = mutableListOf<Int>()
    val ticks = mutableListOf<Tick>()

    val lastBusyTickTime: Int
        get() = ticks.indexOfLast { it.isBusy }

    fun reset(tickCount: Int) {
        results.clear()
        ticks.clear()
        ticks.addAll(List(tickCount) { Tick.Free })
    }

    fun submitTask(task: Task) {
        for ((time, tick) in planTask(task)) {
            ticks[time] = tick
            if (tick is Tick.Read || tick is Tick.Write) {
                Memory.addTick(time, tick)
            }
        }
        results.add(task.i)
    }

    private fun planTask(task: Task): Map<Int, Tick> {
        var time = lastBusyTickTime + 1

        val readsTicks = mutableMapOf<Int, Tick.Read>()
        for (tick in getReadTicksFor(task)) {
            if (tick.fromArgs) {
                while (!Memory.isFreeAt(time)) time++
            } else {
                val bank = Memory.getBankForValue(tick.i)
                time = max(time, bank.calculationTimeFor(tick.i))
                while (!bank.isFreeAt(time)) time++
            }
            readsTicks[time++] = tick
        }

        val calculateTicks = List(task.cost) { time++ to Tick.Calculate(task.i) }.toMap()

        while (!Memory.isFreeAt(time)) time++
        val writeTick: Map<Int, Tick.Write> = mapOf(time to Tick.Write(task.i))

        return readsTicks + calculateTicks + writeTick
    }

    private fun getReadTicksFor(task: Task): List<Tick.Read> =
        if (task.dependencies.isEmpty()) listOf(Tick.Read(task.i, fromArgs = true))
        else task.dependencies
            .filter { it.i !in results }
            .map { Tick.Read(it.i) }

    fun completionTimeFor(task: Task) = planTask(task).keys.max()
}

data class CalculationResults(
    val sequentialTime: Int,
    val parallelTime: Int,
    val speedup: Float,
    val efficiency: Float
)