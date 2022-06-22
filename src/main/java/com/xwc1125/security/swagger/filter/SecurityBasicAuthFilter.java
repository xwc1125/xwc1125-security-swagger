/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.xwc1125.security.swagger.filter;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

/***
 *
 * @since:swagger-bootstrap-ui 1.9.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2019/02/02 19:55
 */
public class SecurityBasicAuthFilter extends BasicFilter implements Filter {

    private Environment environment;
    /***
     * 是否开启basic验证,默认开启
     */
    private boolean enableBasicAuth = true;

    private String userName;

    private String password;

    private String token;

    public SecurityBasicAuthFilter(Environment environment, boolean enableBasicAuth, String userName, String password) {
        this.environment = environment;
        this.enableBasicAuth = enableBasicAuth;
        this.userName = userName;
        this.password = password;
        this.token = getAuthToken(userName, password);
    }

    public boolean isEnableBasicAuth() {
        return enableBasicAuth;
    }

    public void setEnableBasicAuth(boolean enableBasicAuth) {
        this.enableBasicAuth = enableBasicAuth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        //SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
        if (enumeration.hasMoreElements()) {
            setUserName(filterConfig.getInitParameter("userName"));
            setPassword(filterConfig.getInitParameter("password"));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //针对swagger资源请求过滤
        if (enableBasicAuth) {
            if (match(environment, servletRequest.getRequestURI())) {
                //判断Session中是否存在
                Object swaggerSessionValue = servletRequest.getSession().getAttribute(SwaggerUiBasicAuthSession);
                if (swaggerSessionValue != null && swaggerSessionValue.toString().equals(this.token)) {
                    chain.doFilter(request, response);
                } else {
                    //匹配到,判断auth
                    //获取请求头Authorization
                    String auth = servletRequest.getHeader("Authorization");
                    if (auth == null || "".equals(auth)) {
                        writeForbiddenCode(httpServletResponse);
                        return;
                    }
                    String userAndPass = decodeBase64(auth.substring(6));
                    String[] upArr = userAndPass.split(":");
                    if (upArr.length != 2) {
                        writeForbiddenCode(httpServletResponse);
                    } else {
                        String iptUser = upArr[0];
                        String iptPass = upArr[1];
                        //匹配服务端用户名及密码
                        if (iptUser.equals(userName) && iptPass.equals(password)) {
                            servletRequest.getSession().setAttribute(SwaggerUiBasicAuthSession, this.token);
                            chain.doFilter(request, response);
                        } else {
                            writeForbiddenCode(httpServletResponse);
                            return;
                        }
                    }
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private String getAuthToken(String userName, String pwd) {
        return md5(userName + pwd);
    }

    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    private void writeForbiddenCode(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(401);
        httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"input Swagger Basic userName & password \"");
        httpServletResponse.getWriter().write("You do not have permission to access this resource");
    }

    @Override
    public void destroy() {

    }
}
