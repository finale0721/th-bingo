package org.tfcc.bingo.message

import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.tfcc.bingo.Player
import org.tfcc.bingo.RequestHandler
import org.tfcc.bingo.RoomTypeLink

private fun linkRoom(player: Player) = (player.room ?: throw HandlerException("not in room")).also {
    it.started || throw HandlerException("game not started")
    it.type is RoomTypeLink || throw HandlerException("unsupported operation")
}

private fun playerIndex(player: Player): Int {
    val room = player.room ?: throw HandlerException("not in room")
    val idx = room.players.indexOf(player)
    idx >= 0 || throw HandlerException("permission denied")
    return idx
}

private fun canControlLinkPhase(player: Player): Boolean {
    val room = player.room ?: return false
    return if (room.host != null) room.isHost(player) else room.players[0] === player
}

private fun linkTargetPlayerIndex(player: Player, data: JsonElement?): Int {
    val room = player.room ?: throw HandlerException("not in room")
    val ownIndex = playerIndex(player)
    val requested = data?.jsonObject?.get("player_index")?.jsonPrimitive?.int ?: ownIndex
    requested in 0..1 || throw HandlerException("invalid player_index")
    if (room.host != null) {
        room.isHost(player) || throw HandlerException("permission denied")
    } else {
        requested == ownIndex || throw HandlerException("permission denied")
    }
    return requested
}

object LinkRouteHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        val idx = data!!.jsonObject["index"]!!.jsonPrimitive.int
        room.boardSpec.isValidIndex(idx) || throw HandlerException("invalid index")
        RoomTypeLink.appendRoute(room, playerIndex(player), idx)
        return null
    }
}

object LinkUndoHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.undoRoute(room, playerIndex(player))
        return null
    }
}

object LinkConfirmRouteHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        val confirmed = data!!.jsonObject["confirmed"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true
        RoomTypeLink.confirmRoute(room, playerIndex(player), confirmed)
        return null
    }
}

object LinkNextCardHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.selectNext(room, playerIndex(player))
        return null
    }
}

object LinkFinishCardHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.finishSelected(room, playerIndex(player), skip = false)
        return null
    }
}

object LinkSkipCardHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.finishSelected(room, playerIndex(player), skip = true)
        return null
    }
}

object LinkForceSkipHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.finishSelected(room, linkTargetPlayerIndex(player, data), skip = true, force = true)
        return null
    }
}

object LinkUndoFinishHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        RoomTypeLink.undoFinished(room, linkTargetPlayerIndex(player, data))
        return null
    }
}

object LinkSetSkipUsedHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        val value = data!!.jsonObject["value"]!!.jsonPrimitive.int
        value in 0..9999 || throw HandlerException("invalid value")
        RoomTypeLink.setSkipUsedPublic(room, linkTargetPlayerIndex(player, data), value)
        return null
    }
}

object LinkSetPhaseHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        canControlLinkPhase(player) || throw HandlerException("permission denied")
        val phase = data!!.jsonObject["phase"]!!.jsonPrimitive.int
        RoomTypeLink.setPhase(room, phase)
        return null
    }
}

object LinkAiSpeedrunHandler : RequestHandler {
    override fun handle(ctx: ChannelHandlerContext, player: Player, data: JsonElement?): JsonElement? {
        val room = linkRoom(player)
        room.roomConfig.useAI || throw HandlerException("AI practice is not enabled")
        canControlLinkPhase(player) || throw HandlerException("permission denied")
        room.linkAiAgent?.speedrun() ?: throw HandlerException("AI is not running")
        return null
    }
}
