package org.tfcc.bingo

import kotlinx.serialization.json.Json
import org.tfcc.bingo.message.GameLog
import org.tfcc.bingo.message.LinkData
import org.tfcc.bingo.message.PlayerAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpellStatusTest {
    @Test
    fun `constants follow the 32-bit protocol`() {
        assertEquals(0x0, SpellStatus.NONE)
        assertEquals(0x1, SpellStatus.BANNED)
        assertEquals(0x2, SpellStatus.LEFT_SELECT)
        assertEquals(0x4, SpellStatus.RIGHT_SELECT)
        assertEquals(0x8, SpellStatus.LEFT_GET)
        assertEquals(0x10, SpellStatus.RIGHT_GET)
        assertEquals(0x1 shl 16, SpellStatus.LEFT_SEE_ONLY)
        assertEquals(0x10 shl 16, SpellStatus.ONLY_REVEAL_STAR)
    }

    @Test
    fun `basic attributes combine without named aggregate states`() {
        val bothSelect = LEFT_SELECT or RIGHT_SELECT
        val bothGet = LEFT_GET or RIGHT_GET

        assertTrue(bothSelect.hasBasicAttribute(LEFT_SELECT))
        assertTrue(bothSelect.hasBasicAttribute(RIGHT_SELECT))
        assertTrue(bothSelect.isSelectStatus())
        assertTrue(bothGet.isGetStatus())
        assertFalse(bothGet.isSelectStatus())
    }

    @Test
    fun `basic changes and opposite preserve independent optional bits`() {
        val status = ONLY_REVEAL_GAME or LEFT_SELECT

        assertEquals(ONLY_REVEAL_GAME or LEFT_GET, status.withBasicSpellStatus(LEFT_GET))
        assertEquals(ONLY_REVEAL_GAME or RIGHT_SELECT, status.opposite())
        assertEquals(RIGHT_SEE_ONLY or RIGHT_GET, (LEFT_SEE_ONLY or LEFT_GET).opposite())
    }

    @Test
    fun `default optional parser handles visibility grants by bit`() {
        val bothVisible = LEFT_SEE_ONLY or RIGHT_SEE_ONLY

        assertTrue(DefaultOptionalSpellStatusParser.isVisibleTo(LEFT_SEE_ONLY, 0))
        assertFalse(DefaultOptionalSpellStatusParser.isVisibleTo(LEFT_SEE_ONLY, 1))
        assertTrue(DefaultOptionalSpellStatusParser.isVisibleTo(bothVisible, 0))
        assertTrue(DefaultOptionalSpellStatusParser.isVisibleTo(bothVisible, 1))
        assertTrue(DefaultOptionalSpellStatusParser.hasAttribute(BOTH_HIDDEN, BOTH_HIDDEN))
    }

    @Test
    fun `validation accepts optional extension bits but rejects unknown basic bits`() {
        assertEquals(0x40000000 or LEFT_SELECT, (0x40000000 or LEFT_SELECT).toSpellStatus())
        assertFailsWith<Exception> { 0x20.toSpellStatus() }
    }

    @Test
    fun `legacy values convert with blind-mode visibility context`() {
        assertEquals(BANNED, SpellStatusConverter.convertLegacyStatus(-1, 1))
        assertEquals(LEFT_SELECT or RIGHT_SELECT, SpellStatusConverter.convertLegacyStatus(2, 1))
        assertEquals(NONE, SpellStatusConverter.convertLegacyStatus(0, 1))
        assertEquals(LEFT_SEE_ONLY or RIGHT_SEE_ONLY, SpellStatusConverter.convertLegacyStatus(0, 2))
        assertEquals(BOTH_HIDDEN, SpellStatusConverter.convertLegacyStatus(0x1000, 2))
    }

    @Test
    fun `legacy replay conversion updates statuses actions and link snapshots`() {
        val linkData = LinkData().apply {
            statusA.addAll(listOf(0, 1, 5))
            statusB.addAll(listOf(0, 3, 7))
        }
        val legacy = GameLog(
            actions = listOf(PlayerAction(actionType = "set-2", linkData = linkData)),
            initStatus = listOf(-1, 0, 2, 0x1012),
            linkData = linkData,
        )

        val converted = SpellStatusConverter.convertGameLog(legacy)

        assertEquals(SpellStatusConverter.CURRENT_VERSION, converted.spellStatusVersion)
        assertEquals(listOf(BANNED, NONE, LEFT_SELECT or RIGHT_SELECT, ONLY_REVEAL_STAR), converted.initStatus)
        assertEquals("set-${LEFT_SELECT or RIGHT_SELECT}", converted.actions.single().actionType)
        assertEquals(listOf(NONE, LEFT_SELECT, LEFT_GET), converted.linkData!!.statusA)
        assertEquals(listOf(NONE, RIGHT_SELECT, RIGHT_GET), converted.actions.single().linkData!!.statusB)
    }

    @Test
    fun `replays without a status version deserialize as legacy data`() {
        val legacy = Json.decodeFromString<GameLog>("{}")

        assertEquals(SpellStatusConverter.LEGACY_VERSION, legacy.spellStatusVersion)
        assertEquals(SpellStatusConverter.CURRENT_VERSION, SpellStatusConverter.convertGameLog(legacy).spellStatusVersion)
    }
}
