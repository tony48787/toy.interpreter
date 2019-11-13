package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
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
        val program = parseSource("let x = 5;")

        Assertions.assertEquals(1, program.statements.size);

        val expectedIdentifiers = arrayOf("x")

        for ((index, expected) in expectedIdentifiers.withIndex()) {
            testToken(program.statements[index], TokenType.LET)
            testLetIdentifier(program.statements[index], expected)
        }
    }

    @Test
    fun testParserReturnStatement() {
        val program = parseSource("return 5;")

        Assertions.assertEquals(1, program.statements.size);

        val returnStatement = program.statements[0] as ReturnStatement
        Assertions.assertEquals(returnStatement.token.type, TokenType.RETURN)
    }

    @Test
    @Disabled
    fun testProgramToString() {
        val program = parseSource("let x = y; return y;")

        Assertions.assertEquals(program.toString(), "let x = y;return y;")
    }

    @Test
    fun testIdentExpression() {
        val program = parseSource("fooooobar;")

        val expressionStatement = program.statements[0] as ExpressionStatement;
        testIdentifier(expressionStatement.expression!!, "fooooobar")
    }

    @Test
    fun testIntLiteralExpression() {
        val program = parseSource("5454545;")

        val expressionStatement = program.statements[0] as ExpressionStatement;
        testIntegerLiteral(expressionStatement.expression!!, 5454545)
    }

    @Test
    fun testPrefixExpressions() {
        val prefixInputs = arrayOf(
                Triple("-5", "-", 5),
                Triple("!15", "!", 15),
                Triple("!true", "!", true),
                Triple("!false", "!", false)
        )

        prefixInputs.forEach {
            val input = it.first
            val operator = it.second
            val expected = it.third

            val program = parseSource(input)

            val expressionStatement = program.statements[0] as ExpressionStatement;
            testPrefixExpression(expressionStatement.expression!!, operator, expected)
        }
    }

    @Test
    fun testInfixExpressions() {
        data class TestInput(val input:String, val leftValue: Any, val operator: String, val rightValue: Any)

        val infixInputs = arrayOf(
                TestInput("5 + 5", 5, "+", 5),
                TestInput("5 - 5", 5, "-", 5),
                TestInput("5 * 5", 5, "*", 5),

                TestInput("5 / 5", 5, "/", 5),
                TestInput("5 > 5", 5, ">", 5),
                TestInput("5 < 5", 5, "<", 5),

                TestInput("5 == 5", 5, "==", 5),
                TestInput("5 != 5", 5, "!=", 5),

                TestInput("true != false", true, "!=", false),
                TestInput("true ==     true", true, "==", true)

        )

        infixInputs.forEach {
            val program = parseSource(it.input)
            val expressionStatement = program.statements[0] as ExpressionStatement;
            testInfixExpression(expressionStatement.expression!!, it.leftValue, it.operator, it.rightValue)
        }
    }

    @Test
    fun testBooleanExpressions() {
        val boolInputs = arrayOf(
                Pair("true;", true),
                Pair("false;", false)
        )

        boolInputs.forEach {
            val program = parseSource(it.first)
            val expressionStatement = program.statements[0] as ExpressionStatement;
            testBooleanExpression(expressionStatement.expression!!, it.second)
        }
    }

    @Test
    fun testOperatorPrecedenceParsing() {
        val inputs = arrayOf(
                Pair("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                Pair("2 + 3 * 4;", "(2 + (3 * 4))"),
                Pair("(5 + 5) * 2", "((5 + 5) * 2)"),
                Pair("2 / (5 + 4)", "(2 / (5 + 4))"),
                Pair("-(5 + 5)", "(-(5 + 5))"),
                Pair("!(true == true)", "(!(true == true))")
        )

        inputs.forEach {
            val program = parseSource(it.first)
            Assertions.assertEquals(program.toString(), it.second);
        }
    }

    @Test
    fun testIfExpressions() {
        val input = "if (x > y) { x }"
        val program = parseSource(input)

        Assertions.assertEquals(1, program.statements.size)
        val expressionStatement = program.statements[0] as ExpressionStatement
        val ifExpression = expressionStatement.expression as IfExpression

        testInfixExpression(ifExpression.condition!!, "x", ">", "y")
        Assertions.assertEquals(1, ifExpression.consequence?.statements?.size)

        val consExpressionStatement = ifExpression.consequence?.statements?.get(0) as ExpressionStatement
        testIdentifier(consExpressionStatement.expression!!, "x")
        Assertions.assertEquals(null, ifExpression.alternative)

    }

    @Test
    fun testIfElseExpressions() {
        val input = "if (x > y) { x } else { y }"
        val program = parseSource(input)

        Assertions.assertEquals(1, program.statements.size)
        val expressionStatement = program.statements[0] as ExpressionStatement
        val ifExpression = expressionStatement.expression as IfExpression

        testInfixExpression(ifExpression.condition!!, "x", ">", "y")
        Assertions.assertEquals(1, ifExpression.consequence?.statements?.size)

        val consExpressionStatement = ifExpression.consequence?.statements?.get(0) as ExpressionStatement
        testIdentifier(consExpressionStatement.expression!!, "x")

        val alteExpressionStatement = ifExpression.alternative?.statements?.get(0) as ExpressionStatement
        testIdentifier(alteExpressionStatement.expression!!, "y")

    }

    @Test
    fun testFunctionLiterals() {
        val input = "fn (x, y) { return x + y; }"
        val program = parseSource(input)

        Assertions.assertEquals(1, program.statements.size)
        val expressionStatement = program.statements[0] as ExpressionStatement
        val functionLiteral = expressionStatement.expression as FunctionLiteral

        Assertions.assertEquals(2, functionLiteral.parameters.size)
        Assertions.assertArrayEquals(arrayOf("x", "y"), functionLiteral.parameters.map { it.value }.toTypedArray())

        Assertions.assertEquals(1, functionLiteral.body?.statements?.size)
        val blockStatement = functionLiteral.body as BlockStatement
        val bodyReturnStatement = blockStatement.statements[0] as ReturnStatement
        testInfixExpression(bodyReturnStatement.returnValue!!, "x", "+", "y")
    }

    @Test
    fun testFunctionParameters() {
        val inputs = arrayOf(
                Pair("fn() {};", arrayOf<String>()),
                Pair("fn(xx) {}", arrayOf<String>("xx")),
                Pair("fn(xx, yy) {}", arrayOf<String>("xx", "yy"))
        )

        inputs.forEach {
            val program = parseSource(it.first)
            val expected = it.second

            Assertions.assertEquals(1, program.statements.size)
            val expressionStatement = program.statements[0] as ExpressionStatement
            val functionLiteral = expressionStatement.expression as FunctionLiteral

            Assertions.assertEquals(expected.size, functionLiteral.parameters.size)
            Assertions.assertArrayEquals(expected, functionLiteral.parameters.map { it.value }.toTypedArray())
        }
    }

    @Test
    fun testCallExpressions() {
        val input = "add(1, 2 * 3, 4 + 5)"
        val program = parseSource(input)

        Assertions.assertEquals(1, program.statements.size)
        val expressionStatement = program.statements[0] as ExpressionStatement
        val callExpression = expressionStatement.expression as CallExpression
        testIdentifier(callExpression.function!!, "add")

        Assertions.assertEquals(3, callExpression.arguments.size)
        testLiteralExpression(callExpression.arguments[0], 1)
        testInfixExpression(callExpression.arguments[1], 2, "*", 3)
        testInfixExpression(callExpression.arguments[2], 4, "+", 5)
    }

    @Test
    fun testCallExpressionArguments() {
        val inputs = arrayOf(
                Pair("a + add(b * c) + d", "((a + add((b * c))) + d)"),
                Pair("add(x, add(x, 2))", "add(x, add(x, 2))"),
                Pair("add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))")
        )

        inputs.forEach {
            val program = parseSource(it.first)
            val expected = it.second

            Assertions.assertEquals(1, program.statements.size)
            Assertions.assertEquals(it.second, program.toString())
        }
    }

    // ----------------
    // Helper Functions
    // ----------------
    private fun parseSource(source: String): Program {
        val lexer = Lexer(source)
        val parser = Parser(lexer)

        val program = parser.parseProgram()
        checkErrors(parser.errors)

        return program
    }

    private fun testLetIdentifier(statement: Statement, expected: String) {
        val letStatement = statement as LetStatement
        Assertions.assertEquals(letStatement.name?.tokenLiteral(), expected)
    }

    private fun testToken(statement: Statement, expected: TokenType) {
        val letStatement = statement as LetStatement
        Assertions.assertEquals(letStatement.token.type, expected)
    }

    private fun <T> testPrefixExpression(exp: Expression, operator: String, expectedRight: T) {
        val prefixExpression = exp as PrefixExpression

        Assertions.assertEquals(prefixExpression.operator, operator)
        testLiteralExpression(prefixExpression.right!!, expectedRight)
    }

    private fun <T> testInfixExpression(exp: Expression, expectedLeft: T, operator: String, expectedRight: T) {
        val infixExpression = exp as InfixExpression

        testLiteralExpression(infixExpression.left!!, expectedLeft)
        Assertions.assertEquals(infixExpression.operator, operator)
        testLiteralExpression(infixExpression.right!!, expectedRight)
    }

    private fun testLiteralExpression(exp: Expression, expected: Any?) {
        when (exp) {
            is Identifier -> testIdentifier(exp, expected as String)
            is IntegerLiteral -> testIntegerLiteral(exp, expected as Int)
            is BooleanLiteral -> testBooleanExpression(exp, expected as Boolean)
        }
    }

    private fun testIdentifier(exp: Expression, expected:String) {
        val identifier = exp as Identifier
        Assertions.assertEquals(identifier.value, expected)
        Assertions.assertEquals(identifier.tokenLiteral(), expected)
    }

    private fun testIntegerLiteral(exp: Expression, expected: Int) {
        val integerLiteral = exp as IntegerLiteral
        Assertions.assertEquals(integerLiteral.value, expected)
        Assertions.assertEquals(integerLiteral.tokenLiteral(), expected.toString())
    }

    private fun testBooleanExpression(exp: Expression, expected: Boolean) {
        val booleanLiteral = exp as BooleanLiteral
        Assertions.assertEquals(booleanLiteral.value, expected)
        Assertions.assertEquals(booleanLiteral.tokenLiteral(), expected.toString())
    }

    private fun checkErrors(errors: ArrayList<String>) {
        if (errors.isNotEmpty()) {
            println("--- Errors ---")
            errors.forEach {
                println(it)
            }
        }
    }
}
