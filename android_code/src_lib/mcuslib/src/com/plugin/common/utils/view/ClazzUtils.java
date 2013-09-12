package com.plugin.common.utils.view;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClazzUtils {

	public static Object getField(String fieldName,Class clazz ,Object object){
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			return null;
		}
	}
	public static String getFields(Class clazz,Object object){
		try {
			StringBuilder builder = new StringBuilder();
			Field[] fs = clazz.getDeclaredFields();
			for(Field f:fs){
				f.setAccessible(true);
				builder.append(f.getName()+"="+f.get(object)+"\r\n");
			}
			return builder.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static void setField(String fieldName,Class clazz ,Object object,Object values){
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(object,values);
		} catch (Exception e) {
		}
	}
	
	
	public static Object invokeMethod(String methodName,Class clazz ,Object object,Object[] args){
		try {
			Object o = null;
			Method[] ms  = clazz.getDeclaredMethods();
			for(Method m:ms){
				if(m.getName().equals(methodName)){
					m.setAccessible(true);
					o = m.invoke(object, args);
					return o;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
