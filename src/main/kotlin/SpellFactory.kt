package org.tfcc.bingo

import org.tfcc.bingo.message.HandlerException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

object SpellFactory {
    /**
     * Generate high-level (4/5-star) placement positions using BoardSpec.
     * Returns a list of board indices where high-star cards should be placed.
     * Ensures exactly one high-star per row and column, with center always high-star.
     */
    private fun highLevelPositions(board: BoardSpec, rand: Random): IntArray {
        val size = board.size
        // Generate a random permutation of columns for rows 0..size-1
        val perm = (0 until size).toMutableList()
        perm.shuffle(rand)

        // Force center positions to be high-star
        val centers = board.centerIndices()
        if (size % 2 == 1) {
            // Single center: force the center row to map to center column
            val mid = size / 2
            val centerCol = mid
            // Swap whatever was at mid to wherever centerCol was
            val currentColAtMid = perm[mid]
            val indexOfCenterCol = perm.indexOf(centerCol)
            perm[mid] = centerCol
            perm[indexOfCenterCol] = currentColAtMid
        } else {
            // 2x2 center: pick two positions in the center that share different rows and cols
            val mid1 = size / 2 - 1
            val mid2 = size / 2
            // Ensure rows mid1 and mid2 map to columns mid1 and mid2 (in some order)
            if (perm[mid1] != mid1 && perm[mid1] != mid2) {
                // Swap to make it work
                val target = if (perm[mid2] == mid1 || perm[mid2] == mid2) perm[mid2] else mid1
                val idx = perm.indexOf(target)
                val tmp = perm[mid1]
                perm[mid1] = target
                perm[idx] = tmp
            }
            if (perm[mid2] != mid1 && perm[mid2] != mid2) {
                val otherCenterCol = if (perm[mid1] == mid1) mid2 else mid1
                val idx = perm.indexOf(otherCenterCol)
                val tmp = perm[mid2]
                perm[mid2] = otherCenterCol
                perm[idx] = tmp
            }
        }

        return IntArray(size) { row -> board.index(row, perm[row]) }
    }

    /**
     * Build a star array with high-level placement for any board size.
     */
    private fun buildStarArrayWithHighLevel(
        board: BoardSpec,
        lowStarCounts: IntArray, // counts for star levels 1,2,3
        highStarCounts: IntArray, // counts for star levels 4,5
        rand: Random
    ): IntArray {
        val positions = highLevelPositions(board, rand)
        val highStars = IntArray(highStarCounts[0]) { 4 } + IntArray(highStarCounts[1]) { 5 }
        highStars.shuffle(rand)

        val lowStars = IntArray(lowStarCounts[0]) { 1 } +
            IntArray(lowStarCounts[1]) { 2 } +
            IntArray(lowStarCounts[2]) { 3 }
        lowStars.shuffle(rand)

        val stars = IntArray(board.area)
        var lowIdx = 0
        val highPositions = positions.toSet()
        var hiIdx = 0
        for (i in 0 until board.area) {
            if (i in highPositions) {
                stars[i] = highStars[hiIdx++]
            } else {
                stars[i] = lowStars[lowIdx++]
            }
        }
        return stars
    }

    /**
     * Build a star array with high-level placement for OD/extended star levels.
     */
    private fun buildStarArrayWithHighLevelExtended(
        board: BoardSpec,
        lowStarCounts: IntArray, // counts for 1,2,3,4,5,6,7
        highStarCounts: IntArray, // counts for 4,5 placed at high-level positions
        rand: Random
    ): IntArray {
        val positions = highLevelPositions(board, rand)
        val highStars = IntArray(highStarCounts[0]) { 4 } + IntArray(highStarCounts[1]) { 5 }
        highStars.shuffle(rand)

        val lowStars = IntArray(lowStarCounts[0]) { 1 } + IntArray(lowStarCounts[1]) { 2 } +
            IntArray(lowStarCounts[2]) { 3 } + IntArray(lowStarCounts[3]) { 4 } +
            IntArray(lowStarCounts[4]) { 5 } + IntArray(lowStarCounts[5]) { 6 } +
            IntArray(lowStarCounts[6]) { 7 }
        lowStars.shuffle(rand)

        val stars = IntArray(board.area)
        var lowIdx = 0
        val highPositions = positions.toSet()
        var hiIdx = 0
        for (i in 0 until board.area) {
            if (i in highPositions) {
                stars[i] = highStars[hiIdx++]
            } else {
                stars[i] = lowStars[lowIdx++]
            }
        }
        return stars
    }

    /**
     * 随符卡，用于BP赛
     */
    @Throws(HandlerException::class)
    fun randSpellsBP(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, lv1Count: Int): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(5) // BP is always 5x5
        val lv2Count = 20 - lv1Count
        val idx = intArrayOf(0, 1, 3, 4)
        val star12 = IntArray(lv1Count) { 1 } + IntArray(lv2Count) { 2 }
        idx.shuffle(rand)
        star12.shuffle(rand)
        var j = 0
        val idx3 = arrayOf(idx[0], 5 + idx[1], 12, 15 + idx[2], 20 + idx[3])
        val stars = IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
        return SpellConfig.get(
            SpellConfig.BP_GAME,
            spellCardVersion,
            games,
            ranks,
            ranksToExPos(ranks, rand, board),
            stars,
            rand
        )
    }

    /**
     * 随符卡，用于BP赛OD
     */
    @Throws(HandlerException::class)
    fun randSpellsBPOD(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Int): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(5) // BP is always 5x5
        val da = when (difficulty) {
            4 -> Difficulty.ODBP.value
            5 -> Difficulty.ODPBP.value
            else -> Difficulty.LBPDefault.value
        }
        val star12 = IntArray(da[0]) { 1 } + IntArray(da[1]) { 2 } + IntArray(da[3]) { 13 }
        val idx = intArrayOf(0, 1, 3, 4)
        idx.shuffle(rand)
        star12.shuffle(rand)
        var j = 0
        val idx3 = arrayOf(idx[0], 5 + idx[1], 12, 15 + idx[2], 20 + idx[3])
        val stars = IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
        return SpellConfig.getBPOD(
            SpellConfig.BP_GAME,
            spellCardVersion,
            games,
            ranks,
            ranksToExPos(ranks, rand, board),
            stars,
            rand
        )
    }

    /**
     * 随符卡，用于标准模式
     */
    @Throws(HandlerException::class)
    fun randSpells(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Difficulty,
        boardSize: Int = 5,
        useFixedHighLevelLayout: Boolean = true
    ): Array<Spell> {
        val starArray = randSpellsStarArray(difficulty, boardSize, useFixedHighLevelLayout)
        return randSpellsWithStar(spellCardVersion, games, ranks, starArray, boardSize)
    }

    @Throws(HandlerException::class)
    fun randSpellsStarArray(difficulty: Difficulty, boardSize: Int = 5, useFixedHighLevelLayout: Boolean = true): IntArray {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        val highCount = board.size
        val lvCount = if (boardSize == 5) {
            difficulty.value
        } else {
            when (difficulty) {
                Difficulty.E -> DifficultyProfile.E.counts(boardSize)
                Difficulty.N -> DifficultyProfile.N.counts(boardSize)
                Difficulty.L -> DifficultyProfile.L.counts(boardSize)
                else -> {
                    // Difficulty.random() generates 5x5 low-star counts summing to 20; scale to target
                    DifficultyProfile.scaleCounts(difficulty.value, board.area - highCount)
                }
            }
        }

        if (!useFixedHighLevelLayout) {
            // Uniform random shuffle: no positional constraints for high-star cards
            val allStars = IntArray(lvCount[0]) { 1 } +
                IntArray(lvCount[1]) { 2 } +
                IntArray(lvCount[2]) { 3 } +
                IntArray(highCount - 1) { 4 } + IntArray(1) { 5 }
            allStars.shuffle(rand)
            return allStars
        }

        return buildStarArrayWithHighLevel(
            board,
            lowStarCounts = lvCount,
            highStarCounts = intArrayOf(highCount - 1, 1), // 4s and 5s
            rand = rand
        )
    }

    @Throws(HandlerException::class)
    fun randSpellsWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        return SpellConfig.get(
            SpellConfig.NORMAL_GAME,
            spellCardVersion,
            games,
            ranks,
            ranksToExPos(ranks, rand, board),
            stars,
            rand
        )
    }

    /**
     * 随符卡，用于标准模式-OD
     */
    @Throws(HandlerException::class)
    fun randSpellsOD(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Int,
        boardSize: Int = 5
    ): Array<Spell> {
        val starArray = randSpellsODStarArray(difficulty, boardSize)
        return randSpellsODWithStar(spellCardVersion, games, ranks, starArray, boardSize)
    }

    @Throws(HandlerException::class)
    fun randSpellsODStarArray(difficulty: Int, boardSize: Int = 5): IntArray {
        val da = if (boardSize == 5) {
            when (difficulty) {
                4 -> Difficulty.OD.value
                5 -> Difficulty.ODP.value
                else -> Difficulty.LDefault.value
            }
        } else {
            val profile = when (difficulty) {
                4 -> DifficultyProfile.OD
                5 -> DifficultyProfile.ODP
                else -> DifficultyProfile.LDefault
            }
            profile.counts(boardSize)
        }
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        val highCount = board.size

        return buildStarArrayWithHighLevelExtended(
            board,
            lowStarCounts = da,
            highStarCounts = intArrayOf(highCount - 1, 1),
            rand = rand
        )
    }

    @Throws(HandlerException::class)
    fun randSpellsODWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        return SpellConfig.getOD(SpellConfig.NORMAL_GAME, spellCardVersion,
            games, ranks, ranksToExPos(ranks, rand, board), stars, rand)
    }

    @Throws(HandlerException::class)
    fun randSpellsCustomWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        return SpellConfig.getOD(SpellConfig.NORMAL_GAME, spellCardVersion,
            games, ranks, ranksToExPosCustom(ranks, rand, Difficulty.settingCache, board), stars, rand)
    }

    /**
     * 随符卡，用于标准模式-自定义
     */
    @Throws(HandlerException::class)
    fun randSpellsCustom(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Int,
        boardSize: Int = 5
    ): Array<Spell> {
        val starArray = randSpellsCustomStarArray(Difficulty.settingCache, boardSize)
        return randSpellsCustomWithStar(spellCardVersion, games, ranks, starArray, boardSize)
    }

    @Throws(HandlerException::class)
    fun randSpellsCustomStarArray(s: IntArray, boardSize: Int = 5): IntArray {
        val board = BoardSpec(boardSize)
        val boardArea = board.area
        val highCount = board.size

        if (s[0] + s[1] + s[2] + s[3] + s[4] != boardArea) {
            return randSpellsStarArray(Difficulty.L, boardSize)
        }

        val rand = ThreadLocalRandom.current().asKotlinRandom()

        val idx = highLevelPositions(board, rand)
        var diff = IntArray(7)
        var star45: IntArray? = null

        if (s[5] == 1) {
            if (s[3] + s[4] < highCount) {
                throw HandlerException("自定义等级数据错误，4/5级卡数量不足")
            }
            if (s[7] + s[8] != highCount) {
                s[7] = highCount - 1
                s[8] = 1
            }
            star45 = IntArray(s[7]) { 4 } + IntArray(s[8]) { 5 }
            star45.shuffle(rand)
        }
        if (s[6] == 1) {
            if (s[5] == 1) {
                diff = intArrayOf(s[0], s[1], s[2], 0, 0, s[3] - s[7], s[4] - s[8])
            } else {
                diff = intArrayOf(s[0], s[1], s[2], 0, 0, s[3], s[4])
            }
        } else {
            if (s[5] == 1) {
                diff = intArrayOf(s[0], s[1], s[2], s[3] - s[7], s[4] - s[8], 0, 0)
            } else {
                diff = intArrayOf(s[0], s[1], s[2], s[3], s[4], 0, 0)
            }
        }
        val starOther = IntArray(diff[0]) { 1 } + IntArray(diff[1]) { 2 } + IntArray(diff[2]) { 3 } +
            IntArray(diff[3]) { 4 } + IntArray(diff[4]) { 5 } +
            IntArray(diff[5]) { 6 } + IntArray(diff[6]) { 7 }
        starOther.shuffle(rand)

        var j = 0
        if (s[5] == 1 && star45 != null) {
            val highPositions = idx.toSet()
            var hiIdx = 0
            return IntArray(boardArea) { i ->
                if (i in highPositions) {
                    star45[hiIdx++]
                } else {
                    starOther[j++]
                }
            }
        } else {
            return IntArray(boardArea) { starOther[j++] }
        }
    }

    /**
     * 随符卡，用于link赛
     */
    @Throws(HandlerException::class)
    fun randSpellsLink(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Difficulty
    ): Array<Spell> {
        val board = BoardSpec(5) // Link is always 5x5
        val lvCount = difficulty.value
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star123 = IntArray(lvCount[0]) { 1 } + IntArray(lvCount[1]) { 2 } + IntArray(lvCount[2]) { 3 }
        idx.shuffle(rand)
        star123.shuffle(rand)
        var j = 0
        val stars = IntArray(25) { i ->
            when (i) {
                0, 4 -> 1
                6, 8, 16, 18 -> 4
                12 -> 5
                else -> star123[j++]
            }
        }
        return SpellConfig.get(
            SpellConfig.NORMAL_GAME,
            spellCardVersion,
            games,
            ranks,
            ranksToExPos(ranks, rand, board),
            stars,
            rand
        )
    }

    private fun ranksToExPos(ranks: Array<String>?, rand: Random, board: BoardSpec): IntArray {
        if (ranks != null && ranks.all { it == "L" })
            return intArrayOf()
        val idx = (0 until board.size).toMutableList()
        idx.shuffle(rand)
        return IntArray(board.size) { i -> board.index(i, idx[i]) }
    }

    private fun ranksToExPosCustom(ranks: Array<String>?, rand: Random, s: IntArray, board: BoardSpec): IntArray {
        if (ranks != null && ranks.all { it == "L" })
            return intArrayOf()
        if (s[9] == 1) {
            val idx = (0 until board.size).toMutableList()
            idx.shuffle(rand)
            return IntArray(board.size) { i -> board.index(i, idx[i]) }
        } else {
            if (s[10] > board.area || s[10] < 0) return intArrayOf()
            val index = IntArray(board.area) { i -> i }
            index.shuffle(rand)
            return index.sliceArray(0 until s[10])
        }
    }
}
