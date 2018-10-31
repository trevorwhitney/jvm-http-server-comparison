package com.github.trevorwhitney.builtin

import com.github.trevorwhitney.prometheus.JvmPrometheusMeterRegistry
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.apache.logging.log4j.LogManager
import java.net.InetSocketAddress


fun main(args: Array<String>) {
    val server = HttpServer.create(InetSocketAddress(8080), 0)

    server.createContext("/", MyHandler())
    server.createContext("/metrics", MetricsHandler(JvmPrometheusMeterRegistry.prometheusMeterRegistry))
    server.start()
}

class MyHandler : HttpHandler {
    private val logger = LogManager.getLogger()

    override fun handle(t: HttpExchange) {
        logger.debug("handling hello world endpoint")

        val response = "Hello World"
        t.sendResponseHeaders(200, response.length.toLong())
        val os = t.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}

class MetricsHandler(private val prometheusRegistry: PrometheusMeterRegistry) : HttpHandler {
    private val logger = LogManager.getLogger()

    override fun handle(t: HttpExchange) {
        logger.debug("metrics being scraped")

        val response = prometheusRegistry.scrape()
        t.sendResponseHeaders(200, response.length.toLong())
        val os = t.responseBody
        os.write(response.toByteArray())
        os.close()
    }
}