package goorm.eagle7.stelligence.domain.badge.template;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.EnumMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.impl.ContributeAllBadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.impl.ContributeMergedBadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.impl.ContributeRejectedBadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.impl.DocumentBadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.impl.ReportBadgeMatchedCountTemplate;


class BadgeTemplateMatcherTest {

	private BadgeTemplateMatcher badgeTemplateMatcher;

	@BeforeEach
	void setUp() {

		ContributeAllBadgeMatchedCountTemplate allBadgeMatchedCountTemplate = mock(
			ContributeAllBadgeMatchedCountTemplate.class);
		ContributeMergedBadgeMatchedCountTemplate mergedBadgeMatchedCountTemplate = mock(
			ContributeMergedBadgeMatchedCountTemplate.class);
		ContributeRejectedBadgeMatchedCountTemplate rejectedBadgeMatchedCountTemplate = mock(
			ContributeRejectedBadgeMatchedCountTemplate.class);
		DocumentBadgeMatchedCountTemplate documentBadgeMatchedCountTemplate = mock(
			DocumentBadgeMatchedCountTemplate.class);
		ReportBadgeMatchedCountTemplate reportBadgeMatchedCountTemplate = mock(ReportBadgeMatchedCountTemplate.class);

		List<BadgeMatchedCountTemplate> templates
			= List.of(
			allBadgeMatchedCountTemplate,
			mergedBadgeMatchedCountTemplate,
			rejectedBadgeMatchedCountTemplate,
			documentBadgeMatchedCountTemplate,
			reportBadgeMatchedCountTemplate
		);

		badgeTemplateMatcher = new BadgeTemplateMatcher(templates);


		EnumMap<BadgeCategory, Object> templateMap = new EnumMap<>(BadgeCategory.class);
		templateMap.put(BadgeCategory.CONTRIBUTE_ALL, allBadgeMatchedCountTemplate);
		templateMap.put(BadgeCategory.CONTRIBUTE_MERGED, mergedBadgeMatchedCountTemplate);
		templateMap.put(BadgeCategory.CONTRIBUTE_REJECTED, rejectedBadgeMatchedCountTemplate);
		templateMap.put(BadgeCategory.DOCUMENT, documentBadgeMatchedCountTemplate);
		templateMap.put(BadgeCategory.REPORT, reportBadgeMatchedCountTemplate);

		ReflectionTestUtils.setField(badgeTemplateMatcher, "templateMap", templateMap);


	}

	@Test
	@DisplayName("[성공] BadgeCategory에 해당하는 BadgeTemplate 반환 - findTemplate")
	void findTemplate() {

		// given
		// when
		BadgeMatchedCountTemplate resultContributeMerged = badgeTemplateMatcher.findTemplate(
			BadgeCategory.CONTRIBUTE_MERGED);
		BadgeMatchedCountTemplate resultContributeRejected = badgeTemplateMatcher.findTemplate(
			BadgeCategory.CONTRIBUTE_REJECTED);
		BadgeMatchedCountTemplate resultContributeAll = badgeTemplateMatcher.findTemplate(BadgeCategory.CONTRIBUTE_ALL);
		BadgeMatchedCountTemplate resultDocument = badgeTemplateMatcher.findTemplate(BadgeCategory.DOCUMENT);
		BadgeMatchedCountTemplate resultReport = badgeTemplateMatcher.findTemplate(BadgeCategory.REPORT);

		// then
		assertThat(resultContributeMerged).isInstanceOf(ContributeMergedBadgeMatchedCountTemplate.class);
		assertThat(resultContributeRejected).isInstanceOf(ContributeRejectedBadgeMatchedCountTemplate.class);
		assertThat(resultContributeAll).isInstanceOf(ContributeAllBadgeMatchedCountTemplate.class);
		assertThat(resultDocument).isInstanceOf(DocumentBadgeMatchedCountTemplate.class);
		assertThat(resultReport).isInstanceOf(ReportBadgeMatchedCountTemplate.class);

	}

}