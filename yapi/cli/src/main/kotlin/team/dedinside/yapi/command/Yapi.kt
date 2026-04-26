package team.dedinside.yapi.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context

class Yapi : CliktCommand(name = "yapi") {
    override val invokeWithoutSubcommand: Boolean get() = true

    override fun help(context: Context) = "TUI/CLI HTTP API client"

    override fun run() {
        echo("Hello world")
    }
}