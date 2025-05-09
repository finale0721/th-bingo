package org.tfcc.bingo

import org.tfcc.bingo.message.HandlerException

sealed interface RoomType {
    val name: String

    fun onStart(room: Room)

    @Throws(HandlerException::class)
    fun handleNextRound(room: Room)

    val canPause: Boolean

    @Throws(HandlerException::class)
    fun randSpells(spellCardVersion: Int, games: Array<String>, ranks: Array<String>, difficulty: Int?): Array<Spell>

    /**
     * @param banSelect true-ban操作，false-选卡操作
     */
    @Throws(HandlerException::class)
    fun handleSelectSpell(room: Room, playerIndex: Int, spellIndex: Int)

    @Throws(HandlerException::class)
    fun handleFinishSpell(room: Room, isHost: Boolean, playerIndex: Int, spellIndex: Int, success: Boolean)

    /** 向房间内所有玩家推送符卡状态 */
    fun pushSpells(room: Room, spellIndex: Int, causer: String)

    /**
     * 获取所有符卡状态
     *
     * @param playerIndex 0:左侧玩家，1:右侧玩家，-1:不是玩家
     */
    fun getAllSpellStatus(room: Room, playerIndex: Int): List<Int> {
        return room.spellStatus!!.map { it.value }
    }

    /** 有符卡的状态被直接设定时，应如何进行后处理 */
    fun updateSpellStatusPostProcesser(
        room: Room,
        player: Player,
        spellIndex: Int,
        prevStatus: SpellStatus,
        status: SpellStatus
    ) {
    }
}
