package com.github.trevorwhitney.netty

import com.github.trevorwhitney.prometheus.JvmPrometheusMeterRegistry
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.CharsetUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val HTTP_PORT = 8080

val logger: Logger = LogManager.getLogger()

fun main(args: Array<String>) {
    logger.debug("Starting netty")
    val eventLoopGroup = NioEventLoopGroup()

    try {
        val bootstrap = ServerBootstrap()
            .group(eventLoopGroup)
            .handler(LoggingHandler(LogLevel.INFO))
            .childHandler(HttpServerInitializer())
            .channel(NioServerSocketChannel::class.java)

        val ch = bootstrap.bind(HTTP_PORT).sync().channel()
        ch.closeFuture().sync()
    } finally {
        eventLoopGroup.shutdownGracefully()
    }
}

class HttpServerInitializer : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        val pipeline = ch.pipeline()

        pipeline.addLast(HttpServerCodec())
        pipeline.addLast(HttpServerHandler())
    }
}

class HttpServerHandler : SimpleChannelInboundHandler<HttpObject>() {
    private val prometheusMeterRegistry = JvmPrometheusMeterRegistry.prometheusMeterRegistry

    override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpObject) {
        if (msg is DefaultHttpRequest) {
            val uri = msg.uri()
            val content = if (uri.endsWith("/metrics")) {
                logger.debug("Serving metrics")
                Unpooled.copiedBuffer(prometheusMeterRegistry.scrape(), CharsetUtil.UTF_8)
            } else {
                logger.debug("Serving hello world")
                Unpooled.copiedBuffer("Hello World.", CharsetUtil.UTF_8)
            }

            val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content)
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes())
            ctx.write(response)
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
