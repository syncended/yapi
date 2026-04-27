package team.dedinside.yapi.tui.core

import com.github.ajalt.mordant.input.KeyboardEvent
import team.dedinside.yapi.tui.model.ScreenAction

/**
 * Hotkey handler that returns [ScreenAction.Pop] when [KeyboardEvent.key] matches
 * any of [keys], and [ScreenAction.Stay] otherwise. Convenient for simple
 * dismissable overlays:
 *
 * ```kotlin
 * onHotkey = popOn("Escape", "q", "Q", "Backspace")
 * ```
 */
fun popOn(vararg keys: String): (KeyboardEvent) -> ScreenAction {
    val set = keys.toSet()
    return { event -> if (event.key in set) ScreenAction.Pop else ScreenAction.Stay }
}
