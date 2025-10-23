package org.tfcc.bingo

import org.tfcc.bingo.message.HandlerException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

// 史山。啥时候合并下逻辑。
object SpellFactory {
    /**
     * 随符卡，用于BP赛
     */
    @Throws(HandlerException::class)
    fun randSpellsBP(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, lv1Count: Int): Array<Spell> {
        val lv2Count = 20 - lv1Count
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star12 = IntArray(lv1Count) { 1 } + IntArray(lv2Count) { 2 }
        idx.shuffle(rand)
        star12.shuffle(rand)
        var j = 0
        // 每行、每列都只有一个lv3
        val idx3 = arrayOf(idx[0], 5 + idx[1], 12, 15 + idx[2], 20 + idx[3])
        val stars = IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
        return SpellConfig.get(SpellConfig.BP_GAME, spellCardVersion, games, ranks, ranksToExPos(ranks, rand), stars, rand)
    }

    /**
     * 随符卡，用于BP赛OD
     */
    @Throws(HandlerException::class)
    fun randSpellsBPOD(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Int): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val da = when (difficulty) {
            4 -> Difficulty.ODBP.value
            5 -> Difficulty.ODPBP.value
            else -> Difficulty.LBPDefault.value
        }
        val star12 = IntArray(da[0]) { 1 } + IntArray(da[1]) { 2 } + IntArray(da[3]) { 13 }
        idx.shuffle(rand)
        star12.shuffle(rand)
        var j = 0
        // 每行、每列都只有一个lv3
        val idx3 = arrayOf(idx[0], 5 + idx[1], 12, 15 + idx[2], 20 + idx[3])
        val stars = IntArray(25) { i -> if (i in idx3) 3 else star12[j++] }
        return SpellConfig.getBPOD(SpellConfig.BP_GAME, spellCardVersion, games, ranks, ranksToExPos(ranks, rand), stars, rand)
    }

    /**
     * 随符卡，用于标准模式
     * difficulty：123级卡数量 int[]
     * games：开启的游戏 string[]
     *
     */
    @Throws(HandlerException::class)
    fun randSpells(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Difficulty): Array<Spell> {
        val starArray = randSpellsStarArray(difficulty)
        return randSpellsWithStar(spellCardVersion, games, ranks, starArray)
    }

    @Throws(HandlerException::class)
    fun randSpellsStarArray(difficulty: Difficulty): IntArray {
        val lvCount = difficulty.value
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star123 = IntArray(lvCount[0]) { 1 } + IntArray(lvCount[1]) { 2 } + IntArray(lvCount[2]) { 3 }
        val star45 = arrayOf(4, 4, 4, 4, 5)
        idx.shuffle(rand)
        star45.shuffle(rand)
        star123.shuffle(rand)
        var j = 0
        val stars = IntArray(25) { i ->
            when (i) {
                // 每行、每列都只有一个大于等于lv4
                idx[0] -> star45[0]
                5 + idx[1] -> star45[1]
                12 -> star45[2]
                15 + idx[2] -> star45[3]
                20 + idx[3] -> star45[4]
                else -> star123[j++]
            }
        }
        return stars
    }

    @Throws(HandlerException::class)
    fun randSpellsWithStar(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, stars: IntArray): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        return SpellConfig.get(SpellConfig.NORMAL_GAME, spellCardVersion, games, ranks, ranksToExPos(ranks, rand), stars, rand)
    }

    /**
     * 随符卡，用于标准模式-OD
     * difficulty：123级卡数量 int[]
     * games：开启的游戏 string[]
     *
     */
    @Throws(HandlerException::class)
    fun randSpellsOD(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Int): Array<Spell> {
        val starArray = randSpellsODStarArray(difficulty)
        return randSpellsODWithStar(spellCardVersion, games, ranks, starArray)
    }

    @Throws(HandlerException::class)
    fun randSpellsODStarArray(difficulty: Int): IntArray {
        val da = when (difficulty) {
            4 -> Difficulty.OD.value
            5 -> Difficulty.ODP.value
            else -> Difficulty.LDefault.value
        }
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star12367 = IntArray(da[0]) { 1 } + IntArray(da[1]) { 2 } + IntArray(da[2]) { 3 } +
            IntArray(da[5]) { 6 } + IntArray(da[6]) { 7 }
        val star45 = arrayOf(4, 4, 4, 4, 5)
        idx.shuffle(rand)
        star45.shuffle(rand)
        star12367.shuffle(rand)
        var j = 0
        val stars = IntArray(25) { i ->
            when (i) {
                // 每行、每列都只有一个大于等于lv4
                idx[0] -> star45[0]
                5 + idx[1] -> star45[1]
                12 -> star45[2]
                15 + idx[2] -> star45[3]
                20 + idx[3] -> star45[4]
                else -> star12367[j++]
            }
        }
        return stars
    }

    @Throws(HandlerException::class)
    fun randSpellsODWithStar(
        spellCardVersion: Int,
        games: Array<String>,
        ranks: Array<String>?,
        stars: IntArray
    ): Array<Spell> {
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        return SpellConfig.getOD(SpellConfig.NORMAL_GAME, spellCardVersion,
            games, ranks, ranksToExPos(ranks, rand), stars, rand)
    }

    /**
     * 随符卡，用于标准模式-自定义
     * games：开启的游戏 string[]
     * 共用OD的生成逻辑（高阶替换）
     */
    @Throws(HandlerException::class)
    fun randSpellsCustom(spellCardVersion: Int, games: Array<String>, ranks: Array<String>?, difficulty: Int): Array<Spell> {
        val starArray = randSpellsCustomStarArray(Difficulty.settingCache)
        return randSpellsODWithStar(spellCardVersion, games, ranks, starArray)
    }

    // 自定义生成
    @Throws(HandlerException::class)
    fun randSpellsCustomStarArray(s: IntArray): IntArray {
        // 0~4分别表示1~5级卡数量
        // 5,6为标志位，为1分别表示强制4/5级的基础王后分布、是否启用4/5级卡不足替换机制
        // 7,8分别表示强制的4/5级卡数量
        if (s[0] + s[1] + s[2] + s[3] + s[4] != 25) {
            return randSpellsStarArray(Difficulty.L)
        }

        val rand = ThreadLocalRandom.current().asKotlinRandom()

        // 强制45的位置相关
        val idx = intArrayOf(0, 1, 3, 4)
        idx.shuffle(rand)
        // 难度数量分布（包括替换）
        var diff = IntArray(7)
        // 强制45的数量
        var star45: IntArray? = null
        // 如果强制45，生成数量
        if (s[5] == 1) {
            // 如果四五总数不足...
            if (s[3] + s[4] < 5) {
                throw HandlerException("自定义等级数据错误，4/5级卡数量不足")
            }
            // 如果四五强制数量不对，则设定为默认
            if (s[7] + s[8] != 5) {
                s[7] = 4
                s[8] = 1
            }
            star45 = IntArray(s[7]) { 4 } + IntArray(s[8]) { 5 }
            star45.shuffle(rand)
        }
        // 如果启用替换，则4/5
        if (s[6] == 1) {
            // 如果强制45，强制的部分走额外逻辑，所以这里只替换额外部分
            if (s[5] == 1) {
                diff = intArrayOf(s[0], s[1], s[2], 0, 0, s[3] - s[7], s[4] - s[8])
            } else {
                // 改为所有的都可替换
                diff = intArrayOf(s[0], s[1], s[2], 0, 0, s[3], s[4])
            }
        } else {
            if (s[5] == 1) {
                // 扣除强制45的数量
                diff = intArrayOf(s[0], s[1], s[2], s[3] - s[7], s[4] - s[8], 0, 0)
            } else {
                // 直接传递原始数量
                diff = intArrayOf(s[0], s[1], s[2], s[3], s[4], 0, 0)
            }
        }
        // 构筑格子难度数组。特殊生成的45不在此列。
        val starOther = IntArray(diff[0]) { 1 } + IntArray(diff[1]) { 2 } + IntArray(diff[2]) { 3 } +
            IntArray(diff[3]) { 4 } + IntArray(diff[4]) { 5 } +
            IntArray(diff[5]) { 6 } + IntArray(diff[6]) { 7 }
        starOther.shuffle(rand)

        var j = 0
        // 包含强制45的生成
        if (s[5] == 1 && star45 != null) {
            return IntArray(25) { i ->
                when (i) {
                    // 每行、每列都只有一个大于等于lv4
                    idx[0] -> star45[0]
                    5 + idx[1] -> star45[1]
                    12 -> star45[2]
                    15 + idx[2] -> star45[3]
                    20 + idx[3] -> star45[4]
                    else -> starOther[j++]
                }
            }
        } else {
            // 直接生成即可
            return IntArray(25) { i ->
                starOther[j++]
            }
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
        val lvCount = difficulty.value
        val rand = ThreadLocalRandom.current().asKotlinRandom()
        val idx = intArrayOf(0, 1, 3, 4)
        val star123 = IntArray(lvCount[0]) { 1 } + IntArray(lvCount[1]) { 2 } + IntArray(lvCount[2]) { 3 }
        idx.shuffle(rand)
        star123.shuffle(rand)
        var j = 0
        val stars = IntArray(25) { i ->
            when (i) {
                0, 4 -> 1 // 左上lv1，右上lv1
                6, 8, 16, 18 -> 4 // 第二、四排的第二、四列固定4级
                12 -> 5 // 中间5级
                else -> star123[j++]
            }
        }
        return SpellConfig.get(SpellConfig.NORMAL_GAME, spellCardVersion, games, ranks, ranksToExPos(ranks, rand), stars, rand)
    }

    private fun ranksToExPos(ranks: Array<String>?, rand: Random): IntArray {
        if (ranks != null && ranks.all { it == "L" })
            return intArrayOf()
        val idx = intArrayOf(0, 1, 2, 3, 4)
        idx.shuffle(rand)
        for ((i, j) in idx.withIndex())
            idx[i] = i * 5 + j
        return idx
    }
}
