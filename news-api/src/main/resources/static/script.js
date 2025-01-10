document.addEventListener("DOMContentLoaded", () => {
    const searchButton = document.getElementById("search-button");
    const searchInput = document.getElementById("search-input");
    const searchResultContainer = document.getElementById("search-result");
    const rankingContainer = document.getElementById("top-keywords");

    // 상위 10개 키워드 API 호출 및 렌더링
    fetch('/api/v1/webs/stats/ranking')
        .then(response => response.json())
        .then(data => {
            rankingContainer.innerHTML = ""; // 초기화
            data.forEach((stat, index) => {
                const item = document.createElement("li");
                item.innerHTML = `${index + 1}. ${stat.query} <span class="count">(${stat.count})</span>`;
                rankingContainer.appendChild(item);
            });
        })
        .catch(error => {
            console.error("상위 검색어 가져오기 중 오류 발생:", error);
        });

    // 검색 API 호출 및 결과 렌더링
    searchButton.addEventListener("click", () => {
        const query = searchInput.value.trim();
        if (!query) {
            alert("검색어를 입력하세요.");
            return;
        }

        // 검색 API 호출
        fetch(`/api/v1/webs?query=${encodeURIComponent(query)}&page=1&size=15`)
            .then(response => response.json())
            .then(data => {
                // 검색 결과 렌더링
                searchResultContainer.innerHTML = ""; // 기존 결과 초기화
                data.contents.forEach(result => {
                    const resultDiv = document.createElement("div");
                    const titleLink = document.createElement("a");
                    titleLink.href = result.link; // 제목을 클릭하면 링크로 이동
                    titleLink.textContent = result.title.replace(/<\/?b>/g, ""); // <b> 태그 제거
                    titleLink.target = "_blank";
                    const description = document.createElement("p");
                    description.textContent = result.description || "설명 없음";
                    resultDiv.appendChild(titleLink);
                    resultDiv.appendChild(description);
                    searchResultContainer.appendChild(resultDiv);
                });

                // 검색 결과 렌더링 후 상위 검색어 갱신
                return fetch('/api/v1/webs/stats/ranking');
            })
            .then(response => response.json())
            .then(data => {
                // 상위 검색어 다시 렌더링
                rankingContainer.innerHTML = ""; // 초기화
                data.forEach((stat, index) => {
                    const item = document.createElement("li");
                    item.innerHTML = `${index + 1}. ${stat.query} <span class="count">(${stat.count})</span>`;
                    rankingContainer.appendChild(item);
                });
            })
            .catch(error => {
                console.error("검색 또는 상위 검색어 갱신 중 오류 발생:", error);
            });
    });
});
