package ru.nsu.fit.molochev.semanticdiff.core.editscript

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

sealed class EditOperation {

    abstract fun exec()
    abstract fun leftTextRange(): TextRange?
    abstract fun rightTextRange(): TextRange?
}

class Insert(
    val node: DiffTreeNode,
    val parent: DiffTreeNode,
    val index: Int
): EditOperation() {

    override fun exec() {
        parent.addChild(node, index)
    }

    override fun leftTextRange(): TextRange? = null

    override fun rightTextRange(): TextRange? = node.value.textRange
}

class Move(
    val node: DiffTreeNode,
    val parent: DiffTreeNode,
    val index: Int,
    val newPlace: TextRange?
): EditOperation() {

    override fun exec() {
        node.parent?.removeChild(node)
        parent.addChild(node, index)
    }

    override fun leftTextRange(): TextRange? = node.value.textRange

    override fun rightTextRange(): TextRange? = newPlace
}

class Delete(
    val node: DiffTreeNode
): EditOperation() {
    init {
        if (!node.isLeaf()) {
            throw IllegalArgumentException("Not a leaf node")
        }
    }

    override fun exec() {
        node.parent?.removeChild(node)
    }

    override fun leftTextRange(): TextRange? = node.value.textRange

    override fun rightTextRange(): TextRange? = null
}

class Update(
    val node: DiffTreeNode,
    val value: PsiElement
): EditOperation() {
    init {
        if (!node.isLeaf()) {
            throw IllegalArgumentException("Not a leaf node")
        }
    }

    override fun exec() {
        node.value = value
    }

    override fun leftTextRange(): TextRange? = node.value.textRange

    override fun rightTextRange(): TextRange? = value.textRange
}
