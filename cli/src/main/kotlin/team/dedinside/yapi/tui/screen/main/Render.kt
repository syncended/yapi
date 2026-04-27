package team.dedinside.yapi.tui.screen.main

import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.table.verticalLayout
import team.dedinside.yapi.tui.widget.headerBar
import team.dedinside.yapi.tui.widget.statusFooter

internal fun renderHome(s: MainState): Widget {
    val mainCell: Widget = s.palette?.render() ?: editorPanel(s)
    val workspace = if (s.sidebarVisible) {
        horizontalLayout {
            spacing = 1
            column(0) { width = ColumnWidth.Fixed(34) }
            column(1) { width = ColumnWidth.Expand() }
            cell(sidebarPanel(s))
            cell(mainCell)
        }
    } else mainCell

    return verticalLayout {
        spacing = 0
        cell(headerBar("yapi", "0.0.1", "default", "~/.config/yapi      ?  help    Ctrl+P  palette    Ctrl+B  sidebar"))
        cell(workspace)
        cell(footer(s))
    }
}

private fun footer(s: MainState): Widget {
    if (s.palette != null) {
        return statusFooter("Palette", "↑↓  move · ⏎  run · Esc  cancel")
    }
    val (label, keys) = when (s.focus) {
        Focus.PROJECT -> "Project" to (
            "↑↓ move · ←→ collapse/expand · ⏎ open · Tab → editor · n new · D " +
                (if (s.demo) "empty" else "demo") +
                " · Ctrl+P palette · ? help · q quit"
            )
        Focus.URL -> "URL" to "type to edit · ⏎ send · Ctrl+U clear · Tab next · Esc back · Ctrl+B sidebar · Ctrl+P palette · ? help"
        Focus.TABS -> "Tabs: ${s.tab.label}" to "← →  cycle tabs · Tab next · Esc back · Ctrl+B sidebar · Ctrl+P palette · ? help"
        Focus.RESPONSE -> "Response" to "Esc back · Tab next · Ctrl+B sidebar · Ctrl+P palette · ? help"
    }
    return statusFooter(label, keys)
}
