package team.dedinside.yapi.tui.screen

import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.widgets.Padding
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import team.dedinside.yapi.tui.core.Screen
import team.dedinside.yapi.tui.core.popOn
import team.dedinside.yapi.tui.core.screen

/** Generic stub used by command-palette commands whose flow lives in another issue. */
fun placeholderScreen(title: String, body: String): Screen = screen {
    onHotkey = popOn("Escape", "q", "Q", "Backspace")
    verticalLayout {
        spacing = 0
        cell(
            Panel(
                Text(body, whitespace = Whitespace.PRE_WRAP),
                title = Text(title),
                expand = true,
                padding = Padding(1, 2, 1, 2),
            )
        )
        cell(Text(dim("Esc / q  back · Ctrl+C  quit"), align = TextAlign.CENTER))
    }
}

/**
 * Backwards-compatible class wrapper, so existing call sites that say
 * `PlaceholderScreen("Foo", "Bar")` keep working.
 */
class PlaceholderScreen(title: String, body: String) :
    Screen by placeholderScreen(title, body)
