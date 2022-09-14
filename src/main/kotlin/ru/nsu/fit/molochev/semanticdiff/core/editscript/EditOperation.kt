package ru.nsu.fit.molochev.semanticdiff.core.editscript

import fleet.com.intellij.psi.builder.Node
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

sealed class EditOperation {

    abstract fun exec()
    abstract fun leftStart(): Int?
    abstract fun leftEnd(): Int?
    abstract fun rightStart(): Int?
    abstract fun rightEnd(): Int?
}

class Insert(
    val node: DiffTreeNode,
    val parent: DiffTreeNode,
    val index: Int
): EditOperation() {

    override fun exec() {
        parent.addChild(node, index)
    }

    override fun leftStart(): Int? = null

    override fun leftEnd(): Int? = null

    override fun rightStart() = node.value.startOffset

    override fun rightEnd() = node.value.endOffset

    override fun toString(): String {
        return "INSERT ${node.label}(${node.value.startOffset}:${node.value.endOffset})" +
                " as $index-th child of " +
                "${parent.label}(${parent.value.startOffset}:${parent.value.endOffset})"
    }
}

class Move(
    val node: DiffTreeNode,
    val parent: DiffTreeNode,
    val index: Int,
    val newPlaceStart: Int,
    val newPlaceEnd: Int
): EditOperation() {

    override fun exec() {
        node.parent?.removeChild(node)
        parent.addChild(node, index)
    }

    override fun leftStart() = node.value.startOffset

    override fun leftEnd() = node.value.endOffset

    override fun rightStart() = newPlaceStart

    override fun rightEnd() = newPlaceEnd

    override fun toString(): String {
        return "MOVE ${node.label}(${node.value.startOffset}:${node.value.endOffset})" +
                " as $index-th child of " +
                "${parent.label}(${parent.value.startOffset}:${parent.value.endOffset})"
    }
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

    override fun leftStart() = node.value.startOffset

    override fun leftEnd() = node.value.endOffset

    override fun rightStart(): Int? = null

    override fun rightEnd(): Int? = null

    override fun toString(): String {
        return "DELETE ${node.label}(${node.value.startOffset}:${node.value.endOffset})"
    }
}

class Update(
    val node: DiffTreeNode,
    val value: Node,
    val valueText: String
): EditOperation() {
    init {
        if (!node.isLeaf()) {
            throw IllegalArgumentException("Not a leaf node")
        }
    }

    override fun exec() {
        node.value = value
    }

    override fun leftStart() = node.value.startOffset

    override fun leftEnd() = node.value.endOffset

    override fun rightStart() = value.startOffset

    override fun rightEnd() = value.endOffset

    override fun toString(): String {
        return "UPDATE ${node.label}(${node.value.startOffset}:${node.value.endOffset})" +
                "to value '${valueText}'"
    }
}
