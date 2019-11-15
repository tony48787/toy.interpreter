package org.lexparse

class Evaluator {
    val TRUE = BooleanObj(true)
    val FALSE = BooleanObj(false)
    val NULL = NullObj()

    fun eval(node: Node): Object {
        when (node) {
            is IntegerLiteral -> return IntegerObj(node.value)
            is BooleanLiteral -> return nativeBoolToBooleanObj(node.value)
            is Program -> return evalStatements(node.statements)
            is ExpressionStatement -> return eval(node.expression!!)
            is PrefixExpression -> {
                val right = eval(node.right!!)
                return evalPrefixExpression(node.operator, right)
            }
            is InfixExpression -> {
                val left = eval(node.left!!)
                val right = eval(node.right!!)
                return evalInfixExpression(node.operator, left, right)
            }
        }
        return NULL
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left is IntegerObj && right is IntegerObj) {
            return evalIntegerInfixExpression(operator, left, right)
        }

        return when (operator) {
            "==" -> nativeBoolToBooleanObj(left == right)
            "!=" -> nativeBoolToBooleanObj(left != right)
            else -> NULL
        }
    }

    private fun evalIntegerInfixExpression(operator: String, left: IntegerObj, right: IntegerObj): Object {
        return when (operator) {
            "+" -> IntegerObj(left.value + right.value)
            "-" -> IntegerObj(left.value - right.value)
            "*" -> IntegerObj(left.value * right.value)
            "/" -> IntegerObj(left.value / right.value)
            "<" -> nativeBoolToBooleanObj(left.value < right.value)
            ">" -> nativeBoolToBooleanObj(left.value > right.value)
            "==" -> nativeBoolToBooleanObj(left.value == right.value)
            "!=" -> nativeBoolToBooleanObj(left.value != right.value)
            else -> NULL
        }
    }

    private fun evalStatements(stmts: ArrayList<Statement>): Object {
        var result: Object = NULL

        stmts.forEach {
            result = eval(it)
        }

        return result
    }

    private fun evalPrefixExpression(operator: String, right: Object): Object {
        return when (operator) {
            "!" -> return evalBangOperatorExpression(right)
            "-" -> return evalMinusOperatorExpression(right)
            else -> NULL
        }
    }

    private fun evalBangOperatorExpression(right: Object): Object {
        return when (right) {
            TRUE -> FALSE
            FALSE -> TRUE
            NULL -> TRUE
            else -> FALSE
        }
    }

    private fun evalMinusOperatorExpression(right: Object): Object {
        return when (right) {
            is IntegerObj -> IntegerObj(-right.value)
            else -> NULL
        }
    }

    private fun nativeBoolToBooleanObj(bool: Boolean): BooleanObj {
        if (bool) {
            return TRUE
        }
        return FALSE
    }
}