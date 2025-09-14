package org.tfcc.bingo

import kotlinx.serialization.json.Json
import org.tfcc.bingo.message.GameLog
import org.tfcc.bingo.message.NormalData
import org.tfcc.bingo.message.PlayerAction
import org.tfcc.bingo.message.RoomConfig

class GameLogger {
    private var roomConfig: RoomConfig? = null
    private var spells: Array<Spell>? = null
    private var spells2: Array<Spell>? = null
    private var normalData: NormalData? = null
    private val actions = mutableListOf<PlayerAction>()
    private var gameStartTimestamp: Long = 0
    private var players = arrayOf<String>("", "")
    private var score = intArrayOf(0, 0)

    private fun updateScore(room: Room) {
        var left = 0
        var right = 0
        room.spellStatus!!.forEach {
            if (it == SpellStatus.LEFT_GET) left++
            else if (it == SpellStatus.RIGHT_GET) right++
        }
        score[0] = left
        score[1] = right
    }

    private fun getCurrentScore(room: Room): IntArray {
        var left = 0
        var right = 0
        room.spellStatus!!.forEach {
            if (it == SpellStatus.LEFT_GET) left++
            else if (it == SpellStatus.RIGHT_GET) right++
        }
        val scoreNow = intArrayOf(0, 0)
        scoreNow[0] = left
        scoreNow[1] = right
        return scoreNow
    }

    private fun updateNormalData(room: Room) {
        if (room.normalData != null) {
            this.normalData = NormalData()
            this.normalData!!.whichBoardA = room.normalData!!.whichBoardA
            this.normalData!!.whichBoardB = room.normalData!!.whichBoardB
            this.normalData!!.isPortalA = room.normalData!!.isPortalA.copyOf()
            this.normalData!!.isPortalB = room.normalData!!.isPortalB.copyOf()
            this.normalData!!.getOnWhichBoard = room.normalData!!.getOnWhichBoard.copyOf()
        }
    }

    fun startLog(room: Room) {
        clear()
        this.roomConfig = room.roomConfig
        this.spells = room.spells
        this.spells2 = room.spells2
        this.gameStartTimestamp = room.startMs
        this.players[0] = room.players[0]?.name ?: "PlayerA"
        this.players[1] = room.players[1]?.name ?: "PlayerB"
    }

    fun logAction(player: Player, actionType: String, spellIndex: Int, spell: Spell) {
        if (gameStartTimestamp == 0L) return

        val action = PlayerAction(
            playerName = player.name,
            actionType = actionType,
            spellIndex = spellIndex,
            spellName = spell.name,
            timestamp = System.currentTimeMillis() - gameStartTimestamp,
            scoreNow = getCurrentScore(player.room!!).toList(),
        )
        actions.add(action)
        player.room?.let { updateScore(it) }
        player.room?.let { updateNormalData(it) }
    }

    fun getSerializedLog(): String? {
        val config = roomConfig ?: return null
        val spellList = spells!!.toList()
        val spellList2 = spells2?.toList() ?: return null

        val log = GameLog(
            roomConfig = config,
            players = players.toList(),
            spells = spellList,
            spells2 = spellList2,
            normalData = this.normalData,
            actions = actions.toList(),
            gameStartTimestamp = this.gameStartTimestamp,
            score = this.score.toList(),
        )
        // Use a Json configuration that ignores unknown keys for robustness
        return Json.encodeToString(log)
    }

    fun clear() {
        roomConfig = null
        spells = null
        spells2 = null
        normalData = null
        actions.clear()
        gameStartTimestamp = 0
        players.fill("")
        score = intArrayOf(0, 0)
    }
}
