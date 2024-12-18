package lab5

fun Task.print() {
    val buffer = StringBuilder(50)
    print(buffer, "", "")
    println(buffer.toString())
}

private fun Task.print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
    val name = "$i$operation"
    buffer.append(prefix)
    buffer.append(name)
    buffer.append('\n')
    if (dependencies.isEmpty()) return
    val it = dependencies.iterator()
    while (it.hasNext()) {
        val next = it.next()
        if (it.hasNext()) {
            next.print(buffer, "$childrenPrefix├── ", "$childrenPrefix│   ")
        } else {
            next.print(buffer, "$childrenPrefix└── ", "$childrenPrefix    ")
        }
    }
}