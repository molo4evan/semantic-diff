package ru.nsu.fit.molochev.semanticdiff.config

import fleet.com.intellij.psi.TokenType
import fleet.com.intellij.psi.tree.IElementType
import fleet.org.jetbrains.kotlin.lexer.KtTokens
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

class KotlinDiffConfiguration: DiffConfiguration() {

    override fun identify(node: DiffTreeNode): String? {
        val idNode = node.children.find { it.label == KtTokens.IDENTIFIER }
        return idNode?.text
    }

    override fun elementsToIgnore(): List<IElementType> {
        return listOf(
            TokenType.WHITE_SPACE,
            TokenType.ERROR_ELEMENT,
            TokenType.BAD_CHARACTER
        )
    }
}
