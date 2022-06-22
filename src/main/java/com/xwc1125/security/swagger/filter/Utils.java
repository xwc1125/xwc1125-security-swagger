package com.xwc1125.security.swagger.filter;

import java.net.InetAddress;
import java.util.Base64;

/**
 * @Description:
 * @Author: xwc1125
 * @Copyright Copyright@2022
 */
public class Utils {

    public static String initPassword() {
        String pwd = SwaggerConstants.Default_Password;
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            if (hostAddress != null && !hostAddress.isEmpty()) {
                pwd = hostAddress;
            }
        } catch (Exception e) {
        }
        return Base64.getEncoder().encodeToString(pwd.getBytes());
    }
}
