package org.lexparse

fun main(args:Array<String>) {
    Repl.start()
}

class Repl {
    companion object {
        fun start() {
            println("Welcome to Monkey")
            while (true) {
                print(">")
                val userInput = readLine() ?: break
                if (userInput.isEmpty()) break
                println(evaluateSource(userInput).inspect())
            }
            println("See you!")
        }

        private fun evaluateSource(source: String): Object {
            val lexer = Lexer(source)
            val parser = Parser(lexer)
            val program = parser.parseProgram()

            return Evaluator().eval(program, Env())
        }
    }
}

