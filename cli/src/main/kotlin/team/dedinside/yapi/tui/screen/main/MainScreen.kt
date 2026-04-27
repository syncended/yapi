package team.dedinside.yapi.tui.screen.main

import team.dedinside.yapi.tui.core.Screen
import team.dedinside.yapi.tui.core.screen

/**
 * IDE-like workspace screen.
 *
 * Layout:
 * - Header bar.
 * - Workspace row: Project sidebar (left, toggle Ctrl+B) + Editor (right).
 *   When the command palette is open it occupies the editor cell as a dialog.
 * - Footer with context-sensitive hints.
 *
 * Focus:
 * - `Tab` / `Shift+Tab` cycle inside the editor: URL → TABS → RESPONSE → URL.
 *   `Tab` from PROJECT drills into the editor.
 * - `Esc` / `q` drill out: editor → Project, Project → quit. Esc never destroys
 *   typed input — use `Ctrl+U` to clear the URL.
 *
 * State and key handling live on [MainState]; rendering is split across
 * [Render], [Sidebar], and [Editor] siblings.
 */
fun mainScreen(): Screen {
    val state = MainState()
    return screen {
        onHotkey = state::handleKey
        renderHome(state)
    }
}
