package com.example.backend.config;

import com.example.backend.config.filter.LoginFilter;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.file.Path;
import java.util.Optional;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    @Bean
    protected Info apiInfo() {
        return new Info()
                .title("Tech Up API")
                .description("Tech Up API")
                .version("0.0.1");
    }

    @Bean
    OpenApiCustomizer springSecurityLoginEndpointCustomizer(ApplicationContext applicationContext) {
        FilterChainProxy springSecurityFilterChain =
                applicationContext.getBean("springSecurityFilterChain", FilterChainProxy.class);

        return (openApi) -> {
            // 스프링 필터체인에서 필터들을 반복문을 돌린다.
            for (SecurityFilterChain filterChain : springSecurityFilterChain.getFilterChains()) {
                Optional<LoginFilter> loginFilter = filterChain.getFilters()
                        .stream()
                        .filter(LoginFilter.class::isInstance)
                        .map(LoginFilter.class::cast)
                        .findAny();

                if (loginFilter.isPresent()) {
                    // 로그인 입력값 스키마
                    Schema<?> loginSchema = new ObjectSchema()
                            .addProperty("email", new StringSchema().example("example@example.com"))
                            .addProperty("password", new StringSchema().example("asdf1234"));

                    // 로그인 API 추가
                    addApiPath(openApi, "/login", "POST", "로그인", loginSchema);
                }

                // 로그아웃 API 추가 (입력값 없음)
                addApiPath(openApi, "/logout", "GET", "로그아웃", null);
            }
        };
    }

    private void addApiPath(OpenAPI openApi, String path, String method, String summary, Schema<?> requestSchema) {
        Operation operation = new Operation();
        ApiResponses responses = new ApiResponses();

        // 성공/실패 응답 코드 설정
        responses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
                new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
        responses.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase()));

        operation.setResponses(responses);
        operation.addTagsItem("회원 기능");
        operation.summary(summary);

        // 입력값 스키마 설정 (있을 때만 추가)
        if (requestSchema != null) {
            RequestBody requestBody = new RequestBody().content(
                    new Content().addMediaType("application/json", new MediaType().schema(requestSchema))
            );
            operation.setRequestBody(requestBody);
        }

        // PathItem이 존재하지 않으면 새로 생성
        PathItem pathItem = openApi.getPaths().getOrDefault(path, new PathItem());

        // HTTP 메서드별 경로 추가
        if ("POST".equalsIgnoreCase(method)) {
            pathItem.post(operation);
        } else if ("GET".equalsIgnoreCase(method)) {
            pathItem.get(operation);
        }

        // Path 추가
        openApi.getPaths().addPathItem(path, pathItem);
    }

}
