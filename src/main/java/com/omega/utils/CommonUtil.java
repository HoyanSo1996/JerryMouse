package com.omega.utils;

/**
 * Class CalculateUtil
 *
 * @author KennySo
 * @date 2023/12/22
 */
public class CommonUtil {

    private final static String[] resourceTypeArr = {"html", "css", "js", "png"};

    public static int parseValue(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println(value + " 不可以转换.");
        }
        return defaultValue;
    }

    public static boolean isStaticResource(String postfix) {
        for (String resourceType : resourceTypeArr) {
            if (resourceType.equals(postfix)) {
                return true;
            }
        }
        return false;
    }
}
