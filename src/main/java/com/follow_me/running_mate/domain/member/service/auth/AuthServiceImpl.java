package com.follow_me.running_mate.domain.member.service.auth;


import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.domain.member.repository.MemberLocationRepository;
import com.follow_me.running_mate.domain.member.repository.MemberRepository;
import com.follow_me.running_mate.domain.member.repository.MemberWithdrawRepository;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenRepository tokenRepository;
    private final MemberWithdrawRepository memberWithdrawRepository;
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3ImageService s3ImageService;
    private final MemberLocationRepository memberLocationRepository;
    private final CrewService crewService;
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
    public void withdraw(Member member, MemberRequest.WithdrawRequest request) {
        memberWithdrawRepository.save(memberMapper.toMemberWithdraw(member, request));
        tokenRepository.deleteById(member.getEmail()); //토큰 삭제
        crewService.softDeleteCrew(member);
        member.delete();
        memberRepository.save(member);
    }
}
