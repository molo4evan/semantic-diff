package ru.nsu.fit.molochev.semanticdiff.core.matching

import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

class MatchingBuilder(config: DiffConfiguration) {
    private val preProcessor = PreProcessor()
    private val fastMatcher = FastMatcher(config)
    private val postProcessor = PostProcessor(config)

    fun match(t1: DiffTreeNode, t2: DiffTreeNode): Matching {
        val matching = Matching()

        val nodesBefore = t1.bfs()
        val nodesAfter = t2.bfs()

        preProcessor.match(nodesBefore, nodesAfter, matching)
        fastMatcher.match(nodesBefore, nodesAfter, matching)

        postProcessor.treeHeight = t1.height()
        postProcessor.match(t1, matching)

        return matching
    }
}
