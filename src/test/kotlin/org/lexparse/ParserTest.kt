package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.exp

@QuarkusTest
open class ParserTest {

    @Test
    fun testParserInitToken() {
        val lexer = Lexer("let x = 5;");
        val parser = Parser(lexer);

        Assertions.assertEquals(parser.curToken.type, TokenType.LET);
        Assertions.assertEquals(parser.peekToken.type, TokenType.IDENT);
    }

    @Test
    fun testParserLetStatement() {
        val lexer = Lexer("let x = 5;")
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        Assertions.assertEquals(1, program.statements.size);

        val expectedIdentifiers = arrayOf("x")

        for ((index, expected) in expectedIdentifiers.withIndex()) {
            testLetIdentifier(program.statements[index], expected)
        }
    }

    private fun testLetIdentifier(statement: Statement, expected: String) {
        val letStatement = statement as LetStatement
        Assertions.assertEquals(letStatement.name?.tokenLiteral(), expected)
    }
}