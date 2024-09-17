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
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.report.CommentReportRepository;
import goorm.eagle7.stelligence.domain.report.DocumentReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportBadgeMatchedCountTemplateTest {

	@Mock
	private DocumentReportRepository documentReportRepository;

	@Mock
	private CommentReportRepository commentReportRepository;

	@InjectMocks
	private ReportBadgeMatchedCountTemplate template;

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@BeforeEach
	void setUp() {
		requiredCounts.put(10, Badge.GUARD);
	}

	@Test
	@DisplayName("[성공] REPORT인 경우, true 반환 - supports")
	void supports() {

		// given

		// when
		boolean result = template.supports(BadgeCategory.REPORT);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[성공] REPORT 외, false 반환 - supports")
	void supportsFalse() {

		// given

		// when
		boolean resultContributeMerged = template.supports(BadgeCategory.CONTRIBUTE_MERGED);
		boolean resultContributeRejected = template.supports(BadgeCategory.CONTRIBUTE_REJECTED);
		boolean resultContributeAll = template.supports(BadgeCategory.CONTRIBUTE_ALL);
		boolean resultDocument = template.supports(BadgeCategory.DOCUMENT);

		// then
		assertThat(resultContributeMerged).isFalse();
		assertThat(resultContributeRejected).isFalse();
		assertThat(resultContributeAll).isFalse();
		assertThat(resultDocument).isFalse();

	}

	@Test
	@DisplayName("[성공] documentReportRepository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCountDocument() {
		// given
		long memberId = 1;
		long countDocument = 10;
		doReturn(countDocument)
			.when(documentReportRepository)
			.countByReporterId(memberId);
		doReturn(0L)
			.when(commentReportRepository)
			.countByReporterId(memberId);

		// when
		long result = template.getCount(1L);

		// then
		assertThat(result)
			.isEqualTo(countDocument);
		verify(documentReportRepository, times(1)).countByReporterId(memberId);
		verify(commentReportRepository, times(1)).countByReporterId(memberId);

	}

	@Test
	@DisplayName("[성공] commentReportRepository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCountComment() {

		// given
		long memberId = 1;
		long countComment = 10;
		doReturn(0L)
			.when(documentReportRepository)
			.countByReporterId(memberId);
		doReturn(countComment)
			.when(commentReportRepository)
			.countByReporterId(memberId);

		// when
		long result = template.getCount(1L);

		// then
		assertThat(result)
			.isEqualTo(countComment);
		verify(documentReportRepository, times(1)).countByReporterId(memberId);
		verify(commentReportRepository, times(1)).countByReporterId(memberId);

	}

	@Test
	@DisplayName("[성공] document,commentReportRepository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount() {

		// given
		long memberId = 1;
		long countDocument = 3;
		long countComment = 10;
		doReturn(countDocument)
			.when(documentReportRepository)
			.countByReporterId(memberId);
		doReturn(countComment)
			.when(commentReportRepository)
			.countByReporterId(memberId);

		// when
		long result = template.getCount(1L);

		// then
		assertThat(result)
			.isEqualTo(countComment+countDocument);
		verify(documentReportRepository, times(1)).countByReporterId(memberId);
		verify(commentReportRepository, times(1)).countByReporterId(memberId);

	}

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

	@Test
	@DisplayName("[성공] count, document 합산, count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCount() {

		// given
		long count = 10;
		Member member = member(1L, "nickname");
		ReportBadgeMatchedCountTemplate spyTemplate = spy(template);
		doReturn(count).when(spyTemplate).getCount(1L);
		doReturn(requiredCounts).when(spyTemplate).getBadgeCriteria();

		// when
		Optional<Badge> badge = spyTemplate.getBadgeWithCount(member);

		// then
		assertThat(badge)
			.isEqualTo(Optional.of(Badge.GUARD));

	}

}