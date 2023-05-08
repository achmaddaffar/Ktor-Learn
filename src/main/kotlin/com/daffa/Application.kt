package com.daffa

import com.daffa.di.mainModule
import com.daffa.plugins.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
//    configureSockets()
    configureRouting()

    install(Koin) {
        modules(mainModule)
    }
}
