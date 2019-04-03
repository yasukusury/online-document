package org.yasukusury.onlinedocument.commons.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 30254
 * creadtedate:2018/8/20
 */
public class BoxTool {
    public static Integer[] box(int[] orgin){
        Integer[] result = new Integer[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static Long[] box(long[] orgin){
        Long[] result = new Long[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static Boolean[] box(boolean[] orgin){
        Boolean[] result = new Boolean[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static Double[] box(double[] orgin){
        Double[] result = new Double[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static Float[] box(float[] orgin){
        Float[] result = new Float[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static int[] unbox(Integer[] orgin){
        int[] result = new int[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static long[] unbox(Long[] orgin){
        long[] result = new long[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static boolean[] unbox(Boolean[] orgin){
        boolean[] result = new boolean[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static double[] unbox(Double[] orgin){
        double[] result = new double[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

    public static float[] unbox(Float[] orgin){
        float[] result = new float[orgin.length];
        for(int i=0;i<orgin.length;i++){
            result[i] = orgin[i];
        }
        return result;
    }

}
