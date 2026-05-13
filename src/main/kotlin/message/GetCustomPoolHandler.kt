package org.tfcc.bingo.message

import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.tfcc.bingo.Dispatcher
import org.tfcc.bingo.RequestHandler
import org.tfcc.bingo.admin.CustomPoolStore
import org.tfcc.bingo.encode

object GetCustomPoolHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: org.tfcc.bingo.Player, data: JsonElement?): JsonElement? {
        val md5 = data?.jsonObject?.get("md5")?.jsonPrimitive?.contentOrNull
            ?: throw HandlerException("缺少md5参数")
        val metadata = CustomPoolStore.getPoolMetadata(md5)
            ?: throw HandlerException("卡池不存在或已过期")
        val spellsJsonStr = CustomPoolStore.getPoolSpellsJson(md5)
            ?: throw HandlerException("卡池数据已损坏")
        return JsonObject(mapOf(
            "metadata" to metadata.encode(),
            "spells" to Dispatcher.json.parseToJsonElement(spellsJsonStr),
        ))
    }
}
