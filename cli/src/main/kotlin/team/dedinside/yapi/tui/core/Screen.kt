package team.dedinside.yapi.tui.core

import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.rendering.Widget
import team.dedinside.yapi.tui.model.ScreenAction

/** A single screen in the TUI screen stack. */
interface Screen {
    /** Render the screen as a mordant [Widget]. */
    fun render(): Widget

    /** React to a [KeyboardEvent]; the result tells the controller what to do next. */
    fun handle(event: KeyboardEvent): ScreenAction
}
