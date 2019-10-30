package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
open class LexerTest {

    @Test
    fun testBasicSpecialCharacter() {
        val input = "=+,;"
        val expected = listOf(
                Token(TokenType.ASSIGN, "="),
                Token(TokenType.PLUS, "+"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.SEMICOLON, ";")
        )

        val lexer = Lexer(input)

        expected.forEach {
            Assertions.assertEquals(it, lexer.getNextToken())
        }
    }

    @Test
    fun testFullSetSpecialCharacter() {
        val input = "- / * < > !"
        val expected = listOf(
                Token(TokenType.MINUS, "-"),
                Token(TokenType.SLASH, "/"),
                Token(TokenType.ASTERISK, "*"),
                Token(TokenType.LT, "<"),
                Token(TokenType.GT, ">"),
                Token(TokenType.BANG, "!")
        )

        val lexer = Lexer(input)

        expected.forEach {
            Assertions.assertEquals(it, lexer.getNextToken())
        }
    }

    @Test
    fun testCompoundSpecialCharacter() {
        val input = "!= =="
        val expected = listOf(
                Token(TokenType.NOT_EQ, "!="),
                Token(TokenType.EQ, "==")
        )

        val lexer = Lexer(input)

        expected.forEach {
            Assertions.assertEquals(it, lexer.getNextToken())
        }
    }

    @Test
    fun testKeywordWithSpace() {
        val input = """
            let five = 5;
        """.trimIndent()

        val expected = listOf(
                Token(TokenType.LET, "let"),
                Token(TokenType.IDENT, "five"),
                Token(TokenType.ASSIGN, "="),
                Token(TokenType.INT, "5"),
                Token(TokenType.SEMICOLON, ";")
        )

        val lexer = Lexer(input)

        expected.forEach {
            Assertions.assertEquals(it, lexer.getNextToken())
        }
    }
}