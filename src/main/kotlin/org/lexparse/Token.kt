package org.lexparse

data class Token(val type: TokenType, val literal: String);

enum class TokenType(val literal: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // Special Characters
    ASSIGN("="),
    PLUS("+"),
    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("let"),

    // Identifier
    IDENT("IDENT"),

    // Values Type
    INT("INT")
}