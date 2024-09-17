package goorm.eagle7.stelligence.domain.badge.template.impl;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class ContributeRejectedBadgeMatchedCountTemplateTest {

	@Mock
	private ContributeRepository contributeRepository;
	@InjectMocks
	private ContributeRejectedBadgeMatchedCountTemplate template;
	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@BeforeEach
	void setUp() {
		requiredCounts.put(100, Badge.BLACKHOLE);
	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_REJECTED인 경우, true 반환 - supports")
	void supports() {
		// given

		// when
		boolean result = template.supports(BadgeCategory.CONTRIBUTE_REJECTED);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_REJECTED 외, false 반환 - supports")
	void supportsFalse() {
		// given

		// when
		boolean resultContributeMerged = template.supports(BadgeCategory.CONTRIBUTE_MERGED);
		boolean resultContributeAll = template.supports(BadgeCategory.CONTRIBUTE_ALL);
		boolean resultDocument = template.supports(BadgeCategory.DOCUMENT);
		boolean resultReport = template.supports(BadgeCategory.REPORT);

		// then
		assertThat(resultContributeMerged).isFalse();
		assertThat(resultContributeAll).isFalse();
		assertThat(resultDocument).isFalse();
		assertThat(resultReport).isFalse();

	}

	/** getCount **/
	@Test
	@DisplayName("[성공] countByMemberIdAndStatus 호출 - getCount")
	void getCount() {

		// given
		Long memberId = 1L;
		long expectedCount = 10L;
		doReturn(expectedCount)
			.when(contributeRepository)
			.countByMemberIdAndStatus(memberId, ContributeStatus.REJECTED);

		// when
		long result = template.getCount(memberId);

		// then
		assertThat(result)
			.isEqualTo(expectedCount);
		verify(contributeRepository, times(1)).countByMemberIdAndStatus(memberId, ContributeStatus.REJECTED);

	}

	/** getBadgeCriteria **/
	@Test
	@DisplayName("[성공] 배지 조건 반환 - getBadgeCriteria")
	void getBadgeCriteria() {
		// given

		// when
		Map<Integer, Badge> result = template.getBadgeCriteria();

		// then
		assertThat(result)
			.isEqualTo(requiredCounts);
	}

	/** getBadgeWithCount **/
	@Test
	@DisplayName("[성공] 배지와 조건 개수 반환 - getBadgeWithCount")
	void getBadgeWithCount() {

		// given
		Member member = member(1L, "nickname");
		ContributeRejectedBadgeMatchedCountTemplate spyTemplate = spy(template);
		doReturn(100L).when(spyTemplate).getCount(member.getId());
		doReturn(requiredCounts).when(spyTemplate).getBadgeCriteria();

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge)
			.isInstanceOf(Optional.class)
			.isNotEmpty()
			.isEqualTo(Optional.of(Badge.BLACKHOLE));

	}

}

