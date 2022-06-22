package com.xwc1125.security.swagger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: springdoc open api 文档：https://springdoc.org/#properties
 * @Author: xwc1125
 * @Date: 2022/6/20 10:09
 * @Copyright Copyright@2022
 */
@ConfigurationProperties("swagger")
public class SwaggerProperties {

    /**
     * 是否开启swagger
     */
    private Boolean enabled = false;
    /**
     * 应用信息（title，desc，contact等）
     */
    private Info info = new Info();
    /**
     * 扩展文档
     */
    private ExternalDocumentation externalDoc = new ExternalDocumentation();

    private Map<String, SecurityScheme> securitySchemes = new TreeMap<>();
    private Map<String, Header> headers = new TreeMap<>();
    private Map<String, Parameter> parameters = new TreeMap<>();

    /**
     * 接口调试列表
     */
    private List<Server> servers = new ArrayList<>();

    public SwaggerProperties() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public ExternalDocumentation getExternalDoc() {
        return externalDoc;
    }

    public void setExternalDoc(ExternalDocumentation externalDoc) {
        this.externalDoc = externalDoc;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return securitySchemes;
    }

    public void setSecuritySchemes(
            Map<String, SecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }

    public Map<String, Header> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Header> headers) {
        this.headers = headers;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
