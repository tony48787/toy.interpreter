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
    ERROR("ERROR"),
    FUNCTION("FUNCTION")
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

class FunctionObj(val parameters: ArrayList<Identifier>, val body: BlockStatement, val env: Env): Object {
    override fun type(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        val paramStr = parameters.joinToString(", ")
        return "fn ($paramStr) {\n${body.toString()}\n}"
    }

}

class Env() {
    val store = mutableMapOf<String, Object>()
    var outerEnv: Env? = null

    fun get(key: String): Object {

        var value = store.getOrDefault(key, ErrorObj("identifier not found: $key"))

        if (value is ErrorObj) {
            outerEnv?.let {
               value = it.get(key)
            }
        }

        return value
    }

    fun set(key: String, value: Object) {
        store[key] = value
    }
}