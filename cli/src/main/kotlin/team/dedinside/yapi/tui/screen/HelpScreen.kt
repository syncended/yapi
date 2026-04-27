package team.dedinside.yapi.tui.screen

import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.widgets.Padding
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import team.dedinside.yapi.tui.core.Screen
import team.dedinside.yapi.tui.core.popOn
import team.dedinside.yapi.tui.core.screen

private fun helpBody(): String = buildString {
    appendLine(bold("Pane focus"))
    appendLine()
    appendLine("  Tab            Editor: cycle URL → Tabs → Response")
    appendLine("  ${dim("                  ")}From Project: drill into editor (lands on URL)")
    appendLine("  Shift+Tab      Cycle in reverse")
    appendLine("  Esc / q        Drill out: editor → Project, Project → quit")
    appendLine("  Ctrl+B         Toggle Project sidebar")
    appendLine()
    appendLine(bold("Project pane"))
    appendLine()
    appendLine("  ↑ / k, ↓ / j   Move tree selection")
    appendLine("  ← / h          Collapse folder, or jump to parent")
    appendLine("  → / l          Expand folder, or step into it")
    appendLine("  Enter          Open request in editor, or toggle folder")
    appendLine("  n              New request (clears editor, focuses URL)")
    appendLine("  D              Toggle demo data / empty state")
    appendLine()
    appendLine(bold("URL focus"))
    appendLine()
    appendLine("  type           Edit URL")
    appendLine("  Backspace      Delete last character")
    appendLine("  Ctrl+U         Clear URL")
    appendLine("  Enter          Send                                  ${dim("(stub — real flow in #4)")}")
    appendLine("  Esc            Drill out to Project (URL preserved)")
    appendLine()
    appendLine(bold("Tabs focus"))
    appendLine()
    appendLine("  ← / h, → / l   Cycle request tabs (Headers / Query / Body / Auth)")
    appendLine()
    appendLine(bold("Response focus"))
    appendLine()
    appendLine("  Esc            Drill out to Project (notice preserved)")
    appendLine()
    appendLine(bold("Global"))
    appendLine()
    appendLine("  Ctrl+P         Command palette")
    appendLine("  ?              Open this help")
    appendLine("  Ctrl+C         Quit from anywhere")
    appendLine()
    appendLine(dim("Press Esc, q, or ? to return."))
}

fun helpScreen(): Screen = screen {
    onHotkey = popOn("Escape", "q", "Q", "?", "Backspace")
    verticalLayout {
        spacing = 0
        cell(
            Panel(
                Text(helpBody(), whitespace = Whitespace.PRE_WRAP),
                title = Text("Help"),
                expand = true,
                padding = Padding(1, 2, 1, 2),
            )
        )
        cell(Text(dim("Esc / q / ?  back · Ctrl+C  quit"), align = TextAlign.CENTER))
    }
}
