package org.lexparse

class Parser(val lexer: Lexer) {
    var curToken: Token = illegalToken
    var peekToken: Token = illegalToken
    var errors: ArrayList<String> = arrayListOf()
    var prefixParseFns: Map<TokenType, () -> Expression?>
    var infixParseFns: Map<TokenType, (Expression) -> Expression>

    init {
        getNextToken()
        getNextToken()

        prefixParseFns = mapOf(
            TokenType.IDENT to ::parseIdentifier,
            TokenType.INT to ::parseIntegerLiteral,
            TokenType.MINUS to ::parsePrefixExpression,
            TokenType.BANG to ::parsePrefixExpression,
            TokenType.TRUE to ::parseBooleanExpression,
            TokenType.FALSE to ::parseBooleanExpression,
            TokenType.LPAREN to ::parseGroupedExpression,
            TokenType.IF to ::parseIfExpression
        )
        infixParseFns = mapOf(
            TokenType.PLUS to ::parseInfixExpression,
            TokenType.MINUS to ::parseInfixExpression,
            TokenType.ASTERISK to ::parseInfixExpression,
            TokenType.SLASH to ::parseInfixExpression,
            TokenType.EQ to ::parseInfixExpression,
            TokenType.NOT_EQ to ::parseInfixExpression,
            TokenType.LT to ::parseInfixExpression,
            TokenType.GT to ::parseInfixExpression
        )

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
                return parseExpressionStatement();
            }
        }
    }

    private fun parseExpressionStatement(): Statement? {
        val stmt = ExpressionStatement(curToken)

        stmt.expression = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON)) {
            getNextToken()
        }

        return stmt
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        val prefixParseFn = prefixParseFns.get(curToken.type)

        var leftExp = prefixParseFn?.let { it() }
                ?: run {
                    registerNoPrefixParseFnError(curToken.type)
                    null
                }

        leftExp?.let {
            while (!peekTokenIs(TokenType.SEMICOLON)
                    && precedence.ordinal < peekPrecedence()) {
                val infixParseFn = infixParseFns.get(peekToken.type)

                infixParseFn?.let {
                    getNextToken()
                    leftExp = leftExp?.let { infixParseFn(it) }
                } ?: run {
                    return leftExp
                }
            }
        }

        return leftExp
    }

    private fun registerNoPrefixParseFnError(type: TokenType) {
        val msg = "no prefix parse function for ${type.literal} found"
        errors.add(msg)
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

    private fun parseIdentifier(): Expression {
        return Identifier(curToken, curToken.literal)
    }

    private fun parseIntegerLiteral(): Expression {
        val integerValue = curToken.literal.toInt()
        return IntegerLiteral(curToken, integerValue)
    }

    private fun parsePrefixExpression(): Expression {
        val exp = PrefixExpression(curToken, curToken.literal)

        getNextToken()
        exp.right = parseExpression(Precedence.PREFIX)

        return exp
    }

    private fun parseInfixExpression(left: Expression): Expression {
        val exp = InfixExpression(curToken, curToken.literal)

        exp.left = left
        val precedenceOrdinal = curPrecedence()
        val precedence = Precedence.values()[precedenceOrdinal]
        getNextToken()
        exp.right = parseExpression(precedence)

        return exp
    }

    private fun parseBooleanExpression(): Expression {
        return BooleanLiteral(curToken, curTokenIs(TokenType.TRUE))
    }

    private fun parseGroupedExpression(): Expression? {
        getNextToken()
        val exp = parseExpression(Precedence.LOWEST)
        if (!expectPeek(TokenType.RPAREN)) {
            return null
        }
        return exp
    }

    private fun parseIfExpression(): Expression? {
        val ifExpression = IfExpression(curToken)

        if (!expectPeek(TokenType.LPAREN)) {
            return null
        }

        getNextToken()
        ifExpression.condition = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RPAREN)) {
            return null
        }

        if (!expectPeek(TokenType.LBRACE)) {
            return null
        }

        ifExpression.consequence = parseBlockStatement()

        if (peekTokenIs(TokenType.ELSE)) {
            getNextToken()

            if (!expectPeek(TokenType.LBRACE)) {
                return null
            }

            ifExpression.alternative = parseBlockStatement()
        }

        return ifExpression
    }

    private fun parseBlockStatement(): BlockStatement? {
        val blockStatement = BlockStatement(curToken)

        getNextToken()

        while (!curTokenIs(TokenType.RBRACE) && !curTokenIs(TokenType.EOF)) {
            val stmt = parseStatement()
            stmt.let { blockStatement.statements.add(it!!) }
            getNextToken()
        }

        return blockStatement
    }

    private fun expectPeek(expected: TokenType): Boolean {
        return if (peekTokenIs(expected)) {
            getNextToken()
            true
        } else {
            peekError(expected)
            false
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

    private fun peekPrecedence(): Int {
        return precedenceOfTokenType.getOrDefault(peekToken.type, Precedence.LOWEST).ordinal
    }

    private fun curPrecedence(): Int {
        return precedenceOfTokenType.getOrDefault(curToken.type, Precedence.LOWEST).ordinal
    }
}