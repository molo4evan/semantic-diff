package ru.nsu.fit.molochev.semanticdiff.visualization

import ru.nsu.fit.molochev.semanticdiff.core.editscript.EditOperation

data class DiffOperation<O: EditOperation>(
    val operations: MutableList<O>,
    val leftStart: Int?,
    val leftEnd: Int?,
    val rightStart: Int?,
    val rightEnd: Int?
) {

    fun merge(op: O) {

    }
}
