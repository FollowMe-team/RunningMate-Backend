package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.service.record.CourseRecordService;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.domain.member.repository.MemberBadgeRepository;
import com.follow_me.running_mate.domain.member.repository.MemberFollowRepository;
import com.follow_me.running_mate.domain.member.repository.MemberLocationRepository;
import com.follow_me.running_mate.domain.member.repository.MemberRepository;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import com.follow_me.running_mate.global.error.code.CommonErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public MemberResponse.MyProfileResponse getMyProfile(String email) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND)); // 회원이 없으면 예외 처리

        // Member 엔티티를 MyProfileResponse DTO로 변환하여 반환
        return memberMapper.toMyProfileResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse.UpdateMyProfileResponse updateProfile(MemberRequest.UpdateProfileRequest request, String email) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND)); // 회원이 없으면 예외 처리
        //이전과 동일해 바꿀필요가 없는경우
        if (request.getNickname().equals(member.getNickname()) || request.getBirth() == member.getBirth() || request.getGender() == member.getGender()) {
            throw new CustomException(MemberErrorCode.NO_CHANGES_DETECTED);
        }
        // 프로필 정보 업데이트
        member.updateProfile(
                request.getNickname(),
                request.getGender(),
                request.getBirth()
        );
        // 변경된 정보를 저장
        memberRepository.save(member);

        return memberMapper.toUpdateMyProfileResponse(member);
        //TODO: 이미지 변경도 추가하기
    }

    @Override
    @Transactional
    public void changePassword(MemberRequest.ChangePasswordRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));
        //현재 비밀번호가 일치하는지
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(MemberErrorCode.INCORRECT_CURRENT_PASSWORD);
        }
        //현재 비밀번호와 변경할 비밀번호가 일치하는지
        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new CustomException(MemberErrorCode.SAME_AS_CURRENT_PASSWORD);
        }
        // 새 비밀번호와 확인용 비밀번호가 일치하는지 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(MemberErrorCode.PASSWORDS_DO_NOT_MATCH);
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

    //상대방 프로필 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse.MyProfileResponse getMemberProfileByEmail(String email) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND)); // 회원이 없으면 예외 처리

        // Member 엔티티를 MyProfileResponse DTO로 변환하여 반환
        //TODO: Mapper를 따로 만들어 상대방이 볼 수 있는 정보를 분리하기
        return memberMapper.toMyProfileResponse(member);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse.CourseRecordInfo> getMemberRunningRecords(Member member, LocalDate date) {
        return courseRecordService.getRecordsByDate(member, date);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse.FollowResponse> getFollowList(Member member) {
        List<Member> followMemberList = memberFollowRepository.findActiveFollowedsByFollower(member);

        return followMemberList.stream()
                .map(memberMapper::toFollowResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse.FollowResponse> getFollowerList(Member member) {
        List<Member> followerMemberList = memberFollowRepository.findActiveFollowersByFollowed(member);

        return followerMemberList.stream()
                .map(memberMapper::toFollowResponse)
                .toList();
    }
    @Transactional
    public void follow(Member member, Long targetMemberId) {

        // 자기 자신을 팔로우하려는 경우 예외 처리
        if (member.getId().equals(targetMemberId)) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        // 팔로우 대상 사용자 조회
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.ENTITY_NOT_FOUND));

        // 기존 팔로우 여부 확인
        MemberFollow existingFollow = memberFollowRepository.findByFollowerAndFollowed(member, targetMember)
                .orElse(null);

        if (existingFollow != null) {
            if (!existingFollow.getIsActive()) {
                existingFollow.setActive(true);

                targetMember.incrementFollowerCount();
                member.incrementFollowingCount();
                memberRepository.save(member);
            }

        } else {
            MemberFollow newFollow = MemberFollow.builder()
                    .follower(member)
                    .followed(targetMember)
                    .isActive(true)
                    .build();
            memberFollowRepository.save(newFollow);
            targetMember.incrementFollowerCount();
            member.incrementFollowingCount();
            memberRepository.save(member);
        }
    }

    @Transactional
    public void unfollow(Member currentMember, Long targetMemberId) {
        // 자기 자신을 언팔로우하려는 경우 예외 처리
        if (currentMember.getId().equals(targetMemberId)) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE, "자기 자신과의 팔로우 상태를 변경할 수 없습니다.");
        }

        // 팔로우 대상 사용자 조회
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.ENTITY_NOT_FOUND, "팔로우 대상 사용자가 존재하지 않습니다."));

        // 기존 팔로우 관계 확인
        MemberFollow existingFollow = memberFollowRepository.findByFollowerAndFollowed(currentMember, targetMember)
                .orElseThrow(() -> new CustomException(CommonErrorCode.ENTITY_NOT_FOUND, "팔로우 관계가 존재하지 않습니다."));

        if (existingFollow.getIsActive()) {

            existingFollow.setActive(false);

            targetMember.decrementFollowerCount();
            currentMember.decrementFollowingCount();
            memberRepository.save(currentMember);
            existingFollow.delete();
        }
    }
}

