package org.tfcc.bingo.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RoomConfig(
    /** 鎴块棿鍚?*/
    val rid: String,
    /** 1-鏍囧噯璧涳紝2-BP璧涳紝3-link璧?*/
    val type: Int,
    /** 娓告垙鎬绘椂闂达紙涓嶅惈鍊掕鏃讹級锛屽崟浣嶏細鍒?*/
    @SerialName("game_time")
    val gameTime: Int,
    /** 鍊掕鏃讹紝鍗曚綅锛氱 */
    val countdown: Int,
    /** 鍚湁鍝簺浣滃搧 */
    var games: Array<String>,
    /** 鍚湁鍝簺娓告垙闅惧害锛屼篃灏辨槸L鍗″拰EX鍗?*/
    var ranks: Array<String>,
    /** 闇€瑕佽儨鍒╃殑灞€鏁帮紝渚嬪2琛ㄧずbo3锛岀┖琛ㄧず1 */
    @SerialName("need_win")
    val needWin: Int?,
    /** 闅惧害锛堝奖鍝嶄笉鍚屾槦绾х殑鍗＄殑鍒嗗竷锛夛紝1瀵瑰簲E锛?瀵瑰簲N锛?瀵瑰簲L锛?瀵瑰簲OD锛?瀵瑰簲ODP锛?瀵瑰簲鑷畾涔?*/
    val difficulty: Int?,
    /** 閫夊崱cd锛屾敹鍗″悗瑕佸灏戠鎵嶈兘閫変笅涓€寮犲崱锛岀┖琛ㄧず0 */
    @SerialName("cd_time")
    val cdTime: Int?,
    /** 宸︿晶閫夋墜锛圓锛夌殑CD淇鍊硷紝鍗曚綅锛氱 */
    @SerialName("cd_modifier_a")
    val cdModifierA: Int?,
    /** 鍙充晶閫夋墜锛圔锛夌殑CD淇鍊硷紝鍗曚綅锛氱 */
    @SerialName("cd_modifier_b")
    val cdModifierB: Int?,
    /** 鏄惁涓哄洟浣撹禌 */
    @SerialName("reserved_type")
    val reservedType: Int? = null,
    /** 鐩茬洅璁剧疆锛堝鏍囧噯/BP鐢熸晥锛?*/
    @SerialName("blind_setting")
    var blindSetting: Int,
    /** 棰樺簱鐗堟湰 */
    @SerialName("spell_version")
    var spellCardVersion: Int,
    /** 鐗规畩妯″紡锛氬弻閲嶇洏闈?*/
    @SerialName("dual_board")
    var dualBoard: Int,
    /** 鍙岄噸鐩橀潰璁惧畾 */
    @SerialName("portal_count")
    var portalCount: Int,
    /** 鐩茬洅鎻ず绛夌骇 */
    @SerialName("blind_reveal_level")
    var blindRevealLevel: Int,
    /** 鍙岄噸鐩橀潰宸紓搴?*/
    @SerialName("diff_level")
    var diffLevel: Int,
    /** 鏄惁鍚敤AI闄粌 */
    @SerialName("use_ai")
    val useAI: Boolean,
    /** AI绛栫暐闅惧害 */
    @SerialName("ai_strategy_level")
    val aiStrategyLevel: Int, // 1:鍒濈骇, 2:涓骇, 3:楂樼骇
    /** AI鍐崇瓥椋庢牸 */
    @SerialName("ai_style")
    val aiStyle: Int, // 0:榛樿, 1:杩涙敾鍨? 2:闃插畧鍨?
    /** AI搴曞姏 */
    @SerialName("ai_base_power")
    val aiBasePower: Float,
    /** AI鐔熺粌搴?*/
    @SerialName("ai_experience")
    val aiExperience: Float,
    /** AI閫夊崱娓╁害 */
    @SerialName("ai_temperature")
    val aiTemperature: Float,
    /** 娓告垙鏉冮噸 */
    @SerialName("game_weight")
    val gameWeight: HashMap<String, Int>,
    /** AI浣滃搧淇 */
    @SerialName("ai_preference")
    val aiPreference: HashMap<String, Int>,
    /** 鑷畾涔夌瓑绾ф暟閲?*/
    @SerialName("custom_level_count")
    val customLevelCount: Array<Int>,
    /** 妫嬬洏杈归暱锛?x4/5x5/6x6 */
    @SerialName("board_size")
    val boardSize: Int = 5,
    /** 6x6棰濆杩炵嚎鏁伴噺 */
    @SerialName("extra_line_count")
    val extraLineCount: Int = 0,
    @SerialName("hidden_select_threshold_a")
    val hiddenSelectThresholdA: Int? = null,
    @SerialName("hidden_select_threshold_b")
    val hiddenSelectThresholdB: Int? = null,
    /** Link璧涚瓑绾х郴鏁癤 */
    @SerialName("link_level_coefficient")
    val linkLevelCoefficient: Double = 2.0,
    /** Link璧涙椂闀跨郴鏁癥锛屼箻浠astest瀛楁 */
    @SerialName("link_fastest_coefficient")
    val linkFastestCoefficient: Double = 1.0,
    /** Link璧涜繛鎺ヨ鍒欙紝4=鍥涘悜锛?=鍏悜 */
    @SerialName("link_connectivity")
    val linkConnectivity: Int = 8,
    @SerialName("link_disabled_idx")
    val linkDisabledIdx: Array<Int> = emptyArray(),
    @SerialName("link_start_a")
    val linkStartA: Int = 0,
    @SerialName("link_end_a")
    val linkEndA: Int = boardSize * boardSize - 1,
    @SerialName("link_start_b")
    val linkStartB: Int = boardSize - 1,
    @SerialName("link_end_b")
    val linkEndB: Int = boardSize * (boardSize - 1),
    @SerialName("custom_card_pool_enabled")
    val customCardPoolEnabled: Boolean = false,
) {
    @Throws(HandlerException::class)
    fun validate() {
        rid.isNotEmpty() || throw HandlerException("room id is empty")
        type in 1..3 || throw HandlerException("unsupported game type")
        gameTime in 1..1440 || throw HandlerException("invalid game time")
        countdown in 0..86400 || throw HandlerException("invalid countdown")
        games.size < 100 || throw HandlerException("too many games")
        ranks.size <= 6 || throw HandlerException("too many ranks")
        needWin == null || needWin in 1..99 || throw HandlerException("invalid need_win")
        difficulty == null || difficulty in 1..6 || throw HandlerException("invalid difficulty")
        cdTime == null || cdTime in 0..1440 || throw HandlerException("invalid cd_time")
        cdModifierA == null || cdModifierA in -1440..2880 || throw HandlerException("invalid cd_modifier_a")
        cdModifierB == null || cdModifierB in -1440..2880 || throw HandlerException("invalid cd_modifier_b")
        blindSetting in 1..3 || throw HandlerException("invalid blind setting")
        spellCardVersion in 1..10 || throw HandlerException("invalid spell version")
        dualBoard in 0..1 || throw HandlerException("invalid dual board setting")
        blindRevealLevel in 0..4 || throw HandlerException("invalid blind reveal level")
        diffLevel in -1..5 || throw HandlerException("invalid diff level")
        aiStrategyLevel in 1..3 || throw HandlerException("invalid ai strategy level")
        aiStyle in 0..2 || throw HandlerException("invalid ai style")
        aiBasePower < 10.1f || throw HandlerException("invalid ai base power")
        aiExperience < 10.1f || throw HandlerException("invalid ai experience")
        gameWeight.values.all { it in -2..2 } || throw HandlerException("invalid game weight")
        aiPreference.values.all { it in -2..2 } || throw HandlerException("invalid ai preference")
        customLevelCount.size == 11 || throw HandlerException("invalid custom level count")
        customLevelCount.all { it in 0..(boardSize * boardSize) } || throw HandlerException("invalid custom level count")
        boardSize in 4..6 || throw HandlerException("invalid board size")
        (type != 2 || boardSize == 5) || throw HandlerException("BP only supports 5x5 board")
        (type != 3 || blindSetting == 1) || throw HandlerException("Link does not support blind mode")
        (type != 3 || dualBoard == 0) || throw HandlerException("Link does not support dual board")
        (type != 3 || difficulty in 1..3) || throw HandlerException("Link only supports E/N/L difficulty")
        portalCount in 1..(boardSize * boardSize) || throw HandlerException("invalid portal count")
        (extraLineCount == 0 || (boardSize == 6 && type == 1)) ||
            throw HandlerException("extra lines only support 6x6 standard")
        extraLineCount in 0..4 || throw HandlerException("invalid extra line count")
        hiddenSelectThresholdA == null || hiddenSelectThresholdA in 1..(boardSize * boardSize) ||
            throw HandlerException("invalid hidden threshold a")
        hiddenSelectThresholdB == null || hiddenSelectThresholdB in 1..(boardSize * boardSize) ||
            throw HandlerException("invalid hidden threshold b")
        linkLevelCoefficient in 0.0..100.0 || throw HandlerException("invalid link level coefficient")
        linkFastestCoefficient in 0.0..10.0 || throw HandlerException("invalid link fastest coefficient")
        linkConnectivity == 4 || linkConnectivity == 8 || throw HandlerException("invalid link connectivity")
        val boardArea = boardSize * boardSize
        val linkEndpoints = listOf(linkStartA, linkEndA, linkStartB, linkEndB)
        linkEndpoints.all { it in 0 until boardArea } || throw HandlerException("link endpoint out of board")
        linkStartA != linkEndA || throw HandlerException("link A start and end cannot be same")
        linkStartB != linkEndB || throw HandlerException("link B start and end cannot be same")
        linkDisabledIdx.all { it in 0 until boardArea } || throw HandlerException("link disabled index out of board")
        linkDisabledIdx.distinct().size == linkDisabledIdx.size || throw HandlerException("duplicate link disabled index")
        linkDisabledIdx.none { it in linkEndpoints } || throw HandlerException("link disabled index covers endpoint")
        if (type == 3) {
            linkReachable(linkStartA, linkEndA, linkDisabledIdx.toSet()) || throw HandlerException("link A route unreachable")
            linkReachable(linkStartB, linkEndB, linkDisabledIdx.toSet()) || throw HandlerException("link B route unreachable")
        }
    }

    private fun linkReachable(start: Int, end: Int, disabled: Set<Int>): Boolean {
        val queue: java.util.Queue<Int> = java.util.LinkedList()
        val seen = mutableSetOf(start)
        queue.add(start)
        while (queue.isNotEmpty()) {
            val cur = queue.remove()
            if (cur == end) return true
            for (next in linkNeighborIndices(cur)) {
                if (next !in disabled && seen.add(next)) queue.add(next)
            }
        }
        return false
    }

    private fun linkNeighborIndices(index: Int): List<Int> {
        val row = index / boardSize
        val col = index % boardSize
        val offsets = if (linkConnectivity == 4) {
            listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        } else {
            listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1, -1 to -1, -1 to 1, 1 to -1, 1 to 1)
        }
        return offsets.mapNotNull { (dr, dc) ->
            val nr = row + dr
            val nc = col + dc
            if (nr in 0 until boardSize && nc in 0 until boardSize) nr * boardSize + nc else null
        }
    }

    operator fun plus(config: RoomConfigNullable): RoomConfig {
        return RoomConfig(
            rid = config.rid,
            type = config.type ?: type,
            gameTime = config.gameTime ?: gameTime,
            countdown = config.countdown ?: countdown,
            games = config.games ?: games,
            ranks = config.ranks ?: ranks,
            needWin = config.needWin ?: needWin,
            difficulty = config.difficulty ?: difficulty,
            cdTime = config.cdTime ?: cdTime,
            reservedType = config.reservedType ?: reservedType,
            blindSetting = config.blindSetting ?: blindSetting,
            spellCardVersion = config.spellCardVersion ?: spellCardVersion,
            dualBoard = config.dualBoard ?: dualBoard,
            portalCount = config.portalCount ?: portalCount,
            blindRevealLevel = config.blindRevealLevel ?: blindRevealLevel,
            diffLevel = config.diffLevel ?: diffLevel,
            useAI = config.useAI ?: useAI,
            aiStrategyLevel = config.aiStrategyLevel ?: aiStrategyLevel,
            aiStyle = config.aiStyle ?: aiStyle,
            aiBasePower = config.aiBasePower ?: aiBasePower,
            aiExperience = config.aiExperience ?: aiExperience,
            gameWeight = config.gameWeight ?: gameWeight,
            aiPreference = config.aiPreference ?: aiPreference,
            customLevelCount = config.customLevelCount ?: customLevelCount,
            aiTemperature = config.aiTemperature ?: aiTemperature,
            cdModifierA = config.cdModifierA ?: cdModifierA,
            cdModifierB = config.cdModifierB ?: cdModifierB,
            boardSize = config.boardSize ?: boardSize,
            extraLineCount = config.extraLineCount ?: extraLineCount,
            hiddenSelectThresholdA = config.hiddenSelectThresholdA ?: hiddenSelectThresholdA,
            hiddenSelectThresholdB = config.hiddenSelectThresholdB ?: hiddenSelectThresholdB,
            linkLevelCoefficient = config.linkLevelCoefficient ?: linkLevelCoefficient,
            linkFastestCoefficient = config.linkFastestCoefficient ?: linkFastestCoefficient,
            linkConnectivity = config.linkConnectivity ?: linkConnectivity,
            linkDisabledIdx = config.linkDisabledIdx ?: linkDisabledIdx,
            linkStartA = config.linkStartA ?: linkStartA,
            linkEndA = config.linkEndA ?: linkEndA,
            linkStartB = config.linkStartB ?: linkStartB,
            linkEndB = config.linkEndB ?: linkEndB,
            customCardPoolEnabled = config.customCardPoolEnabled ?: customCardPoolEnabled
        )
    }
}

@Serializable
class RoomConfigNullable(
    /** 鎴块棿鍚?*/
    val rid: String,
    /** 1-鏍囧噯璧涳紝2-BP璧涳紝3-link璧?*/
    val type: Int? = null,
    /** 娓告垙鎬绘椂闂达紙涓嶅惈鍊掕鏃讹級锛屽崟浣嶏細鍒?*/
    @SerialName("game_time")
    val gameTime: Int? = null,
    /** 鍊掕鏃讹紝鍗曚綅锛氱 */
    val countdown: Int? = null,
    /** 鍚湁鍝簺浣滃搧 */
    val games: Array<String>? = null,
    /** 鍚湁鍝簺娓告垙闅惧害锛屼篃灏辨槸L鍗″拰EX鍗?*/
    val ranks: Array<String>? = null,
    /** 闇€瑕佽儨鍒╃殑灞€鏁帮紝渚嬪2琛ㄧずbo3锛岀┖琛ㄧず1 */
    @SerialName("need_win")
    val needWin: Int? = null,
    /** 闅惧害锛堝奖鍝嶄笉鍚屾槦绾х殑鍗＄殑鍒嗗竷锛夛紝1瀵瑰簲E锛?瀵瑰簲N锛?瀵瑰簲L锛?瀵瑰簲OD锛?瀵瑰簲ODP锛?瀵瑰簲鑷畾涔?*/
    val difficulty: Int? = null,
    /** 閫夊崱cd锛屾敹鍗″悗瑕佸灏戠鎵嶈兘閫変笅涓€寮犲崱锛岀┖琛ㄧず0 */
    @SerialName("cd_time")
    val cdTime: Int? = null,
    /** 宸︿晶閫夋墜锛圓锛夌殑CD淇鍊硷紝鍗曚綅锛氱 */
    @SerialName("cd_modifier_a")
    val cdModifierA: Int? = null,
    /** 鍙充晶閫夋墜锛圔锛夌殑CD淇鍊硷紝鍗曚綅锛氱 */
    @SerialName("cd_modifier_b")
    val cdModifierB: Int? = null,
    /** 鏄惁涓哄洟浣撹禌 */
    @SerialName("reserved_type")
    val reservedType: Int? = null,
    /** 鐩茬洅璁剧疆锛堟殏鏃跺彧瀵规爣鍑嗙敓鏁堬級 */
    @SerialName("blind_setting")
    var blindSetting: Int? = null,
    /** 棰樺簱鐗堟湰 */
    @SerialName("spell_version")
    var spellCardVersion: Int? = null,
    /** 鐗规畩妯″紡锛氬弻閲嶇洏闈?*/
    @SerialName("dual_board")
    var dualBoard: Int? = null,
    /** 鍙岄噸鐩橀潰璁惧畾 */
    @SerialName("portal_count")
    var portalCount: Int? = null,
    /** 鐩茬洅鎻ず绛夌骇 */
    @SerialName("blind_reveal_level")
    var blindRevealLevel: Int? = null,
    /** 鍙岄噸鐩橀潰宸紓搴?*/
    @SerialName("diff_level")
    var diffLevel: Int? = null,
    /** 鏄惁鍚敤AI闄粌 */
    @SerialName("use_ai")
    val useAI: Boolean? = null, // 0/1
    /** AI绛栫暐闅惧害 */
    @SerialName("ai_strategy_level")
    val aiStrategyLevel: Int? = null, // 1:鍒濈骇, 2:涓骇, 3:楂樼骇
    /** AI鍐崇瓥椋庢牸 */
    @SerialName("ai_style")
    val aiStyle: Int? = null, // 0:榛樿, 1:杩涙敾鍨? 2:闃插畧鍨?
    /** AI搴曞姏 */
    @SerialName("ai_base_power")
    val aiBasePower: Float? = null,
    /** AI鐔熺粌搴?*/
    @SerialName("ai_experience")
    val aiExperience: Float? = null,
    /** AI閫夊崱娓╁害 */
    @SerialName("ai_temperature")
    val aiTemperature: Float? = null,
    /** 娓告垙鏉冮噸 */
    @SerialName("game_weight")
    val gameWeight: HashMap<String, Int>? = null,
    /** AI浣滃搧淇 */
    @SerialName("ai_preference")
    val aiPreference: HashMap<String, Int>? = null,
    /** 鑷畾涔夌瓑绾ф暟閲?*/
    @SerialName("custom_level_count")
    val customLevelCount: Array<Int>? = null,
    /** 妫嬬洏杈归暱锛?x4/5x5/6x6 */
    @SerialName("board_size")
    val boardSize: Int? = null,
    /** 6x6棰濆杩炵嚎鏁伴噺 */
    @SerialName("extra_line_count")
    val extraLineCount: Int? = null,
    @SerialName("hidden_select_threshold_a")
    val hiddenSelectThresholdA: Int? = null,
    @SerialName("hidden_select_threshold_b")
    val hiddenSelectThresholdB: Int? = null,
    @SerialName("link_level_coefficient")
    val linkLevelCoefficient: Double? = null,
    @SerialName("link_fastest_coefficient")
    val linkFastestCoefficient: Double? = null,
    @SerialName("link_connectivity")
    val linkConnectivity: Int? = null,
    @SerialName("link_disabled_idx")
    val linkDisabledIdx: Array<Int>? = null,
    @SerialName("link_start_a")
    val linkStartA: Int? = null,
    @SerialName("link_end_a")
    val linkEndA: Int? = null,
    @SerialName("link_start_b")
    val linkStartB: Int? = null,
    @SerialName("link_end_b")
    val linkEndB: Int? = null,
    @SerialName("custom_card_pool_enabled")
    val customCardPoolEnabled: Boolean? = null,
) {
    @Throws(HandlerException::class)
    fun validate() {
        rid.isNotEmpty() || throw HandlerException("room id is empty")
        type == null || type in 1..3 || throw HandlerException("unsupported game type")
        gameTime == null || gameTime in 1..1440 || throw HandlerException("invalid game time")
        countdown == null || countdown in 0..86400 || throw HandlerException("invalid countdown")
        games == null || games.size < 100 || throw HandlerException("too many games")
        ranks == null || ranks.size <= 6 || throw HandlerException("too many ranks")
        needWin == null || needWin in 1..99 || throw HandlerException("invalid need_win")
        difficulty == null || difficulty in 1..6 || throw HandlerException("invalid difficulty")
        cdTime == null || cdTime in 0..1440 || throw HandlerException("invalid cd_time")
        cdModifierA == null || cdModifierA in -1440..2880 || throw HandlerException("invalid cd_modifier_a")
        cdModifierB == null || cdModifierB in -1440..2880 || throw HandlerException("invalid cd_modifier_b")
        blindSetting == null || blindSetting in 1..3 || throw HandlerException("invalid blind setting")
        spellCardVersion == null || spellCardVersion in 1..10 || throw HandlerException("invalid spell version")
        dualBoard == null || dualBoard in 0..1 || throw HandlerException("invalid dual board setting")
        blindRevealLevel == null || blindRevealLevel in 0..4 || throw HandlerException("invalid blind reveal level")
        diffLevel == null || diffLevel in -1..5 || throw HandlerException("invalid diff level")
        aiStrategyLevel == null || aiStrategyLevel in 1..3 || throw HandlerException("invalid ai strategy level")
        aiStyle == null || aiStyle in 0..2 || throw HandlerException("invalid ai style")
        aiBasePower == null || aiBasePower < 10.1f || throw HandlerException("invalid ai base power")
        aiExperience == null || aiExperience < 10.1f || throw HandlerException("invalid ai experience")
        gameWeight == null || gameWeight.values.all { it in -2..2 } || throw HandlerException("invalid game weight")
        aiPreference == null || aiPreference.values.all { it in -2..2 } || throw HandlerException("invalid ai preference")
        customLevelCount == null || customLevelCount.size == 11 || throw HandlerException("invalid custom level count")
        customLevelCount == null || boardSize == null || customLevelCount.all { it in 0..(boardSize * boardSize) } ||
            throw HandlerException("invalid custom level count")
        boardSize == null || boardSize in 4..6 || throw HandlerException("invalid board size")
        type == null || boardSize == null || type != 2 || boardSize == 5 || throw HandlerException("BP only supports 5x5 board")
        type == null || blindSetting == null || type != 3 || blindSetting == 1 ||
            throw HandlerException("Link does not support blind mode")
        type == null || dualBoard == null || type != 3 || dualBoard == 0 ||
            throw HandlerException("Link does not support dual board")
        type == null || difficulty == null || type != 3 || difficulty in 1..3 ||
            throw HandlerException("Link only supports E/N/L difficulty")
        extraLineCount == null || extraLineCount in 0..4 || throw HandlerException("invalid extra line count")
        hiddenSelectThresholdA == null || boardSize == null || hiddenSelectThresholdA in 1..(boardSize * boardSize) ||
            throw HandlerException("invalid hidden threshold a")
        hiddenSelectThresholdB == null || boardSize == null || hiddenSelectThresholdB in 1..(boardSize * boardSize) ||
            throw HandlerException("invalid hidden threshold b")
        portalCount == null || boardSize == null || portalCount in 1..(boardSize * boardSize) ||
            throw HandlerException("invalid portal count")
        linkLevelCoefficient == null || linkLevelCoefficient in 0.0..100.0 ||
            throw HandlerException("invalid link level coefficient")
        linkFastestCoefficient == null || linkFastestCoefficient in 0.0..10.0 ||
            throw HandlerException("invalid link fastest coefficient")
        linkConnectivity == null || linkConnectivity == 4 || linkConnectivity == 8 ||
            throw HandlerException("invalid link connectivity")
        val boardArea = boardSize?.let { it * it }
        linkDisabledIdx == null || boardArea == null || linkDisabledIdx.all { it in 0 until boardArea } ||
            throw HandlerException("link disabled index out of board")
        linkDisabledIdx == null || linkDisabledIdx.distinct().size == linkDisabledIdx.size ||
            throw HandlerException("duplicate link disabled index")
        listOf(linkStartA, linkEndA, linkStartB, linkEndB)
            .filterNotNull()
            .all { boardArea == null || it in 0 until boardArea } ||
            throw HandlerException("link endpoint out of board")
    }
}
