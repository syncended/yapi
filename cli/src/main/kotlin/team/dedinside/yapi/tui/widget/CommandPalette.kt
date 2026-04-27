package team.dedinside.yapi.tui.widget

import com.github.ajalt.mordant.input.KeyboardEvent
import team.dedinside.yapi.tui.model.ScreenAction
import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.TextStyles.italic
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.widgets.Padding
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text

/**
 * A single entry in the command palette.
 *
 * - [shortcut] is displayed right-aligned next to the label, e.g. `"Ctrl+B"`.
 * - [category] groups commands under a section header (preserves first-seen order).
 * - [action] runs after the palette closes; the returned [ScreenAction] is applied
 *   by the host screen (e.g. push a help screen, exit, or stay).
 */
data class PaletteCommand(
    val label: String,
    val hint: String? = null,
    val shortcut: String? = null,
    val category: String = "General",
    val action: () -> ScreenAction,
)

/**
 * Modeless command palette **dialog**. Not a [Screen] — a host screen (e.g.
 * [MainScreen]) keeps an instance of this in its state, calls [render] inside its
 * own layout, and routes key events through [handle] until the palette is
 * cancelled or runs a command.
 *
 * Keeping the palette inside the host avoids the "full-screen popup" feel and
 * lets the surrounding workspace (header, project sidebar) stay visible behind
 * the dialog.
 */
class CommandPalette(val commands: List<PaletteCommand>) {

    sealed interface Outcome {
        /** Palette stays open; re-render. */
        data object Stay : Outcome
        /** User dismissed the palette; close it. */
        data object Cancel : Outcome
        /** User picked a command; close the palette and apply [action]. */
        data class Run(val action: () -> ScreenAction) : Outcome
    }

    private var selected = 0

    fun render(): Widget {
        val titleRow = horizontalLayout {
            spacing = 2
            column(0) { width = ColumnWidth.Expand() }
            column(1) { width = ColumnWidth.Auto }
            cell(Text(brightWhite(bold("Command palette"))))
            cell(Text(dim("${commands.size} items")))
        }

        val searchRow = horizontalLayout {
            spacing = 1
            column(0) { width = ColumnWidth.Fixed(2) }
            column(1) { width = ColumnWidth.Expand() }
            cell(Text(brightBlue("›")))
            cell(Text(italic(dim("type to filter… ")) + dim("(filtering lands later)")))
        }

        val rowList = verticalLayout {
            spacing = 0
            var lastCategory: String? = null
            for ((i, cmd) in commands.withIndex()) {
                if (cmd.category != lastCategory) {
                    if (lastCategory != null) cell(Text(""))
                    cell(Text("  " + dim(cmd.category.uppercase())))
                    lastCategory = cmd.category
                }
                cell(commandRow(cmd, isCurrent = i == selected))
            }
        }

        val footer = Text(dim("  ↑↓  move    ⏎  run    Esc  cancel"))

        val body = verticalLayout {
            spacing = 0
            cell(titleRow)
            cell(Text(""))
            cell(searchRow)
            cell(Text(""))
            cell(rowList)
            cell(Text(""))
            cell(footer)
        }

        return Panel(
            content = body,
            title = Text(brightBlue(bold(" ◆ Palette "))),
            expand = true,
            padding = Padding(1, 2, 1, 2),
        )
    }

    private fun commandRow(cmd: PaletteCommand, isCurrent: Boolean): Widget {
        val marker = if (isCurrent) brightBlue(bold("›")) else " "
        val label = if (isCurrent) brightWhite(bold(cmd.label)) else cmd.label
        val hint = cmd.hint?.let { dim(it) } ?: ""
        val shortcut = cmd.shortcut?.let { dim(it) } ?: ""

        return horizontalLayout {
            spacing = 1
            column(0) { width = ColumnWidth.Fixed(2) }
            column(1) { width = ColumnWidth.Fixed(22) }
            column(2) { width = ColumnWidth.Expand() }
            column(3) { width = ColumnWidth.Fixed(10) }
            cell(Text(marker))
            cell(Text(label))
            cell(Text(hint))
            cell(Text(shortcut))
        }
    }

    fun handle(event: KeyboardEvent): Outcome = when (event.key) {
        "ArrowUp", "k" -> {
            selected = (selected - 1 + commands.size) % commands.size
            Outcome.Stay
        }
        "ArrowDown", "j" -> {
            selected = (selected + 1) % commands.size
            Outcome.Stay
        }
        "Home", "g" -> { selected = 0; Outcome.Stay }
        "End", "G" -> { selected = commands.lastIndex; Outcome.Stay }
        "Enter", " " -> Outcome.Run(commands[selected].action)
        "Escape" -> Outcome.Cancel
        else -> Outcome.Stay
    }
}
