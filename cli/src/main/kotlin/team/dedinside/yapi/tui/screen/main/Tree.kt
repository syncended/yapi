package team.dedinside.yapi.tui.screen.main

internal sealed interface TreeNode { val name: String }

internal class Folder(
    override val name: String,
    val children: List<TreeNode>,
    var expanded: Boolean = true,
) : TreeNode

internal class RequestFile(
    override val name: String,
    val method: String,
    val path: String,
) : TreeNode

internal data class RecentEntry(
    val method: String,
    val path: String,
    val status: Int,
    val elapsedMs: Int,
    val time: String,
)

internal data class TreeRow(val depth: Int, val node: TreeNode)
