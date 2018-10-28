package com.github.trevorwhitney.netty

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

const val HTTP_PORT = 8080

fun main(args: Array<String>) {
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
    override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpObject) {
        if (msg is LastHttpContent) {
            val content = Unpooled.copiedBuffer("Hello World.", CharsetUtil.UTF_8)
            val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content)
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes())
            ctx.write(response)
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
