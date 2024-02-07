package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.util.RandomUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member signUp(OAuth2Request oAuth2Request) {

		String nickname = oAuth2Request.getNickname();

		// 닉네임이 중복인지 확인, 중복이면 랜덤 닉네임 생성
		if (isNicknameDuplicate(nickname)) {
			nickname = generateUniqueNickname(nickname);
		}

		Member newMember = Member.of(
			oAuth2Request.getName(),
			nickname,
			oAuth2Request.getEmail(),
			oAuth2Request.getImageUrl(),
			oAuth2Request.getSocialId(),
			oAuth2Request.getSocialType()
		);

		// 해당 닉네임으로 저장
		return memberRepository.save(newMember);

	}

	// 닉네임 중복 확인 메서드
	private boolean isNicknameDuplicate(String nickname) {
		return memberRepository.existsByNicknameAndActiveTrue(nickname);
	}

	// 닉네임 생성 메서드
	public String generateUniqueNickname(String nickname) {
		while (isNicknameDuplicate(nickname)) {
			nickname = RandomUtils.createNicknameWithRandomNumber(nickname);
		}
		return nickname;
	}

}
