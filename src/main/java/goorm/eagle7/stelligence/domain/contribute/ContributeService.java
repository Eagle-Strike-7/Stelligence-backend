package goorm.eagle7.stelligence.domain.contribute;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.contribute.dto.ContributeCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ContributeService {

	private final ContributeRepository contributeRepository;

	// TODO: 수정 요청 엔티티를 생성하고, 리포지토리에 save
	public void createContribute(ContributeCreateRequest contributeCreateRequest) {

	}

	// TODO: 멤버에 따라 수정 요청 엔티티 리스트를 찾아서, 적절히 반환
	public void getContributesByMember() {

	}

	// TODO: 문서에 따라 수정 요청 엔티티 리스트를 찾아서, 적절히 반환
	public void getContributesByDocument() {

	}

	// TODO: 수정 요청 id에 따라 수정 요청 엔티티를 찾아서, 적절히 반환
	public void getContributeById() {

	}

	// TODO: 수정 요청 id에 따라 수정 요청 엔티티를 찾아서 수정 요청을 철회 - 이때 기존 수정 요청은 상태를 pending으로 수정
	public void deleteContributesById() {

	}
}
