package com.xwc1125.security.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2022/6/20 10:09
 * @Copyright Copyright@2022
 */
@Profile({"dev", "test"})// 对swagger文档配置只在测试环境可访问，生产环境不可访问。
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = false)
@EnableConfigurationProperties({SwaggerProperties.class})
public class SpringdocConfiguration implements WebMvcConfigurer {

    private final SwaggerProperties swaggerProperties;

    public SpringdocConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI springDocOpenAPI() {
        // 配置认证、请求头参数
        Components components = new Components();
        if (!swaggerProperties.getSecuritySchemes().isEmpty()) {
            for (Map.Entry<String, SecurityScheme> entry : swaggerProperties.getSecuritySchemes().entrySet()) {
                components.addSecuritySchemes(entry.getKey(), entry.getValue());
            }
        }
        if (!swaggerProperties.getHeaders().isEmpty()) {
            for (Map.Entry<String, Header> entry : swaggerProperties.getHeaders().entrySet()) {
                components.addHeaders(entry.getKey(), entry.getValue());
            }
        }
        if (!swaggerProperties.getParameters().isEmpty()) {
            for (Map.Entry<String, Parameter> entry : swaggerProperties.getParameters().entrySet()) {
                components.addParameters(entry.getKey(), entry.getValue());
            }
        }

        OpenAPI openAPI = new OpenAPI().components(components).servers(swaggerProperties.getServers())
                .info(new Info().title(swaggerProperties.getInfo().getTitle())
                        .description(swaggerProperties.getInfo().getDescription())
                        .version(swaggerProperties.getInfo().getVersion()).license(
                                new License().name(swaggerProperties.getInfo().getLicense().getName())
                                        .url(swaggerProperties.getInfo().getLicense().getUrl())));
        if (swaggerProperties.getExternalDoc() != null) {
            openAPI = openAPI.externalDocs(swaggerProperties.getExternalDoc());
        }
        return openAPI;
    }

    /**
     * 添加全局的请求头参数
     */
    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> {
                    String summary = operation.getSummary();
                    if (summary != null) {
                        if (!summary.equals("Logs user into the system")) {
                            if (!swaggerProperties.getParameters().isEmpty()) {
                                for (Map.Entry<String, Parameter> entry : swaggerProperties.getParameters()
                                        .entrySet()) {
                                    operation = operation.addParametersItem(
                                            new HeaderParameter().$ref("#/components/parameters/" + entry.getKey()));
                                }
                            }
                        }
                    } else {
                        if (!swaggerProperties.getParameters().isEmpty()) {
                            for (Map.Entry<String, Parameter> entry : swaggerProperties.getParameters().entrySet()) {
                                operation = operation.addParametersItem(
                                        new HeaderParameter().$ref("#/components/parameters/" + entry.getKey()));
                            }
                        }
                    }
                });
    }
//    /**
//     * 通用拦截器排除设置，所有拦截器都会自动加springdoc-opapi相关的资源排除信息，不用在应用程序自身拦截器定义的地方去添加，算是良心解耦实现。
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        try {
//            Field registrationsField = FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
//            List<InterceptorRegistration> registrations = (List<InterceptorRegistration>) ReflectionUtils.getField(
//                    registrationsField, registry);
//            if (registrations != null) {
//                for (InterceptorRegistration interceptorRegistration : registrations) {
//                    interceptorRegistration.excludePathPatterns("/springdoc**/**");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
