import lab1.*
import lab2.*
import lab5.*

fun main() {
    val tests = listOf(
        "a+b+c*d+d/e-g*h+i/j",
        "a+b+c+d+e+f+g+h",
        "a/b/c/d/e/f/g/h",
        "a*2 + b/(b-a) - 1/x/y",
        "a+b+c+d+e+f+g+h+i+j+k+l+m*n+o+p+q+a+a+t+u+v+w+x+y+z",
    )

    tests.forEach(::processExpression)
}

private fun processExpression(expression: String) {

    // Lab 1

    val errors = mutableListOf<Error>()

    val tokenizeResult = tokenize(expression)
    errors.addAll(tokenizeResult.errors)
    errors.addAll(parse(tokenizeResult.tokens))

    if (errors.isNotEmpty())
        return printExpressionErrors(expression, errors)

    println("Expression: $expression")
    println("Expression is valid")
    println()

    // Lab 2

    val tokens = tokenizeResult.tokens.map { it.second }
    val tree = buildTree(tokens).simplify()
//    println("Parallel tree:")
//    println(tree.stringify())
//    println()

    // Lab 5

    val tasks = tree.createTasks()
    println("Operation Tree:")
    tasks.firstOrNull()?.print()

    val sortedTasks = tasks
        .sortedWith(compareBy({ it.depth }, { it.cost }))
        .reversed()
    println("\nTask assignment order:")
    sortedTasks.forEach { println("Task ${it.i}") }

    val results = CPU.submitTasks(sortedTasks)
    println("\nDiagram:")
    printDiagram()
    println("\nResults: $results")
    println()
}