/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.xwc1125.security.swagger.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * description:
 * </p>
 *
 * @author: xwc1125
 * @copyright Copyright@2022
 */
@Configuration
public class SecurityAutoConfiguration {

    Logger logger = LoggerFactory.getLogger(SecurityAutoConfiguration.class);

    @Autowired
    private Environment environment;

    @Bean
    public ProductionSecurityFilter productionSecurityFilter() {
        return new ProductionSecurityFilter(environment, isProd(environment), swaggerEnable(environment));
    }

    @Bean
    public SecurityBasicAuthFilter securityBasicAuthFilter() {
        boolean enableSwaggerBasicAuth = true;
        String dftUserName = SwaggerConstants.Default_Username;
        String dftPass = Utils.initPassword();
        if (environment != null) {
            //如果开启basic验证,从配置文件中获取用户名和密码
            String pUser = environment.getProperty("swagger.basic.username");
            String pPass = environment.getProperty("swagger.basic.password");
            if (pUser != null && !"".equals(pUser)) {
                dftUserName = pUser;
            } else {
                logger.info("[swagger]default basic username：{}", dftUserName);
                System.out.println("[swagger]default basic username：" + dftUserName);
            }
            if (pPass != null && !"".equals(pPass)) {
                dftPass = pPass;
            } else {
                logger.info("[swagger]default basic password：{}", dftPass);
                System.out.println("[swagger]default basic password：" + dftPass);
            }
        }
        SecurityBasicAuthFilter securityBasicAuthFilter = new SecurityBasicAuthFilter(
                environment,
                enableSwaggerBasicAuth, dftUserName, dftPass);
        return securityBasicAuthFilter;
    }

    public boolean swaggerEnable(Environment environment) {
        if (environment != null) {
            String swaggerEnableBoolStr = environment.getProperty("swagger.enabled");
            if (swaggerEnableBoolStr == null || swaggerEnableBoolStr.isEmpty()) {
                return false;
            }
            return Boolean.valueOf(swaggerEnableBoolStr);
        }
        return false;
    }

    /**
     * 判断是否能显示swagger
     * 1、swagger.enabled为空，返回true，不能显示swagger
     * 2、swagger.enabled=true，返回true，不能显示swagger
     * 3、activeProfiles!=dev，返回true，不能显示swagger
     * 4、activeProfiles!=test，返回true，不能显示swagger
     *
     * @param environment
     * @return
     */
    public boolean isProd(Environment environment) {
        boolean swaggerEnable = swaggerEnable(environment);
        if (!swaggerEnable) {
            return true;
        }

        if (environment != null) {
            String[] activeProfiles = environment.getActiveProfiles();
            if (activeProfiles != null && activeProfiles.length > 0) {
                String prodStr = activeProfiles[0];
                if (logger.isDebugEnabled()) {
                    logger.debug("active profiles:{}", prodStr);
                }
                // 环境是dev/test
                if (prodStr.equalsIgnoreCase(
                        SwaggerConstants.ACTIVE_PROFILE_DEV)
                        || prodStr.equalsIgnoreCase(
                        SwaggerConstants.ACTIVE_PROFILE_TEST)) {
                    return false;
                }
            }
        }
        return true;
    }
}
