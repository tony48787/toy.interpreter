package org.lexparse

class Evaluator {
    val TRUE = BooleanObj(true)
    val FALSE = BooleanObj(false)
    val NULL = NullObj()

    fun eval(node: Node): Object {
        when (node) {
            is IntegerLiteral -> return IntegerObj(node.value)
            is BooleanLiteral -> return nativeBoolToBooleanObj(node.value)
            is Program -> return evalProgram(node.statements)
            is ExpressionStatement -> return eval(node.expression!!)
            is PrefixExpression -> {
                val right = eval(node.right!!)
                if (isError(right)) {
                    return right
                }
                return evalPrefixExpression(node.operator, right)
            }
            is InfixExpression -> {
                val left = eval(node.left!!)
                if (isError(left)) {
                    return left
                }
                val right = eval(node.right!!)
                if (isError(right)) {
                    return right
                }
                return evalInfixExpression(node.operator, left, right)
            }
            is IfExpression -> return evalIfExpression(node)
            is BlockStatement -> return evalStatements(node.statements)
            is ReturnStatement -> return evalReturnStatement(node)
        }
        return NULL
    }

    private fun evalProgram(statements: java.util.ArrayList<Statement>): Object {
        var result: Object = NULL

        statements.forEach {
            result = eval(it)

            if (result is ReturnValueObj) {
                return (result as ReturnValueObj).value
            } else if (result is ErrorObj) {
                return result
            }
        }

        return result
    }

    private fun evalReturnStatement(node: ReturnStatement): Object {
        val evaluated = eval(node.returnValue!!)
        return ReturnValueObj(evaluated)
    }

    private fun evalIfExpression(node: IfExpression): Object {
        val conditionObj = eval(node.condition!!)
        if (isError(conditionObj)) {
            return conditionObj
        }
        return if (isTruthy(conditionObj)) {
            eval(node.consequence!!)
        } else {
            node.alternative?.let { eval(it) } ?: NULL
        }
    }

    private fun isError(obj: Object): Boolean {
        return obj.type() == ObjectType.ERROR ?: false
    }

    private fun isTruthy(obj: Object): Boolean {
        return when (obj) {
            is NullObj -> false
            FALSE -> false
            else -> true
        }
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left is IntegerObj && right is IntegerObj) {
            return evalIntegerInfixExpression(operator, left, right)
        } else if (left.type().toString() != right.type().toString()) {
            return ErrorObj("type mismatch: ${left.type()} $operator ${right.type()}")
        }

        return when (operator) {
            "==" -> nativeBoolToBooleanObj(left == right)
            "!=" -> nativeBoolToBooleanObj(left != right)
            else -> ErrorObj("unknown operator: ${left.type()} $operator ${right.type()}")
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
            else -> ErrorObj("unknown operator: ${left.type()} $operator ${right.type()}")
        }
    }

    private fun evalStatements(stmts: ArrayList<Statement>): Object {
        var result: Object = NULL

        stmts.forEach {
            result = eval(it)

            if (result is ReturnValueObj || result is ErrorObj) {
                return result
            }
        }

        return result
    }

    private fun evalPrefixExpression(operator: String, right: Object): Object {
        return when (operator) {
            "!" -> return evalBangOperatorExpression(right)
            "-" -> return evalMinusOperatorExpression(right)
            else -> ErrorObj("unknown operator: $operator ${right.type()}")
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
            else -> ErrorObj("unknown operator: -${right.type()}")
        }
    }

    private fun nativeBoolToBooleanObj(bool: Boolean): BooleanObj {
        if (bool) {
            return TRUE
        }
        return FALSE
    }
}