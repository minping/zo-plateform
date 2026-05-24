package com.common.json;

import cn.hutool.json.JSONException;
import com.common.json.kit.JSONKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class TypeUtils {


    private static final Pattern NUMBER_WITH_TRAILING_ZEROS_PATTERN = Pattern.compile("\\.0*$");

    public static <R> R cast(Object value, Class<R> clz) {
        return cast(value, clz, null, null);
    }

    public static <R> R cast(Object value, Class<R> clz, R defaultVal) {
        return cast(value, clz, null, defaultVal);
    }

    public static <R> R castFun(Object value, Class<R> clz, Function<Object, ? extends R> defaultFun) {
        return cast(value, clz, defaultFun, null);
    }

    static <R> R cast(Object value, Class<R> clz, Function<Object, ? extends R> defaultFun, R defaultVal) {
        R r = null;
        switch (clz.getName()) {
            case "java.lang.Integer":
            case "int":
                r = (R) castToInt(value);
                break;
            case "java.math.BigDecimal":
                r = (R) castToBigDecimal(value);
                break;
            case "java.math.BigInteger":
                r = (R) castToBigInteger(value);
                break;
            case "java.lang.Boolean":
            case "boolean":
                r = (R) castToBoolean(value);
                break;
            case "java.util.Date":
                r = (R) castToDate(value);
                break;
            case "java.sql.Date":
                r = (R) castToSqlDate(value);
                break;
            case "java.lang.Double":
            case "double":
                r = (R) castToDouble(value);
                break;
            case "java.lang.Float":
            case "float":
                r = (R) castToFloat(value);
                break;
            case "java.lang.Long":
            case "long":
                r = (R) castToLong(value);
                break;
            case "java.lang.String":
                r = (R) castToString(value);
                break;
            case "java.sql.Timestamp":
                r = (R) castToTimestamp(value);
                break;
            case "java.sql.Time":
                r = (R) castToTime(value);
                break;
            default:
                if (defaultFun != null) {
                    defaultFun.apply(value);
                } else {
                    r = (R) value;
                }
        }
        if (defaultFun == null && Objects.isNull(r)) {
            return defaultVal;
        }
        return r;
    }

    public static Time castToTime(Object value) {
        if (value != null && String.class.isAssignableFrom(value.getClass())) {
            String v = (String) value;
            if ("".equals(v.trim())) {
                return null;
            }
            return Time.valueOf(v);
        }
        return (Time) value;
    }

    public static String castToString(Object value) {
        return value == null ? null : (value instanceof String ? value.toString() : JSONKit.object2Json(value));
    }

    public static BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else {
            String strVal = value.toString();
            if (strVal.length() == 0) {
                return null;
            } else {
                return value instanceof Map && ((Map) value).size() == 0 ? null : new BigDecimal(strVal);
            }
        }
    }

    public static BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else if (!(value instanceof Float) && !(value instanceof Double)) {
            if (value instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) value;
                int scale = decimal.scale();
                if (scale > -1000 && scale < 1000) {
                    return ((BigDecimal) value).toBigInteger();
                }
            }
            String strVal = value.toString();
            return strVal.length() != 0 && !"null".equals(strVal) && !"NULL".equals(strVal) ? new BigInteger(strVal) : null;
        } else {
            return BigInteger.valueOf(((Number) value).longValue());
        }
    }

    public static Float castToFloat(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() != 0 && !"null".equals(strVal) && !"NULL".equals(strVal)) {
                if (strVal.indexOf(44) != -1) {
                    strVal = strVal.replaceAll(",", "");
                }

                return Float.parseFloat(strVal);
            } else {
                return null;
            }
        } else {
            throw new JSONException("can not cast to float, value : " + value);
        }
    }

    public static Double castToDouble(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() != 0 && !"null".equals(strVal) && !"NULL".equals(strVal)) {
                if (strVal.indexOf(44) != -1) {
                    strVal = strVal.replaceAll(",", "");
                }

                return Double.parseDouble(strVal);
            } else {
                return null;
            }
        } else {
            throw new JSONException("can not cast to double, value : " + value);
        }
    }

    public static Date castToDate(Object value) {
        return castToDate(value, (String) null);
    }

    public static Date castToDate(Object value, String format) {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        } else {
            long longValue = -1L;
            if (value instanceof BigDecimal) {
                longValue = longValue((BigDecimal) value);
                return new Date(longValue);
            } else if (value instanceof Number) {
                longValue = ((Number) value).longValue();
                if ("unixtime".equals(format)) {
                    longValue *= 1000L;
                }

                return new Date(longValue);
            } else {
                if (value instanceof String) {
                    String strVal = (String) value;
                    if (strVal.indexOf(45) > 0 || strVal.indexOf(43) > 0) {
                        if (format == null) {
                            String datetimePattern = "yyyy-MM-dd HH:mm:ss";
                            if (strVal.length() == datetimePattern.length()) {
                                format = datetimePattern;
                            } else if (strVal.length() == 10) {
                                format = "yyyy-MM-dd";
                            } else if (strVal.length() == 29 && strVal.charAt(26) == ':' && strVal.charAt(28) == '0') {
                                format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
                            } else if (strVal.length() == 23 && strVal.charAt(19) == ',') {
                                format = "yyyy-MM-dd HH:mm:ss,SSS";
                            } else {
                                format = "yyyy-MM-dd HH:mm:ss.SSS";
                            }
                        }
                        return formatStr2Date(strVal, format);
                    }else if(JSONKit.isBlank(strVal)){
                        return null;
                    }
                    throw new JSONException("date value format is error [" + strVal + "]");
                }
                throw new JSONException("date value is not String [" + value + "]");
            }
        }
    }

    /**
     * 转换指定格式的字符串为时间
     *
     * @param str
     * @param pattern
     * @return
     */
    private static Date formatStr2Date(String str, String pattern) {
        Date d = null;
        if (null != str && !str.trim().equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                d = sdf.parse(str);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return d;
    }

    public static java.sql.Date castToSqlDate(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        } else if (value instanceof Date) {
            return new java.sql.Date(((Date) value).getTime());
        } else if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        } else {
            long longValue = 0L;
            if (value instanceof BigDecimal) {
                longValue = longValue((BigDecimal) value);
            } else if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            }

            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.length() == 0 || "null".equals(strVal) || "NULL".equals(strVal)) {
                    return null;
                } else if (Pattern.matches("\\d{4}-\\d{1,2}-\\d{1,2}", strVal.trim())) {
                    return java.sql.Date.valueOf(strVal.trim());
                }

                if (isNumber(strVal)) {
                    longValue = Long.parseLong(strVal);
                } else {
                    throw new JSONException("can not cast to Timestamp, value : " + strVal);
                }
            }

            if (longValue <= 0L) {
                throw new JSONException("can not cast to Date, value : " + value);
            } else {
                return new java.sql.Date(longValue);
            }
        }
    }

    public static long longValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0L;
        } else {
            int scale = decimal.scale();
            return scale >= -100 && scale <= 100 ? decimal.longValue() : decimal.longValueExact();
        }
    }

    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch != '+' && ch != '-') {
                if (ch < '0' || ch > '9') {
                    return false;
                }
            } else if (i != 0) {
                return false;
            }
        }

        return true;
    }

    public static Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof BigDecimal) {
            return intValue((BigDecimal) value) == 1;
        } else if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        } else if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() != 0 && !"null".equals(strVal) && !"NULL".equals(strVal)) {
                if (!"true".equalsIgnoreCase(strVal) && !"1".equals(strVal)) {
                    if (!"false".equalsIgnoreCase(strVal) && !"0".equals(strVal)) {
                        if (!"Y".equalsIgnoreCase(strVal) && !"T".equals(strVal)) {
                            if (!"F".equalsIgnoreCase(strVal) && !"N".equals(strVal)) {
                                throw new JSONException("can not cast to boolean, value : " + value);
                            } else {
                                return Boolean.FALSE;
                            }
                        } else {
                            return Boolean.TRUE;
                        }
                    } else {
                        return Boolean.FALSE;
                    }
                } else {
                    return Boolean.TRUE;
                }
            } else {
                return null;
            }
        } else {
            throw new JSONException("can not cast to boolean, value : " + value);
        }
    }

    public static int intValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        } else {
            int scale = decimal.scale();
            return scale >= -100 && scale <= 100 ? decimal.intValue() : decimal.intValueExact();
        }
    }

    public static Integer castToInt(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof BigDecimal) {
            return intValue((BigDecimal) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() != 0 && !"null".equals(strVal) && !"NULL".equals(strVal)) {
                if (strVal.indexOf(44) != -1) {
                    strVal = strVal.replaceAll(",", "");
                }

                Matcher matcher = NUMBER_WITH_TRAILING_ZEROS_PATTERN.matcher(strVal);
                if (matcher.find()) {
                    strVal = matcher.replaceAll("");
                }

                return Integer.parseInt(strVal);
            } else {
                return null;
            }
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else {
            if (value instanceof Map) {
                Map map = (Map) value;
                if (map.size() == 2 && map.containsKey("andIncrement") && map.containsKey("andDecrement")) {
                    Iterator iter = map.values().iterator();
                    iter.next();
                    Object value2 = iter.next();
                    return castToInt(value2);
                }
            }

            throw new JSONException("can not cast to int, value : " + value);
        }
    }

    public static Long castToLong(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return longValue((BigDecimal) value);
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.length() == 0 || "null".equals(strVal) || "NULL".equals(strVal)) {
                    return null;
                }

                if (strVal.indexOf(44) != -1) {
                    strVal = strVal.replaceAll(",", "");
                }

                try {
                    return Long.parseLong(strVal);
                } catch (NumberFormatException var4) {
                    throw new JSONException("can not cast to long, value : " + value);
                }
            }

            if (value instanceof Map) {
                Map map = (Map) value;
                if (map.size() == 2 && map.containsKey("andIncrement") && map.containsKey("andDecrement")) {
                    Iterator iter = map.values().iterator();
                    iter.next();
                    Object value2 = iter.next();
                    return castToLong(value2);
                }
            }

            throw new JSONException("can not cast to long, value : " + value);
        }
    }

    public static Timestamp castToTimestamp(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Calendar) {
            return new Timestamp(((Calendar) value).getTimeInMillis());
        } else if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        } else {
            long longValue = 0L;
            if (value instanceof BigDecimal) {
                longValue = longValue((BigDecimal) value);
            } else if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            }

            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.length() == 0 || "null".equals(strVal) || "NULL".equals(strVal)) {
                    return null;
                }

                if (strVal.endsWith(".000000000")) {
                    strVal = strVal.substring(0, strVal.length() - 10);
                } else if (strVal.endsWith(".000000")) {
                    strVal = strVal.substring(0, strVal.length() - 7);
                }

                if (strVal.length() > 20 && strVal.charAt(4) == '-' && strVal.charAt(7) == '-' && strVal.charAt(10) == ' ' && strVal.charAt(13) == ':' && strVal.charAt(16) == ':' && strVal.charAt(19) == '.') {
                    int year = num(strVal.charAt(0), strVal.charAt(1), strVal.charAt(2), strVal.charAt(3));
                    int month = num(strVal.charAt(5), strVal.charAt(6));
                    int day = num(strVal.charAt(8), strVal.charAt(9));
                    int hour = num(strVal.charAt(11), strVal.charAt(12));
                    int minute = num(strVal.charAt(14), strVal.charAt(15));
                    int second = num(strVal.charAt(17), strVal.charAt(18));
                    int diff = 29 - strVal.length();
                    if (diff > 0) {
                        strVal += String.format("%0" + diff + "d", 0);
                    }
                    int nanos = num(strVal.charAt(20), strVal.charAt(21), strVal.charAt(22), strVal.charAt(23), strVal.charAt(24), strVal.charAt(25), strVal.charAt(26), strVal.charAt(27), strVal.charAt(28));
                    return new Timestamp(year - 1900, month - 1, day, hour, minute, second, nanos);
                }

                if (isNumber(strVal)) {
                    longValue = Long.parseLong(strVal);
                } else {
                    throw new JSONException("can not cast to Timestamp, value : " + value);
                }
            }

            if (longValue <= 0L) {
                throw new JSONException("can not cast to Timestamp, value : " + value);
            } else {
                return new Timestamp(longValue);
            }
        }
    }

    static int num(char c0, char c1) {
        return c0 >= '0' && c0 <= '9' && c1 >= '0' && c1 <= '9' ? (c0 - 48) * 10 + (c1 - 48) : -1;
    }

    static int num(char c0, char c1, char c2, char c3) {
        return c0 >= '0' && c0 <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9' && c3 >= '0' && c3 <= '9' ? (c0 - 48) * 1000 + (c1 - 48) * 100 + (c2 - 48) * 10 + (c3 - 48) : -1;
    }

    static int num(char c0, char c1, char c2, char c3, char c4, char c5, char c6, char c7, char c8) {
        return c0 >= '0' && c0 <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9' && c3 >= '0' && c3 <= '9' && c4 >= '0' && c4 <= '9' && c5 >= '0' && c5 <= '9' && c6 >= '0' && c6 <= '9' && c7 >= '0' && c7 <= '9' && c8 >= '0' && c8 <= '9' ? (c0 - 48) * 100000000 + (c1 - 48) * 10000000 + (c2 - 48) * 1000000 + (c3 - 48) * 100000 + (c4 - 48) * 10000 + (c5 - 48) * 1000 + (c6 - 48) * 100 + (c7 - 48) * 10 + (c8 - 48) : -1;
    }
}
