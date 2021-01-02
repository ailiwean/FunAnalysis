
# FunAnalysis
tranform+asm无侵入统计方法耗时可对三方jar包方法插入拦截

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

### 配置
    
   build.gradle中配置
   

```
funAnalysis {
    enable = true        //全局开关
    tag = "shuai"        //Log的tag
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
持续维护增加新功能，求star
