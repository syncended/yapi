package team.dedinside.yapi.tui.screen.main

import com.github.ajalt.mordant.input.KeyboardEvent
import team.dedinside.yapi.tui.model.ScreenAction
import team.dedinside.yapi.tui.screen.helpScreen
import team.dedinside.yapi.tui.screen.placeholderScreen
import team.dedinside.yapi.tui.widget.CommandPalette
import team.dedinside.yapi.tui.widget.PaletteCommand

internal enum class WorkbenchTab(val label: String) {
    HEADERS("Headers"),
    QUERY("Query"),
    BODY("Body"),
    AUTH("Auth"),
}

internal enum class Focus { PROJECT, URL, TABS, RESPONSE }

internal class MainState {
    var demo: Boolean = false
    var sidebarVisible: Boolean = true
    var focus: Focus = Focus.PROJECT
    var treeSelected: Int = 0

    var url: String = ""
    val method: String = "GET"
    var tab: WorkbenchTab = WorkbenchTab.HEADERS
    var lastSent: String? = null

    /** Non-null while the command palette dialog is open. */
    var palette: CommandPalette? = null

    val tree: List<Folder> = listOf(
        Folder(
            "users", listOf(
                RequestFile("list.http", "GET", "/users"),
                RequestFile("get-by-id.http", "GET", "/users/{id}"),
                RequestFile("create.http", "POST", "/users"),
                RequestFile("delete.http", "DELETE", "/users/{id}"),
            )
        ),
        Folder(
            "orders", listOf(
                RequestFile("list.http", "GET", "/orders"),
                RequestFile("submit.http", "POST", "/orders"),
            ),
            expanded = false,
        ),
        Folder(
            "auth", listOf(
                RequestFile("login.http", "POST", "/auth/login"),
                RequestFile("logout.http", "POST", "/auth/logout"),
                RequestFile("refresh.http", "POST", "/auth/refresh"),
            )
        ),
    )

    val recent: List<RecentEntry> = listOf(
        RecentEntry("GET", "/users/42", 200, 142, "12:43"),
        RecentEntry("POST", "/users", 201, 88, "12:39"),
        RecentEntry("GET", "/users/9999", 404, 31, "12:31"),
    )

    fun visibleRows(): List<TreeRow> {
        if (!demo) return emptyList()
        val out = mutableListOf<TreeRow>()
        for (f in tree) walk(f, depth = 0, out)
        return out
    }

    private fun walk(node: TreeNode, depth: Int, out: MutableList<TreeRow>) {
        out += TreeRow(depth, node)
        if (node is Folder && node.expanded) for (child in node.children) walk(child, depth + 1, out)
    }

    fun startNewRequest() {
        url = ""
        lastSent = null
        tab = WorkbenchTab.HEADERS
        focus = Focus.URL
    }

    fun drillOutToProject(): ScreenAction {
        if (!sidebarVisible) sidebarVisible = true
        focus = Focus.PROJECT
        return ScreenAction.Stay
    }

    private fun cycleFocus(forward: Boolean) {
        focus = when (focus) {
            Focus.PROJECT -> Focus.URL
            Focus.URL -> if (forward) Focus.TABS else Focus.RESPONSE
            Focus.TABS -> if (forward) Focus.RESPONSE else Focus.URL
            Focus.RESPONSE -> if (forward) Focus.URL else Focus.TABS
        }
    }

    private fun isPrintable(event: KeyboardEvent): Boolean =
        !event.ctrl && !event.alt && event.key.length == 1 && event.key[0] >= ' '

    fun handleKey(event: KeyboardEvent): ScreenAction {
        // Palette dialog absorbs all input while open.
        palette?.let { p ->
            return when (val outcome = p.handle(event)) {
                CommandPalette.Outcome.Stay -> ScreenAction.Stay
                CommandPalette.Outcome.Cancel -> { palette = null; ScreenAction.Stay }
                is CommandPalette.Outcome.Run -> {
                    palette = null
                    outcome.action()
                }
            }
        }

        if (event.ctrl && event.key == "p") {
            palette = CommandPalette(buildPaletteCommands())
            return ScreenAction.Stay
        }
        if (event.ctrl && event.key == "b") {
            sidebarVisible = !sidebarVisible
            if (!sidebarVisible && focus == Focus.PROJECT) focus = Focus.URL
            return ScreenAction.Stay
        }
        if (event.key == "Tab" && !event.shift) { cycleFocus(forward = true); return ScreenAction.Stay }
        if (event.key == "Tab" && event.shift) { cycleFocus(forward = false); return ScreenAction.Stay }

        return when (focus) {
            Focus.PROJECT -> handleProjectFocus(event)
            Focus.URL -> handleUrlFocus(event)
            Focus.TABS -> handleTabsFocus(event)
            Focus.RESPONSE -> handleResponseFocus(event)
        }
    }

    private fun handleProjectFocus(event: KeyboardEvent): ScreenAction {
        val rows = visibleRows()
        return when (event.key) {
            "ArrowUp", "k" -> { if (rows.isNotEmpty()) treeSelected = (treeSelected - 1 + rows.size) % rows.size; ScreenAction.Stay }
            "ArrowDown", "j" -> { if (rows.isNotEmpty()) treeSelected = (treeSelected + 1) % rows.size; ScreenAction.Stay }
            "Home", "g" -> { treeSelected = 0; ScreenAction.Stay }
            "End", "G" -> { if (rows.isNotEmpty()) treeSelected = rows.lastIndex; ScreenAction.Stay }
            "ArrowLeft", "h" -> { collapseOrAscend(rows); ScreenAction.Stay }
            "ArrowRight", "l" -> { expandOrDescend(rows); ScreenAction.Stay }
            "Enter", " " -> activateTreeSelection(rows)
            "n", "N" -> { startNewRequest(); ScreenAction.Stay }
            "d", "D" -> { demo = !demo; treeSelected = 0; ScreenAction.Stay }
            "?" -> ScreenAction.Push(helpScreen())
            "q", "Q", "Escape" -> ScreenAction.Exit
            else -> ScreenAction.Stay
        }
    }

    private fun collapseOrAscend(rows: List<TreeRow>) {
        if (rows.isEmpty()) return
        val current = rows[treeSelected]
        when (val node = current.node) {
            is Folder -> if (node.expanded) node.expanded = false
            is RequestFile -> {
                val parentIdx = (treeSelected - 1 downTo 0).firstOrNull { rows[it].depth == current.depth - 1 }
                if (parentIdx != null) treeSelected = parentIdx
            }
        }
    }

    private fun expandOrDescend(rows: List<TreeRow>) {
        if (rows.isEmpty()) return
        val current = rows[treeSelected]
        if (current.node is Folder && !current.node.expanded) {
            current.node.expanded = true
        } else if (current.node is Folder && current.node.expanded && current.node.children.isNotEmpty()) {
            treeSelected = (treeSelected + 1).coerceAtMost(rows.size - 1)
        }
    }

    private fun activateTreeSelection(rows: List<TreeRow>): ScreenAction {
        if (rows.isEmpty()) { startNewRequest(); return ScreenAction.Stay }
        val current = rows[treeSelected]
        when (val node = current.node) {
            is Folder -> node.expanded = !node.expanded
            is RequestFile -> {
                url = "https://api.example.com" + node.path
                lastSent = null
                tab = WorkbenchTab.HEADERS
                focus = Focus.URL
            }
        }
        return ScreenAction.Stay
    }

    private fun handleUrlFocus(event: KeyboardEvent): ScreenAction = when {
        event.ctrl && event.key == "u" -> { url = ""; ScreenAction.Stay }
        event.key == "Backspace" -> { if (url.isNotEmpty()) url = url.dropLast(1); ScreenAction.Stay }
        event.key == "Enter" -> { if (url.isNotEmpty()) lastSent = url; ScreenAction.Stay }
        event.key == "Escape" -> drillOutToProject()
        event.key == "?" && url.isEmpty() -> ScreenAction.Push(helpScreen())
        isPrintable(event) -> { url += event.key; ScreenAction.Stay }
        else -> ScreenAction.Stay
    }

    private fun handleTabsFocus(event: KeyboardEvent): ScreenAction {
        val tabs = WorkbenchTab.entries
        return when (event.key) {
            "ArrowLeft", "h" -> { tab = tabs[(tab.ordinal - 1 + tabs.size) % tabs.size]; ScreenAction.Stay }
            "ArrowRight", "l" -> { tab = tabs[(tab.ordinal + 1) % tabs.size]; ScreenAction.Stay }
            "?" -> ScreenAction.Push(helpScreen())
            "Escape", "q", "Q" -> drillOutToProject()
            "n", "N" -> { startNewRequest(); ScreenAction.Stay }
            else -> ScreenAction.Stay
        }
    }

    private fun handleResponseFocus(event: KeyboardEvent): ScreenAction = when (event.key) {
        "Escape" -> drillOutToProject()
        "?" -> ScreenAction.Push(helpScreen())
        "q", "Q" -> drillOutToProject()
        "n", "N" -> { startNewRequest(); ScreenAction.Stay }
        else -> ScreenAction.Stay
    }

    private fun buildPaletteCommands(): List<PaletteCommand> = listOf(
        PaletteCommand(
            label = "New request",
            hint = "clear editor & focus URL",
            shortcut = "n",
            category = "Workspace",
        ) { startNewRequest(); ScreenAction.Stay },
        PaletteCommand(
            label = "Toggle sidebar",
            hint = if (sidebarVisible) "hide project pane" else "show project pane",
            shortcut = "Ctrl+B",
            category = "Workspace",
        ) {
            sidebarVisible = !sidebarVisible
            if (!sidebarVisible && focus == Focus.PROJECT) focus = Focus.URL
            ScreenAction.Stay
        },
        PaletteCommand(
            label = "Toggle demo data",
            hint = if (demo) "show empty state" else "show demo tree",
            shortcut = "D",
            category = "Workspace",
        ) { demo = !demo; treeSelected = 0; ScreenAction.Stay },
        PaletteCommand(
            label = "History",
            hint = "recent requests · #12",
            category = "Navigation",
        ) {
            ScreenAction.Push(
                placeholderScreen(
                    title = "History",
                    body = "Recent request log will appear here.\n\nTracked in issue #12.",
                )
            )
        },
        PaletteCommand(
            label = "Settings",
            hint = "edit configuration · #7",
            category = "Navigation",
        ) {
            ScreenAction.Push(
                placeholderScreen(
                    title = "Settings",
                    body = "Configuration editor will appear here.\n\nTracked in issue #7.",
                )
            )
        },
        PaletteCommand(
            label = "Help",
            hint = "key bindings reference",
            shortcut = "?",
            category = "General",
        ) { ScreenAction.Push(helpScreen()) },
        PaletteCommand(
            label = "Quit",
            hint = "exit yapi",
            shortcut = "q",
            category = "General",
        ) { ScreenAction.Exit },
    )
}
