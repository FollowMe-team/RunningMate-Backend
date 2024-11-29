package com.follow_me.running_mate.domain.member.mapper;

import com.follow_me.running_mate.domain.enums.BadgeType;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Role;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import com.follow_me.running_mate.domain.member.entity.MemberLocation;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import java.util.Arrays;
import java.util.Set;
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
            .introduce(request.getIntroduce())
            .runningCareer(request.getRunningCareer())
            .role(Role.USER)
            .ranking(Ranking.JOGGER)
            .build();
    }

    public MemberLocation toMemberLocation(MemberRequest.LocationInfo locationInfo, Member member) {
        return MemberLocation.builder()
            .member(member)
            .address(locationInfo.getAddress())
            .location(FormatterUtil.formatPoint(locationInfo))
            .build();
    }

    public MemberResponse.MyProfileResponse toMyProfileResponse(Member member) {
        return MemberResponse.MyProfileResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .birth(member.getBirth())
                .runningCareer(member.getRunningCareer())
                .footPrint(member.getFootprint())
                .build();
    }
    public MemberResponse.UpdateMyProfileResponse toUpdateMyProfileResponse(Member member) {
        return MemberResponse.UpdateMyProfileResponse.builder()
                .nickname(member.getNickname())
                .gender(member.getGender())
                .birth(member.getBirth())
                .build(); //TODO: 주소 데이터 추가하기
    }

    public List<MemberResponse.BadgeResponse> toBadgeResponses(List<MemberBadge> memberBadges) {
        Set<BadgeType> acquiredBadges = memberBadges.stream()
                .map(MemberBadge::getType)
                .collect(Collectors.toSet());

        return Arrays.stream(BadgeType.values())
                .map(badgeType -> MemberResponse.BadgeResponse.builder()
                        .name(badgeType.getName())
                        .description(badgeType.getDescription())
                        .iconUrl(badgeType.getIconUrl())
                        .criteria(badgeType.getCriteria())
                        .isAcquired(acquiredBadges.contains(badgeType))
                        .build())
                .collect(Collectors.toList());
    }

    public MemberResponse.FollowResponse toFollowResponse(Member member) {
        return MemberResponse.FollowResponse.builder()
                .nickname(member.getNickname())
                .iconUrl(member.getProfileImageUrl())
                .footPrint(member.getFootprint())
                .build();
    }
}
