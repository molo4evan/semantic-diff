package ru.nsu.fit.molochev.semanticdiff.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import ru.nsu.fit.molochev.semanticdiff.config.DiffConfiguration

data class DiffTreeNode(var value: PsiElement) {
    private val _children: MutableList<DiffTreeNode> = arrayListOf()
    val children: List<DiffTreeNode>
        get() = _children

    var parent: DiffTreeNode? = null
        private set

    val label: IElementType?
        get() = value.node?.elementType

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

    fun bfs(): List<DiffTreeNode> {
        val result = mutableListOf<DiffTreeNode>()
        children.forEach {
            result.addAll(it.bfs())
        }
        return result
    }

    fun copy(): DiffTreeNode {
        if (!isLeaf()) {
            throw IllegalStateException("Cannot copy not leaf node")
        }
        return DiffTreeNode(value)
    }
}

fun PsiElement.toDiffTreeNode(config: DiffConfiguration): DiffTreeNode {
    val node = DiffTreeNode(this)
    for (child in children) {
        if (child !is PsiWhiteSpace) {
            node.addChild(child.toDiffTreeNode(config))
        }
    }
    node.id = config.identify(node)
    return node
}
