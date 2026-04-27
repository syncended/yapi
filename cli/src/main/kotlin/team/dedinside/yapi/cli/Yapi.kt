package team.dedinside.yapi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.mordant.terminal.Terminal
import team.dedinside.yapi.tui.Tui

class Yapi : CliktCommand(name = "yapi") {
    override val invokeWithoutSubcommand: Boolean get() = true

    override fun help(context: Context) = "TUI/CLI HTTP API client"

    override fun run() {
        if (currentContext.invokedSubcommand != null) return
        Tui(Terminal()).run()
    }
}