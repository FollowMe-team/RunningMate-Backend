package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;

import java.time.YearMonth;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void signup(MemberRequest.SignUpRequest request, MultipartFile profileImage);
    void logout(String email);
    void withdraw(Member member, MemberRequest.WithdrawRequest request);
    MemberResponse.MyProfileResponse getMyProfile(Member member);
    MemberResponse.MyProfileSummaryResponse getMyProfileSummary(Member member);
    MemberResponse.UpdateMyProfileResponse updateProfile(
        Member member, MemberRequest.UpdateProfileRequest request, MultipartFile profileImage
    );
    void changePassword(MemberRequest.ChangePasswordRequest request, Member member);
    MemberResponse.BadgeListResponse getMemberBadges(Member member);
    MemberResponse.DuplicateCheckResponse isNicknameDuplicate(String nickname);
    MemberResponse.DuplicateCheckResponse isEmailDuplicate(String email);
    MemberResponse.OtherProfileResponse getOtherProfile(Member member, Long memberId);
    CourseResponse.CourseRecordInfoList getMyCourseRecords(Member member , YearMonth yearMonth);
    CourseResponse.CourseRecordInfoList getOtherCourseRecords(Member member , Long memberId , YearMonth yearMonth);
    MemberResponse.FollowingListResponse getFollowingList(Member member);
    MemberResponse.FollowerListResponse getFollowerList(Member member);
    void follow(Member member , Long memberId);
    void unfollow(Member member , Long memberId);
    void createFootprint(Member member, Long memberId, MemberRequest.FootprintRequest request);
    MemberResponse.FootprintListResponse getFootprints(Member member, Long memberId);
}
