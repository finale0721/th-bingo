package org.tfcc.bingo

import org.tfcc.bingo.message.HandlerException

/** 32-bit spell-cell status: basic attributes use the low 16 bits and optional attributes use the high 16 bits. */
object SpellStatus {
    const val BASIC_MASK = 0x0000FFFF
    const val OPTIONAL_MASK = -0x10000

    const val NONE = 0x0
    const val BANNED = 0x1
    const val LEFT_SELECT = 0x2
    const val RIGHT_SELECT = 0x4
    const val LEFT_GET = 0x8
    const val RIGHT_GET = 0x10

    const val BOTH_HIDDEN = 0x0
    const val LEFT_SEE_ONLY = 0x1 shl 16
    const val RIGHT_SEE_ONLY = 0x2 shl 16
    const val ONLY_REVEAL_GAME = 0x4 shl 16
    const val ONLY_REVEAL_GAME_STAGE = 0x8 shl 16
    const val ONLY_REVEAL_STAR = 0x10 shl 16

    const val SELECT_MASK = LEFT_SELECT or RIGHT_SELECT
    const val GET_MASK = LEFT_GET or RIGHT_GET
    const val KNOWN_BASIC_MASK = BANNED or SELECT_MASK or GET_MASK
    const val VISIBILITY_MASK = LEFT_SEE_ONLY or RIGHT_SEE_ONLY
}

const val NONE = SpellStatus.NONE
const val BANNED = SpellStatus.BANNED
const val LEFT_SELECT = SpellStatus.LEFT_SELECT
const val RIGHT_SELECT = SpellStatus.RIGHT_SELECT
const val LEFT_GET = SpellStatus.LEFT_GET
const val RIGHT_GET = SpellStatus.RIGHT_GET
const val BOTH_HIDDEN = SpellStatus.BOTH_HIDDEN
const val LEFT_SEE_ONLY = SpellStatus.LEFT_SEE_ONLY
const val RIGHT_SEE_ONLY = SpellStatus.RIGHT_SEE_ONLY
const val ONLY_REVEAL_GAME = SpellStatus.ONLY_REVEAL_GAME
const val ONLY_REVEAL_GAME_STAGE = SpellStatus.ONLY_REVEAL_GAME_STAGE
const val ONLY_REVEAL_STAR = SpellStatus.ONLY_REVEAL_STAR

/**
 * Optional-attribute interpretation is replaceable per game mode. The default parser is shared by all current modes.
 */
interface OptionalSpellStatusParser {
    fun hasAttribute(status: Int, attribute: Int): Boolean

    fun isVisibleTo(status: Int, playerIndex: Int): Boolean
}

object DefaultOptionalSpellStatusParser : OptionalSpellStatusParser {
    override fun hasAttribute(status: Int, attribute: Int): Boolean {
        val optionalStatus = status.optionalSpellStatus()
        return if (attribute == BOTH_HIDDEN) optionalStatus == BOTH_HIDDEN else optionalStatus and attribute == attribute
    }

    override fun isVisibleTo(status: Int, playerIndex: Int): Boolean {
        val visibilityAttribute = when (playerIndex) {
            0 -> LEFT_SEE_ONLY
            1 -> RIGHT_SEE_ONLY
            else -> SpellStatus.VISIBILITY_MASK
        }
        return status and visibilityAttribute != 0
    }
}

fun Int.basicSpellStatus(): Int = this and SpellStatus.BASIC_MASK

fun Int.optionalSpellStatus(): Int = this and SpellStatus.OPTIONAL_MASK

fun Int.withBasicSpellStatus(basicStatus: Int): Int = optionalSpellStatus() or (basicStatus and SpellStatus.BASIC_MASK)

fun Int.withOptionalSpellStatus(optionalStatus: Int): Int = basicSpellStatus() or (optionalStatus and SpellStatus.OPTIONAL_MASK)

fun Int.hasBasicAttribute(attribute: Int): Boolean = basicSpellStatus() and attribute == attribute

fun Int.hasAnyBasicAttribute(mask: Int): Boolean = basicSpellStatus() and mask != 0

fun Int.isSelectStatus(): Boolean = hasAnyBasicAttribute(SpellStatus.SELECT_MASK)

fun Int.isGetStatus(): Boolean = hasAnyBasicAttribute(SpellStatus.GET_MASK)

fun Int.isEmptyStatus(): Boolean = basicSpellStatus() == NONE

fun Int.isOneSelectStatus(): Boolean = basicSpellStatus() == LEFT_SELECT || basicSpellStatus() == RIGHT_SELECT

/** Swap all left/right attributes without changing unrelated or unknown optional attributes. */
fun Int.opposite(): Int {
    var result = this and (LEFT_SELECT or RIGHT_SELECT or LEFT_GET or RIGHT_GET or
        LEFT_SEE_ONLY or RIGHT_SEE_ONLY).inv()
    if (this and LEFT_SELECT != 0) result = result or RIGHT_SELECT
    if (this and RIGHT_SELECT != 0) result = result or LEFT_SELECT
    if (this and LEFT_GET != 0) result = result or RIGHT_GET
    if (this and RIGHT_GET != 0) result = result or LEFT_GET
    if (this and LEFT_SEE_ONLY != 0) result = result or RIGHT_SEE_ONLY
    if (this and RIGHT_SEE_ONLY != 0) result = result or LEFT_SEE_ONLY
    return result
}

@Throws(HandlerException::class)
fun Int.toSpellStatus(): Int {
    if (basicSpellStatus() and SpellStatus.KNOWN_BASIC_MASK.inv() != 0) {
        throw HandlerException("status不合法")
    }
    return this
}
