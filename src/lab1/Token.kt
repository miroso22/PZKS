package lab1

sealed interface Token {
    data class Number(val value: String) : Token
    data class Identifier(val name: String) : Token
    data class Operation(val symbol: Char) : Token
    data object OpenBracket : Token
    data object CloseBracket : Token
    data object Comma : Token
}

val Token.length get() = when (this) {
    is Token.Number -> value.length
    is Token.Identifier -> name.length
    else -> 1
}

abstract class Error(val position: Int): Throwable()