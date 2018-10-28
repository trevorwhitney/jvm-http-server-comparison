package com.github.trevorwhitney.vertx

import io.vertx.core.Vertx

fun main(args: Array<String>) {
    // Create an HTTP server which simply returns "Hello World!" to each request.
    Vertx.vertx()
        .createHttpServer()
        .requestHandler { req -> req.response().end("Hello World!") }
        .listen(8080) { handler ->
            if (handler.succeeded()) {
                println("http://localhost:8080/")
            } else {
                System.err.println("Failed to listen on port 8080")
            }
        }
}
