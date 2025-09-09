package org.tfcc.bingo.message

import kotlinx.serialization.Serializable
import org.tfcc.bingo.Spell

@Serializable
data class PlayerAction(
    val playerName: String,
    val actionType: String,
    val spellIndex: Int,
    val spellName: String,
    val timestamp: Long
)

@Serializable
data class GameLog(
    val roomConfig: RoomConfig,
    val players: List<String>,
    val spells: List<Spell>,
    val spells2: List<Spell>?,
    val normalData: NormalData?,
    val actions: List<PlayerAction>,
    val gameStartTimestamp: Long,
    val score: List<Int>
)
