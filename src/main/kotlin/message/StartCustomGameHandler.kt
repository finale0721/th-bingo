package org.tfcc.bingo.message

import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.json.JsonElement
import org.tfcc.bingo.GameLogger
import org.tfcc.bingo.Player
import org.tfcc.bingo.RequestHandler
import org.tfcc.bingo.decode
import org.tfcc.bingo.encode
import org.tfcc.bingo.push
import org.tfcc.bingo.toSpellStatus

object StartCustomGameHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = player.room ?: throw HandlerException("不在房间里")
        room.isHost(player) || throw HandlerException("没有权限")
        !room.started || throw HandlerException("游戏已经开始")
        room.players.all { it != null } || throw HandlerException("玩家没满")

        val msg = data!!.decode<StartCustomGameMessage>()
        msg.validate()
        msg.roomConfig.validate()

        // 一般的游戏开始逻辑
        room.type.resetData(room)
        room.type.setUp(room)

        room.spells2 = emptyArray()
        room.spells = msg.spells.toTypedArray()
        if (room.roomConfig.dualBoard > 0) {
            room.spells2 = msg.spells2!!.toTypedArray()
        } else {
            room.spells2 = null
        }

        room.type.initStatus(room)
        if (room.gameLogger == null) {
            room.gameLogger = GameLogger()
        }

        // 自定义处理环节
        room.isCustomGame = true
        room.normalData = NormalData()
        room.roomConfig = msg.roomConfig

        val spellStatusInts = msg.spellStatus.toIntArray()
        room.spellStatus = spellStatusInts.map { it.toSpellStatus() }.toTypedArray()
        room.spellStatusInPlayerClient = Array(2) { spellStatusInts }

        if (room.roomConfig.dualBoard > 0) {
            val normalData = NormalData()
            normalData.whichBoardA = 0
            normalData.whichBoardB = 0
            normalData.isPortalA = msg.isPortalA.toTypedArray()
            normalData.isPortalB = msg.isPortalB.toTypedArray()
            normalData.getOnWhichBoard = Array(room.boardArea) { 0 }
            room.normalData = normalData
        }

        room.push("push_start_game", room.roomConfig.encode())
        room.gameLogger!!.startLog(room)
        return null
    }
}
