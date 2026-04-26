package org.tfcc.bingo

data class DifficultyProfile(
    val name: String,
    val countsBySize: Map<Int, IntArray>,
) {
    fun counts(boardSize: Int): IntArray {
        return countsBySize[boardSize]
            ?: countsBySize[5]!!.let { base ->
                // Scale low-star counts: total low = boardArea - highCount (highCount = boardSize)
                scaleCounts(base, boardSize * boardSize - boardSize)
            }
    }

    companion object {
        fun scaleCounts(base: IntArray, targetTotal: Int): IntArray {
            val baseTotal = base.sum()
            if (baseTotal == targetTotal) return base.copyOf()
            val result = IntArray(base.size)
            var remaining = targetTotal
            for (i in base.indices) {
                if (i == base.lastIndex) {
                    result[i] = remaining
                } else {
                    val scaled = (base[i].toDouble() * targetTotal / baseTotal).toInt()
                    result[i] = scaled.coerceIn(0, remaining)
                    remaining -= result[i]
                }
            }
            return result
        }

        val E = DifficultyProfile(
            "E",
            mapOf(
                4 to intArrayOf(7, 4, 1), // 7+4+1=12 non-high + 4 high = 16
                5 to intArrayOf(12, 6, 2), // 12+6+2=20 non-high + 5 high = 25
                6 to intArrayOf(18, 9, 3), // 18+9+3=30 non-high + 6 high = 36
            )
        )

        val N = DifficultyProfile(
            "N",
            mapOf(
                4 to intArrayOf(4, 5, 3), // 4+5+3=12
                5 to intArrayOf(6, 8, 6), // 6+8+6=20
                6 to intArrayOf(9, 12, 9), // 9+12+9=30
            )
        )

        val L = DifficultyProfile(
            "L",
            mapOf(
                4 to intArrayOf(1, 4, 7), // 1+4+7=12
                5 to intArrayOf(2, 6, 12), // 2+6+12=20
                6 to intArrayOf(3, 9, 18), // 3+9+18=30
            )
        )

        val OD = DifficultyProfile(
            "OD",
            mapOf(
                5 to intArrayOf(1, 4, 8, 0, 0, 5, 2),
            )
        )

        val ODP = DifficultyProfile(
            "ODP",
            mapOf(
                5 to intArrayOf(0, 0, 6, 0, 0, 9, 5),
            )
        )

        val ODBP = DifficultyProfile(
            "ODBP",
            mapOf(
                5 to intArrayOf(3, 12, 0, 5),
            )
        )

        val ODPBP = DifficultyProfile(
            "ODPBP",
            mapOf(
                5 to intArrayOf(0, 10, 0, 10),
            )
        )

        val LDefault = DifficultyProfile(
            "LDefault",
            mapOf(
                5 to intArrayOf(2, 6, 12, 0, 0, 0, 0),
            )
        )

        val LBPDefault = DifficultyProfile(
            "LBPDefault",
            mapOf(
                5 to intArrayOf(5, 15, 0, 0),
            )
        )
    }
}
