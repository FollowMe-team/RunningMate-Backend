package com.follow_me.running_mate.domain.crew.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CrewResponse {
    @Getter
    @Builder // 인자가 많아 생성자가 많이 필요할때 사용해 선택적으로 인자를 사용해 편하게 해주는 어노테이션
    @AllArgsConstructor // 모든 인자들을 포함하는 생성자 만들어주는 어노테이션
    @NoArgsConstructor
    public static class MyCrewResponse{
        private Long id;
        private String name;
        private Integer memberCount;
        private String shortDescription;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyCrewListResponse{
        List<MyCrewResponse> myCrews;
        List<MyCrewResponse> recommendedCrews;
    }
}
