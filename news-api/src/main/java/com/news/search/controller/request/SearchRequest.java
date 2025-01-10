package com.news.search.controller.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class SearchRequest {
    @NotBlank(message = "입력은 비어있을 수 없습니다.")
    @Size(max = 50, message = "query는 최대 50자를 초과할 수 없습니다.")
    private String query;

    @NotNull(message = "페이지 번호는 필수입니다.")
    @Min(value = 1, message = "페이지번호는 1이상이어야 합니다.")
    @Max(value = 10000, message = "페이지번호는 10000이하여야 합니다.")
    private int page;

    @NotNull(message = "페이지 사이즈는 필수입니다.")
    @Min(value = 1, message = "페이지크기는 1이상이어야 합니다.")
    @Max(value = 50, message = "페이지크기는 50이하여야 합니다.")
    private int size;
}
