package com.news.search.service.response;

import java.util.List;

public record PageQueryResult<T> (
        int page,
        int size,
        int totalElements,
        List<T> contents
){}
