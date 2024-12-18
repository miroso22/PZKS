package lab1

fun printExpressionErrors(expression: String, errors: List<Error>) {
    val sortedErrors = errors.sortedBy { it.position }
    val errorPositions = sortedErrors.map { it.position }

    val formattedExpression = StringBuilder()
    val pointers = StringBuilder()

    for (i in expression.indices) {
        val errorIndex = errorPositions.indexOf(i)
        val isError = errorIndex != -1
        val char = expression[i].toString()
        formattedExpression.append(if (isError) char.colored(errorIndex) else char)
        pointers.append(if (isError) '^' else ' ')
    }
    println(formattedExpression)
    println(pointers)
    sortedErrors.forEachIndexed { i, error ->
        println(error.message?.colored(i))
    }
}

private val colors = listOf(
    "\u001b[31m",
    "\u001b[32m",
    "\u001b[33m",
    "\u001b[34m",
    "\u001b[35m",
    "\u001b[36m"
)
private val reset = "\u001b[0m"

private fun String.colored(i: Int) = "${colors[i % 6]}$this$reset"