CREATE TABLE IF NOT EXISTS chat_rooms(
        chat_room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        room_keyword VARCHAR(20) NOT NULL UNIQUE,
        is_group_chat BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_messages(
                                            chat_message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
         registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            chat_room_id BIGINT,
                                            user_token VARCHAR(30) NOT NULL,
    content VARCHAR(100) NOT NULL,
         is_group_chat BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS search_info(
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            search_count INT NOT NULL,
                                            event_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            query VARCHAR(200) NOT NULL
    );