package ru.nsu.fit.molochev.semanticdiff.core

import com.intellij.psi.PsiElement
import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.core.editscript.EditScript
import ru.nsu.fit.molochev.semanticdiff.core.editscript.EditScriptBuilder
import ru.nsu.fit.molochev.semanticdiff.core.matching.FastMatcher
import ru.nsu.fit.molochev.semanticdiff.core.matching.MatchingBuilder
import ru.nsu.fit.molochev.semanticdiff.utils.toDiffTreeNode

class Diff {

    private var config = DiffConfiguration(0.5, 0.5, 0.5)

    private var matchingBuilder = MatchingBuilder(config)
    private val editScriptBuilder = EditScriptBuilder()

    fun configuration(config: DiffConfiguration) {
        this.config = config
        matchingBuilder = MatchingBuilder(config)
    }

    fun diff(treeBefore: PsiElement, treeAfter: PsiElement): EditScript {
        val t1 = treeBefore.toDiffTreeNode(config)
        val t2 = treeAfter.toDiffTreeNode(config)

        val matching = matchingBuilder.match(t1, t2)
        return editScriptBuilder.buildScript(t1, t2, matching)
    }
}
