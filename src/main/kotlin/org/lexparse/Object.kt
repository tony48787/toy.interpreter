package org.lexparse

import kotlin.Boolean

interface Object {
    fun type(): ObjectType
    fun inspect(): String
}

enum class ObjectType(val type: String) {
    INTEGER("INTEGER"),
    BOOLEAN("BOOLEAN"),
    NULL("NULL"),
    RETURN_VALUE("RETURN_VALUE"),
    ERROR("ERROR")
}

class IntegerObj(var value: Int = 0): Object {

    override fun type(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class BooleanObj(var value: Boolean = false): Object {

    override fun type(): ObjectType {
        return ObjectType.BOOLEAN
    }

    override fun inspect(): String {
        return value.toString()
    }
}

class NullObj: Object {
    override fun type(): ObjectType {
         return ObjectType.NULL
    }

    override fun inspect(): String {
        return "null"
    }
}

class ReturnValueObj(val value: Object): Object {
    override fun type(): ObjectType {
        return ObjectType.RETURN_VALUE
    }

    override fun inspect(): String {
        return value.inspect()
    }

}

class ErrorObj(val message: String): Object {
    override fun type(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "Error: $message"
    }

}