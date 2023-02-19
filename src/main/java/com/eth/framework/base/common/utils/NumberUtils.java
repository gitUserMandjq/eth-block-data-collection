package com.eth.framework.base.common.utils;

import java.math.BigDecimal;

public class NumberUtils {
	/**
	 * 保留小数位
	 * @param d 数字
	 * @param i 保留小数位
	 * @return
	 */
	public static Double roundHalfUp(Double d,int i) {
		if(d == null) {
			return null;
		}
		if(d.isNaN()) {
			return 0.0;
		}
		if(d.isInfinite()) {
			return 0.0;
		}
        BigDecimal bg = new BigDecimal(d);
		return bg.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	/**
	 * 保留小数位 返回字符串
	 * @param d 数字
	 * @param i 保留小数位
	 * @return
	 */
	public static String roundHalfUpToString(Double d,int i) {
		if(d == null) {
			return null;
		}
		if(d.isNaN()) {
			return "0.0";
		}
		if(d.isInfinite()) {
			return "0.0";
		}
        return String.format("%."+i+"f", d);
	}
	public static String roundHalfUpOrIntegerToString(Double d,int i) {
		if(d == null) {
			return null;
		}
		if(d.isNaN()) {
			return "0.0";
		}
		if(d % 1.0 == 0) {
			return String.format("%.0f", d);
		}
        return String.format("%."+i+"f", d);
	}
	public static int convert(Integer i) {
		if(i == null) {
			return 0;
		}
		return i;
	}
	public static Integer intValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Integer.valueOf(num);
	}
	public static Integer intValueOf(Long num) {
		if(num == null) {
			return null;
		}
		return num.intValue();
	}
	public static Short shortValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Short.valueOf(num);
	}
	public static Long longValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Long.valueOf(num);
	}
	public static Integer intValueOf(String num, int i) {
		if(StringUtils.isEmpty(num)) {
			return i;
		}
		return Integer.valueOf(num);
	}
	public static Integer intValueOf16(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Integer.valueOf(num, 16);
	}
	public static Double doubleValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Double.valueOf(num);
	}
	public static Float floatValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return Float.valueOf(num);
	}
	public static BigDecimal decimalValueOf(String num) {
		if(StringUtils.isEmpty(num)) {
			return null;
		}
		return new BigDecimal(num);
	}
	public static Double doubleValueOf(Long num) {
		if(num == null) {
			return null;
		}
		return Double.valueOf(num);
	}
	public static Double doubleValueOf(String num, double i) {
		if(StringUtils.isEmpty(num)) {
			return i;
		}
		return Double.valueOf(num);
	}
	public static String getFileSize(Long fileSize) {
		if(fileSize == null) {
			return "";
		}
		if(fileSize < 1024) {
			return fileSize + "B";
		}else if(fileSize < 1024 * 1024){
			return roundHalfUpToString(fileSize*1.0/1024, 2) + "KB";
		}else if(fileSize < 1024 * 1024 * 1024) {
			return roundHalfUpToString(fileSize*1.0/1024/1024, 2) + "MB";
		}else {
			return roundHalfUpToString(fileSize*1.0/1024/1024/1024, 2) + "GB";
		}
	}

    public static Integer subtract(Integer inspectionSum, int i) {
		if (inspectionSum == null){
			return null;
		}
		return inspectionSum - i;
    }

	/**
	 * 判断 值是否大于
	 * @param num
	 * @param i
	 * @return
	 */
    public static boolean isGreater(Integer num, int i) {
		if (num == null){
			return false;
		}
		return num > i;
    }
}
