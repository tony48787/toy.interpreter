package org.lexparse

interface Node {
    fun tokenLiteral():String
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

}

class LetStatement(val token:Token): Statement {
    var name:Identifier? = null
    var value:Expression? = null

    override fun statementNode() {

    }

    override fun tokenLiteral(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class Identifier(val token:Token, val value:String): Expression {

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun expressionNode() {

    }

}