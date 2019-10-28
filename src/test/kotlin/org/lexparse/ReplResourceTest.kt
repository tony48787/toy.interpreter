package org.lexparse

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
open class ReplResourceTest {

    @Test
    fun testHelloEndpoint() {
        given()
          .`when`().get("/repl")
          .then()
             .statusCode(200)
             .body(`is`("hello"))
    }

}