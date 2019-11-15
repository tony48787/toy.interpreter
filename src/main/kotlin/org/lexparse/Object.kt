package org.lexparse

import kotlin.Boolean

interface Object {
    fun type(): ObjectType
    fun inspect(): String
}

enum ObjectType(val type: String) {
    INTEGER_OBJ("INTEGER"),
    BOOLEAN_OBJ("BOOLEAN"),
    NULL_OBJ("NULL")
}

class IntegerObj: Object {
    var value: Int = 0

    override fun type(): ObjectType {
        return ObjectType.INTEGER_OBJ
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class BooleanObj: Object {
    var value: Boolean = false

    override fun type(): ObjectType {
        return ObjectType.BOOLEAN_OBJ
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class NullObj: Object {
    override fun type(): ObjectType {
         return ObjectType.NULL_OBJ
    }

    override fun inspect(): String {
        return "null"
    }
}

