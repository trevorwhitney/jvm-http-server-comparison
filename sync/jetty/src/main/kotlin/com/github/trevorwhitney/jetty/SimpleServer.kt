package com.github.trevorwhitney.jetty

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun main(args: Array<String>) {
    val server = Server(8080)
    server.setHandler(HelloWorld())

    server.start()
    server.join()
}

class HelloWorld : AbstractHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun handle(target: String,
                        baseRequest: Request,
                        request: HttpServletRequest,
                        response: HttpServletResponse) {
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8")

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK)

        // Write back response
        response.getWriter().println("Hello World")

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true)
    }
}