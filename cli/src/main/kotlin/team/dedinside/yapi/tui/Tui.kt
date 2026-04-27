package team.dedinside.yapi.tui

import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.input.InputReceiver
import com.github.ajalt.mordant.input.isCtrlC
import com.github.ajalt.mordant.input.receiveKeyEvents
import com.github.ajalt.mordant.terminal.Terminal
import team.dedinside.yapi.tui.core.Screen
import team.dedinside.yapi.tui.model.ScreenAction
import team.dedinside.yapi.tui.screen.main.mainScreen

/** Drives a stack of [Screen]s, repainting on every key event. */
class Tui(private val terminal: Terminal) {
    fun run() {
        val stack = ArrayDeque<Screen>().apply { addLast(mainScreen()) }
        val animation: Animation<Screen> = terminal.animation { it.render() }
        animation.update(stack.last())

        try {
            terminal.receiveKeyEvents { event ->
                if (event.isCtrlC) return@receiveKeyEvents InputReceiver.Status.Finished(Unit)

                val action = stack.last().handle(event)
                when (action) {
                    ScreenAction.Stay -> {
                        animation.update(stack.last())
                        InputReceiver.Status.Continue
                    }

                    is ScreenAction.Push -> {
                        stack.addLast(action.screen)
                        animation.update(stack.last())
                        InputReceiver.Status.Continue
                    }

                    is ScreenAction.Replace -> {
                        if (stack.isNotEmpty()) stack.removeLast()
                        stack.addLast(action.screen)
                        animation.update(stack.last())
                        InputReceiver.Status.Continue
                    }

                    ScreenAction.Pop -> {
                        if (stack.size > 1) {
                            stack.removeLast()
                            animation.update(stack.last())
                            InputReceiver.Status.Continue
                        } else {
                            InputReceiver.Status.Finished(Unit)
                        }
                    }

                    ScreenAction.Exit -> InputReceiver.Status.Finished(Unit)
                }
            }
        } finally {
            animation.clear()
        }
    }
}
