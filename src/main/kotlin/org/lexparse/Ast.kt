package org.lexparse

import javax.swing.plaf.nimbus.State

interface Node {
    fun tokenLiteral():String
    override fun toString():String
}

interface Statement: Node {
    fun statementNode()
}

interface Expression: Node {
    fun expressionNode()
}

class Program: Node {
    var statements:ArrayList<Statement> = arrayListOf()

    override fun tokenLiteral(): String {
        if (statements.size > 0) {
            return statements[0].tokenLiteral()
        } else {
            return "Empty Statement"
        }
    }

    override fun toString(): String {
        return statements.fold("") { strs, statement -> strs.plus(statement.toString())}
    }
}

class LetStatement(val token:Token): Statement {
    var name:Identifier? = null

    var value:Expression? = null
    override fun statementNode() {

    }

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${tokenLiteral()} ${name.toString()} = ${value?.toString()};"
    }
}

class Identifier(val token:Token, val value:String): Expression {

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

    override fun toString(): String {
        return "${value.toString()}"
    }
}

class IntegerLiteral(val token:Token, val value:Int): Expression {

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

    override fun toString(): String {
        return value.toString()
    }
}

class BooleanLiteral(val token:Token, val value:Boolean): Expression {

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

    override fun toString(): String {
        return value.toString()
    }
}

class PrefixExpression(val token:Token, val operator: String): Expression {
    var right: Expression? = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

    override fun toString(): String {
        return "(${operator}${right.toString()})"
    }
}

class InfixExpression(val token:Token, val operator: String): Expression {
    var left: Expression? = null
    var right: Expression? = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

    override fun toString(): String {
        return "(${left.toString()} $operator ${right.toString()})"
    }
}

class IfExpression(val token:Token): Expression {
    var condition: Expression? = null
    var consequence: BlockStatement? = null
    var alternative: BlockStatement? = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        val elseStr = alternative.let { " else ${alternative.toString()}" }
        return "if ${condition.toString()} ${consequence.toString()} $elseStr"
    }

    override fun expressionNode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class FunctionLiteral(val token:Token): Expression {
    var parameters: ArrayList<Identifier> = arrayListOf()
    var body: BlockStatement? = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        val paramStr = parameters.joinToString(", ")
        return "${tokenLiteral()} ($paramStr) ${body.toString()}"
    }

    override fun expressionNode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class BlockStatement(val token:Token): Statement {
    var statements:ArrayList<Statement> = arrayListOf()

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return statements.fold("") { strs, statement -> strs.plus(statement.toString())}
    }

    override fun statementNode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class ReturnStatement(val token: Token): Statement {
    var returnValue: Expression?  = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun statementNode() {

    }

    override fun toString(): String {
        return "${tokenLiteral()} ${returnValue?.toString()};"
    }
}

class ExpressionStatement(val token: Token): Statement {
    var expression: Expression?  = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun statementNode() {

    }

    override fun toString(): String {
        return "${expression?.toString()}"
    }

}