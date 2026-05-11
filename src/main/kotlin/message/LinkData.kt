package org.tfcc.bingo.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LinkData {
    @SerialName("link_idx_a")
    val linkIdxA = ArrayList<Int>()

    @SerialName("link_idx_b")
    val linkIdxB = ArrayList<Int>()

    @SerialName("start_ms_a")
    var startMsA = 0L

    @SerialName("end_ms_a")
    var endMsA = 0L

    @SerialName("event_a")
    var eventA = 0

    @SerialName("start_ms_b")
    var startMsB = 0L

    @SerialName("end_ms_b")
    var endMsB = 0L

    @SerialName("event_b")
    var eventB = 0

    @SerialName("route_confirmed_a")
    var routeConfirmedA = false

    @SerialName("route_confirmed_b")
    var routeConfirmedB = false

    @SerialName("current_step_a")
    var currentStepA = 0

    @SerialName("current_step_b")
    var currentStepB = 0

    @SerialName("status_a")
    var statusA = ArrayList<Int>()

    @SerialName("status_b")
    var statusB = ArrayList<Int>()

    @SerialName("last_get_time_a")
    var lastGetTimeA = 0L

    @SerialName("last_get_time_b")
    var lastGetTimeB = 0L

    @SerialName("skip_used_a")
    var skipUsedA = 0

    @SerialName("skip_used_b")
    var skipUsedB = 0

    @SerialName("score_a")
    var scoreA = 0.0

    @SerialName("score_b")
    var scoreB = 0.0

    @SerialName("disabled_idx")
    var disabledIdx = ArrayList<Int>()

    @Transient
    var boardSize: Int = 5

    private val bottomRight: Int get() = boardSize * boardSize - 1
    private val bottomLeft: Int get() = boardSize * (boardSize - 1)

    fun selectCompleteA() = routeConfirmedA && linkIdxA.last() == bottomRight
    fun selectCompleteB() = routeConfirmedB && linkIdxB.last() == bottomLeft

    fun ensureStatusSize(area: Int) {
        while (statusA.size < area) statusA.add(0)
        while (statusB.size < area) statusB.add(0)
        while (statusA.size > area) statusA.removeAt(statusA.lastIndex)
        while (statusB.size > area) statusB.removeAt(statusB.lastIndex)
    }
}
