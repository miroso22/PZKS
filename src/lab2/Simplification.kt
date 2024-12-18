package lab2

fun Expression.simplify(): Expression {
    if (this !is Expression.Function) return this
    val leftOperand = leftOperand.simplify()
    val rightOperand = rightOperand.simplify()

    if (leftOperand is Expression.Constant && rightOperand is Expression.Constant) {
        val value = operator.evaluate(leftOperand.value, rightOperand.value)
        return Expression.Constant(value)
    }

    if (rightOperand.isZero) {
        when (operator) {
            Operator.Add, Operator.Sub -> return leftOperand
            Operator.Mul -> return Expression.Constant(0.0)
            Operator.Div, Operator.Mod -> throw Exception("Division by zero")
            Operator.Pow ->
                if (leftOperand.isZero) throw Exception("0^0")
                else return Expression.Constant(1.0)
            else -> {}
        }
    }
    if (leftOperand.isZero) {
        when (operator) {
            Operator.Add -> return rightOperand
            Operator.Mul, Operator.Div, Operator.Mod, Operator.Pow -> return Expression.Constant(0.0)
            else -> {}
        }
    }
    if (leftOperand.isOne) {
        when (operator) {
            Operator.Mul -> return rightOperand
            Operator.Pow, Operator.Mod -> return leftOperand
            else -> {}
        }
    }
    if (rightOperand.isOne) {
        when (operator) {
            Operator.Mul, Operator.Pow, Operator.Mod, Operator.Div -> return leftOperand
            else -> {}
        }
    }
    if (leftOperand is Expression.Variable &&
        rightOperand is Expression.Variable &&
        leftOperand.name == rightOperand.name &&
        operator == Operator.Sub) {
        return Expression.Constant(0.0)
    }
    return Expression.Function(operator, leftOperand, rightOperand)
}

private val Expression.isZero get() = this is Expression.Constant && value == 0.0
private val Expression.isOne get() = this is Expression.Constant && value == 1.0