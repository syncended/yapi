package team.dedinside.yapi.application

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import team.dedinside.yapi.command.Test
import team.dedinside.yapi.command.Yapi

fun main(args: Array<String>) = Yapi()
    .subcommands(Test())
    .main(args)
