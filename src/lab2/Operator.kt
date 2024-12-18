package lab2

import lab1.Token
import kotlin.math.pow

sealed interface TreeComponent {
    companion object {
        fun fromToken(token: Token, isNextOpenBracket: Boolean): TreeComponent? = when (token) {
            Token.CloseBracket -> Operator.CloseBracket
            Token.OpenBracket -> Operator.OpenBracket
            Token.Comma -> null
            is Token.Identifier -> if (isNextOpenBracket) Operator.Fun(token.name) else Operand(token.name)
            is Token.Number -> Operand(token.value)
            is Token.Operation -> Operator.values.firstOrNull { it.name == token.symbol.toString() }
        }
    }
}

data class Operand(val name: String) : TreeComponent

sealed interface Operator : TreeComponent {
    data object CloseBracket : Operator
    data object OpenBracket : Operator
    data object Add : Operator
    data object Sub : Operator
    data object Div : Operator
    data object Mul : Operator
    data object Pow : Operator
    data object Mod : Operator
    data class Fun(val name: String) : Operator

    companion object {
        val values = listOf(CloseBracket, OpenBracket, Add, Sub, Div, Mul, Pow, Mod)
    }
}

val Operator.name: String get() = when (this) {
    Operator.CloseBracket -> ")"
    Operator.OpenBracket -> "("
    Operator.Add -> "+"
    Operator.Sub -> "-"
    Operator.Div -> "/"
    Operator.Mul -> "*"
    Operator.Pow -> "^"
    Operator.Mod -> "%"
    is Operator.Fun -> name
}

val Operator.priority: Int get() = when (this) {
    Operator.CloseBracket, Operator.OpenBracket -> 0
    Operator.Add, Operator.Sub -> 1
    Operator.Div, Operator.Mul -> 2
    Operator.Mod -> 3
    Operator.Pow -> 4
    is Operator.Fun -> 5
}

val Operator.needsReverse: Boolean get() = this == Operator.Div || this == Operator.Sub

val Operator.reversed: Operator
    get() = when (this) {
    Operator.Sub -> Operator.Add
    Operator.Add -> Operator.Sub
    Operator.Div -> Operator.Mul
    Operator.Mul -> Operator.Div
    else -> this
}

fun Operator.evaluate(left: Double, right: Double): Double = when (this) {
    Operator.Add -> left.plus(right)
    Operator.Sub -> left.minus(right)
    Operator.Div -> left.div(right)
    Operator.Mul -> left.times(right)
    Operator.Mod -> left.mod(right)
    Operator.Pow -> left.pow(right)
    else -> 0.0
}