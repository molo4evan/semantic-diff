package ru.nsu.fit.molochev.semanticdiff.core.matching

import com.intellij.util.diff.Diff
import ru.nsu.fit.molochev.semanticdiff.utils.DiffTreeNode

class Matching {
    val pairs = mutableListOf<Pair<DiffTreeNode, DiffTreeNode>>()

    fun partner(x: DiffTreeNode?): DiffTreeNode? {
        if (x == null) return null
        for (pair in pairs) {
            if (pair.first === x) return pair.second
            if (pair.second === x) return pair.first
        }
        return null
    }

    fun contains(x: DiffTreeNode?): Boolean {
        if (x == null) return false
        return pairs.any { it.first === x || it.second === x }
    }

    fun contains(x1: DiffTreeNode?, x2: DiffTreeNode?): Boolean {
        if (x1 == null || x2 == null) return false
        return pairs.any { it.first === x1 && it.second === x2 }
    }

    fun add(pair: Pair<DiffTreeNode, DiffTreeNode>) {
        pairs.add(pair)
    }

    fun add(x1: DiffTreeNode, x2: DiffTreeNode) {
        pairs.add(Pair(x1, x2))
    }

    fun removeWith(node: DiffTreeNode) = pairs.removeIf { it.first === node || it.second === node }
}
