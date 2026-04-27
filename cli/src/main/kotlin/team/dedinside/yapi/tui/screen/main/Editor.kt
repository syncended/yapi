package team.dedinside.yapi.tui.screen.main

import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.TextStyles.inverse
import com.github.ajalt.mordant.rendering.TextStyles.italic
import com.github.ajalt.mordant.rendering.TextStyles.underline
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.widgets.HorizontalRule
import com.github.ajalt.mordant.widgets.Padding
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import team.dedinside.yapi.tui.widget.focusMarker

internal fun editorPanel(s: MainState): Widget {
    val focused = s.focus in setOf(Focus.URL, Focus.TABS, Focus.RESPONSE)
    val titleText = when (s.focus) {
        Focus.URL -> brightBlue(bold("◆ Editor · URL"))
        Focus.TABS -> brightBlue(bold("◆ Editor · ${s.tab.label}"))
        Focus.RESPONSE -> brightBlue(bold("◆ Editor · Response"))
        Focus.PROJECT -> dim("◇ Editor")
    }

    val body = verticalLayout {
        spacing = 0
        cell(urlRow(s))
        cell(HorizontalRule(ruleCharacter = "─"))
        cell(tabsRow(s))
        cell(Text(""))
        cell(tabBody(s))
        cell(HorizontalRule(ruleCharacter = "─"))
        cell(responseHeader(s))
        cell(Text(""))
        cell(responseBody(s))
    }

    return Panel(
        content = body,
        title = Text(titleText),
        expand = true,
        padding = Padding(0, 1, 0, 1),
        borderStyle = if (focused) brightBlue + bold else null,
    )
}

private fun urlRow(s: MainState): Widget {
    val urlFocused = s.focus == Focus.URL
    val urlField = when {
        s.url.isEmpty() && urlFocused -> italic(dim("type a URL or paste a curl command…")) + brightWhite("▌")
        s.url.isEmpty() -> italic(dim("type a URL or paste a curl command…"))
        urlFocused -> s.url + brightWhite("▌")
        else -> s.url
    }
    return horizontalLayout {
        spacing = 1
        column(0) { width = ColumnWidth.Fixed(2) }
        column(1) { width = ColumnWidth.Auto }
        column(2) { width = ColumnWidth.Expand() }
        column(3) { width = ColumnWidth.Auto }
        cell(Text(focusMarker(urlFocused)))
        cell(Text(brightGreen(bold(" ${s.method} ")) + dim(" ▾")))
        cell(Text(urlField))
        cell(Text(brightWhite(inverse(" ⏎ Send "))))
    }
}

private fun tabsRow(s: MainState): Widget {
    val tabsFocused = s.focus == Focus.TABS
    val tabsLine = WorkbenchTab.entries.joinToString("  ·  ") { tabLabel(it, it == s.tab, tabsFocused) }
    return horizontalLayout {
        spacing = 1
        column(0) { width = ColumnWidth.Fixed(2) }
        column(1) { width = ColumnWidth.Expand() }
        cell(Text(focusMarker(tabsFocused)))
        cell(Text(tabsLine))
    }
}

private fun tabLabel(t: WorkbenchTab, active: Boolean, regionFocused: Boolean): String = when {
    active && regionFocused -> brightWhite(bold(underline(t.label)))
    active -> bold(underline(t.label))
    else -> dim(t.label)
}

private fun tabBody(s: MainState): Widget {
    val text = when (s.tab) {
        WorkbenchTab.HEADERS -> "No headers yet. ${dim("(Adding headers lands in #4.)")}"
        WorkbenchTab.QUERY -> "No query parameters yet. ${dim("(Editing lands in #4.)")}"
        WorkbenchTab.BODY -> "No body. ${dim("(Body editing lands in #4.)")}"
        WorkbenchTab.AUTH -> "No auth configured. ${dim("(Auth helpers land in v0.2.)")}"
    }
    return horizontalLayout {
        spacing = 1
        column(0) { width = ColumnWidth.Fixed(2) }
        column(1) { width = ColumnWidth.Expand() }
        cell(Text(""))
        cell(Text(text, whitespace = Whitespace.PRE_WRAP))
    }
}

private fun responseHeader(s: MainState): Widget {
    val responseFocused = s.focus == Focus.RESPONSE
    val status = s.lastSent?.let { dim("stub send · ") + brightGreen("queued") } ?: dim("no request sent yet")
    return horizontalLayout {
        spacing = 2
        column(0) { width = ColumnWidth.Fixed(2) }
        column(1) { width = ColumnWidth.Expand() }
        column(2) { width = ColumnWidth.Auto }
        cell(Text(focusMarker(responseFocused)))
        cell(Text(bold("Response")))
        cell(Text(status))
    }
}

private fun responseBody(s: MainState): Widget {
    val text = s.lastSent?.let { sent ->
        buildString {
            appendLine(dim("──> would send (real executor lands in #4):"))
            appendLine()
            appendLine("  ${s.method} $sent")
            appendLine()
            appendLine(dim("Press Esc to drill out to Project."))
        }
    } ?: buildString {
        appendLine(bold("Welcome to yapi."))
        appendLine()
        appendLine("To get started:")
        appendLine("  · " + bold("Tab") + " into the URL field, type a URL, press " + bold("Enter"))
        appendLine("  · Pick a saved request from the " + bold("Project") + " sidebar")
        appendLine("  · Press " + bold("Ctrl+P") + " for the command palette")
        appendLine()
        appendLine(dim("Tip: ") + bold("Ctrl+B") + dim(" hides the sidebar to give the editor full width."))
    }
    return horizontalLayout {
        spacing = 1
        column(0) { width = ColumnWidth.Fixed(2) }
        column(1) { width = ColumnWidth.Expand() }
        cell(Text(""))
        cell(Text(text, whitespace = Whitespace.PRE_WRAP))
    }
}
