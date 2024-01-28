package goorm.eagle7.stelligence.domain.member;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.dto.MemberBadgesResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMiniProfileResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMyPageResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Badge;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final String EXCEPTION_MSG = "해당 멤버가 없습니다. id="; // TODO static

	/**
	 * 회원의 정보를 조회합니다.
	 * @param memberId 회원 id
	 * @return MemberMyPageResponse (닉네임, 이메일, 프로필 사진, 가입한 소셜 타입)
	 */
	@Override
	public MemberMyPageResponse getMyPageById(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + memberId)
		);
		return MemberMyPageResponse.from(member);
	}

	/**
	 * 회원 탈퇴 요청시 회원을 삭제합니다.
	 * @param memberId 회원 id
	 */
	@Override
	@Transactional
	public void delete(Long memberId) {
		memberRepository.deleteById(memberId);
	}

	/**
	 * 회원의 닉네임을 수정합니다.
	 * 이때, 닉네임이 존재하는지를 먼저 조회해 이미 사용 중인 닉네임이면 예외를 발생시킵니다.
	 * 이후, 사용중이지 않다면 Id로 멤버를 조회해 해당 닉네임으로 닉네임을 변경합니다.
	 * @param memberId 회원 id
	 * @param memberUpdateNicknameRequest 수정할 닉네임
	 */
	@Override
	@Transactional
	public void updateNickname(Long memberId, MemberUpdateNicknameRequest memberUpdateNicknameRequest) {

		String nickname = memberUpdateNicknameRequest.getNickname();
		if (memberRepository.existsByNickname(nickname)) {
			// 이미 사용 중인 닉네임이면 예외 발생
			throw new BaseException("이미 사용 중인 닉네임입니다. nickname=" + nickname);
		}
		// 사용 중이지 않은 닉네임이면 닉네임 변경
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + memberId)
		);
		member.updateNickname(nickname);
	}

	/**
	 * 회원의 mini profile을 조회합니다.
	 * mini profile이란, 현재는 오른쪽 상단에 보이는 닉네임과 프로필 사진을 의미합니다.
	 * @param memberId 회원 id
	 * @return MemberMiniProfileResponse (닉네임, 프로필 사진)
	 */
	@Override
	public MemberMiniProfileResponse getMiniProfileById(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + memberId));
		return MemberMiniProfileResponse.of(
			member.getNickname(),
			member.getImageUrl()
		);
	}

	/**
	 * 회원의 badge 목록을 조회합니다.
	 * @param memberId 회원 id
	 * @return MemberBadgesResponse (badge 목록)
	 */
	@Override
	public List<MemberBadgesResponse> getBadgesById(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + memberId));
		Set<Badge> badges = member.getBadges();
		return badges.stream()
			.map(MemberBadgesResponse::from)
			.toList();
	}

}