package ru.nsu.fit.molochev.semanticdiff.utils

import fleet.com.intellij.psi.builder.Node
import fleet.com.intellij.psi.tree.IElementType
import fleet.org.jetbrains.kotlin.kdoc.lexer.KDocTokens
import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration

data class DiffTreeNode(var value: Node, var text: String) {
    private val _children: MutableList<DiffTreeNode> = arrayListOf()
    val children: List<DiffTreeNode>
        get() = _children

    var parent: DiffTreeNode? = null
        private set

    val label: IElementType
        get() = value.elementType

    var id: String? = null

    fun isLeaf(): Boolean = children.isEmpty()
            || children.size == 1 && children[0].isLeaf()

    fun height(): Int = (children.map(DiffTreeNode::height).maxOrNull() ?: 0) + 1

    fun haveParent(x: DiffTreeNode): Boolean {
        if (parent == null) return false
        if (parent === x) return true
        return parent!!.haveParent(x)
    }

    fun addChild(node: DiffTreeNode, k: Int? = null) {
        if (k == null) {
            _children.add(node)
        } else {
            _children.add(k, node)
        }
        node.parent = this
    }

    fun removeChild(node: DiffTreeNode) {
        _children.remove(node)
        node.parent = null
    }

    fun moveTo(node: DiffTreeNode, k: Int) {
        parent?.removeChild(this)
        node.addChild(this, k)
    }

    fun nodes(): List<DiffTreeNode> {
        val result = mutableListOf(this)
        children.forEach {
            result.addAll(it.nodes())
        }
        return result
    }

    fun copy(): DiffTreeNode {
        val newNode = DiffTreeNode(value, text)
        newNode.id = this.id
        return newNode
    }
}

fun Node.toDiffTreeNode(config: DiffConfiguration, text: String): DiffTreeNode {
    val node = DiffTreeNode(this, text.substring(this.startOffset, this.endOffset))
    for (child in children) {
        if (!config.elementsToIgnore().contains(child.elementType)) {
            if (this.elementType == KDocTokens.KDOC) {  // TODO: workaround, fix when parser will be fixed
                node.addChild(child.toDiffTreeNode(config, node.text))
            } else {
                node.addChild(child.toDiffTreeNode(config, text))
            }
        }
    }
    node.id = config.identify(node)
    return node
}
