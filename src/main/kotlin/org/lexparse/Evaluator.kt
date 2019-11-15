package org.lexparse

class Evaluator {

    fun eval(node: Node): Object {
        when (node) {
            is IntegerLiteral -> return IntegerObj(node.value)
            is BooleanLiteral -> return BooleanObj(node.value)
            is Program -> return evalStatements(node.statements)
            is ExpressionStatement -> return eval(node.expression!!)
        }
        return NullObj()
    }

    private fun evalStatements(stmts: ArrayList<Statement>): Object {
        var result: Object = NullObj()

        stmts.forEach {
            result = eval(it)
        }

        return result
    }
}