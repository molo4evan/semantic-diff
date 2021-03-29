package ru.nsu.fit.molochev.semanticdiff.core.editscript

class EditScript {
    val operations = mutableListOf<EditOperation>()

    fun cost() = operations.size

    fun addAndExecOp(op: EditOperation) {
        operations.add(op)
        op.exec()
    }
}
