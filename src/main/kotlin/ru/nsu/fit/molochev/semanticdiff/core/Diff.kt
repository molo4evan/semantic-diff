package ru.nsu.fit.molochev.semanticdiff.core

import fleet.com.intellij.psi.FleetPsiParser
import fleet.com.intellij.psi.builder.parse
import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.core.editscript.EditScript
import ru.nsu.fit.molochev.semanticdiff.core.editscript.EditScriptBuilder
import ru.nsu.fit.molochev.semanticdiff.core.matching.MatchingBuilder
import ru.nsu.fit.molochev.semanticdiff.utils.toDiffTreeNode

class Diff(private var parser: FleetPsiParser) {

    private var config = DiffConfiguration(0.5, 0.5, 0.5)

    private var matchingBuilder = MatchingBuilder(config)
    private val editScriptBuilder = EditScriptBuilder()

    fun configuration(config: DiffConfiguration) {
        this.config = config
        matchingBuilder = MatchingBuilder(config)
    }

    fun parser(parser: FleetPsiParser) {
        this.parser = parser
    }

    fun diff(textBefore: String, textAfter: String): EditScript {
        val treeBefore = parser.parse(textBefore)
        val treeAfter = parser.parse(textAfter)

        val t1 = treeBefore.toDiffTreeNode(config, textBefore)
        val t2 = treeAfter.toDiffTreeNode(config, textAfter)
        val matching = matchingBuilder.match(t1, t2)
        return editScriptBuilder.buildScript(t1, t2, matching)
    }
}
