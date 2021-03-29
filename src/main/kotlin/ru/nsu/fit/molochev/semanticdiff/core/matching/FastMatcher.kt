package ru.nsu.fit.molochev.semanticdiff.core.matching

import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode
import ru.nsu.fit.molochev.semanticdiff.utils.LongestCommonSubsequence
import kotlin.math.max

class FastMatcher(private val config: DiffConfiguration) {

    fun match(nodesBefore: List<DiffTreeNode>, nodesAfter: List<DiffTreeNode>, matching: Matching) {
        val nodesToMatchBefore = nodesBefore.filter { it.isLeaf() && !matching.contains(it) }.toMutableList()
        val nodesToMatchAfter = nodesAfter.filter { it.isLeaf() && !matching.contains(it) }.toMutableList()

        while (nodesToMatchBefore.isNotEmpty() || nodesToMatchAfter.isNotEmpty()) {
            val lcs = LongestCommonSubsequence
                .find(nodesToMatchBefore, nodesToMatchAfter) { x, y -> areNodesEqual(x, y, matching) }
            lcs.forEach(matching::add)

            val unmatched = nodesToMatchBefore.filter(matching::contains)
            val newlyMatched = mutableListOf<Pair<DiffTreeNode, DiffTreeNode>>()
            for (node in unmatched) {
                val forMatch = nodesToMatchAfter
                    .filter { areNodesEqual(node, it, matching) && !matching.contains(node, it) }
                if (forMatch.size == 1) {   // TODO: Should we just take first? Or introduce 'compare'?
                    val match = forMatch.first()
                    matching.add(node, match)
                    newlyMatched.add(Pair(node, match))
                }
            }

            nodesToMatchBefore.clear()
            nodesToMatchAfter.clear()

            for (pair in (lcs + newlyMatched)) {
                val parentBefore = pair.first.parent
                if (parentBefore != null
                    && !nodesToMatchBefore.contains(parentBefore)
                    && !matching.contains(parentBefore)
                    && parentBefore.childrenMatched(matching)) {
                    nodesToMatchBefore.add(parentBefore)
                }

                val parentAfter = pair.second.parent
                if (parentAfter != null
                    && !nodesToMatchAfter.contains(parentAfter)
                    && !matching.contains(parentAfter)
                    && parentAfter.childrenMatched(matching)) {
                    nodesToMatchAfter.add(parentAfter)
                }
            }
        }
    }

    private fun DiffTreeNode.childrenMatched(matching: Matching) = children.all(matching::contains)

    fun leafLabels(nodes: List<DiffTreeNode>) = nodes
        .filter(DiffTreeNode::isLeaf)
        .map(DiffTreeNode::label)
        .distinct()

    fun areNodesEqual(x: DiffTreeNode, y: DiffTreeNode, matching: Matching): Boolean {
        if (x.label != y.label) return false

        if (x.isLeaf() && y.isLeaf()) {
            return x.value == y.value //TODO: f compare parameter?
        }

        val maxChildren = max(x.children.size, y.children.size)
        val commonCount = matching.pairs
            .count { it.first.haveParent(x) && it.second.haveParent(y) }
        val commonScore = commonCount * 1.0 / maxChildren
        return commonScore > config.fastMatcherCommonBound
    }
}
