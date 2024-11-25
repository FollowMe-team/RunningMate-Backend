package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface MemberService {

    String signup(MemberRequest.SignUpRequest request);
    void logout(String email);
    void withdraw(Member member);
    MemberResponse.MyProfileResponse getMyProfile(String email);
    MemberResponse.UpdateMyProfileResponse updateProfile(MemberRequest.UpdateProfileRequest request , String email);
    void changePassword(MemberRequest.ChangePasswordRequest request, String email);
    List<MemberResponse.BadgeResponse> getMemberBadges(String email);
    boolean isNicknameDuplicate(String nickname);
    boolean isEmailDuplicate(String email);
    MemberResponse.MyProfileResponse getMemberProfileByEmail(String email);
    List<CourseResponse.CourseRecordInfo> getMemberRunningRecords(Member member , LocalDate date);
    List<MemberResponse.FollowResponse> getFollowList(Member member);
    List<MemberResponse.FollowResponse> getFollowerList(Member member);
    void follow(Member member , Long id);
    void unfollow(Member member , Long id);
}
