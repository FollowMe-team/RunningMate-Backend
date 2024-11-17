package com.follow_me.running_mate.domain.member.mapper;

import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Role;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {

    public Member toEntity(MemberRequest.SignUpRequest request, String encodedPassword) {
        return Member.builder()
            .email(request.getEmail())
            .password(encodedPassword)
            .name(request.getName())
            .gender(request.getGender())
            .birth(request.getBirth())
            .nickname(request.getNickname())
            .runningCareer(request.getRunningCareer())
            .role(Role.USER)
            .ranking(Ranking.JOGGER)
            .build();
    }

    // 마이 프로필 조회 시 사용: Entity -> Response DTO 변환
    public MemberResponse.MyProfileResponse toMyProfileResponse(Member member) {
        return MemberResponse.MyProfileResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .birth(member.getBirth())
                .runningGoal(member.getRunningGoal())
                .runningCareer(member.getRunningCareer())
                .build(); //TODO: 발자취 추가하기
    }
    public MemberResponse.UpdateMyProfileResponse toUpdateMyProfileResponse(Member member) {
        return MemberResponse.UpdateMyProfileResponse.builder()
                .nickname(member.getNickname())
                .gender(member.getGender())
                .birth(member.getBirth())
                .build(); //TODO: 주소 데이터 추가하기
    }
    // MemberBadge 리스트를 BadgeResponse 리스트로 변환
    public List<MemberResponse.BadgeResponse> toBadgeResponseList(List<MemberBadge> memberBadges) {
        return memberBadges.stream() // list -> stream 타입으로 변경
                .map(badge -> MemberResponse.BadgeResponse.builder() // badge 는 변수이름 설정해준것 map 으로 각각의 스트림 요소 변환 작업
                        .name(badge.getType().getName()) //getType 으로 BadgeType 이라는 enum 을 가져오고 거기서 추가 정보를 가져오는 방식
                        .description(badge.getType().getDescription())
                        .iconUrl(badge.getType().getIconUrl())
                        .criteria(badge.getType().getCriteria())
                        .build()) // 빌더로 객체 생성
                .collect(Collectors.toList()); // 다시 stream -> list 로 변경
    }

}
