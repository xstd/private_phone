package com.plugin.common.utils.view;


import android.view.View;
import android.view.Window;

import java.lang.reflect.Field;

/**
 * @说明 视图和ID映射关系工具
 * @说明 解除了ID映射间的重复代码和类型装换, 这样可以把重心转移到业务逻辑 在继承上不侵入任何类
 * @使用 1.为了提高效率将需要映射的View控件可见域置为public 2.对于需要映射的对象中的属性导入:
 * {@link ViewMapping} 3.分离视图
 */
public final class ViewMapUtil {

    /**
     * @param object   要映射对象
     * @param rootView 要映射对象所要查询的根控件
     * @author dingwei.chen
     */
    public static void viewMapping(Object object, View rootView) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getFields();// 必须是public
        ViewMapping mapping = null;
        for (Field f : fields) {
            mapping = f.getAnnotation(ViewMapping.class);
            int id = 0;
            if (mapping != null) {
                try {
                    id = mapping.ID();
                    f.setAccessible(true);
                    f.set(object, rootView.findViewById(id));
                } catch (Exception e) {
                    throw new RuntimeException(mapping.ID() + " mapping error");
                }
            }
        }
    }

    public static void viewMapping(Object object, Window window) {
        viewMapping(object, window.getDecorView());
    }
}
