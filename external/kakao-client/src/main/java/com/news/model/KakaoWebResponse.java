package com.news.model;

import java.util.List;

public record KakaoWebResponse(
        List<Document> documents,
        Meta meta
) {
}
