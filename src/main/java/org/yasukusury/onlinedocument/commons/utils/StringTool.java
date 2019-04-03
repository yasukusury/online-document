package org.yasukusury.onlinedocument.commons.utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StringTool {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH-mm-ss");

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

    public static <T> String join(String delimiter, T[] arr, int left, int right) {
        if (arr.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(arr[left].toString());
        for (int i = left + 1; i < right && i < arr.length; i++) {
            sb.append(delimiter).append(arr[i].toString());
        }
        return sb.toString();
    }


    public static <T> String join(String delimiter, T... arr) {
        if (arr.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(arr[0].toString());
        for (int i = 1; i < arr.length; i++) {
            sb.append(delimiter).append(arr[i].toString());
        }
        return sb.toString();
    }

    public static <T> String join(String delimiter, List<T> list, int left, int right) {
        if (list.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(list.get(left).toString());
        for (int i = left + 1; i < right && i < list.size(); i++) {
            sb.append(delimiter).append(list.get(i).toString());
        }
        return sb.toString();
    }

    public static <T> String join(String delimiter, List<T> list) {
        if (list.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(list.get(0).toString());
        for (int i = 1; i < list.size(); i++) {
            sb.append(delimiter).append(list.get(i).toString());
        }
        return sb.toString();
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

    /**
     * 字符串为 null 或者为  "" 时返回 true
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim()) ? true : false;
    }

    /**
     * 字符串不为 null 而且不为  "" 时返回 true
     */
    public static boolean notBlank(String str) {
        return str == null || "".equals(str.trim()) ? false : true;
    }

    public static boolean notBlank(String... strings) {
        if (strings == null) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    public static boolean notNull(Object... paras) {
        if (paras == null) {
            return false;
        }
        for (Object obj : paras) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public static String[] int2String(int... ints) {
        String[] result = new String[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[i] = Integer.toString(ints[i]);
        }
        return result;
    }

    public static int[] string2Int(String ints) {
        String[] seg = ints.split(",");
        int[] result = new int[seg.length];
        for (int i = 0; i < seg.length; i++) {
            result[i] = Integer.valueOf(seg[i]);
        }
        return result;
    }

    public static List<Long> strings2LongList(String string) {
        List<Long> longs = new ArrayList<>();
        if (string == null || string.length() == 0) return longs;
        String[] ss = string.split(",");
        for (String s : ss) {
            longs.add(Long.valueOf(s));
        }
        return longs;
    }

    public static String longList2String(List<Long> longs) {
        return join(", ", longs);
    }

    public static synchronized String time2String(Timestamp time) {
        return sdf.format(time);
    }

}
