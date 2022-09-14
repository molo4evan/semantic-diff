package ru.nsu.fit.molochev.semanticdiff

import fleet.org.jetbrains.kotlin.KotlinPsiParser
import ru.nsu.fit.molochev.semanticdiff.config.KotlinDiffConfiguration
import ru.nsu.fit.molochev.semanticdiff.core.Diff
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val before = readFile(args[0])
    val after = readFile(args[1])

    val diff = Diff(KotlinPsiParser())
    diff.configuration(KotlinDiffConfiguration())
    val editScript = diff.diff(before, after)
    println(editScript)
}

private fun readFile(path: String, encoding: Charset = Charsets.UTF_8): String {
    val bytes = Files.readAllBytes(Paths.get(path))
    return String(bytes, encoding)
}
