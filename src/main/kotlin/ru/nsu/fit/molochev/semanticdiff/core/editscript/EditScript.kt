package ru.nsu.fit.molochev.semanticdiff.core.editscript

class EditScript {
    val operations = mutableListOf<EditOperation>()

    fun addAndExecOp(op: EditOperation) {
        operations.add(op)
        op.exec()
    }

    override fun toString(): String {
        return operations.joinToString("\n", transform = EditOperation::toString)
    }
}
