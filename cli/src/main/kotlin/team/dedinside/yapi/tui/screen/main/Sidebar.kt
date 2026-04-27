package team.dedinside.yapi.tui.screen.main

import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.widgets.HorizontalRule
import com.github.ajalt.mordant.widgets.Padding
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import team.dedinside.yapi.tui.widget.methodBadge
import team.dedinside.yapi.tui.widget.statusBadge

internal fun sidebarPanel(s: MainState): Widget {
    val focused = s.focus == Focus.PROJECT
    val title = if (focused) brightWhite(bold("Project")) else dim("Project")
    val rightHint = dim(if (s.demo) "[D] empty" else "[D] demo")

    val titleRow = horizontalLayout {
        spacing = 1
        column(0) { width = ColumnWidth.Expand() }
        column(1) { width = ColumnWidth.Auto }
        cell(Text(title))
        cell(Text(rightHint))
    }

    val body = if (!s.demo) {
        verticalLayout {
            spacing = 0
            cell(titleRow)
            cell(Text(""))
            cell(emptyTreeOnboarding())
        }
    } else {
        verticalLayout {
            spacing = 0
            cell(titleRow)
            cell(Text(""))
            cell(Text(dim("  collections")))
            cell(treeWidget(s))
            cell(Text(""))
            cell(HorizontalRule(ruleCharacter = "─"))
            cell(Text(dim("  recent")))
            cell(verticalLayout { for (r in s.recent) cell(Text(formatRecent(r))) })
        }
    }

    return Panel(
        content = body,
        title = Text(if (focused) brightBlue(bold("◆ Project")) else dim("◇ Project")),
        expand = true,
        padding = Padding(0, 1, 0, 1),
    )
}

private fun treeWidget(s: MainState): Widget {
    val rows = s.visibleRows()
    if (rows.isEmpty()) return emptyTreeOnboarding()
    return verticalLayout {
        for ((i, row) in rows.withIndex()) {
            val isCurrent = i == s.treeSelected && s.focus == Focus.PROJECT
            val isDimSelect = i == s.treeSelected && s.focus != Focus.PROJECT
            cell(Text(formatTreeRow(row, isCurrent, isDimSelect)))
        }
    }
}

private fun emptyTreeOnboarding(): Widget = Text(
    buildString {
        appendLine("  " + dim("Empty project"))
        appendLine()
        appendLine("  No saved requests yet.")
        appendLine()
        appendLine("  " + brightBlue("›") + " " + bold("n") + "      compose a new request")
        appendLine("  " + brightBlue("›") + " " + bold("Ctrl+P") + " browse commands")
        appendLine("  " + brightBlue("›") + " " + bold("D") + "      load demo tree")
        appendLine()
        appendLine(dim("  ─"))
        appendLine()
        appendLine(dim("  Saved requests live as .http files"))
        appendLine(dim("  under ~/.config/yapi/requests/."))
        appendLine(dim("  Disk loading lands in issue #11."))
    },
    whitespace = Whitespace.PRE_WRAP,
)

private fun formatTreeRow(row: TreeRow, isCurrent: Boolean, isDimSelect: Boolean): String {
    val indent = "  ".repeat(row.depth)
    val marker = when {
        isCurrent -> brightBlue("▶ ")
        isDimSelect -> dim("▶ ")
        else -> "  "
    }
    return when (val node = row.node) {
        is Folder -> {
            val chevron = if (node.expanded) "▾" else "▸"
            val name = if (isCurrent) brightWhite(bold(node.name + "/")) else node.name + "/"
            val count = dim(" (${node.children.size})")
            "$marker$indent$chevron $name$count"
        }

        is RequestFile -> {
            val name = if (isCurrent) brightWhite(bold(node.name)) else node.name
            "$marker$indent  ${methodBadge(node.method)} $name"
        }
    }
}

private fun formatRecent(r: RecentEntry): String = "  ${methodBadge(r.method)}  ${statusBadge(r.status)}  " +
    "${r.path}${dim("   ${r.elapsedMs}ms  ${r.time}")}"
