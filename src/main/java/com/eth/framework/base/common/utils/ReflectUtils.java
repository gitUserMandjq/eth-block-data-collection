package com.eth.framework.base.common.utils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


public class ReflectUtils {
	public static Pattern linePattern = Pattern.compile("_(\\w)");

	public static String getFieldColumn(Class clazz,String columnName) throws Exception {
    	Field[] field = clazz.getDeclaredFields();
    	for(Field f:field) {
    		boolean fieldHasAnno = f.isAnnotationPresent(Column.class);  
    		if(fieldHasAnno) {
    			Column column = f.getAnnotation(Column.class);
    			if(column.name().equals(columnName)) {
					return f.getName();
				}
    		}
    		String fieldName =f.getName();
    		if(fieldName.equals(columnName)) {
				return f.getName();
			}
    		fieldName = "get"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    		Method method = getDeclaredMethod(clazz, fieldName);
    		if(method == null) {
				continue;
			}
    		Column column = method.getAnnotation(Column.class);
    		if(column != null && column.name().equals(columnName)) {
				return f.getName();
			}
    	}
    	return "";
    }
	public static Method getDeclaredMethod(Class clazz,String methodName) {
		try {
			return clazz.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
		}
		return null;
	}
	public static <T> T setExcelField(T t, String columnName, Object o) throws Exception{
		String fieldName = getFieldColumn(t.getClass(), columnName);
		Field f = t.getClass().getDeclaredField(fieldName);
		Class<?> type = f.getType();
		f.setAccessible(true);
		if("Double".equals(type.getSimpleName()))
			f.set(t, StringUtils.ifnull(NumberUtils.doubleValueOf((String)o), 0.0));
		else if("Float".equals(type.getSimpleName())) {
			f.set(t, StringUtils.ifnull(NumberUtils.floatValueOf((String)o), 0.0f));
		} else if("Integer".equals(type.getSimpleName())) {
			f.set(t, StringUtils.ifnull(NumberUtils.intValueOf((String)o), 0));
		} else if("Long".equals(type.getSimpleName())) {
			f.set(t, StringUtils.ifnull(NumberUtils.longValueOf((String)o), 0L));
		} else if("BigDecimal".equals(type.getSimpleName())) {
			f.set(t, StringUtils.ifnull(NumberUtils.decimalValueOf((String)o), new BigDecimal(0)));
		} else if("Date".equals(type.getSimpleName())) {
			String value = (String)o;
			if(!StringUtils.isEmpty(value)) {
				String format = "yyyy/MM/dd HH:mm:ss";
				if(!value.contains(":")) {
					format = "yyyy/MM/dd";
				}
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				f.set(t, sdf.parse(value.replace("-", "/")));
			}
		}else {
			f.set(t, o);
		}
		return t;
	}
//	public static void main(String[] args) throws ParseException {
//		String value = "2020/11/18";
//		if(!StringUtils.isEmpty(value)) {
//			String format = "yyyy/MM/dd HH:mm:ss";
//			if(!value.contains(":")) {
//				format = "yyyy/MM/dd";
//			}
//			SimpleDateFormat sdf = new SimpleDateFormat(format);
//			System.out.println(sdf.parse(value.replace("-", "/")));
//		}
//	}
	public static <T> T setField(T t, String fieldName, Object o) throws Exception{
		Field f = t.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(t, o);
		return t;
	}
	public static <T> String getFieldName(T t, String fieldName) throws Exception {
		Field f = t.getClass().getDeclaredField(fieldName);
		Class<?> type = f.getType();
		return type.getName();
	}
	public static <T> boolean hasField(T t,String fieldName){
		if(StringUtils.isEmpty(fieldName)) {
			return false;
		}
		Field[] field = t.getClass().getDeclaredFields();
		for(Field f:field) {
			if(f.getName().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}
	public static <T> Object getField(T t, String fieldName) throws Exception {
		Field f = t.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(t);
	}

	public static <T> void checkLength(T t) throws Exception {
		Method[] methods = t.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Column annotation = m.getAnnotation(Column.class);
			if( null != annotation){
				if(m.getReturnType().isAssignableFrom(String.class)) {
					String value = (String)m.invoke(t);
					if(!StringUtils.isEmpty(value) && value.length() > annotation.length()) {
						throw new Exception("输入文字长度过长");
					}
				}
			}
		}
	}

	private static String getMethodName(String fildeName) throws Exception{
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}



	public static String getTableName(Class<?> clazz){
		Table annotation = clazz.getAnnotation(Table.class);
		return annotation.name();
	}



}
