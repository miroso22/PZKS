package lab1

class ParseError(position: Int, message: String) : Error(position) {
    override val message: String = "Parsing error at $position: $message"
}

fun parse(tokens: List<Pair<Int, Token>>): List<ParseError> {
    val parser = Parser()
    val errors = mutableListOf<ParseError>()

    for ((position, token) in tokens) {
        try {
            parser.processToken(token)
        } catch (e: Exception) {
            errors.add(ParseError(position, e.message.orEmpty()))
        }
    }
    try {
        parser.processEnd()
    } catch (e: Exception) {
        errors.add(ParseError(tokens.last().first, e.message.orEmpty()))
    }

    return errors
}

class Parser {
    enum class State {
        Start,
        OpenBracket,
        CloseBracket,
        Constant,
        Operation,
        Identifier,
        Comma
    }

    private var state: State = State.Start
    // true if we're in function brackets
    // false if we're in common brackets
    private val bracketsContext = ArrayDeque<Boolean>()

    fun processToken(token: Token) {
        when (token) {
            Token.OpenBracket -> processOpenBracket()
            Token.CloseBracket -> processCloseBracket()
            is Token.Number -> processConstant()
            is Token.Identifier -> processIdentifier()
            is Token.Operation -> processOperation()
            Token.Comma -> processComma()
        }
    }

    fun processEnd() {
        when (state) {
            State.OpenBracket -> throw Exception("Expression ends with open bracket")
            State.Operation -> throw Exception("Expression ends with operation")
            State.Comma -> throw Exception("Expression ends with comma")
            State.Start, State.CloseBracket, State.Constant, State.Identifier -> {}
        }
        if (bracketsContext.size > 0)
            throw Exception("Missing ')'")
        state = State.Start
    }

    private fun processOpenBracket() {
        val enteredFunction = when (state) {
            State.CloseBracket, State.Constant -> throw Exception("Expected operation, but found open bracket")
            State.Comma -> throw Exception("Expected operand, but found open bracket")
            State.Start, State.OpenBracket, State.Operation -> false
            State.Identifier -> true
        }
        bracketsContext.addLast(enteredFunction)
        state = State.OpenBracket
    }

    private fun processCloseBracket() {
        val isFunctionContext = bracketsContext.lastOrNull() ?: false
        when (state) {
            State.Start -> throw Exception("Expression starts with close bracket")
            State.OpenBracket -> {
                if (!isFunctionContext) throw Exception("Empty brackets")
            }
            State.Operation, State.Comma -> throw Exception("Expected operand, but found close bracket")
            State.Constant, State.Identifier, State.CloseBracket -> {}
        }

        runCatching { bracketsContext.removeLast() }
            .onFailure { throw Exception("Close bracket without an open bracket") }
        state = State.CloseBracket
    }

    private fun processConstant() {
        when (state) {
            State.CloseBracket -> throw Exception("Expected operation, but found operand")
            State.Constant, State.Identifier -> throw Exception("Two operands in a row")
            State.Operation, State.OpenBracket, State.Start, State.Comma -> {}
        }
        state = State.Constant
    }

    private fun processIdentifier() {
        when (state) {
            State.CloseBracket -> throw Exception("Expected operation, but found operand")
            State.Constant, State.Identifier -> throw Exception("Two operands in a row")
            State.Operation, State.OpenBracket, State.Start, State.Comma -> {}
        }
        state = State.Identifier
    }

    private fun processOperation() {
        when (state) {
            State.Start -> throw Exception("Expression starts with operation")
            State.OpenBracket, State.Comma -> throw Exception("Expected operand, but found operation")
            State.Operation -> throw Exception("Two operations in a row")
            State.CloseBracket, State.Constant, State.Identifier -> {}
        }
        state = State.Operation
    }

    private fun processComma() {
        val isFunctionContext = bracketsContext.lastOrNull() ?: false
        when (state) {
            State.Start -> throw Exception("Expression starts with comma")
            State.OpenBracket, State.Comma, State.Operation -> throw Exception("Expected operand, but found comma")
            State.CloseBracket, State.Constant, State.Identifier -> {
                if (!isFunctionContext) throw Exception("Comma outside of function")
            }
        }
        state = State.Comma
    }
}