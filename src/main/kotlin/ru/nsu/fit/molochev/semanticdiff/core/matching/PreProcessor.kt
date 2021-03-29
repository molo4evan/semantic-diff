package ru.nsu.fit.molochev.semanticdiff.core.matching

import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

class PreProcessor {

    fun match(nodesBefore: List<DiffTreeNode>, nodesAfter: List<DiffTreeNode>, matching: Matching) {
        val labels = nodesBefore.map(DiffTreeNode::label).distinct()
        for (label in labels) {
            val labeledBefore = nodesBefore.filter { it.label == label }
            val labeledAfter = nodesAfter.filter { it.label == label }

            val ids = labeledBefore.mapNotNull(DiffTreeNode::id).distinct()
            for (id in ids) {
                val beforeWithId = labeledBefore.filter { it.id == id }
                val afterWithId = labeledAfter.filter { it.id == id }

                if (beforeWithId.size == 1 && afterWithId.size == 1) {
                    val identifiedBefore = beforeWithId.first()
                    val identifiedAfter = afterWithId.first()
                    matching.add(identifiedBefore, identifiedAfter)

                    for (beforeChild in identifiedBefore.children) {
                        val partner = identifiedAfter.children.find { afterChild ->
                            matching.contains(beforeChild) && matching.contains(afterChild)
                                    && beforeChild.label == afterChild.label
                                    && (!beforeChild.isLeaf() && !afterChild.isLeaf() // TODO: What to do with not exactly equal children?
                                    || beforeChild.value == afterChild.value)         // TODO: Specific config?
                        }

                        if (partner != null) {
                            matching.add(beforeChild, partner)
                        }
                    }
                }
            }
        }
    }
}
