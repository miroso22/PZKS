package lab5

import lab2.Expression
import lab2.Operator
import lab2.name

val Operator.cost
    get() = when (this) {
        Operator.Add, Operator.Sub -> 1
        Operator.Mul -> 2
        Operator.Div -> 4
        else -> 0
    }

data class Task(
    val i: Int,
    val cost: Int,
    val depth: Int,
    val dependencies: List<Task>,
    val operation: String
)

fun Expression.createTasks(num: Int = 0, depth: Int = 0): List<Task> {
    if (this !is Expression.Function) return emptyList()
    val leftTree = leftOperand.createTasks(num, depth + 1)
    val rightTree = rightOperand.createTasks(num + leftTree.size, depth + 1)
    val task = Task(
        i = num + leftTree.size + rightTree.size + 1,
        cost = operator.cost,
        depth = depth,
        dependencies = listOfNotNull(leftTree.firstOrNull(), rightTree.firstOrNull()),
        operation = operator.name
    )
    return listOf(task) + leftTree + rightTree
}