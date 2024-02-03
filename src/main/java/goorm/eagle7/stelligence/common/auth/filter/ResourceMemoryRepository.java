package goorm.eagle7.stelligence.common.auth.filter;

import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

/**
 * <h2>허용 리소스 정보 메모리 저장소</h2>
 * <p>인증이 필요한 리소스(httpMethod, uri) 정보를 Set으로 저장</p>
 * <p>인증이 필요한 리소스인지 확인하는 메소드(exist) 제공</p>
 */
@Repository
@NoArgsConstructor
class ResourceMemoryRepository {
	private static final Set<RequestResource> REQUEST_RESOURCES =
		Set.of(

			// application 권한
			RequestResource.of(HttpMethod.GET.name(), "/api/documents"),
			RequestResource.of(HttpMethod.GET.name(), "/api/contributes"),
			RequestResource.of(HttpMethod.GET.name(), "/api/comments"),
			RequestResource.of(HttpMethod.GET.name(), "/api/debates"),

			// oauth2
			RequestResource.of(HttpMethod.GET.name(), "/oauth2/authorization/google"),
			RequestResource.of(HttpMethod.GET.name(), "/login/oauth2/code/**"),

			// swagger
			RequestResource.of(HttpMethod.GET.name(), "/swagger-ui/**"),
			RequestResource.of(HttpMethod.GET.name(), "/swagger-resources/**"),
			RequestResource.of(HttpMethod.GET.name(), "/v3/api-docs/**"),

			// error
			RequestResource.of(HttpMethod.POST.name(), "/error/**"),

			// login - dev
			RequestResource.of(HttpMethod.POST.name(), "/api/login"),
			RequestResource.of(HttpMethod.GET.name(), "/api/oauth2/**")
		);

	/**
	 * <h2>허용하는 uri 확인</h2>
	 * <p>- 요청의 httpMethod, uri가 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인</p>
	 * <p>- 허용하는 uri가 /**로 끝나면, basePath 비교</p>
	 * @param requestResource 요청의 httpMethod, uri
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	public boolean exist(RequestResource requestResource) {

		for (RequestResource resource : REQUEST_RESOURCES) {
			// httpMethod가 다르면 다음 리소스로
			if (!resource.getHttpMethod().equals(requestResource.getHttpMethod())) {
				continue;
			}

			// uri가 다르면 다음 리소스로
			// 허용하는 uri가 /**로 끝나면 basePath만 비교
			if (resource.getUri().endsWith("/**")) {
				String basePath = resource.getUri().substring(0, resource.getUri().length() - 3);
				if (requestResource.getUri().startsWith(basePath)) {
					return true;
				}
			} else {
				// 그 외의 경우는 uri 전체 비교
				if (resource.getUri().equals(requestResource.getUri())) {
					return true;
				}
			}
		}
		return false;
	}
}
