package org.tfcc.bingo

import org.tfcc.bingo.message.GameLog
import org.tfcc.bingo.message.HandlerException
import org.tfcc.bingo.message.LinkData

object SpellStatusConverter {
    const val LEGACY_VERSION = 1
    const val CURRENT_VERSION = 2

    fun convertStatuses(statuses: List<Int>, sourceVersion: Int, blindSetting: Int): List<Int> {
        requireSupportedVersion(sourceVersion)
        return if (sourceVersion == CURRENT_VERSION) {
            statuses.map { it.toSpellStatus() }
        } else {
            statuses.map { convertLegacyStatus(it, blindSetting) }
        }
    }

    fun convertGameLog(gameLog: GameLog): GameLog {
        requireSupportedVersion(gameLog.spellStatusVersion)
        if (gameLog.spellStatusVersion == CURRENT_VERSION) return gameLog

        val blindSetting = gameLog.roomConfig.blindSetting
        return gameLog.copy(
            actions = gameLog.actions.map { action ->
                action.copy(
                    actionType = convertLegacyActionType(action.actionType, blindSetting),
                    linkData = action.linkData?.let(::convertLegacyLinkData),
                )
            },
            initStatus = gameLog.initStatus.map { convertLegacyStatus(it, blindSetting) },
            linkData = gameLog.linkData?.let(::convertLegacyLinkData),
            spellStatusVersion = CURRENT_VERSION,
        )
    }

    fun convertLegacyStatus(status: Int, blindSetting: Int): Int = when (status) {
        -1 -> BANNED
        0 -> if (blindSetting > 1) LEFT_SEE_ONLY or RIGHT_SEE_ONLY else NONE
        1 -> LEFT_SELECT
        2 -> LEFT_SELECT or RIGHT_SELECT
        3 -> RIGHT_SELECT
        5 -> LEFT_GET
        6 -> LEFT_GET or RIGHT_GET
        7 -> RIGHT_GET
        0x1000 -> BOTH_HIDDEN
        0x1001 -> LEFT_SEE_ONLY
        0x1002 -> RIGHT_SEE_ONLY
        0x1010 -> ONLY_REVEAL_GAME
        0x1011 -> ONLY_REVEAL_GAME_STAGE
        0x1012 -> ONLY_REVEAL_STAR
        else -> throw HandlerException("旧版status不合法：$status")
    }

    private fun convertLegacyActionType(actionType: String, blindSetting: Int): String {
        if (!actionType.startsWith("set-")) return actionType
        val legacyStatus = actionType.removePrefix("set-").toIntOrNull() ?: return actionType
        return "set-${convertLegacyStatus(legacyStatus, blindSetting)}"
    }

    private fun convertLegacyLinkData(linkData: LinkData): LinkData = linkData.copy().also { converted ->
        converted.statusA = ArrayList(converted.statusA.map(::convertLegacyBasicStatus))
        converted.statusB = ArrayList(converted.statusB.map(::convertLegacyBasicStatus))
    }

    private fun convertLegacyBasicStatus(status: Int): Int = when (status) {
        -1 -> BANNED
        0 -> NONE
        1 -> LEFT_SELECT
        2 -> LEFT_SELECT or RIGHT_SELECT
        3 -> RIGHT_SELECT
        5 -> LEFT_GET
        6 -> LEFT_GET or RIGHT_GET
        7 -> RIGHT_GET
        else -> throw HandlerException("旧版基础status不合法：$status")
    }

    private fun requireSupportedVersion(sourceVersion: Int) {
        if (sourceVersion !in LEGACY_VERSION..CURRENT_VERSION) {
            throw HandlerException("不支持的status数据版本：$sourceVersion")
        }
    }
}
