package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
open class LexerTest {

    @Test
    fun testSpecialCharacterWithoutSpace() {
        val input = "=+,;";
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
}