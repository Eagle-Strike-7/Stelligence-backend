package goorm.eagle7.stelligence.domain.contribute.event.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.NewContributeEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewContributeEventListener {

	private final BadgeService badgeService;
	private final ContributeRepository contributeRepository;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = NewContributeEvent.class)
	public void onContributeNew(NewContributeEvent event) {

		contributeRepository
			.findWithMember(event.contributeId())
			.ifPresent(contribute ->
				badgeService.checkAndAwardBadge(
					BadgeCategory.CONTRIBUTE_ALL
					, contribute.getMember())
			);

	}

}
