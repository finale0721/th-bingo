package org.tfcc.bingo

class BoardSpec(val size: Int) {
    val area: Int = size * size

    init {
        require(size in 4..6) { "Board size must be 4, 5, or 6, got $size" }
    }

    fun row(index: Int): Int = index / size

    fun col(index: Int): Int = index % size

    fun index(row: Int, col: Int): Int = row * size + col

    fun isValidIndex(index: Int): Boolean = index in 0 until area

    fun rows(): List<List<Int>> = (0 until size).map { r ->
        (0 until size).map { c -> index(r, c) }
    }

    fun cols(): List<List<Int>> = (0 until size).map { c ->
        (0 until size).map { r -> index(r, c) }
    }

    fun diagonals(): List<List<Int>> {
        if (size < 5) return emptyList()
        val mainDiag = (0 until size).map { index(it, it) }
        val antiDiag = (0 until size).map { index(it, size - 1 - it) }
        return listOf(mainDiag, antiDiag)
    }

    fun winningLines(extraLineCount: Int = 0): List<List<Int>> {
        val lines = mutableListOf<List<Int>>()
        lines.addAll(rows())
        lines.addAll(cols())
        lines.addAll(diagonals())
        return lines
    }

    fun outerRingIndices(): List<Int> {
        val indices = mutableListOf<Int>()
        for (c in 0 until size) {
            indices.add(index(0, c))
            if (size > 1) indices.add(index(size - 1, c))
        }
        for (r in 1 until size - 1) {
            indices.add(index(r, 0))
            indices.add(index(r, size - 1))
        }
        return indices
    }

    fun innerIndices(): List<Int> {
        val outer = outerRingIndices().toSet()
        return (0 until area).filter { it !in outer }
    }

    fun centerIndices(): List<Int> {
        return if (size % 2 == 1) {
            val mid = size / 2
            listOf(index(mid, mid))
        } else {
            val mid1 = size / 2 - 1
            val mid2 = size / 2
            listOf(index(mid1, mid1), index(mid1, mid2), index(mid2, mid1), index(mid2, mid2))
        }
    }

    fun neighbors4(index: Int): List<Int> {
        val r = row(index)
        val c = col(index)
        val result = mutableListOf<Int>()
        if (r > 0) result.add(this.index(r - 1, c))
        if (r < size - 1) result.add(this.index(r + 1, c))
        if (c > 0) result.add(this.index(r, c - 1))
        if (c < size - 1) result.add(this.index(r, c + 1))
        return result
    }

    fun neighbors8(index: Int): List<Int> {
        val r = row(index)
        val c = col(index)
        val result = mutableListOf<Int>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until size && nc in 0 until size) {
                    result.add(this.index(nr, nc))
                }
            }
        }
        return result
    }

    companion object {
        val DEFAULT = BoardSpec(5)
    }
}
