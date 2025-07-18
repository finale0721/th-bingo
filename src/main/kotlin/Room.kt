package org.tfcc.bingo

import RefreshSpellManager
import org.tfcc.bingo.message.*
import java.util.*
import kotlin.collections.ArrayList

class Room(
    val roomId: String,
    val host: Player?,
    var roomConfig: RoomConfig,
) {
    val players = arrayOf<Player?>(null, null)
    var started = false
    var spells: Array<Spell>? = null
    var spells2: Array<Spell>? = null
    var startMs: Long = 0

    /** 每个格子的状态 */
    var spellStatus: Array<SpellStatus>? = null

    /**
     * 双方选手客户端目前显示的符卡状态（业务逻辑不要乱改这个字段）
     */
    var spellStatusInPlayerClient: Array<IntArray>? = null

    /** 比分 */
    val score = intArrayOf(0, 0)

    /** 连续多局就需要锁上 */
    var locked = false
    val changeCardCount = intArrayOf(0, 0)

    /** 上次收卡时间戳 */
    val lastGetTime = longArrayOf(0, 0)

    /** 累计暂停时长，毫秒 */
    var totalPauseMs: Long = 0

    /** 开始暂停时刻，毫秒，0表示没暂停 */
    var pauseBeginMs: Long = 0

    /** 上一次结束暂停的时刻，毫秒，0表示从未暂停过 */
    var pauseEndMs: Long = 0

    /** 上一场是谁赢，1或2 */
    var lastWinner: Int = 0
    var bpData: BpData? = null
    var linkData: LinkData? = null
    var normalData: NormalData? = null

    /** 纯客户端用，服务器只记录 */
    var phase: Int = 0

    /** 观众 */
    val watchers = ArrayList<Player>()

    var banPick: BanPick? = null
    var debugSpells: IntArray? = null

    /** 最后一次操作的时间戳，毫秒，业务逻辑中请勿修改此字段 */
    var lastOperateMs: Long = 0
    val type
        get() = when (roomConfig.type) {
            1 -> RoomTypeNormal
            2 -> RoomTypeBP
            3 -> RoomTypeLink
            else -> throw HandlerException("不支持的游戏类型")
        }

    fun isHost(player: Player) = if (host != null) host === player
    else player in players

    var refreshManager1: RefreshSpellManager? = null
    var refreshManager2: RefreshSpellManager? = null
}
