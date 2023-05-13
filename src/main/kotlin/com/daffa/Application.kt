package com.daffa

import com.daffa.di.mainModule
import com.daffa.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }

    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
//    configureSockets()
    configureRouting()
}
