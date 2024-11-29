package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.CourseRecord;
import com.follow_me.running_mate.domain.course.service.record.CourseRecordService;
import com.follow_me.running_mate.domain.enums.FootprintType;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import com.follow_me.running_mate.domain.member.entity.MemberFootprint;
import com.follow_me.running_mate.domain.member.entity.MemberLocation;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.domain.member.repository.MemberBadgeRepository;
import com.follow_me.running_mate.domain.member.repository.MemberFollowRepository;
import com.follow_me.running_mate.domain.member.repository.MemberFootprintRepository;
import com.follow_me.running_mate.domain.member.repository.MemberLocationRepository;
import com.follow_me.running_mate.domain.member.repository.MemberRepository;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.time.YearMonth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final CourseRecordService courseRecordService;
    private final S3ImageService s3ImageService;

    private final MemberFollowRepository memberFollowRepository;
    private final MemberLocationRepository memberLocationRepository;
    private final MemberFootprintRepository memberFootprintRepository;

    @Override
    public void signup(MemberRequest.SignUpRequest request, MultipartFile profileImage) {

        Member member = memberMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));

        if (profileImage != null) {
            String profileImageUrl = s3ImageService.upload(profileImage);
            member.updateProfileImage(profileImageUrl);
        }

        Member savedMember = memberRepository.save(member);
        memberLocationRepository.save(memberMapper.toMemberLocation(request.getLocationInfo(), savedMember));
    }

    @Override
    public void logout(String email) {
        tokenRepository.deleteById(email);
    }

    @Override
    @Transactional
    public void withdraw(Member member) {
        tokenRepository.deleteById(member.getEmail()); //토큰 삭제
        member.delete();
        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse.MyProfileResponse getMyProfile(Member member) {

        Optional<MemberLocation> memberLocation = memberLocationRepository.findByMember(member);
        String address = memberLocation.map(MemberLocation::getAddress).orElse(null);

        List<CourseRecord> recordsByMember = courseRecordService.getRecordsByMember(member);

        return memberMapper.toMyProfileResponse(
            member,
            address,
            calculateTotalDistance(recordsByMember),
            (long) recordsByMember.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse.MyProfileSummaryResponse getMyProfileSummary(Member member) {
        Optional<MemberLocation> memberLocation = memberLocationRepository.findByMember(member);

        return memberMapper.toMyProfileSummaryResponse(
            member,
            memberLocation
        );
    }

    @Override
    @Transactional
    public MemberResponse.UpdateMyProfileResponse updateProfile(
        Member member, MemberRequest.UpdateProfileRequest request, MultipartFile profileImage
    ) {
        member.updateProfile(request);

        if (profileImage != null) {
            // 기존 이미지 삭제
            String oldProfileImageUrl = member.getProfileImageUrl();
            s3ImageService.deleteImageFromS3(oldProfileImageUrl);

            // 새로운 이미지로 교체
            String profileImageUrl = s3ImageService.upload(profileImage);
            member.updateProfileImage(profileImageUrl);
        }

        Member savedMember = memberRepository.save(member);

        // 거주지 정보 업데이트
        memberLocationRepository.findByMember(savedMember)
                .ifPresent(memberLocation -> {
                    memberLocation.updateLocation(
                        request.getIntroduce(), FormatterUtil.formatPoint(request.getLocationInfo())
                    );
                    memberLocationRepository.save(memberLocation);
                });

        return new MemberResponse.UpdateMyProfileResponse(savedMember.getId());
    }

    @Override
    @Transactional
    public void changePassword(MemberRequest.ChangePasswordRequest request, Member member) {

        //현재 비밀번호가 일치하는지
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(MemberErrorCode.INCORRECT_CURRENT_PASSWORD);
        }
        //현재 비밀번호와 변경할 비밀번호가 일치하는지
        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new CustomException(MemberErrorCode.SAME_AS_CURRENT_PASSWORD);
        }
        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);
    }

    //배지 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse.BadgeListResponse getMemberBadges(Member member) {

        // 회원의 배지 목록 조회
        List<MemberBadge> memberBadges = memberBadgeRepository.findByMember(member);

        return new MemberResponse.BadgeListResponse(
                memberMapper.toBadgeResponses(memberBadges)
        );
    }

    //닉네임 중복 확인
    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    //이메일 중복 확인
    @Override
    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    //타인 프로필 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse.OtherProfileResponse getOtherProfile(Member member, Long memberId) {

        Member otherMember = memberRepository.getMember(memberId);

        // 내 프로필 조회 시 예외 처리
        if (otherMember.getId().equals(member.getId())) {
            throw new CustomException(MemberErrorCode.INVALID_PROFILE_API);
        }

        List<CourseRecord> recordsByMember = courseRecordService.getRecordsByMember(otherMember);

        // TODO: 같은 크루 소속 여부 체크

        return memberMapper.toOtherProfileResponse(
            otherMember,
            calculateTotalDistance(recordsByMember),
            (long) recordsByMember.size(),
            false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseRecordInfoList getMyCourseRecords(Member member, YearMonth yearMonth) {

        return courseRecordService.getRecordsByMonth(member, yearMonth, true);
    }

    @Override
    public CourseResponse.CourseRecordInfoList getOtherCourseRecords(
        Member member, Long memberId, YearMonth yearMonth
    ) {
        if (member.getId().equals(memberId)) {
            throw new CustomException(MemberErrorCode.INVALID_COURSE_RECORD_API);
        }

        Member otherMember = memberRepository.getMember(memberId);

        return courseRecordService.getRecordsByMonth(otherMember, yearMonth, false);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse.FollowingListResponse getFollowingList(Member member) {
        List<Member> followingMembers = memberFollowRepository.findFollowingByFollower(member);

        return new MemberResponse.FollowingListResponse(
                followingMembers.stream()
                        .map(memberMapper::toFollowResponse)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse.FollowerListResponse getFollowerList(Member member) {
        List<Member> followerMembers = memberFollowRepository.findFollowerByFollowing(member);

        return new MemberResponse.FollowerListResponse(
                followerMembers.stream()
                        .map(memberMapper::toFollowResponse)
                        .toList()
        );
    }

    @Override
    @Transactional
    public void follow(Member member, Long targetMemberId) {

        // 자기 자신을 팔로우하려는 경우 예외 처리
        validateSelfFollow(member, targetMemberId);

        // 팔로우 대상 사용자 조회
        Member targetMember = findTargetMember(targetMemberId);

        // 기존 팔로우 여부 확인
        MemberFollow existingFollow = memberFollowRepository.findByFollowerAndFollowing(member, targetMember)
                .orElse(memberMapper.toMemberFollow(member, targetMember));

        if (existingFollow.getIsActive()) {
            throw new CustomException(MemberErrorCode.ALREADY_FOLLOWING);
        }

        existingFollow.setActive(true);
        targetMember.incrementFollowerCount();
        member.incrementFollowingCount();
        memberRepository.save(member);
        memberFollowRepository.save(existingFollow);
    }

    @Override
    @Transactional
    public void unfollow(Member member, Long targetMemberId) {
        // 자기 자신을 언팔로우하려는 경우 예외 처리
        validateSelfFollow(member, targetMemberId);

        // 팔로우 대상 사용자 조회
        Member targetMember = findTargetMember(targetMemberId);

        // 기존 팔로우 관계 확인
        MemberFollow existingFollow = memberFollowRepository.findByFollowerAndFollowing(member, targetMember)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOLLOWING));

        if (!existingFollow.getIsActive()) {
            throw new CustomException(MemberErrorCode.NOT_FOLLOWING);
        }

        existingFollow.setActive(false);
        targetMember.decrementFollowerCount();
        member.decrementFollowingCount();
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void createFootprint(Member member, Long memberId, MemberRequest.FootprintRequest request) {
        validateSelfFollow(member, memberId);
        Member targetMember = findTargetMember(memberId);

        memberFootprintRepository.save(
            memberMapper.toMemberFootprint(member, targetMember, request)
        );
        updateFootprint(targetMember, request.getType());
    }

    private void validateSelfFollow(Member member, Long targetMemberId) {
        if (member.getId().equals(targetMemberId)) {
            throw new CustomException(MemberErrorCode.NOT_SELF_TARGET);
        }
    }

    private Member findTargetMember(Long targetMemberId) {
        return memberRepository.findById(targetMemberId)
            .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND_TARGET));
    }

    public Double calculateTotalDistance(List<CourseRecord> records) {
        return records.isEmpty() ? 0L : records.stream()
            .mapToDouble(CourseRecord::getDistance)
            .sum();
    }

    private void updateFootprint(Member targetMember, FootprintType type) {
        if (type == FootprintType.GOOD) {
            targetMember.incrementFootprint();
        } else if (type == FootprintType.BAD) {
            targetMember.decrementFootprint();
        }
    }
}

