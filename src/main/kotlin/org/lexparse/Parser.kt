package org.lexparse

class Parser(val lexer: Lexer) {
    var curToken: Token = illegalToken
    var peekToken: Token = illegalToken
    var errors: ArrayList<String> = arrayListOf()

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
            TokenType.RETURN -> return parseReturnStatement()
            else -> {
                return null
            }
        }
    }

    private fun parseReturnStatement(): Statement? {
        val stmt = ReturnStatement(curToken)

        while (!curTokenIs(TokenType.SEMICOLON)) {
            getNextToken()
        }

        return stmt
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
            peekError(expected)
            return false
        }
    }

    private fun curTokenIs(expected: TokenType): Boolean {
        return curToken.type == expected
    }

    private fun peekTokenIs(expected: TokenType): Boolean {
        return peekToken.type == expected
    }

    private fun peekError(expected: TokenType) {
        val msg = "expected next token to be ${expected.literal}, got ${peekToken.literal} instead"
        errors.add(msg)
    }
}