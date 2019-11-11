package org.lexparse

data class Token(val type: TokenType, val literal: String)

val illegalToken = Token(TokenType.ILLEGAL, TokenType.ILLEGAL.literal);

enum class TokenType(val literal: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // Special Characters
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    ASTERISK("*"),

    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    SLASH("/"),
    BANG("!"),
    LT("<"),
    GT(">"),

    //Compound operators
    EQ("=="),
    NOT_EQ("!="),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("let"),
    IF("if"),
    ELSE("else"),

    // Identifier
    IDENT("IDENT"),

    // Values Type
    INT("INT"),
    RETURN("RETURN"),

    TRUE("TRUE"),
    FALSE("FALSE")
}

val keywords = mapOf<String, TokenType>(
        "fn" to TokenType.FUNCTION,
        "let" to TokenType.LET,
        "return" to TokenType.RETURN,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE
)

fun lookupTypeFromLiteral(literal: String): TokenType {
    return keywords.getOrDefault(literal, TokenType.IDENT)
}

