package org.tfcc.bingo

import org.tfcc.bingo.message.HandlerException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

/**
 * Identifies the difficulty/spell-generation mode for a game.
 * Each mode determines how star arrays are built and which SpellConfig draw method is used.
 */
enum class DifficultyMode {
    /** Standard: E/N/L with 3-level profiles (1/2/3★ + high-level 4/5★) */
    NORMAL,

    /** OD/ODP: extended 7-level profiles with upgrade logic (1-7★) */
    OD,

    /** Custom: user-defined star counts with fixed high-level layout option */
    CUSTOM,

    /** BP: fixed 3-star at specific positions, rest 1/2★ */
    BP,

    /** BPOD/ODPBP: BP with OD-style upgrade logic */
    BP_OD,

    /** Link: fixed corner/center positions */
    LINK,
}

object SpellFactory {
    // ---- Board-level star placement ----

    /**
     * Generate high-level (4/5-star) placement positions using BoardSpec.
     * Ensures exactly one high-star per row and column, with center always high-star.
     */
    private fun highLevelPositions(board: BoardSpec, rand: Random): IntArray {
        val size = board.size
        val perm = (0 until size).toMutableList()
        perm.shuffle(rand)

        val centers = board.centerIndices()
        if (size % 2 == 1) {
            val mid = size / 2
            val centerCol = mid
            val currentColAtMid = perm[mid]
            val indexOfCenterCol = perm.indexOf(centerCol)
            perm[mid] = centerCol
            perm[indexOfCenterCol] = currentColAtMid
        } else {
            val mid1 = size / 2 - 1
            val mid2 = size / 2
            if (perm[mid1] != mid1 && perm[mid1] != mid2) {
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
     * Assign star values to board positions.
     * High-level positions get stars from [highStars]; remaining positions get stars from [lowStars].
     */
    private fun assignStarsToBoard(
        board: BoardSpec,
        highPositions: IntArray,
        highStars: IntArray,
        lowStars: IntArray,
    ): IntArray {
        val stars = IntArray(board.area)
        var lowIdx = 0
        var hiIdx = 0
        val highSet = highPositions.toSet()
        for (i in 0 until board.area) {
            stars[i] = if (i in highSet) highStars[hiIdx++] else lowStars[lowIdx++]
        }
        return stars
    }

    // ---- Star array builders by mode ----

    /**
     * Build a star array for NORMAL mode using DifficultyProfile.
     */
    @Throws(HandlerException::class)
    private fun buildNormalStarArray(difficulty: Difficulty, boardSize: Int, useFixedHighLevelLayout: Boolean): IntArray {
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
                else -> DifficultyProfile.scaleCounts(difficulty.value, board.area - highCount)
            }
        }

        if (!useFixedHighLevelLayout) {
            val allStars = IntArray(lvCount[0]) { 1 } +
                IntArray(lvCount[1]) { 2 } +
                IntArray(lvCount[2]) { 3 } +
                IntArray(highCount - 1) { 4 } + IntArray(1) { 5 }
            allStars.shuffle(rand)
            return allStars
        }

        val lowStars = IntArray(lvCount[0]) { 1 } + IntArray(lvCount[1]) { 2 } + IntArray(lvCount[2]) { 3 }
        lowStars.shuffle(rand)
        val highStars = IntArray(highCount - 1) { 4 } + IntArray(1) { 5 }
        highStars.shuffle(rand)

        return assignStarsToBoard(board, highLevelPositions(board, rand), highStars, lowStars)
    }

    /**
     * Build a star array for OD mode (7-level profiles with upgrade placeholders).
     * Low positions: 1-3★ base + 4-7★ from profile. High positions: 4/5★.
     * Star values 6 and 7 are placeholders that trigger upgrade logic in SpellConfig.getOD().
     */
    @Throws(HandlerException::class)
    private fun buildODStarArray(difficulty: Int, boardSize: Int): IntArray {
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

        val lowStars = IntArray(da[0]) { 1 } + IntArray(da[1]) { 2 } +
            IntArray(da[2]) { 3 } + IntArray(da[3]) { 4 } +
            IntArray(da[4]) { 5 } + IntArray(da[5]) { 6 } +
            IntArray(da[6]) { 7 }
        lowStars.shuffle(rand)
        val highStars = IntArray(highCount - 1) { 4 } + IntArray(1) { 5 }
        highStars.shuffle(rand)

        return assignStarsToBoard(board, highLevelPositions(board, rand), highStars, lowStars)
    }

    /**
     * Build a star array for CUSTOM mode.
     * s = Difficulty.settingCache (11-element array from client custom config).
     * s[0..4] = counts for stars 1-5, s[5] = OD flag, s[6] = fixed layout flag (unused here),
     * s[7..8] = counts for high-level 4/5★, s[9] = ex position mode, s[10] = ex position count.
     */
    @Throws(HandlerException::class)
    private fun buildCustomStarArray(s: IntArray, boardSize: Int, useFixedHighLevelLayout: Boolean): IntArray {
        val board = BoardSpec(boardSize)
        val boardArea = board.area
        val highCount = board.size

        if (s[0] + s[1] + s[2] + s[3] + s[4] != boardArea) {
            return buildNormalStarArray(Difficulty.L, boardSize, useFixedHighLevelLayout)
        }

        val rand = ThreadLocalRandom.current().asKotlinRandom()

        if (useFixedHighLevelLayout) {
            if (s[3] + s[4] < highCount) {
                throw HandlerException("自定义等级数据错误，4/5级卡数量不足")
            }
            val s7 = if (s[7] + s[8] != highCount) {
                highCount - 1
            } else {
                s[7]
            }
            val s8 = if (s[7] + s[8] != highCount) {
                1
            } else {
                s[8]
            }
            val highStars = IntArray(s7) { 4 } + IntArray(s8) { 5 }
            highStars.shuffle(rand)

            val diff = if (s[6] == 1) {
                intArrayOf(s[0], s[1], s[2], 0, 0, s[3] - s7, s[4] - s8)
            } else {
                intArrayOf(s[0], s[1], s[2], s[3] - s7, s[4] - s8, 0, 0)
            }
            val lowStars = IntArray(diff[0]) { 1 } + IntArray(diff[1]) { 2 } +
                IntArray(diff[2]) { 3 } + IntArray(diff[3]) { 4 } +
                IntArray(diff[4]) { 5 } + IntArray(diff[5]) { 6 } +
                IntArray(diff[6]) { 7 }
            lowStars.shuffle(rand)

            return assignStarsToBoard(board, highLevelPositions(board, rand), highStars, lowStars)
        } else {
            val diff = if (s[6] == 1) {
                intArrayOf(s[0], s[1], s[2], 0, 0, s[3], s[4])
            } else {
                intArrayOf(s[0], s[1], s[2], s[3], s[4], 0, 0)
            }
            val allStars = IntArray(diff[0]) { 1 } + IntArray(diff[1]) { 2 } +
                IntArray(diff[2]) { 3 } + IntArray(diff[3]) { 4 } +
                IntArray(diff[4]) { 5 } + IntArray(diff[5]) { 6 } +
                IntArray(diff[6]) { 7 }
            allStars.shuffle(rand)
            return allStars
        }
    }

    /**
     * Build a star array for BP mode: 3★ at fixed row-center positions, rest 1/2★.
     */
    @Throws(HandlerException::class)
    private fun buildBPStarArray(lv1Count: Int): IntArray {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val lv2Count = 20 - lv1Count
        val idx = intArrayOf(0, 1, 3, 4)
        val star12 = IntArray(lv1Count) { 1 } + IntArray(lv2Count) { 2 }
        idx.shuffle(rand)
        star12.shuffle(rand)
        var j = 0
        val idx3 = arrayOf(idx[0], 5 + idx[1], 12, 15 + idx[2], 20 + idx[3])
        return IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
    }

    /**
     * Build a star array for BPOD mode: 3★ at fixed positions, star=13 placeholders for upgrade, rest 1/2★.
     */
    @Throws(HandlerException::class)
    private fun buildBPODStarArray(difficulty: Int): IntArray {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
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
        return IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
    }

    /**
     * Build a star array for LINK mode: fixed positions for corners/center/edges.
     */
    @Throws(HandlerException::class)
    private fun buildLinkStarArray(difficulty: Difficulty): IntArray {
        val lvCount = difficulty.value
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star123 = IntArray(lvCount[0]) { 1 } + IntArray(lvCount[1]) { 2 } + IntArray(lvCount[2]) { 3 }
        idx.shuffle(rand)
        star123.shuffle(rand)
        var j = 0
        return IntArray(25) { i ->
            when (i) {
                0, 4 -> 1
                6, 8, 16, 18 -> 4
                12 -> 5
                else -> star123[j++]
            }
        }
    }

    // ---- EX position helpers ----

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

    // ---- Unified public API ----

    /**
     * Build a star array for the given mode. All star array construction flows through this method.
     */
    @Throws(HandlerException::class)
    fun buildStarArray(
        mode: DifficultyMode,
        difficulty: Int? = null,
        difficultyObj: Difficulty? = null,
        boardSize: Int = 5,
        useFixedHighLevelLayout: Boolean = true,
        bpLv1Count: Int = 5,
    ): IntArray = when (mode) {
        DifficultyMode.NORMAL -> buildNormalStarArray(
            difficultyObj ?: when (difficulty) {
                1 -> Difficulty.E
                2 -> Difficulty.N
                3 -> Difficulty.L
                else -> Difficulty.random()
            }, boardSize, useFixedHighLevelLayout
        )
        DifficultyMode.OD -> buildODStarArray(difficulty ?: 4, boardSize)
        DifficultyMode.CUSTOM -> buildCustomStarArray(Difficulty.settingCache, boardSize, useFixedHighLevelLayout)
        DifficultyMode.BP -> buildBPStarArray(bpLv1Count)
        DifficultyMode.BP_OD -> buildBPODStarArray(difficulty ?: 4)
        DifficultyMode.LINK -> buildLinkStarArray(
            difficultyObj ?: when (difficulty) {
                1 -> Difficulty.E
                2 -> Difficulty.N
                3 -> Difficulty.L
                else -> Difficulty.random()
            }
        )
    }

    /**
     * Draw spells for the given mode using a pre-built star array.
     */
    @Throws(HandlerException::class)
    fun drawSpellsWithStar(
        mode: DifficultyMode,
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5,
    ): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val board = BoardSpec(boardSize)
        val exPos = if (mode == DifficultyMode.CUSTOM) {
            ranksToExPosCustom(ranks, rand, Difficulty.settingCache, board)
        } else {
            ranksToExPos(ranks, rand, board)
        }

        return when (mode) {
            DifficultyMode.NORMAL,
            DifficultyMode.CUSTOM,
            DifficultyMode.LINK -> SpellConfig.get(
                SpellConfig.NORMAL_GAME, spellCardVersion, games, ranks, exPos, stars, rand
            )
            DifficultyMode.OD -> SpellConfig.getOD(
                SpellConfig.NORMAL_GAME, spellCardVersion, games, ranks, exPos, stars, rand
            )
            DifficultyMode.BP,
            DifficultyMode.BP_OD -> SpellConfig.getBPOD(
                SpellConfig.BP_GAME, spellCardVersion, games, ranks, exPos, stars, rand
            )
        }
    }

    /**
     * Build star array and draw spells in one call.
     */
    @Throws(HandlerException::class)
    fun drawSpells(
        mode: DifficultyMode,
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Int? = null,
        boardSize: Int = 5,
        useFixedHighLevelLayout: Boolean = true,
        difficultyObj: Difficulty? = null,
        bpLv1Count: Int = 5,
    ): Array<Spell> {
        val stars = buildStarArray(
            mode, difficulty, difficultyObj = difficultyObj,
            boardSize = boardSize, useFixedHighLevelLayout = useFixedHighLevelLayout,
            bpLv1Count = bpLv1Count,
        )
        return drawSpellsWithStar(mode, spellCardVersion, games, ranks, stars, boardSize)
    }

    // ---- Legacy public API (delegates to unified methods) ----
    // Kept for backward compatibility during transition; RoomType* will be updated next.

    @Throws(HandlerException::class)
    fun randSpellsBP(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, lv1Count: Int): Array<Spell> =
        drawSpellsWithStar(
            DifficultyMode.BP, spellCardVersion, games, ranks,
            buildStarArray(DifficultyMode.BP, bpLv1Count = lv1Count),
        )

    @Throws(HandlerException::class)
    fun randSpellsBPOD(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Int): Array<Spell> =
        drawSpells(
            DifficultyMode.BP_OD, spellCardVersion, games, ranks, difficulty
        )

    @Throws(HandlerException::class)
    fun randSpells(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Difficulty,
        boardSize: Int = 5,
        useFixedHighLevelLayout: Boolean = true
    ): Array<Spell> = drawSpellsWithStar(
        DifficultyMode.NORMAL, spellCardVersion, games, ranks,
        buildStarArray(
            DifficultyMode.NORMAL,
            difficultyObj = difficulty,
            boardSize = boardSize,
            useFixedHighLevelLayout = useFixedHighLevelLayout,
        ),
        boardSize,
    )

    @Throws(HandlerException::class)
    fun randSpellsStarArray(difficulty: Difficulty, boardSize: Int = 5, useFixedHighLevelLayout: Boolean = true): IntArray =
        buildStarArray(
            DifficultyMode.NORMAL,
            difficultyObj = difficulty,
            boardSize = boardSize,
            useFixedHighLevelLayout = useFixedHighLevelLayout,
        )

    @Throws(HandlerException::class)
    fun randSpellsWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> = drawSpellsWithStar(
        DifficultyMode.NORMAL, spellCardVersion, games, ranks, stars, boardSize
    )

    @Throws(HandlerException::class)
    fun randSpellsOD(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Int,
        boardSize: Int = 5
    ): Array<Spell> = drawSpells(
        DifficultyMode.OD, spellCardVersion, games, ranks, difficulty, boardSize
    )

    @Throws(HandlerException::class)
    fun randSpellsODStarArray(difficulty: Int, boardSize: Int = 5): IntArray =
        buildStarArray(DifficultyMode.OD, difficulty, boardSize = boardSize)

    @Throws(HandlerException::class)
    fun randSpellsODWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> = drawSpellsWithStar(
        DifficultyMode.OD, spellCardVersion, games, ranks, stars, boardSize
    )

    @Throws(HandlerException::class)
    fun randSpellsCustom(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Int,
        boardSize: Int = 5,
        useFixedHighLevelLayout: Boolean = true
    ): Array<Spell> = drawSpells(
        DifficultyMode.CUSTOM, spellCardVersion, games, ranks, difficulty,
        boardSize, useFixedHighLevelLayout
    )

    @Throws(HandlerException::class)
    fun randSpellsCustomStarArray(s: IntArray, boardSize: Int = 5, useFixedHighLevelLayout: Boolean = true): IntArray {
        return buildStarArray(
            DifficultyMode.CUSTOM,
            boardSize = boardSize,
            useFixedHighLevelLayout = useFixedHighLevelLayout,
        )
    }

    @Throws(HandlerException::class)
    fun randSpellsCustomWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray,
        boardSize: Int = 5
    ): Array<Spell> = drawSpellsWithStar(
        DifficultyMode.CUSTOM, spellCardVersion, games, ranks, stars, boardSize
    )

    @Throws(HandlerException::class)
    fun randSpellsLink(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        difficulty: Difficulty
    ): Array<Spell> = drawSpellsWithStar(
        DifficultyMode.LINK, spellCardVersion, games, ranks,
        buildStarArray(DifficultyMode.LINK, difficultyObj = difficulty),
    )
}
