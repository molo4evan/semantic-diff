package ru.nsu.fit.molochev.semanticdiff.utils

import java.lang.IllegalStateException
import kotlin.math.max

object LongestCommonSubsequence {

    private enum class Direction {
        LEFT, TOP, DIAGONAL, NONE
    }

    fun <T> find(s1: List<T>, s2: List<T>, equal: (T, T) -> Boolean): List<Pair<T, T>> {
        val subLengths = Array(s1.size + 1) { IntArray(s2.size + 1) }
        val directions = Array(s1.size + 1) { Array(s2.size + 1) { Direction.NONE } }

        for (i in 1..s1.size) {
            for (j in 1..s2.size) {
                if (equal(s1[i - 1], s2[j - 1])) {
                    subLengths[i][j] = subLengths[i - 1][j - 1] + 1
                    directions[i][j] = Direction.DIAGONAL
                } else {
                    subLengths[i][j] = max(subLengths[i][j - 1], subLengths[i - 1][j])
                    if (subLengths[i][j] == subLengths[i][j - 1]) {
                        directions[i][j] = Direction.LEFT
                    } else {
                        directions[i][j] = Direction.TOP
                    }
                }
            }
        }

        var i = s1.size
        var j = s2.size
        var direction = directions[i][j]
        val result = mutableListOf<Pair<T, T>>()
        while (direction != Direction.NONE) {
            when (direction) {
                Direction.DIAGONAL -> {
                    result.add(Pair(s1[i - 1], s2[j - 1]))
                    --i
                    --j
                }
                Direction.TOP -> --i
                Direction.LEFT -> --j
                else -> throw IllegalStateException()
            }
            direction = directions[i][j]
        }

        return result.reversed()
    }
}
