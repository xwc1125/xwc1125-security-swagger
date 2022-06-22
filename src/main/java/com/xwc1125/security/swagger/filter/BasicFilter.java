/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.xwc1125.security.swagger.filter;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/***
 *
 * @since:swagger-bootstrap-ui 1.9.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2019/02/02 19:57
 */
public class BasicFilter {

    private Logger logger = LoggerFactory.getLogger(BasicFilter.class);
    /***
     * basic auth验证
     */
    String SwaggerUiBasicAuthSession = "SwaggerUiBasicAuthSession";

    protected List<Pattern> urlFilters = null;
    protected List<String> propertiesFilters = null;

    public BasicFilter() {
        urlFilters = new ArrayList<>();
        urlFilters.add(Pattern.compile(".*?/doc\\.html.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v2/api-docs.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v3/api-docs.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v3/api-docs/swagger-config.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v2/api-docs-ext.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-ui\\.html.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-ui/index.html.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources/configuration/ui.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources/configuration/security.*", Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/actuator.*", Pattern.CASE_INSENSITIVE));

        propertiesFilters = new ArrayList<>();
        propertiesFilters.add("springdoc.api-docs.path");
        propertiesFilters.add("springdoc.webjars.prefix");
        propertiesFilters.add("springdoc.swagger-ui.path");
        propertiesFilters.add("springdoc.swagger-ui.configUrl");
        propertiesFilters.add("springdoc.swagger-ui.oauth2RedirectUrl");
    }

    /**
     * 匹配uri是否为我们需要过滤的页面
     *
     * @param uri
     * @return
     */
    protected boolean match(Environment environment, String uri) {
        boolean match = false;
        if (uri != null) {
            for (Pattern pattern : getUrlFilters()) {
                if (pattern.matcher(uri).matches()) {
                    match = true;
                    break;
                }
            }
        }

        for (String propertyKey : getPropertiesFilters()) {
            try {
                String property = environment.getProperty(propertyKey);
                if (property != null && !property.isEmpty()) {
                    Pattern pattern = Pattern.compile(".*?" + property + ".*", Pattern.CASE_INSENSITIVE);
                    if (pattern.matcher(uri).matches()) {
                        match = true;
                        break;
                    }
                }
            } catch (Exception e) {

            }
        }

        return match;
    }

    protected String decodeBase64(String source) {
        String decodeStr = null;
        if (source != null) {
            try {
                byte[] bytes = Base64.getDecoder().decode(source);
                decodeStr = new String(bytes);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return decodeStr;
    }

    public List<Pattern> getUrlFilters() {
        return urlFilters;
    }

    public List<String> getPropertiesFilters() {
        return propertiesFilters;
    }

}
