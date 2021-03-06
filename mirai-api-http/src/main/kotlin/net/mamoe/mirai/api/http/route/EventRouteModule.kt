/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.routing
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent

/**
 * 事件响应路由
 */

fun Application.eventRouteModule() {

    routing {

        miraiVerify<EventRespDTO>("/resp/newFriendRequestEvent") {
            val event = NewFriendRequestEvent(
                it.session.bot,
                it.eventId,
                "",
                it.fromId,
                it.groupId,
                ""
            )
            when(it.operate) {
                0 -> event.accept() // accept
                1 -> event.reject(blackList = false) // reject
                2 -> event.reject(blackList = true) // black list
                else -> {
                    call.respondDTO(StateCode.NoOperateSupport)
                    return@miraiVerify
                }
            }
            call.respondDTO(StateCode.Success)
        }

        miraiVerify<EventRespDTO>("/resp/memberJoinRequestEvent") {
            val event = MemberJoinRequestEvent(
                it.session.bot,
                it.eventId,
                "",
                it.fromId,
                it.groupId,
                "",
                ""
            )
            when(it.operate) {
                0 -> event.accept() // accept
                1 -> event.reject(blackList = false) // reject
                2 -> event.ignore(blackList = false) //ignore
                3 -> event.reject(blackList = true) // reject and black list
                4 -> event.ignore(blackList = true) // ignore and black list
                else -> {
                    call.respondDTO(StateCode.NoOperateSupport)
                    return@miraiVerify
                }
            }
            call.respondDTO(StateCode.Success)
        }

    }
}

@Serializable
private data class EventRespDTO(
    override val sessionKey: String,
    val eventId: Long,
    val fromId: Long,
    val groupId: Long,
    val operate: Int,
    val message: String
) : VerifyDTO()
