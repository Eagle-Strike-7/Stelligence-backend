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
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class DocumentBadgeMatchedCountTemplateTest {

	@Mock
	private DocumentContentRepository documentContentRepository;

	@InjectMocks
	private DocumentBadgeMatchedCountTemplate template;

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@BeforeEach
	void setUp() {
		requiredCounts.put(1, Badge.ASTRONAUT);
		requiredCounts.put(5, Badge.MOON);
		requiredCounts.put(10, Badge.MARS);
		requiredCounts.put(20, Badge.URANUS);
	}

	@Test
	@DisplayName("[성공] DOCUMENT인 경우, true 반환 - supports")
	void supports() {
		// given

		// when
		boolean result = template.supports(BadgeCategory.DOCUMENT);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("[성공] DOCUMENT 외, false 반환 - supports")
	void supportsFalse() {
		// given

		// when
		boolean resultContributeMerged = template.supports(BadgeCategory.CONTRIBUTE_MERGED);
		boolean resultContributeAll = template.supports(BadgeCategory.CONTRIBUTE_ALL);
		boolean resultContributeRejected = template.supports(BadgeCategory.CONTRIBUTE_REJECTED);
		boolean resultReport = template.supports(BadgeCategory.REPORT);

		// then
		assertThat(resultContributeMerged).isFalse();
		assertThat(resultContributeAll).isFalse();
		assertThat(resultContributeRejected).isFalse();
		assertThat(resultReport).isFalse();
	}

	/** getCount **/
	@Test
	@DisplayName("[성공] countByAuthor_Id 호출 - getCount")
	void getCount() {

		// given
		Long memberId = 1L;
		Long expectedCount = 10L;
		doReturn(expectedCount).when(documentContentRepository).countByAuthor_Id(memberId);

		// when
		long count = template.getCount(memberId);

		// then
		assertThat(count).isEqualTo(expectedCount);

	}

	/** getBadgeCriteria **/
	@Test
	@DisplayName("[성공] requiredCounts 반환 - getBadgeCriteria")
	void getBadgeCriteria() {

		// given
		// when
		Map<Integer, Badge> result = template.getBadgeCriteria();

		// then
		assertThat(result).isEqualTo(requiredCounts);
	}

	/** getBadgeWithCount **/
	@Test
	@DisplayName("[성공] 배지와 조건 개수 반환 - getBadgeWithCount")
	void getBadgeWithCount() {

		// given
		long count = 10L;
		Member member = member(1L, "nickname");
		DocumentBadgeMatchedCountTemplate spyTemplate = spy(template);
		doReturn(count).when(spyTemplate).getCount(member.getId());
		doReturn(requiredCounts).when(spyTemplate).getBadgeCriteria();

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge)
			.isEqualTo(Optional.of(Badge.MARS));

	}

}