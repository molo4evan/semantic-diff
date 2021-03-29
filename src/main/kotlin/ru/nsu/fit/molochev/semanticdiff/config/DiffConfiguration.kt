package ru.nsu.fit.molochev.semanticdiff.config

import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

open class DiffConfiguration(
    val fastMatcherCommonBound: Double,
    val similarityBoundaryCoefficient: Double,
    val strongSimilarityBoundaryPercentage: Double
) {

    open val nodesIncompatibilityConditions = mutableListOf<(DiffTreeNode, DiffTreeNode) -> Boolean>()

    fun areNodesCompatible(x: DiffTreeNode, y: DiffTreeNode): Boolean {
        return nodesIncompatibilityConditions.all { !it(x, y) }
    }

    open fun identify(node: DiffTreeNode): String? = null
}
