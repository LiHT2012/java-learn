@Resource 注解的使用：
https://www.cnblogs.com/mr-wuxiansheng/p/6392190.html

1、@controller 控制器（注入服务），用于标注控制层组件

2、@service 服务（注入dao），用于标注业务层组件

3、@repository dao（实现dao访问），用于标注数据访问组件

4、@component （把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>），泛指组件，当组件不好归类的时候，可以使用这个注解进行标注。

 @Component,@Service,@Controller,@Repository注解的类，并把这些类纳入进spring容器中管理。 

5、getBean的默认名称是类名（头字母小写），如果想自定义，可以@Service(“***”)这样来指定，这种bean默认是单例的，如果想改变，可以使用
@Service(“beanName”)
@Scope(“prototype”)
public class User {

} 

@Resource注解 vs @Autowired
1、@Autowired与@Resource都可以用来装配bean. 都可以写在字段上,或写在setter方法上。 

2、@Autowired默认按类型装配（这个注解是属业spring的），默认情况下必须要求依赖对象必须存在，如果要允许null 值，可以设置它的required属性为false，如：@Autowired(required=false) ，如果我们想使用名称装配可以结合@Qualifier注解进行使用，如下： 
Java代码  收藏代码
@Autowired
@Qualifier("baseDao")     
private BaseDao baseDao;   
 
 3、@Resource（这个注解属于J2EE的），默认安照名称进行装配，名称可以通过name属性进行指定， 
如果没有指定name属性，当注解写在字段上时，默认取字段名进行按照名称查找，如果注解写在setter方法上默认取属性名进行装配。 当找不到与名称匹配的bean时才按照类型进行装配。但是需要注意的是，如果name属性一旦指定，就只会按照名称进行装配。
Java代码  收藏代码
@Resource(name="baseDao")     
private BaseDao baseDao;    
 
推荐使用：@Resource注解在字段上，且这个注解是属于J2EE的，减少了与spring的耦合。最重要的这样代码看起就比较优雅。



1.注释是一种接口形式，其中关键字接口以@开头，其主体包含与方法非常相似的注释类型元素声明：
public @interface SimpleAnnotation {
	String value();//default "dddd"
	int[] types();//可以有default {1,2,3}
}
使用：
@SimpleAnnotation(value="ssss",types={1,2})
public class Element {
……
}

2.可以从注解方法声明返回哪些对象类型？
返回类型必须是基本类型，String，Class，Enum或前三类类型之一的数组。否则，编译器将抛出错误。

3.注解可以应用于整个源代码的多个位置。它们可以应用于类，构造函数和字段的声明；方法及其参数；局部变量，包括循环和资源变量。

4.有没有办法限制可以应用注释的元素？
是的，@ Target注释可用于此目的。如果我们尝试在不适用的上下文中使用注释，编译器将发出错误。

5.什么是元注释？
是否适用于其他注释的注释。
所有未标记为@Target的注释，或者使用它标记但包含ANNOTATION_TYPE常量的注释也都是元注释。

6.是否可以扩展注解？
不能，注解
总是扩展java.lang.annotation.Annotation，如Java语言规范中所述。

如果我们尝试在注释声明中使用extends子句，我们将得到一个编译错误：
public @interface AnAnnotation extends OtherAnnotation {
   // Compilation error
}

7.@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.FIELD })//不能编译。多次出现相同的枚举常量，那么这是一个编译时错误
public @interface TestAnnotation {
    int[] value() default {};
}

8.什么是重复注释？
这些是可以多次应用于同一元素声明的注释。
出于兼容性原因，由于此功能是在Java 8中引入的，因此重复注释存储在由Java编译器自动生成的容器注释中。对于编译器来说，执行此操作有两个步骤来声明它们。

首先，我们需要声明一个可重复的注释：

@Repeatable(Schedules.class)
public @interface Schedule {
 
    String time() default "morning";
 
}
然后，我们使用强制值元素定义包含注释，其类型必须是可重复注释类型的数组：

public @interface Schedules {
 
    Schedule[] value();
}
现在，我们可以多次使用@Schedule：

@Schedule
@Schedule(time = "afternoon")
@Schedule(time = "night")
void scheduledMethod() {
    // ...
}

9.怎么能检索注释？这与保留政策有何关系？
您可以使用Reflection API或注释处理器来检索注释。

该@Retention注解和其的RetentionPolicy参数会影响您检索它们。RetentionPolicy枚举中有三个常量：

RetentionPolicy.SOURCE - 使注释被编译器丢弃，但注释处理器可以读取它们
RetentionPolicy.CLASS - 表示注释已添加到类文件中，但无法通过反射访问
RetentionPolicy.RUNTIME -Annotations由编译器记录在类文件中，并由JVM在运行时保留，以便可以反射性地读取它们
这是一个用于创建可在运行时读取的注释的示例代码：

@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
 
    String value();
 
}
现在，可以通过反射检索注释：

Description description
 
  = AnnotatedClass.class.getAnnotation(Description.class);
 
System.out.println(description.value());
注释处理器可以与RetentionPolicy.SOURCE一起使用，这在Java Annotation Processing和Creating a Builder一文中有所描述。

当您编写Java字节码解析器时，RetentionPolicy.CLASS可用。

说是面试需要掌握的，但是写的没有那么好：https://www.cnblogs.com/dz11/p/10396495.html

注解（Annotation），也叫元数据。一种代码级别的说明。它是JDK1.5及以后版本引入的一个特性，与类、接口、枚举是在同一个层次。它可以声明在包、类、字段、方法、局部变量、方法参数等的前面，用来对这些元素进行说明，注释。
简单来说注解其实就是代码中的特殊标记，这些标记可以在编译、类加载、运行时被读取，并执行相对应的处理。


全局处理异常的：@ControllerAdvice：包含@Component。可以被扫描到。统一处理异常。

@ExceptionHandler（Exception.class）：
用在方法上面表示遇到这个异常就执行以下方法：
/** 
 * 全局异常处理 
 */  
@ControllerAdvice  
class GlobalDefaultExceptionHandler {  
    public static final String DEFAULT_ERROR_VIEW = "error";  
  
    @ExceptionHandler({TypeMismatchException.class,NumberFormatException.class})  
    public ModelAndView formatErrorHandler(HttpServletRequest req, Exception e) throws Exception     
   {  
        ModelAndView mav = new ModelAndView();  
        mav.addObject("error","参数类型错误");  
        mav.addObject("exception", e);  
        mav.addObject("url", RequestUtils.getCompleteRequestUrl(req));  
        mav.addObject("timestamp", new Date());  
        mav.setViewName(DEFAULT_ERROR_VIEW);  
        return mav;  
    }
}



@Lazy
