package org.tfcc.bingo.message

import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import org.tfcc.bingo.Player
import org.tfcc.bingo.RequestHandler

object PrintLogHandler : RequestHandler {
    @Throws(HandlerException::class)
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = player.room ?: throw HandlerException("不在房间里")

        val serializedLog = room.gameLogger?.getSerializedLog()
            ?: throw HandlerException("当前没有可供打印的游戏记录")

        // Return the serialized JSON string as a primitive
        return JsonPrimitive(serializedLog)
    }
}
