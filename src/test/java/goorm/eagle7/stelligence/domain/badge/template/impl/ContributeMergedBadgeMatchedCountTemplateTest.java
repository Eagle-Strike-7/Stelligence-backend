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
class ContributeMergedBadgeMatchedCountTemplateTest {

	@Mock
	private ContributeRepository contributeRepository;
	@InjectMocks
	private ContributeMergedBadgeMatchedCountTemplate template;

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@BeforeEach
	void setUp() {
		requiredCounts.put(1, Badge.JUPITER);
		requiredCounts.put(5, Badge.SATURN);
		requiredCounts.put(10, Badge.PLUTO);
		requiredCounts.put(50, Badge.ANDROMEDA);
	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_MERGED인 경우, true 반환 - supports")
	void supports() {
		// given

		// when
		boolean result = template.supports(BadgeCategory.CONTRIBUTE_MERGED);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_MERGED 외, false 반환 - supports")
	void supportsFalse() {

		// given

		// when
		boolean resultContributeAll = template.supports(BadgeCategory.CONTRIBUTE_ALL);
		boolean resultContributeRejected = template.supports(BadgeCategory.CONTRIBUTE_REJECTED);
		boolean resultDocument = template.supports(BadgeCategory.DOCUMENT);
		boolean resultReport = template.supports(BadgeCategory.REPORT);

		// then
		assertThat(resultContributeAll).isFalse();
		assertThat(resultContributeRejected).isFalse();
		assertThat(resultDocument).isFalse();
		assertThat(resultReport).isFalse();

	}

	/** getCount **/
	@Test
	@DisplayName("[성공] repository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount() {

		// given
		when(contributeRepository.countByMemberIdAndStatus(1L, ContributeStatus.MERGED)).thenReturn(10L);

		// when
		long result = template.getCount(1L);

		// then
		assertThat(result)
			.isInstanceOf(Long.class)
			.isEqualTo(10L);
		verify(contributeRepository, times(1)).countByMemberIdAndStatus(1L, ContributeStatus.MERGED);
		
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
			.isNotNull()
			.isNotEmpty()
			.isEqualTo(requiredCounts);

	}

	/** getBadgeWithCount **/
	@Test
	@DisplayName("[성공] 조건에 맞는 배지 반환 - getBadgeWithCount")
	void getBadgeWithCount() {

		// given
		Member member = member(1L, "nickname");
		ContributeMergedBadgeMatchedCountTemplate spyTemplate = spy(template);
		doReturn(1L).when(spyTemplate).getCount(member.getId());
		doReturn(requiredCounts).when(spyTemplate).getBadgeCriteria();

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge)
			.isInstanceOf(Optional.class)
			.isNotEmpty()
			.isEqualTo(Optional.of(Badge.JUPITER));

	}

}