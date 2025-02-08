package com.search.domain.websearch.controller.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SearchRequest(
        @field:NotBlank(message = "입력은 비어있을 수 없습니다.")
        @field:Size(max = 50, message = "query는 최대 50자를 초과할 수 없습니다.")
        val query: String,

        @field:NotNull(message = "페이지 번호는 필수입니다.")
        @field:Min(value = 1, message = "페이지번호는 1이상이어야 합니다.")
        @field:Max(value = 10000, message = "페이지번호는 10000이하여야 합니다.")
        val page: Int,

        @field:NotNull(message = "페이지 사이즈는 필수입니다.")
        @field:Min(value = 1, message = "페이지크기는 1이상이어야 합니다.")
        @field:Max(value = 50, message = "페이지크기는 50이하여야 합니다.")
        val size: Int
)