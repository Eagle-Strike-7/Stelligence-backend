package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginRequest;
import goorm.eagle7.stelligence.common.login.dto.LoginTokensResponse;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SignUpService signUpService;

	public LoginTokensResponse login(LoginRequest loginRequest) {

		// socialId로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		Member member = memberRepository.findByNickname(loginRequest.getNickname())
			.orElseGet(() -> signUpService.signUp(loginRequest.getNickname())); //TODO getSocialId()

		// socialId가 중복이면 로그인


		// token 생성 후 저장
		return generateAndSaveTokens(member);
	}

	/**
	 * 토큰 생성 후 저장
	 * @param member 회원
	 * @return 토큰
	 */

	private LoginTokensResponse generateAndSaveTokens(Member member) {

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
		String socialTypeToken = jwtTokenProvider.createSocialTypeToken(member.getSocialType());

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

		return
			LoginTokensResponse.of(
				accessToken,
				refreshToken,
				socialTypeToken
			);

	}

}
