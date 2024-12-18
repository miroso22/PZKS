package lab1

private const val operations = "+-/*^%"
private val letters = ('A'..'Z').joinToString("") + ('a'..'z').joinToString("") + '_'
private val digits = '0'..'9'
private const val dot = '.'

data class TokenizeResult(
    val tokens: List<Pair<Int, Token>>,
    val errors: List<TokenizeError>
)

sealed class TokenizeError private constructor(position: Int, message: String) : Error(position) {
    override val message: String = "Tokenize error at $position: $message"

    class DoubleDotInNumber(position: Int) :
        TokenizeError(position, "Double dot in number literal")

    class UnexpectedDot(position: Int) : TokenizeError(position, "Unexpected dot")

    class UnknownSymbol(position: Int, symbol: Char) :
        TokenizeError(position, "Unknown symbol '$symbol'")
}

fun tokenize(expression: String): TokenizeResult {
    val tokens = mutableListOf<Pair<Int, Token>>()
    val errors = mutableListOf<TokenizeError>()

    var pointer = 0
    while (pointer < expression.length) {
        if (expression[pointer].isWhitespace()) {
            pointer++
            continue
        }

        try {
            val token = when (val symbol = expression[pointer]) {
                '(' -> Token.OpenBracket
                ')' -> Token.CloseBracket
                ',' -> Token.Comma
                in operations -> Token.Operation(symbol)
                in digits -> tokenizeConstant(expression, pointer)
                in letters -> tokenizeIdentifier(expression, pointer)
                '.' -> throw TokenizeError.UnexpectedDot(pointer)
                else -> throw TokenizeError.UnknownSymbol(pointer, symbol)
            }

            tokens.add(pointer to token)
            pointer += token.length
        } catch (e: TokenizeError) {
            errors.add(e)
            pointer = e.position + 1
        }
    }
    return TokenizeResult(tokens, errors)
}

private fun tokenizeConstant(expression: String, startIndex: Int): Token.Number {
    var wasDot = false
    var pointer = startIndex

    while (pointer < expression.length) {
        val symbol = expression[pointer]
        if (symbol == dot) {
            if (wasDot) throw TokenizeError.DoubleDotInNumber(pointer)
            wasDot = true
            pointer++
            continue
        }
        if (symbol !in digits) break

        pointer++
    }
    val value = expression.substring(startIndex..<pointer)
    return Token.Number(value)
}

private fun tokenizeIdentifier(expression: String, startIndex: Int): Token.Identifier {
    var pointer = startIndex
    while (pointer < expression.length) {
        if (expression[pointer] !in letters) break
        pointer++
    }
    val name = expression.substring(startIndex..<pointer)
    return Token.Identifier(name)
}