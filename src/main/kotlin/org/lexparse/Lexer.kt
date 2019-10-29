package org.lexparse

class Lexer (val source: String){
    var position: Int = 0
    var readPosition: Int = 0
    var ch: Char = 0.toChar()

    init {
        readChar()
    }

    fun tokenize(): List<Token> {
        return emptyList()
    }

    fun readChar() {
        if (source.length <= readPosition) {
            ch = 0.toChar()
        } else {
            ch = source[readPosition]
        }
        position = readPosition
        readPosition += 1
    }

    fun getNextToken(): Token {
        val type = TokenType.values().first { it.literal == ch.toString() }
        readChar()
        return Token(type, type.literal)
    }

}