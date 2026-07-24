package com.jfinal.kit;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class StringKit {
    public static final String UTF8 = "UTF-8";

    /**
     * 32位uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * md5 加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(str.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        int length = md5code.length();
        for (int i = 0; i < 32 - length; i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static String leftFill(String str, int length, String fillStr) {
        return fill(str, length, fillStr, true);
    }

    public static String rightFill(String str, int length, String fillStr) {
        return fill(str, length, fillStr, false);
    }

    /**
     * 左右补全字符
     *
     * @param str     待补全的字符
     * @param length  预计补全后的字符长度
     * @param fillStr 补全符
     * @param isLeft  是否左补全
     * @return
     */
    public static String fill(String str, int length, String fillStr, boolean isLeft) {
        if (str == null) {
            return null;
        }
        int strLength = str.length();
        StringBuffer sb = new StringBuffer();
        while (strLength < length) {
            sb.append(fillStr);
            strLength++;
        }
        return isLeft ? sb.toString() + str : str + sb.toString();
    }


    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 字符串为 null 或者内部字符全部为 ' ' '\t' '\n' '\r' 这四类字符时返回 true
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            switch (str.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // case '\b':
                    // case '\f':
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }


    public static String toCamelCase(String stringWithUnderline) {
        if (stringWithUnderline.indexOf('_') == -1) {
            return stringWithUnderline;
        }

        stringWithUnderline = stringWithUnderline.toLowerCase();
        char[] fromArray = stringWithUnderline.toCharArray();
        char[] toArray = new char[fromArray.length];
        int j = 0;
        for (int i = 0; i < fromArray.length; i++) {
            if (fromArray[i] == '_') {
                // 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
                i++;
                if (i < fromArray.length) {
                    toArray[j++] = Character.toUpperCase(fromArray[i]);
                }
            } else {
                toArray[j++] = fromArray[i];
            }
        }
        return new String(toArray, 0, j);
    }

    public static String join(String delimiter, Object... objs) {
        return join(delimiter, true, objs);
    }

    /**
     * 将对象使用指定的分隔符转换成一个字符串，
     *
     * @param delimiter 分隔符
     * @param ignore    为true忽略null值 和空字符串
     * @param objs      分隔对象
     * @return
     */
    public static String join(String delimiter, boolean ignore, Object... objs) {
        if (objs == null || 0 == objs.length) {
            return "";
        }
        StringBuffer bf = new StringBuffer();
        Object obj;
        boolean flag = true;
        for (int i = 0; i < objs.length; i++) {
            obj = objs[i];
            if ((obj == null || isBlank(obj.toString())) && true == ignore) {
                continue;
            } else {
                if (flag) {
                    bf.append(obj);
                    flag = false;
                } else {
                    bf.append(delimiter).append(obj);
                }
            }
        }
        return bf.toString();
    }

    public static String join(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        for (String s : stringArray) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String join(String[] stringArray, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(stringArray[i]);
        }
        return sb.toString();
    }

    public static String join(List<String> lStrs, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lStrs.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(lStrs.get(i));
        }
        return sb.toString();
    }

    public static boolean slowEquals(String a, String b) {
        byte[] aBytes = (a != null ? a.getBytes() : null);
        byte[] bBytes = (b != null ? b.getBytes() : null);
        return HashKit.slowEquals(aBytes, bBytes);
    }

    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }


    public static String base64Decoder(String base64Str) {
        return new String(Base64.getDecoder().decode(base64Str));
    }

    public static String base64Encoder(String base64Str) {
        try {
            return new String(Base64.getEncoder().encode(base64Str.getBytes(UTF8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(String strPattern, Object... argArray) {
        if (isNotBlank(strPattern) && (argArray != null && argArray.length != 0)) {
            int strPatternLength = strPattern.length();
            StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
            int handledPosition = 0;

            for (int argIndex = 0; argIndex < argArray.length; ++argIndex) {
                int delimIndex = strPattern.indexOf("{}", handledPosition);
                if (delimIndex == -1) {
                    if (handledPosition == 0) {
                        return strPattern;
                    }

                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }

                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == '\\') {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == '\\') {
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(argArray[argIndex]);
                        handledPosition = delimIndex + 2;
                    } else {
                        --argIndex;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append('{');
                        handledPosition = delimIndex + 1;
                    }
                } else {
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(argArray[argIndex]);
                    handledPosition = delimIndex + 2;
                }
            }

            sbuf.append(strPattern, handledPosition, strPattern.length());
            return sbuf.toString();
        } else {
            return strPattern;
        }
    }

    /**
     * 字符串 URLEncode
     *
     * @param content 字符内容
     * @param enc     字符编码
     * @return
     */
    public static String urlEncode(String content, String enc) {

        if (isBlank(content)) return content;
        try {
            return URLEncoder.encode(content, enc);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 字符串 URLEncode 默认UTF-8
     *
     * @param content 字符内容
     * @return
     * @description
     */
    public static String urlEncode(String content) {
        return urlEncode(content, UTF8);
    }

    /**
     * 解码
     *
     * @param content
     * @return
     */
    public static String urlDecode(String content) {
        return urlDecode(content, UTF8);
    }

    public static String urlDecode(String content, String enc) {
        try {
            content = URLDecoder.decode(content, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * 判断字符串是否为数字
     *
     * @param sStr
     * @return
     */
    public static boolean isNumber(String sStr) {
        try {
            double v = Double.valueOf(sStr);
        } catch (Exception e) {
            //异常 说明包含非数字。
            return false;
        }
        return true;
    }

    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str != null && suffix != null) {
            if (suffix.length() > str.length()) {
                return false;
            } else {
                int strOffset = str.length() - suffix.length();
                return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
            }
        } else {
            return str == null && suffix == null;
        }
    }


    /**
     * SHA1加密
     *
     * @param decript 加密原字符串
     * @return
     */
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
