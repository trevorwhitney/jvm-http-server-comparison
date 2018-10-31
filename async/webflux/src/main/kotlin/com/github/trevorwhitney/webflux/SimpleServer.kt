package com.github.trevorwhitney.webflux

import com.github.trevorwhitney.prometheus.JvmPrometheusMeterRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer

val logger: Logger = LogManager.getLogger()

fun main(args: Array<String>) {
    logger.debug("Starting server")
    val prometheusMeterRegistry = JvmPrometheusMeterRegistry.prometheusMeterRegistry

    val routerFunction = RouterFunctions
        .route(RequestPredicates.GET("/metrics").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), HandlerFunction {
            logger.debug("Serving metrics")
            val response = prometheusMeterRegistry.scrape()
            ServerResponse.ok().body(BodyInserters.fromObject(response))
        }).andRoute(RequestPredicates.GET("/").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), HandlerFunction {
            logger.debug("Serving hello world")
            val response = "Hello World"
            ServerResponse.ok().body(BodyInserters.fromObject(response))
        })

    val handler = WebHttpHandlerBuilder
        .webHandler(RouterFunctions.toWebHandler(routerFunction))
        .build()

    val adapter = ReactorHttpHandlerAdapter(handler)


    HttpServer.create(8080).startAndAwait(adapter)
}