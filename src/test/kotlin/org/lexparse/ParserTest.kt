package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
            testToken(program.statements[index], TokenType.LET)
            testLetIdentifier(program.statements[index], expected)
        }
    }

    @Test
    fun testParserReturnStatement() {
        val lexer = Lexer("return 5;")
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        Assertions.assertEquals(1, program.statements.size);

        val returnStatement = program.statements[0] as ReturnStatement
        Assertions.assertEquals(returnStatement.token.type, TokenType.RETURN)
    }

    @Test
    fun testProgramToString() {
        val lexer = Lexer("let x = y; return y;")
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        Assertions.assertEquals(program.toString(), "let x = y;return y;")
    }

    @Test
    fun testIdentExpression() {
        val lexer = Lexer("fooooobar;")
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val expressionStatement = program.statements[0] as ExpressionStatement;
        val ident = expressionStatement.expression as Identifier;

        Assertions.assertEquals(ident.value, "fooooobar")
    }

    @Test
    fun testIntLiteralExpression() {
        val lexer = Lexer("5454545;")
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        val expressionStatement = program.statements[0] as ExpressionStatement;
        val ident = expressionStatement.expression as IntegerLiteral;

        Assertions.assertEquals(ident.value, 5454545)
    }

    @Test
    fun testPrefixExpression() {
        val prefixInputs = arrayOf(
                Triple("-5", "-", 5),
                Triple("!15", "!", 15)
        )

        prefixInputs.forEach {
            val input = it.first
            val operator = it.second
            val expectedIntegerLiteral = it.third

            val lexer = Lexer(input)
            val parser = Parser(lexer)

            val program = parser.parseProgram()

            checkErrors(parser.errors)

            val expressionStatement = program.statements[0] as ExpressionStatement;
            val prefixExpression = expressionStatement.expression as PrefixExpression;
            val integerLiteral = prefixExpression.right as IntegerLiteral;

            Assertions.assertEquals(prefixExpression.operator, operator)
            Assertions.assertEquals(integerLiteral.value, expectedIntegerLiteral)
        }
    }

    @Test
    fun testInfixExpression() {
        data class TestInput(val input:String, val leftValue: Int, val operator: String, val rightValue: Int)

        val infixInputs = arrayOf(
                TestInput("5 + 5", 5, "+", 5),
                TestInput("5 - 5", 5, "-", 5),
                TestInput("5 * 5", 5, "*", 5),

                TestInput("5 / 5", 5, "/", 5),
                TestInput("5 > 5", 5, ">", 5),
                TestInput("5 < 5", 5, "<", 5),

                TestInput("5 == 5", 5, "==", 5),
                TestInput("5 != 5", 5, "!=", 5)

        )

        infixInputs.forEach {
            val lexer = Lexer(it.input)
            val parser = Parser(lexer)

            val program = parser.parseProgram()

            checkErrors(parser.errors)

            val expressionStatement = program.statements[0] as ExpressionStatement;
            val infixExpression = expressionStatement.expression as InfixExpression;
            val leftIntegerLiteral = infixExpression.right as IntegerLiteral;
            val rightIntegerLiteral = infixExpression.right as IntegerLiteral;

            Assertions.assertEquals(infixExpression.operator, it.operator)
            Assertions.assertEquals(leftIntegerLiteral.value, it.leftValue)
            Assertions.assertEquals(rightIntegerLiteral.value, it.rightValue)
        }
    }

    private fun testLetIdentifier(statement: Statement, expected: String) {
        val letStatement = statement as LetStatement
        Assertions.assertEquals(letStatement.name?.tokenLiteral(), expected)
    }

    private fun testToken(statement: Statement, expected: TokenType) {
        val letStatement = statement as LetStatement
        Assertions.assertEquals(letStatement.token.type, expected)
    }

    private fun checkErrors(errors: ArrayList<String>) {
        if (errors.isEmpty()) {
            println("--- Errors ---")
            errors.forEach {
                println(it)
            }
        }
    }
}
