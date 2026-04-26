package team.dedinside.yapi.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional

class Test : CliktCommand(name = "test") {
    override fun help(context: Context) =
        "Test command, should be removed soon"

    val foo by argument(help = "foo").optional()

    override fun run() {
        echo("$foo.bar")
    }
}
