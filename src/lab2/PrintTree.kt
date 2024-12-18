package lab2

fun Expression.stringify(): String {
    val buffer = StringBuilder(50)
    print(buffer, "", "")
    return buffer.toString()
}

private fun Expression.print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
    val name = when (this) {
        is Expression.Function -> operator.name
        is Expression.Constant -> value.toString()
        is Expression.Variable -> name
    }
    buffer.append(prefix)
    buffer.append(name)
    buffer.append('\n')
    if (this !is Expression.Function) return
    val it = listOf(leftOperand, rightOperand).iterator()
    while (it.hasNext()) {
        val next = it.next()
        if (it.hasNext()) {
            next.print(buffer, "$childrenPrefix├── ", "$childrenPrefix│   ")
        } else {
            next.print(buffer, "$childrenPrefix└── ", "$childrenPrefix    ")
        }
    }
}