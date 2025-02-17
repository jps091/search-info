package com.search.error

enum class StompErrorType(override val description: String): ErrorTypeIfs {
    SESSION_NOT_EXIST("연결 이벤트에 세션 ID가 없습니다."),
    ROOM_ID_NOT_EXIST("헤더에 Room ID가 없습니다."),
    ROOM_PEOPLE_EXCEED("해당 채팅방 인원수가 초과하였습니다.")
}