package team.dedinside.yapi.tui.core

import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.rendering.Widget
import team.dedinside.yapi.tui.model.ScreenAction

@DslMarker
annotation class TuiDsl

@TuiDsl
class ScreenScope {
    /**
     * Hotkey handler invoked for every keyboard event the screen receives.
     * Defaults to a no-op that returns [ScreenAction.Stay].
     */
    var onHotkey: (KeyboardEvent) -> ScreenAction = { ScreenAction.Stay }
}

/**
 * Build a [Screen] from a builder block. The block re-runs on every render, so
 * mutable state captured in the surrounding scope can be read freely:
 *
 * ```kotlin
 * fun home(): Screen {
 *     var url = ""
 *     return screen {
 *         onHotkey = { e ->
 *             when (e.key) {
 *                 "Backspace" -> { url = url.dropLast(1); ScreenAction.Stay }
 *                 else -> ScreenAction.Stay
 *             }
 *         }
 *         verticalLayout {
 *             cell(Text("URL: $url"))
 *         }
 *     }
 * }
 * ```
 *
 * The block's return value (last expression) is the widget tree to render. Set
 * `onHotkey` somewhere inside the block to wire keyboard input.
 */
fun screen(block: ScreenScope.() -> Widget): Screen {
    val scope = ScreenScope()
    // Prime so that onHotkey is set even if handle() is invoked before the
    // first render — defensive against future runtime changes.
    scope.block()
    return object : Screen {
        override fun render(): Widget = scope.block()
        override fun handle(event: KeyboardEvent): ScreenAction = scope.onHotkey(event)
    }
}
