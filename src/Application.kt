package dev.hikari

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.li
import kotlinx.html.ul
import java.io.File
import javax.swing.filechooser.FileSystemView

val panDir = File(FileSystemView.getFileSystemView().homeDirectory, "pan").also {
    if (!it.exists()) {
        it.mkdirs()
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        route("/pan") {
            get {
                val files = panDir.listFiles()!!.toList()
                call.respondHtml {
                    body {
                        ul {
                            files.forEach { file ->
                                li {
                                    a("/pan/${file.name}") {
                                        +file.name
                                    }
                                }
                            }
                        }
                    }
                }
            }

            get("/{fileName}") {
                val fileName = call.parameters["filename"]
                val file = panDir.listFiles()!!.find { it.name == fileName }
                if (file == null) {
                    call.respond(HttpStatusCode.NotFound, "File Not Found!")
                } else {
                    call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"${file.name}\"")
                    call.respondFile(file)
                }
            }
        }

    }
}

