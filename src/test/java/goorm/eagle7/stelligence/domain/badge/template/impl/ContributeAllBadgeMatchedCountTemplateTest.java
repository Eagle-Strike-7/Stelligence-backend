package goorm.eagle7.stelligence.domain.badge.template.impl;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static goorm.eagle7.stelligence.domain.badge.model.Badge.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class ContributeAllBadgeMatchedCountTemplateTest {

	@Mock
	private ContributeRepository contributeRepository;
	@InjectMocks
	private ContributeAllBadgeMatchedCountTemplate template;

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@Test
	@DisplayName("[성공] CONTRIBUTE_ALL인 경우, true 반환 - supports")
	void supports() {
		// given

		// when
		boolean result = template.supports(BadgeCategory.CONTRIBUTE_ALL);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_ALL 외, false 반환 - supports")
	void supportsFalse() {
		// given

		// when
		boolean resultContributeMerged = template.supports(BadgeCategory.CONTRIBUTE_MERGED);
		boolean resultContributeRejected = template.supports(BadgeCategory.CONTRIBUTE_REJECTED);
		boolean resultDocument = template.supports(BadgeCategory.DOCUMENT);
		boolean resultReport = template.supports(BadgeCategory.REPORT);

		// then
		assertThat(resultContributeMerged).isFalse();
		assertThat(resultContributeRejected).isFalse();
		assertThat(resultDocument).isFalse();
		assertThat(resultReport).isFalse();

	}

	/** getCount **/
	@Test
	@DisplayName("[성공] repository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount() {
		// given
		Member member = member(1L, "nickname");
		when(contributeRepository.countByMemberId(member.getId())).thenReturn(1L);

		// when
		long count = template.getCount(member.getId());

		// then
		assertThat(count).isEqualTo(1L);
		verify(contributeRepository, times(1)).countByMemberId(member.getId());

	}

	@Test
	@DisplayName("[확인] 0개인 경우, repository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount0() {
		// given
		Member member = member(1L, "nickname");
		when(contributeRepository.countByMemberId(member.getId())).thenReturn(0L);

		// when
		long count = template.getCount(member.getId());

		// then
		assertThat(count).isZero();
		verify(contributeRepository, times(1)).countByMemberId(member.getId());

	}

	/** getBadgeCriteria **/

	@Test
	@DisplayName("[성공] map에 저장된 배지 조건 얻기 - getBadgeCriteria")
	void getBadgeCriteria() {
		// given
		requiredCounts.put(1, MERCURY);
		requiredCounts.put(5, VENUS);
		requiredCounts.put(10, NEPTUNE);
		requiredCounts.put(30, SUN);
		requiredCounts.put(50, GALAXY);

		// when
		Map<Integer, Badge> badgeCriteria = template.getBadgeCriteria();

		// then
		assertThat(badgeCriteria).isEqualTo(requiredCounts);

	}

	
	/** getBadgeWithCount **/
	@Test
	@DisplayName("[성공] 0개, count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountUnder() {

		// given
		ContributeAllBadgeMatchedCountTemplate spyTemplate = spy(template);
		Member member = member(1L, "nickname");
		when(spyTemplate.getCount(member.getId())).thenReturn(0L);

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(spyTemplate.getBadgeWithCount(member)).isEmpty();
		assertThat(badge).isEmpty();
		// verify(spyTemplate, times(1)).getCount(member.getId()); // TODO 왜 2번이지?
		// verify(spyTemplate, times(1)).getBadgeCriteria();

	}

	@Test
	@DisplayName("[성공] 1개(충족), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountExact() {
		// given
		ContributeAllBadgeMatchedCountTemplate spyTemplate = spy(template);
		Member member = member(1L, "nickname");
		when(spyTemplate.getCount(member.getId())).thenReturn(1L);
		when(spyTemplate.getBadgeCriteria()).thenReturn(requiredCounts);
		requiredCounts.put(1, MERCURY);
		requiredCounts.put(5, VENUS);
		requiredCounts.put(10, NEPTUNE);
		requiredCounts.put(30, SUN);
		requiredCounts.put(50, GALAXY);

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge)
			.isPresent()
			.contains(MERCURY);
		verify(spyTemplate, times(1)).getCount(member.getId());
		verify(spyTemplate, times(1)).getBadgeCriteria();
	}

	@Test
	@DisplayName("[성공] 4개(사잇값), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountMid() {
		// given
		ContributeAllBadgeMatchedCountTemplate spyTemplate = spy(template);
		Member member = member(1L, "nickname");
		when(spyTemplate.getCount(member.getId())).thenReturn(4L);
		when(spyTemplate.getBadgeCriteria()).thenReturn(requiredCounts);
		requiredCounts.put(1, MERCURY);
		requiredCounts.put(5, VENUS);
		requiredCounts.put(10, NEPTUNE);
		requiredCounts.put(30, SUN);
		requiredCounts.put(50, GALAXY);

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge).isEmpty();
		verify(spyTemplate, times(1)).getCount(member.getId());
		verify(spyTemplate, times(1)).getBadgeCriteria();
	}

	@Test
	@DisplayName("[성공] 51개(초과), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountOver() {
		// given
		ContributeAllBadgeMatchedCountTemplate spyTemplate = spy(template);
		Member member = member(1L, "nickname");
		when(spyTemplate.getCount(member.getId())).thenReturn(51L);
		when(spyTemplate.getBadgeCriteria()).thenReturn(requiredCounts);
		requiredCounts.put(1, MERCURY);
		requiredCounts.put(5, VENUS);
		requiredCounts.put(10, NEPTUNE);
		requiredCounts.put(30, SUN);
		requiredCounts.put(50, GALAXY);

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge).isEmpty();
		verify(spyTemplate, times(1)).getCount(member.getId());
		verify(spyTemplate, times(1)).getBadgeCriteria();

	}

}