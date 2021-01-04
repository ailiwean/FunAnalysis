
# FunAnalysis
tranform+asm无侵入统计方法耗时可对jar包方法插入拦截

### 目前仅为测试版

### 依赖

 1 , clone项目，需要plugins模块与annotation模块
 
 2 , plugins模块下执行maven本地打包任务，项目根目录将生成一个repo文件夹
 
 3 , classpath依赖插件
```
    repositories {
        maven { url "$rootDir/repo/" }
    }
    dependencies {
        classpath "com.ailiwean:iplugins:1.0.1"
    }
```

4 , 项目依赖annotation模块

### 使用  
#### 方法耗时监听
    
 ```
  //app或module引用插件  
  apply plugin: "fun-analysis"
```



   在某个类上使用@Analysis注解标记，会对这个类的getDeclaredMethod对应所有方法都插桩，或单独对某个方法
   ```
@Analysis
public class MainActivity extends AppCompatActivity {

    @Analysis
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

```


#### jar包插入拦截
 ```
  //app或module引用插件  
  apply plugin: "fun-analysis"
```

```
public class Test {

    @InjectTo(targetMethod = {"Landroidx/appcompat/app/AppCompatActivity;", "onCreate", "(Landroid/os/Bundle;)V"},
            type = InjectTo.BOTTOM)
    public void test() {
        App app = App.getApp();
        if (app != null)
            Toast.makeText(app.getTopActivity(), "test", Toast.LENGTH_LONG).show();
    }

}
```

创建一个空构造函数类， 使用@InjectTo注解标记方法

```
/***
 *      1 ： 该注解标记的方法必须参数为空返回void，且类的构造函数为空
 *      2 ： 注解值如下：
 *
 *          type 插入位置：
 *                   TOP: 方法前插入，起到拦截作用
 *                   BOTTOM ：方法后插入 ，目前不能用于有返回值的函数
 *
 *
 *          targetMethod  定位方法：
 *             index1 ： 目标类的签名： 例 ‘  Landroid/java/lang/Lang;'   前边大写L不能少
 *             index2 :   目标方法名： 例 ‘  valueOf '
 *             index3 ： 目标方法对应的参数描述： 例     ‘ (J)Ljava/lang/Long;  ’括号内为入参外为出参，具体可以用相关工具查看
 *
 */
```


### 配置
    
   build.gradle中配置
   

```
funAnalysis {
    enableUseTime = true     //方法耗时开关
    tag = "shuai"                   //方法耗时tag
    enableJarInject = false       //jar插桩开关
}
```
效果如下
```
2021-01-03 01:13:43.924 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:<init>----------0ms
2021-01-03 01:13:44.075 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onCreate----------148ms
2021-01-03 01:13:44.080 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onResume----------1ms
2021-01-03 01:14:00.047 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onStop----------1ms
2021-01-03 01:14:02.350 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onResume----------1ms
2021-01-03 01:14:03.812 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onStop----------1ms
2021-01-03 01:14:05.211 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onResume----------1ms
2021-01-03 01:14:06.180 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onStop----------1ms
2021-01-03 01:14:06.682 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onResume----------1ms
2021-01-03 01:24:06.753 13980-13980/com.ailiwean.funanalysis I/shuai: MainActivity:onStop----------2ms
```

jar包方法插入成功会在控制台打印信息

```

> Task :app:transformClassesWithAppfunAnalysisTaskForDebug
start insert:@@@from >>>Lcom/ailiwean/funanalysis/Test;:test
 @@@to >>>Landroidx/appcompat/app/AppCompatActivity;:onCreate
insert ok!!!
```


持续维护增加新功能，求star
