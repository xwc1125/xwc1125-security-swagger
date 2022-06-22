/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.xwc1125.security.swagger.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/***
 *
 * @since:swagger-bootstrap-ui 1.9.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2019/01/18 17:15
 */
public class ProductionSecurityFilter extends BasicFilter implements Filter {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /***
     * 是否生产环境,如果是生成环境,过滤Swagger的相关资源请求
     */
    private Environment environment;
    private boolean production = true;
    private boolean swaggerEnabled = false;

    public ProductionSecurityFilter(Environment environment, boolean production, boolean swaggerEnabled) {
        this.environment = environment;
        this.production = production;
        this.swaggerEnabled = swaggerEnabled;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public boolean isSwaggerEnabled() {
        return swaggerEnabled;
    }

    public void setSwaggerEnabled(boolean swaggerEnabled) {
        this.swaggerEnabled = swaggerEnabled;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //判断filterConfig
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        //SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
        if (enumeration.hasMoreElements()) {
            setProduction(Boolean.valueOf(filterConfig.getInitParameter("production")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (isProduction()) {
            String uri = httpServletRequest.getRequestURI();
            if (!match(environment, uri)) {
                chain.doFilter(request, response);
            } else {
                if (!isSwaggerEnabled()) {
                    httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                    httpServletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    // 返回值编码 ，utf-8,不设置可能出现乱码
                    httpServletResponse.setCharacterEncoding(UTF_8.toString());
                    httpServletResponse.getWriter().write(HttpStatus.NOT_FOUND.getReasonPhrase());
                } else {
                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    httpServletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    // 返回值编码 ，utf-8,不设置可能出现乱码
                    httpServletResponse.setCharacterEncoding(UTF_8.toString());
                    httpServletResponse.getWriter().write("You do not have permission to access this page");
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

}
