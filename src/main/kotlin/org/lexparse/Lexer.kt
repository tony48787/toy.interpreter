package org.lexparse

import java.lang.Exception

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

    fun getNextToken(): Token {
        skipWhitespace()

        when {
            ch == 0.toChar() -> {
                return Token(TokenType.EOF, TokenType.EOF.literal)
            }
            isLetter() -> {
                val literal = readIdentifier()
                val type = lookupTypeFromLiteral(literal)
                return Token(type, literal)
            }
            isDigit() -> {
                val numberLiteral = readNumber()
                return Token(TokenType.INT, numberLiteral)
            }
            else -> {

                var target = ch.toString()
                if ((target == "=" || target == "!") && peekChar() == '=') {
                    target += "="
                    readChar()
                }

                val type = try {
                    TokenType.values().first { it.literal == target }
                } catch (e: Exception) {
                    TokenType.ILLEGAL
                }
                readChar()
                return Token(type, type.literal)
            }
        }
    }

    private fun readNumber(): String {
        val startPosition = position

        while (isDigit()) {
            readChar()
        }

        return source.substring(startPosition until position)
    }

    private fun readIdentifier(): String {
        val startPosition = position

        while (isLetter()) {
            readChar()
        }

        return source.substring(startPosition until position)
    }

    private fun readChar() {
        if (source.length <= readPosition) {
            ch = 0.toChar()
        } else {
            ch = source[readPosition]
        }
        position = readPosition
        readPosition += 1
    }

    private fun peekChar(): Char {
        if (source.length <= readPosition) {
            return 0.toChar()
        } else {
            return source[readPosition]
        }
    }

    private fun skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar()
        }
    }

    private fun isLetter(): Boolean {
        return ch in 'a'..'z' || ch in 'A'..'Z' || ch == '_'
    }

    private fun isDigit(): Boolean {
        return ch in '1'..'9'
    }
}