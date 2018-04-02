自定义Android Lint，用于静态扫描项目代码

目前已有的功能：

### 性能相关

- 1、初始变量时多余默认赋值 [`InitialFieldDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/performance/InitialFieldDetector.java)

- 2、IO/Cursor 检测是否正确使用了close方法，避免内存泄露 [`CloseStreamDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/performance/CloseStreamDetector.java)

- 3、避免使用Enum，两倍内存影响 [`EnumDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/performance/EnumDetector.java)

- 4、避免使用new Message(), 使用Message.Obtain()复用message  [`MessageObtainDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/performance/MessageObtainDetector.java)

- 5、避免直接调用new Thread()创建线程 [`ThreadCreateDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/performance/ThreadCreateDetector.java)
 
### 代码规范

- 1、layout文件命名，根据使用场景不同加前缀，目前暂时只处理Activity（activity_）和Fragment（fragment_）  [`ActivityFragmentLayoutNameDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/ActivityFragmentLayoutNameDetector.java)

- 2、需继承某一基类，目前只处理Activity作为基类的场景  （避免基类统一事件的漏处理） [`BaseActivityDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/BaseActivityDetector.java)

- 3、变量命名规范，常量大写，static变量加前缀s，普通变量加m  [`FieldNameDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/FieldNameDetector.java)

- 4、intent传参时避免硬编码，参数key必须以EXTRA_的宏定义  （避免硬编造成的传输错误） [`IntentExtraKeyDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/IntentExtraKeyDetector.java)

- 5、统一自定义Log类使用，避免正式包输出调试过程的无用日志  [`LoggerDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/LoggerDetector.java)

- 6、layout中id命名规范，以组件缩写开头   [`ViewIdNameDetector.java`](./lintrules/src/main/java/com/ulexzhong/lintrules/detector/standard/ViewIdNameDetector.java)



<br>
<br>
后续功能扩展中...
