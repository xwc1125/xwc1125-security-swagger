package com.xwc1125.security.swagger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description:
 * @Author: xwc1125
 * @Copyright Copyright@2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@EnableConfigurationProperties({SwaggerProperties.class})
@Configuration
@Import({SpringdocConfiguration.class,
        SpringDocConfiguration.class, SpringDocConfigProperties.class,
        SwaggerConfig.class, SwaggerUiConfigProperties.class,
        SwaggerUiConfigParameters.class, SwaggerUiOAuthProperties.class,
        SpringDocWebMvcConfiguration.class, MultipleOpenApiSupportConfiguration.class})
public @interface EnableSwaggerSecurity {

}
