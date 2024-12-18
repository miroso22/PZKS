package lab2

import lab1.Token

sealed interface Expression {
    data class Constant(val value: Double) : Expression
    data class Variable(val name: String) : Expression
    data class Function(val operator: Operator, val leftOperand: Expression, val rightOperand: Expression) : Expression

    val depth: Int get() = when (this) {
        is Constant, is Variable -> 0
        is Function ->  maxOf(leftOperand.depth, rightOperand.depth) + 1
    }
}

fun buildTree(tokens: List<Token>): Expression {
    val operands = ArrayDeque<Expression>()
    val operators = ArrayDeque<Operator>()

    fun applyOperator() {
        val operator = operators.removeLast()
        val rightOperand = operands.removeLast()
        val leftOperand = operands.removeLast()

        val newOperand = tryInsertOperand(tree = leftOperand, expr = rightOperand, operator)
        operands.addLast(newOperand)
    }

    for (i in tokens.indices) {
        val token = tokens[i]
        val isNextOpenBracket = tokens.getOrNull(i + 1) is Token.OpenBracket
        val component = TreeComponent.fromToken(token, isNextOpenBracket) ?: continue

        if (component is Operand) {
            val value = component.name.toDoubleOrNull()
            val expr = if (value != null) Expression.Constant(value) else Expression.Variable(component.name)
            operands.addLast(expr)
            continue
        }

        if (component as Operator == Operator.CloseBracket) {
            while (operators.last() != Operator.OpenBracket)
                applyOperator()
            operators.removeLast()
            continue
        }
        while (operators.isNotEmpty() && component != Operator.OpenBracket && component.priority <= operators.last().priority) {
            applyOperator()
        }
        operators.addLast(component)
    }

    while (operators.isNotEmpty()) {
        applyOperator()
    }

    return operands.first()
}

private fun tryInsertOperand(tree: Expression, expr: Expression, operator: Operator): Expression.Function {
    val appendedTree = Expression.Function(operator, tree, expr)
    if (tree is Expression.Constant || tree is Expression.Variable) return appendedTree
    tree as Expression.Function
    if (tree.operator.priority != operator.priority) return appendedTree
    val innerOperator = if (tree.operator.needsReverse) operator.reversed else operator
    val newRightOperand = tryInsertOperand(tree.rightOperand, expr, innerOperator)
    val insertedTree = Expression.Function(tree.operator, tree.leftOperand, newRightOperand)
    return if (insertedTree.depth < appendedTree.depth) insertedTree else appendedTree
}