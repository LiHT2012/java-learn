本文中所述SpringBoot基于1.4.0.RELEASE版本
基本方法就是将compile范围依赖的spring-boot-starter-logging排除，然后依赖spring-boot-starter-log4j即可
build.gradle中配置如下

configurations {    
    compile.exclude module: 'spring-boot-starter-logging'//排除对默认logging的依赖
}

dependencies {    
    testCompile group: 'junit', name: 'junit', version: '4.12' 
    compile("org.springframework.boot:spring-boot-starter-web:1.4.0.RELEASE")  
    //添加对log4j starter的依赖。
    compile("org.springframework.boot:spring-boot-starter-log4j:1.3.7.RELEASE") 
}

dependencies {    
    testCompile group: 'junit', name: 'junit', version: '4.12' 
    compile("org.springframework.boot:spring-boot-starter-web:1.4.0.RELEASE")   {
        exclude module:'spring-boot-starter-logging'
    }
    //添加对log4j starter的依赖。
    compile("org.springframework.boot:spring-boot-starter-log4j:1.3.7.RELEASE") 

springboot gradle log4j2
https://www.jb51.net/article/113567.htm


https://blog.csdn.net/lu8000/article/details/25754415

https://blog.csdn.net/zy_281870667/article/details/86672254
https://www.cnblogs.com/godtrue/p/6442273.html


格式说明layout中的参数都以%开始，后面不同的参数代表不同的格式化信息（参数按字母表顺序列出）：

     %c    输出所属类的全名，可在修改为 %d{Num} ,Num类名输出的维（如："org.apache.elathen.ClassName",%C{2}将输出elathen.ClassName）

     %d    输出日志时间其格式为 %d{yyyy-MM-dd HH:mm:ss,SSS}，可指定格式 如 %d{HH:mm:ss}

     %l    输出日志事件发生位置，包括类目名、发生线程，在代码中的行数

     %n    换行符

     %m    输出代码指定信息，如info(“message”),输出message

     %p    输出优先级，即 FATAL ,ERROR 等

     %r    输出从启动到显示该log信息所耗费的毫秒数

     %t    输出产生该日志事件的线程名

vs不同的文章中的说明
Console节点中的PatternLayout定义了输出日志时的格式

%d{HH:mm:ss.SSS} 表示输出到毫秒的时间

%t 输出当前线程名称

%-5level 输出日志级别，-5表示左对齐并且固定输出5个字符，如果不足在右边补0

%logger 输出logger名称，因为Root Logger没有名称，所以没有输出

%msg 日志文本

%n 换行
