package org.tfcc.bingo

import kotlin.random.Random

class Difficulty(val value: IntArray) {
    companion object {
        val E = Difficulty(intArrayOf(12, 6, 2))
        val N = Difficulty(intArrayOf(6, 8, 6))
        val L = Difficulty(intArrayOf(2, 6, 12))
        val OD = Difficulty(intArrayOf(1, 4, 8, 0, 0, 5, 2))
        val ODP = Difficulty(intArrayOf(0, 0, 6, 0, 0, 9, 5))
        val ODBP = Difficulty(intArrayOf(3, 12, 0, 5))
        val ODPBP = Difficulty(intArrayOf(0, 10, 0, 10))
        val LDefault = Difficulty(intArrayOf(2, 6, 12, 0, 0, 0, 0))
        val LBPDefault = Difficulty(intArrayOf(5, 15, 0, 0))

        fun random(): Difficulty {
            val weights = intArrayOf(11, 10, 9)
            val n = Random.nextInt(weights.sum()).let {
                if (it < weights[0]) 6
                else if (it < weights[0] + weights[1]) 7
                else 8
            }
            val e = (2..(18 - n)).random()
            return Difficulty(intArrayOf(e, n, 20 - e - n))
        }

        var settingCache: IntArray = IntArray(9)

        fun constructCustom(setting: Array<Int>) {
            settingCache = setting.map { it.toInt() }.toIntArray()
        }
    }
}
