package org.lexparse

import kotlin.Boolean

interface Object {
    fun type(): ObjectType
    fun inspect(): String
}

enum class ObjectType(val type: String) {
    INTEGER_OBJ("INTEGER"),
    BOOLEAN_OBJ("BOOLEAN"),
    NULL_OBJ("NULL"),
    RETURN_VALUE_OBJ("RETURN_VALUE")
}

class IntegerObj(var value: Int = 0): Object {

    override fun type(): ObjectType {
        return ObjectType.INTEGER_OBJ
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class BooleanObj(var value: Boolean = false): Object {

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

class ReturnValueObj(val value: Object): Object {
    override fun type(): ObjectType {
        return ObjectType.RETURN_VALUE_OBJ
    }

    override fun inspect(): String {
        return value.inspect()
    }

}