package ru.nsu.fit.molochev.semanticdiff.core.matching

import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode
import ru.nsu.fit.molochev.semanticdiff.utils.LongestCommonSubsequence
import kotlin.math.max

class PostProcessor(
    private val config: DiffConfiguration,
    var treeHeight: Int = 0
) {

    fun match(node: DiffTreeNode, matching: Matching) {
        val partner = matching.partner(node) ?: return
        for (child in node.children) {
            val childPartner = matching.partner(child)
            var match: DiffTreeNode? = null

            if (childPartner == null) {
                val partnerMatchedChildren = partner.children.filter(matching::contains)
                val partnerUnmatchedChildren = partner.children.filter { !matching.contains(it) }

                val bestMatched = findBestPartner(child, partnerMatchedChildren)
                val bestUnmatched = findBestPartner(child, partnerUnmatchedChildren)
                if (bestMatched == null) {
                    if (bestUnmatched != null) {
                        match = bestUnmatched
                    } else {
                        child.removeFromMatchingRecursively(matching)
                        continue
                    }
                } else {
                    val partnerForMatched = matching.partner(bestMatched)
                    val realParentDiffers = (partnerForMatched != null
                            && partnerForMatched.parent !== node)
                    if (realParentDiffers) {
                        match = bestMatched
                    } else if (bestUnmatched != null) {
                        match = bestUnmatched
                    }
                }
            } else if (childPartner.parent !== partner) {
                match = findBestPartner(child, partner.children)
                if (match == null && !config.areNodesCompatible(child, childPartner)) {
                    child.removeFromMatchingRecursively(matching)
                    continue
                }
            }

            if (match != null) {
                matching.removeWith(child)
                matching.removeWith(match)
                matching.add(child, match)
            }

            match(child, matching)
        }
    }

    private fun findBestPartner(node: DiffTreeNode, candidates: List<DiffTreeNode>): DiffTreeNode? {
        val boundPercentage = config.similarityBoundaryCoefficient * (1 - node.height() * 1.0 / treeHeight)

        var maxPercentage = 0.0
        var partner: DiffTreeNode? = null

        candidates.filter { it.label == node.label }
            .forEach {
                val maxLength = max(node.value.textLength, it.value.textLength)
                val lcs = LongestCommonSubsequence.find(
                    node.value.text.toCharArray().asList(),
                    it.value.text.toCharArray().asList(),
                    Char::equals)
                val percentage = lcs.size * 1.0 / maxLength
                if (percentage > maxPercentage) {
                    maxPercentage = percentage
                    partner = it
                }
            }

        if (node.isLeaf()) return if (maxPercentage == 1.0) partner else null

        if (node.id != null) {
            return if (node.id == partner?.id || maxPercentage > config.strongSimilarityBoundaryPercentage) {
                partner
            } else null
        }

        return if (maxPercentage > boundPercentage) partner else null
    }

    private fun DiffTreeNode.removeFromMatchingRecursively(matching: Matching) {
        matching.removeWith(this)
        children.forEach { it.removeFromMatchingRecursively(matching) }
    }
}
