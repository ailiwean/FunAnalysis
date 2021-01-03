package com.ailiwean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 将当前方法插入到某个方法中并return
 * 注意事项：
 *      1 ： 该注解标记的方法必须参数为空返回void，且类的构造函数为空
 *      2 ： 注解值如下：
 *             index1 ： 目标类的签名： 例 ‘  Landroid/java/lang/Lang ’   前边大写L不能少
 *             index2 :   目标方法名： 例 ‘  valueOf '
 *             index3 ： 目标方法对应的参数描述： 例     ‘ (J)Ljava/lang/Long;  ’括号内为入参外为出参，具体可以用相关工具查看
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectTo {
    String[] targetMethod() default {"", "", ""};
}
