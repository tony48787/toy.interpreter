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
               Pair("99", 99)
       )

        tests.forEach {
            val evaluated: Object = evaluateSource(it.first)
            testIntegerObject(evaluated, it.second)
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
}