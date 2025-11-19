package org.tfcc.bingo.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tfcc.bingo.Spell

@Serializable
data class StartCustomGameMessage(
    @SerialName("room_config")
    val roomConfig: RoomConfig,

    val spells: List<Spell>,

    @SerialName("spells2")
    val spells2: List<Spell>? = null,

    @SerialName("spell_status")
    val spellStatus: List<Int>,

    @SerialName("initial_left_time")
    val initialLeftTime: Int, // 秒

    @SerialName("initial_cd_time_a")
    val initialCdTimeA: Int, // 秒

    @SerialName("initial_cd_time_b")
    val initialCdTimeB: Int, // 秒

    @SerialName("is_portal_a")
    val isPortalA: List<Int>,

    @SerialName("is_portal_b")
    val isPortalB: List<Int>
) {
    fun validate() {
        if (spells.size != 25) throw HandlerException("盘面A符卡数量不正确")
        if (roomConfig.dualBoard > 0) {
            if (spells2 == null || spells2.size != 25) throw HandlerException("盘面B符卡数量不正确")
        }
        if (spellStatus.size != 25) throw HandlerException("状态数据长度不正确")
    }
}
