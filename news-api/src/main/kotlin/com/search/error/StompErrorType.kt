package com.search.error

enum class StompErrorType(override val description: String): ErrorTypeIfs {
    SESSION_NOT_EXIST("연결 이벤트에 세션 ID가 없습니다."),
    HEADER_MISSING("필수 헤더가 존재하지 않습니다."),
    ROOM_PEOPLE_EXCEED("해당 채팅방 인원수가 초과하였습니다.")
}