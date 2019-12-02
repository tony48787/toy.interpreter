package org.lexparse

class Evaluator {
    val TRUE = BooleanObj(true)
    val FALSE = BooleanObj(false)
    val NULL = NullObj()

    fun eval(node: Node, env: Env): Object {
        when (node) {
            is IntegerLiteral -> return IntegerObj(node.value)
            is BooleanLiteral -> return nativeBoolToBooleanObj(node.value)
            is Program -> return evalProgram(node.statements, env)
            is ExpressionStatement -> return eval(node.expression!!, env)
            is PrefixExpression -> {
                val right = eval(node.right!!, env)
                if (isError(right)) {
                    return right
                }
                return evalPrefixExpression(node.operator, right)
            }
            is InfixExpression -> {
                val left = eval(node.left!!, env)
                if (isError(left)) {
                    return left
                }
                val right = eval(node.right!!, env)
                if (isError(right)) {
                    return right
                }
                return evalInfixExpression(node.operator, left, right)
            }
            is IfExpression -> return evalIfExpression(node, env)
            is BlockStatement -> return evalStatements(node.statements, env)
            is ReturnStatement -> return evalReturnStatement(node, env)
            is Identifier -> return evalIdentifier(node, env)
            is LetStatement -> return evalLetStatement(node, env)
            is FunctionLiteral -> return evalFunctionLiteral(node, env)
            is CallExpression -> return evalCallExpression(node, env)
        }
        return NULL
    }

    private fun evalCallExpression(node: CallExpression, env: Env): Object {
        val result = eval(node.function!!, env)
        if (isError(result)) {
            return  result
        }
        //eval arguments
        val evaluatedArgs = node.arguments.map {
            eval(it, env)
        }

        if (!evaluatedArgs.isEmpty() && isError(evaluatedArgs[0])) {
            return evaluatedArgs[0]
        }

        return applyFunction(result, evaluatedArgs)
    }

    private fun applyFunction(result: Object, args: List<Object>): Object {
        val function = result as FunctionObj
        val extendedEnv = extendFunctionEnv(function.env)

        function.parameters.forEachIndexed { index, identifier ->
            extendedEnv.set(identifier.value, args[index])
        }

        val result = eval(function.body, extendedEnv)
        return unwrapReturnValue(result)
    }

    private fun extendFunctionEnv(outerEnv: Env): Env {
        val env = Env()
        env.outerEnv = outerEnv
        return env
    }

    private fun unwrapReturnValue(result: Object): Object {
        if (result is ReturnValueObj) {
            return result.value
        }

        return result
    }

    private fun evalFunctionLiteral(node: FunctionLiteral, env: Env): Object {
        return FunctionObj(node.parameters, node.body!!, env)
    }

    private fun evalLetStatement(node: LetStatement, env: Env): Object {
        val result = node.value?.let { eval(it, env) }
        if (result != null && isError(result)) {
            return result
        }
        env.set(node.name!!.value, result!!)
        return NULL
    }

    private fun evalIdentifier(node: Identifier, env: Env): Object {
        return env.get(node.value)
    }

    private fun evalProgram(statements: java.util.ArrayList<Statement>, env: Env): Object {
        var result: Object = NULL

        statements.forEach {
            result = eval(it, env)

            if (result is ReturnValueObj) {
                return (result as ReturnValueObj).value
            } else if (result is ErrorObj) {
                return result
            }
        }

        return result
    }

    private fun evalReturnStatement(node: ReturnStatement, env: Env): Object {
        val evaluated = eval(node.returnValue!!, env)
        return ReturnValueObj(evaluated)
    }

    private fun evalIfExpression(node: IfExpression, env: Env): Object {
        val conditionObj = eval(node.condition!!, env)
        if (isError(conditionObj)) {
            return conditionObj
        }
        return if (isTruthy(conditionObj)) {
            eval(node.consequence!!, env)
        } else {
            node.alternative?.let { eval(it, env) } ?: NULL
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

    private fun evalStatements(stmts: ArrayList<Statement>, env: Env): Object {
        var result: Object = NULL

        stmts.forEach {
            result = eval(it, env)

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