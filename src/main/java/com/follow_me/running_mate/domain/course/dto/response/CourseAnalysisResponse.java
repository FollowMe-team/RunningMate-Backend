package com.follow_me.running_mate.domain.course.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CourseAnalysisResponse {
    private String difficulty;
    private String gradient;
    private List<VoicePointInfo> voicePoints;

    @Getter
    @Builder
    public static class VoicePointInfo {
        private Integer sequenceNumber;
        private String voiceType;
    }
}
