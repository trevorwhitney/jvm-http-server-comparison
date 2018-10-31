package com.github.trevorwhitney.vertx

import com.github.trevorwhitney.prometheus.JvmPrometheusMeterRegistry
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()

fun main(args: Array<String>) = Vertx.vertx().deployVerticle(MainVerticle()) { response ->
    if (!response.succeeded()) {
        logger.fatal("Deploy verticle failed!")
    }
}

class MainVerticle : AbstractVerticle() {
    private val prometheusRegistry = JvmPrometheusMeterRegistry.prometheusMeterRegistry

    override fun start(startFuture: Future<Void>) {
        val mainRouter = Router.router(getVertx())

        mainRouter.route("/").handler { routingContext ->
            routingContext
                .response()
                .putHeader("content-type", "text/html")
                .end("Hello World!")
        }

        mainRouter.route("/metrics").handler { routingContext ->
            routingContext
                .response()
                .putHeader("content-type", "text/plain")
                .end(prometheusRegistry.scrape())
        }

        getVertx().createHttpServer().requestHandler(mainRouter::accept).listen(8080)
    }
}