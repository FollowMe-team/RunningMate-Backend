package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;

import java.time.YearMonth;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void signup(MemberRequest.SignUpRequest request, MultipartFile profileImage);
    void logout(String email);
    void withdraw(Member member);
    MemberResponse.MyProfileResponse getMyProfile(Member member);
    MemberResponse.MyProfileSummaryResponse getMyProfileSummary(Member member);
    MemberResponse.UpdateMyProfileResponse updateProfile(
        Member member, MemberRequest.UpdateProfileRequest request, MultipartFile profileImage
    );
    void changePassword(MemberRequest.ChangePasswordRequest request, Member member);
    MemberResponse.BadgeListResponse getMemberBadges(Member member);
    boolean isNicknameDuplicate(String nickname);
    boolean isEmailDuplicate(String email);
    MemberResponse.OtherProfileResponse getOtherProfile(Member member, Long memberId);
    CourseResponse.CourseRecordInfoList getMyCourseRecords(Member member , YearMonth yearMonth);
    CourseResponse.CourseRecordInfoList getOtherCourseRecords(Member member , Long memberId , YearMonth yearMonth);
    List<MemberResponse.FollowResponse> getFollowList(Member member);
    List<MemberResponse.FollowResponse> getFollowerList(Member member);
    void follow(Member member , Long id);
    void unfollow(Member member , Long id);
}
