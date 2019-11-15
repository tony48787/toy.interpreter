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
}