package ru.nsu.fit.molochev.semanticdiff.config

import fleet.com.intellij.psi.tree.IElementType
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

open class DiffConfiguration(
    val fastMatcherCommonBound: Double = 0.5,
    val similarityBoundaryCoefficient: Double = 0.5,
    val strongSimilarityBoundaryPercentage: Double = 0.5
) {

    open val nodesIncompatibilityConditions = mutableListOf<(DiffTreeNode, DiffTreeNode) -> Boolean>()

    fun areNodesCompatible(x: DiffTreeNode, y: DiffTreeNode): Boolean {
        return nodesIncompatibilityConditions.all { !it(x, y) }
    }

    open fun identify(node: DiffTreeNode): String? = null

    open fun elementsToIgnore(): List<IElementType> {
        return listOf()
    }
}
