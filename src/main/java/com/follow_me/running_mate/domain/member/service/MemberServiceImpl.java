package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.domain.member.repository.MemberRepository;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    @Override
    public String signup(MemberRequest.SignUpRequest request) {

        // TODO: 이메일, 닉네임 중복 체크
        Member member = memberMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        return memberRepository.save(member).getEmail();
    }

    @Override
    public void logout(String email) {
        tokenRepository.deleteById(email);
    }

    @Override
    @Transactional
    public void withdraw(Member member) {
        tokenRepository.deleteById(member.getEmail());
        member.delete();
        memberRepository.save(member);
    }
}
