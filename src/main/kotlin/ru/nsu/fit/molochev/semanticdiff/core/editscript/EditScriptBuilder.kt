package ru.nsu.fit.molochev.semanticdiff.core.editscript

import ru.nsu.fit.molochev.semanticdiff.core.matching.Matching
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode
import ru.nsu.fit.molochev.semanticdiff.utils.LongestCommonSubsequence

class EditScriptBuilder {
    private lateinit var matching: Matching

    fun buildScript(
        beforeTree: DiffTreeNode,
        afterTree: DiffTreeNode,
        matching: Matching
    ): EditScript {
        this.matching = matching
        val script = EditScript()
        val nodes = ArrayDeque<DiffTreeNode>()
        nodes.add(afterTree)
        bfs(nodes, script)
        deleteUnmatched(beforeTree, script)
        return script
    }

    private fun bfs(nodes: ArrayDeque<DiffTreeNode>, script: EditScript) {
        while (!nodes.isEmpty()) {
            val node = nodes.removeFirst()
            val parent = node.parent
            var partner = matching.partner(node)
            val parentPartner = matching.partner(parent)
            if (partner == null && parentPartner != null) {
                // Insert
                partner = node.copy()
                val pos = findPosition(node) { matching.contains(it) }
                script.operations.add(Insert(node, parentPartner, pos))
                matching.add(partner, node)
            } else if (partner != null && parent != null) {
                // Update
                if (partner.value != node.value) {
                    script.addAndExecOp(Update(partner, node.value))
                }
                //Move
                if (!matching.contains(partner.parent, parent) && parentPartner != null) {
                    val k = findPosition(node) { matching.contains(it) }
                    script.addAndExecOp(Move(partner, parentPartner, k, node.value.textRange))
                    partner.moveTo(parentPartner, k)
                }
            }
            // Align
            alignChildren(partner!!, node, script)

            node.children.forEach(nodes::addFirst)
        }
    }

    private fun findPosition(node: DiffTreeNode, inOrder: (DiffTreeNode) -> Boolean): Int {
        val parent = node.parent ?: return 0

        val index = parent.children.indexOf(node)
        if (index == 0) return 0

        var leftSiblingIndex = index - 1
        var leftSibling = parent.children[leftSiblingIndex]
        while (leftSiblingIndex >= 0 && !inOrder(leftSibling)) {
            leftSibling = parent.children[leftSiblingIndex--]
        }
        if (!inOrder(leftSibling)) return 0

        val leftSiblingPartner = matching.partner(leftSibling)
        val leftSiblingPartnerIndex = leftSiblingPartner?.parent?.children
            ?.filter(inOrder)
            ?.indexOf(leftSiblingPartner)
            ?: -1

        return leftSiblingPartnerIndex + 1
    }

    private fun alignChildren(node1: DiffTreeNode, node2: DiffTreeNode, script: EditScript) {
        val s1 = node1.children.filter(matching::contains)
        val s2 = node2.children.filter(matching::contains)
        val s = LongestCommonSubsequence.find(s1, s2) { a, b -> matching.contains(a, b) }

        val unordered = matching.pairs
            .filter{ s1.contains(it.first) && s2.contains(it.second) }
            .filter { !s.contains(it) }
        val ordered = s.toMutableList()

        for (pair in unordered) {
            val pos = findPosition(pair.second) { el -> ordered.any { it.second === el } }
            script.addAndExecOp(Move(pair.first, node1, pos, pair.second.value.textRange))
            ordered.add(pair)
        }
    }

    private fun deleteUnmatched(node: DiffTreeNode, script: EditScript) {
        for (child in node.children) {
            if (!matching.contains(child)) {
                script.addAndExecOp(Delete(child))
            } else {
                deleteUnmatched(child, script)
            }
        }
    }
}
