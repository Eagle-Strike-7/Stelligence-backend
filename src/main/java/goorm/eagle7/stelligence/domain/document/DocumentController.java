package goorm.eagle7.stelligence.domain.document;

import static goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponseOuterClass.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentGraphResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 문서 관련 API를 제공하는 컨트롤러입니다.
 * * 문서 생성
 * * 문서 버전 별 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

	private final DocumentService documentService;

	@Operation(summary = "문서 생성", description = "문서를 생성합니다")
	@ApiResponse(
		responseCode = "200",
		description = "문서 생성 성공",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = DocumentResponse.class)
		)
	)
	@PostMapping
	public ResponseTemplate<DocumentResponse> createDocument(
		@RequestBody DocumentCreateRequest documentCreateRequest
	) {
		log.trace("requested /api/documents body={}", documentCreateRequest);
		return ResponseTemplate.ok(documentService.createDocument(documentCreateRequest));
	}

	@Operation(summary = "문서 조회", description = "문서의 내용을 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "문서 조회 성공",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = DocumentResponse.class)
		)
	)
	@GetMapping("/{documentId}")
	public ResponseTemplate<DocumentResponse> getDocument(
		@Parameter(description = "조회할 문서의 ID", example = "1")
		@PathVariable Long documentId,
		@Parameter(description = "문서의 특정 버전을 가져올 수 있습니다. 전달되지 않는 경우 기본값으로 최신본을 반환합니다", example = "1")
		@RequestParam(required = false) Long revision
	) {
		log.trace("requested /api/documents/{}?revision={}", documentId, revision);

		//revision이 null인 경우는 service에서 최신값을 찾아 반환하도록 되어있습니다.
		return ResponseTemplate.ok(documentService.getDocumentContent(documentId, revision));
	}

	@GetMapping
	public ResponseTemplate<DocumentGraphResponse> getDocumentGraph(
		@RequestParam(value = "documentId", required = false) Long documentId,
		@RequestParam(value = "depth", defaultValue = "0") int depth
	) {
		log.trace("requested /api/documents?documentId={}&depth={}", documentId, depth);

		// documentId가 null이라면 최상위 문서로부터 조회합니다.
		return ResponseTemplate.ok(documentService.getDocumentGraph(documentId, depth));
	}
}
