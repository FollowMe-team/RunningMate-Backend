package com.follow_me.running_mate.domain.member.mapper;

import com.follow_me.running_mate.domain.enums.BadgeType;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Role;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import com.follow_me.running_mate.domain.member.entity.MemberFootprint;
import com.follow_me.running_mate.domain.member.entity.MemberLocation;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import java.util.Arrays;
import java.util.Optional;
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

    public MemberFollow toMemberFollow(Member follower, Member following) {
        return MemberFollow.builder()
            .follower(follower)
            .following(following)
            .isActive(false)
            .build();
    }

    public MemberFootprint toMemberFootprint(Member writer, Member target, MemberRequest.FootprintRequest request) {
        return MemberFootprint.builder()
            .writer(writer)
            .target(target)
            .content(request.getContent())
            .type(request.getType())
            .isAnonymous(request.getIsAnonymous())
            .build();
    }

    public MemberResponse.FootprintInfo toFootprintInfo(MemberFootprint footprint) {

        MemberResponse.FootprintInfo.FootprintInfoBuilder builder = MemberResponse.FootprintInfo.builder()
            .footprintId(footprint.getId())
            .content(footprint.getContent())
            .footprintType(footprint.getType())
            .createdAt(FormatterUtil.formatTime(footprint.getCreatedAt()));

        if (footprint.getIsAnonymous()) {
            builder
                .memberInfo(null);
        } else {
            builder
                .memberInfo(toMemberInfo(footprint.getWriter()));
        }
        return builder.build();
    }

    private MemberResponse.MemberInfo toMemberInfo(Member member) {
        return MemberResponse.MemberInfo.builder()
            .memberId(member.getId())
            .profileImageUrl(member.getProfileImageUrl())
            .nickname(member.getNickname())
            .ranking(member.getRanking())
            .footPrint(member.getFootprint())
            .build();
    }

    public MemberResponse.MyProfileResponse toMyProfileResponse(
        Member member, String address, Double runningDistance, Long runningCount
    ) {
        return MemberResponse.MyProfileResponse.builder()
            .profileImageUrl(member.getProfileImageUrl())
            .nickname(member.getNickname())
            .ranking(member.getRanking())
            .introduce(member.getIntroduce())
            .followerCount(FormatterUtil.formatMemberCount(member.getFollowerCount()))
            .followingCount(FormatterUtil.formatMemberCount(member.getFollowingCount()))
            .name(member.getName())
            .gender(member.getGender())
            .birth(member.getBirth())
            .address(address)
            .runningDistance(runningDistance)
            .runningCount(runningCount)
            .footPrint(member.getFootprint())
            .build();
    }

    public MemberResponse.MyProfileSummaryResponse toMyProfileSummaryResponse(
        Member member, Optional<MemberLocation> location
    ) {
        return MemberResponse.MyProfileSummaryResponse.builder()
            .profileImageUrl(member.getProfileImageUrl())
            .nickname(member.getNickname())
            .introduce(member.getIntroduce())
            .birth(member.getBirth())
            .gender(member.getGender())
            .locationInfo(toLocationInfo(location))
            .build();
    }

    private MemberResponse.LocationInfo toLocationInfo(Optional<MemberLocation> location) {
        return location.map(memberLocation -> MemberResponse.LocationInfo.builder()
            .address(memberLocation.getAddress())
            .latitude(memberLocation.getLocation().getY())
            .longitude(memberLocation.getLocation().getX())
            .build()).orElse(null);
    }

    public MemberResponse.OtherProfileResponse toOtherProfileResponse(
        Member member, Double runningDistance, Long runningCount, Boolean isSameCrew
    ) {
        return MemberResponse.OtherProfileResponse.builder()
            .profileImageUrl(member.getProfileImageUrl())
            .nickname(member.getNickname())
            .ranking(member.getRanking())
            .introduce(member.getIntroduce())
            .followerCount(FormatterUtil.formatMemberCount(member.getFollowerCount()))
            .followingCount(FormatterUtil.formatMemberCount(member.getFollowingCount()))
            .runningDistance(runningDistance)
            .runningCount(runningCount)
            .footPrint(member.getFootprint())
            .isSameCrew(isSameCrew)
            .build();
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

    public MemberResponse.FollowInfo toFollowResponse(Member member) {
        return MemberResponse.FollowInfo.builder()
            .memberId(member.getId())
            .profileImageUrl(member.getProfileImageUrl())
            .nickname(member.getNickname())
            .footPrint(member.getFootprint())
            .ranking(member.getRanking())
            .build();
    }
}
