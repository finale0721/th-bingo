package org.tfcc.bingo

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

    /** 姣忎釜鏍煎瓙鐨勭姸鎬?*/
    var spellStatus: Array<SpellStatus>? = null

    /**
     * 鍙屾柟閫夋墜瀹㈡埛绔洰鍓嶆樉绀虹殑绗﹀崱鐘舵€侊紙涓氬姟閫昏緫涓嶈涔辨敼杩欎釜瀛楁锛?
     */
    var spellStatusInPlayerClient: Array<IntArray>? = null

    /** 姣斿垎 */
    val score = intArrayOf(0, 0)

    /** 杩炵画澶氬眬灏遍渶瑕侀攣涓?*/
    var locked = false
    val changeCardCount = intArrayOf(0, 0)

    /** 涓婃鏀跺崱鏃堕棿鎴?*/
    val lastGetTime = longArrayOf(0, 0)

    /** 鍚勯€夋墜鐨勫疄闄匔D鏃堕棿锛堝熀纭€CD + 淇鍊硷級锛屽崟浣嶏細姣 */
    val actualCdTime = longArrayOf(0, 0)

    /** 绱鏆傚仠鏃堕暱锛屾绉?*/
    var totalPauseMs: Long = 0

    /** 寮€濮嬫殏鍋滄椂鍒伙紝姣锛?琛ㄧず娌℃殏鍋?*/
    var pauseBeginMs: Long = 0

    /** 涓婁竴娆＄粨鏉熸殏鍋滅殑鏃跺埢锛屾绉掞紝0琛ㄧず浠庢湭鏆傚仠杩?*/
    var pauseEndMs: Long = 0

    /** 涓婁竴鍦烘槸璋佽耽锛?鎴? */
    var lastWinner: Int = 0
    var bpData: BpData? = null
    var linkData: LinkData? = null
    var normalData: NormalData? = null
    var customCardPool: Array<Spell> = emptyArray()

    /** 绾鎴风鐢紝鏈嶅姟鍣ㄥ彧璁板綍 */
    var phase: Int = 0

    /** 瑙備紬 */
    val watchers = ArrayList<Player>()

    var banPick: BanPick? = null
    var debugSpells: IntArray? = null

    /** 鎿嶄綔璁板綍鍣?*/
    var gameLogger: GameLogger? = null

    /** 鏈€鍚庝竴娆℃搷浣滅殑鏃堕棿鎴筹紝姣锛屼笟鍔￠€昏緫涓鍕夸慨鏀规瀛楁 */
    var lastOperateMs: Long = 0
    val boardSpec: BoardSpec
        get() = BoardSpec(roomConfig.boardSize)

    val boardArea: Int
        get() = boardSpec.area

    val type
        get() = when (roomConfig.type) {
            1 -> RoomTypeNormal
            2 -> RoomTypeBP
            3 -> RoomTypeLink
            else -> throw HandlerException("涓嶆敮鎸佺殑娓告垙绫诲瀷")
        }

    fun isHost(player: Player) = if (host != null) host === player
    else player in players

    var refreshManager1: RefreshSpellManager? = null
    var refreshManager2: RefreshSpellManager? = null

    var aiAgent: AIAgent? = null
    var linkAiAgent: LinkAIAgent? = null

    var isCustomGame = false
}
