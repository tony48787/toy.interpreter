package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
class EvaluatorTest {

    @Test
    fun testEvalIntegerExpression() {
       val tests = arrayOf(
               Pair("5", 5),
               Pair("99", 99),
               Pair("-99", -99),
               Pair("-929", -929),
               Pair("5 + 5", 10),
               Pair("5 - 5", 0),
               Pair("5 * 5", 25),
               Pair("5 / 5", 1),
               Pair("5 + 5 + 5 + 5 - 10", 10 ),
               Pair("-5 / 5", -1),
               Pair("5 - 5 / 5", 4),
               Pair("(5 - 4) + 3", 4)
       )

        tests.forEach {
            val evaluated: Object = evaluateSource(it.first)
            testIntegerObject(evaluated, it.second)
        }
    }

    @Test
    fun testEvalBooleanExpression() {
        val tests = arrayOf(
                Pair("true", true),
                Pair("false", false),
                Pair("1 < 2", true),
                Pair("3 > 4", false),
                Pair("1 < 1", false),
                Pair("1 > 1", false),
                Pair("1 == 1", true),
                Pair("1 != 1", false),
                Pair("1 == 10", false),
                Pair("1 != 10", true),
                Pair("true == true", true),
                Pair("false == false", true),
                Pair("true == false", false),
                Pair("true != false", true),
                Pair("false != true", true),
                Pair("(1 < 2) == true", true),
                Pair("(1 < 2) == false", false),
                Pair("(1 > 2) == true", false),
                Pair("(1 > 2) == false", true)
        )

        tests.forEach {
            val evaluated = evaluateSource(it.first)
            testBooleanObject(evaluated, it.second)
        }
    }

    @Test
    fun testEvalBangOperator() {
        val tests = arrayOf(
                Pair("!true", false),
                Pair("!false", true),
                Pair("!5", false),
                Pair("!!true", true),
                Pair("!!false", false),
                Pair("!!5", true)
        )

        tests.forEach {
            val evaluated = evaluateSource(it.first)
            testBooleanObject(evaluated, it.second)
        }
    }

    @Test
    fun testIfElseExpression() {
        val tests = arrayOf(
                Pair("if (true) { 10 }", 10),
                Pair("if (false) { 10 }", null),
                Pair("if (1) { 10 }", 10),
                Pair("if (1 < 2) { 10 }", 10),
                Pair("if (true) { 10 }", 10),
                Pair("if (1 > 2) { 10 }", null),
                Pair("if (1 < 2) { 10 } else { 20 }", 10),
                Pair("if (1 > 2) { 10 } else { 20 }", 20)
        )

        tests.forEach {
            val evaluated = evaluateSource(it.first)
            val expected = it.second
            if (expected == null) {
                testNullObject(evaluated)
            } else {
                testIntegerObject(evaluated, expected)
            }
        }
    }

    @Test
    fun testReturnStatement() {
        val tests = arrayOf(
                Pair("return 1;", 1),
                Pair("return 1; 2;", 1),
                Pair("return 2* 8; 1;", 16),
                Pair("2; return 1; 3;", 1),
                Pair("if (10 > 1) { if (10 > 1) { return 1; } } return 10;", 1)
        )

        tests.forEach {
            val evaluated = evaluateSource(it.first)
            testIntegerObject(evaluated, it.second)
        }
    }

    @Test
    fun testErrorHandling() {
        val tests = arrayOf(
                Pair("true + 5;", "type mismatch: BOOLEAN + INTEGER"),
                Pair("true + 5; 10;", "type mismatch: BOOLEAN + INTEGER"),
                Pair("-false", "unknown operator: -BOOLEAN"),
                Pair("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
                Pair("10; true + false; 10;", "unknown operator: BOOLEAN + BOOLEAN"),
                Pair("if (true) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
                Pair("return 1 + true;", "type mismatch: INTEGER + BOOLEAN"),
                Pair("if (true == 10) { true + false; }", "type mismatch: BOOLEAN == INTEGER"),
                Pair("if (true + 5 == 10) { true + false; }", "type mismatch: BOOLEAN + INTEGER"),
                Pair("if (true == false + 10) { true + false; }", "type mismatch: BOOLEAN + INTEGER"),
                Pair("-(true == 5);", "type mismatch: BOOLEAN == INTEGER")
        )

        tests.forEach {
            val evaluated = evaluateSource(it.first)
            testErrorObject(evaluated, it.second)
        }
    }

    private fun testErrorObject(obj: Object, expected: String) {
        val errorObj = obj as ErrorObj
        Assertions.assertEquals(expected, errorObj.message)
    }

    private fun evaluateSource(source: String): Object {
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()

        return Evaluator().eval(program)
    }

    private fun testIntegerObject(obj:Object, expected:Int) {
        val integerObj = obj as IntegerObj
        Assertions.assertEquals(expected, integerObj.value)
    }

    private fun testBooleanObject(obj:Object, expected: Boolean) {
        val booleanObj = obj as BooleanObj
        Assertions.assertEquals(expected, booleanObj.value)
    }

    private fun testNullObject(obj: Object) {
        val nullObj = obj as NullObj
        Assertions.assertEquals("null", nullObj.inspect())
    }
}