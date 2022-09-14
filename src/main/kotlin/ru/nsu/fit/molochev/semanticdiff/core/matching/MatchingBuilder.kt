package ru.nsu.fit.molochev.semanticdiff.core.matching

import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

class MatchingBuilder(config: DiffConfiguration) {
    private val preProcessor = PreProcessor()
    private val fastMatcher = FastMatcher(config)
    private val postProcessor = PostProcessor(config)

    fun match(t1: DiffTreeNode, t2: DiffTreeNode): Matching {
        val matching = Matching()

        val nodesBefore = t1.nodes()
        val nodesAfter = t2.nodes()

        preProcessor.match(nodesBefore, nodesAfter, matching)
        fastMatcher.match(nodesBefore, nodesAfter, matching)

        var matchedRoot1 = t1
        if (!matching.contains(t1) && !matching.contains(t2)) {
            matching.add(t1, t2)
        } else if (!matching.contains(t1) || !matching.contains(t2)) {
            matchedRoot1 = t1.copy()
            matchedRoot1.addChild(t1)
            val matchedRoot2 = t2.copy()
            matchedRoot2.addChild(t2)
            matching.add(matchedRoot1, matchedRoot2)
        }

        postProcessor.treeHeight = matchedRoot1.height()
        postProcessor.match(matchedRoot1, matching)

        return matching
    }
}
