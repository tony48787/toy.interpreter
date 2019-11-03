package org.lexparse

class Parser(val lexer: Lexer) {
    var curToken: Token = illegalToken
    var peekToken: Token = illegalToken

    init {
        getNextToken()
        getNextToken()
    }

    fun getNextToken() {
        curToken = peekToken
        peekToken = lexer.getNextToken()
    }

    fun parseProgram(): Program {
        val program = Program()

        while (curToken.type != TokenType.EOF) {
            val stmt = parseStatement()
            if (stmt != null) {
                program.statements.add(stmt)
            }
            getNextToken()
        }

        return program
    }

    private fun parseStatement(): Statement? {
        when (curToken.type) {
            TokenType.LET -> return parseLetStatement()
            else -> {
                return null
            }
        }
    }

    private fun parseLetStatement(): Statement? {
        val stmt = LetStatement(curToken)

        if (!expectPeek(TokenType.IDENT)) {
            return null
        }
        stmt.name = Identifier(curToken, curToken.literal)

        if (!expectPeek(TokenType.ASSIGN)) {
            return null
        }

        while (!curTokenIs(TokenType.SEMICOLON)) {
            getNextToken()
        }

        return stmt
    }

    private fun expectPeek(expected: TokenType): Boolean {
        if (peekTokenIs(expected)) {
            getNextToken()
            return true
        } else {
            return false
        }
    }

    private fun curTokenIs(expected: TokenType): Boolean {
        return curToken.type == expected
    }

    private fun peekTokenIs(expected: TokenType): Boolean {
        return peekToken.type == expected
    }
}