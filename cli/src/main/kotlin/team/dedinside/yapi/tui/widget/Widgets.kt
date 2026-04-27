package team.dedinside.yapi.tui.widget

import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightCyan
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextColors.brightYellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.widgets.Text

/** Two-column app header bar: brand + profile on the left, hint on the right. */
fun headerBar(
    brand: String,
    version: String,
    profile: String,
    rightHint: String,
): Widget = horizontalLayout {
    spacing = 2
    column(0) { width = ColumnWidth.Expand() }
    column(1) { width = ColumnWidth.Auto }
    cell(Text(brightWhite(bold(brand)) + dim(" $version   ") + brightBlue("profile:") + " $profile"))
    cell(Text(dim(rightHint)))
}

/** Left-edge focus marker — bright when focused, blank otherwise. */
fun focusMarker(focused: Boolean): String = if (focused) brightBlue("▎") else " "

/** Coloured HTTP method badge. */
fun methodBadge(method: String): String = when (method.uppercase()) {
    "GET" -> brightGreen(bold(method.padEnd(6)))
    "POST" -> brightYellow(bold(method.padEnd(6)))
    "PUT" -> brightBlue(bold(method.padEnd(6)))
    "PATCH" -> brightCyan(bold(method.padEnd(6)))
    "DELETE" -> brightRed(bold(method.padEnd(6)))
    else -> dim(method.padEnd(6))
}

/** Coloured HTTP status badge by class (2xx green, 3xx cyan, 4xx yellow, 5xx red). */
fun statusBadge(status: Int): String = when (status) {
    in 200..299 -> brightGreen(bold(status.toString()))
    in 300..399 -> brightCyan(bold(status.toString()))
    in 400..499 -> brightYellow(bold(status.toString()))
    in 500..599 -> brightRed(bold(status.toString()))
    else -> dim(status.toString())
}

/** Footer line: blue `[Label]` + dim context-keys string. */
fun statusFooter(label: String, keys: String): Widget =
    Text(brightBlue("[$label] ") + dim(keys), align = TextAlign.LEFT)
