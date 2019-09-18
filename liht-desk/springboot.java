springboot是Spring开源组织下的子项目，是Spring组件一站式解决方案，主要是简化了使用Spring的难度，简省了繁重的配置，提供了各种启动器，开发者能快速上手。

SpringApplication只是将一个典型的Spring应用的启动流程进行了扩展。

spring Ioc容器：
1.IoC容器想要管理各个业务对象以及它们之间的依赖关系，需要通过某种途径来记录和管理这些信息。 BeanDefinition对象就承担了这个责任：容器中的每一个bean都会有一个对应的BeanDefinition实例，该实例负责保存bean对象的所有必要信息，包括bean对象的class类型、是否是抽象类、构造方法和参数、其它属性等等。当客户端向容器请求相应对象时，容器就会通过这些信息为客户端返回一个完整可用的bean实例。

2.BeanDefinitionRegistry抽象出bean的注册逻辑，而BeanFactory则抽象出了bean的管理逻辑，而各个BeanFactory的实现类就具体承担了bean的注册以及管理工作。



定义一个类，有@ConfigurationProperties(prefix ="solr")
该类的属性将对应 solr.*  的值。
@EnableConfigurationProperties注解表示对 @ConfigurationProperties的内嵌支持，默认会将对应Properties Class作为bean注入的IOC容器中，即在相应的Properties类上不用加 @Component注解。----对应在application启动类上用
@EnableConfigurationProperties(SolrConfigProperties.class)


springboot配置加载顺序：
https://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247486895&idx=2&sn=1d49a0de72f9dee2c434ae905d5bc2e4&chksm=eb538899dc24018ffb0e618abfe7e2223da20e5b8a2d5be36267769779a82701699cd5476748&scene=21#wechat_redirect


Spring Boot有两种类型的配置文件，application和bootstrap文件。Spring Boot会自动加载classpath目前下的这两个文件，文件格式为properties或者yml格式。

*.properties文件大家都知道是key=value的形式\
*.yml是key: value的形式

*.yml加载的属性是有顺序的，但不支持@PropertySource注解来导入配置，一般推荐用yml文件，看下来更加形象。
application配置文件

application配置文件是应用级别的，是当前应用的配置文件。
bootstrap配置文件

bootstrap配置文件是系统级别的，用来加载外部配置，如配置中心的配置信息，也可以用来定义系统不会变化的属性。bootstatp文件的加载先于application文件。

    在 Spring Boot 中有两种上下文，一种是 bootstrap, 另外一种是 application, bootstrap 是应用程序的父上下文，也就是说 bootstrap 加载优先于 applicaton。bootstrap 主要用于从额外的资源来加载配置信息，还可以在本地外部配置文件中解密属性。这两个上下文共用一个环境，它是任何Spring应用程序的外部属性的来源。bootstrap 里面的属性会优先加载，它们默认也不能被本地相同配置覆盖。

因此，对比 application 配置文件，bootstrap 配置文件具有以下几个特性。

    boostrap 由父 ApplicationContext 加载，比 applicaton 优先加载

    boostrap 里面的属性不能被覆盖。


运行Spring Boot的3种方式

    运行启动类的main方法。

    使用spring-boot:run命令。

    打成jar包后使用java -jar xx.jar命令。

Spring Boot默认的端口是8080，可以通过server.port=8081来修改，或者通过命令行指定也行。



springboot自定义配置热部署

引用devtools依赖

    <dependency>

        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-devtools</artifactId>

        <optional>true</optional>

    </dependency>

以下配置用于自定义配置热部署，可以不设置。

    # 热部署开关，false即不启用热部署

    spring.devtools.restart.enabled: true

    # 指定热部署的目录

    #spring.devtools.restart.additional-paths: src/main/java

    # 指定目录不更新

    spring.devtools.restart.exclude: test/**



写一个Application类extends SpringBootServletInitializer，有注解：
@SpringBootApplication
@MapperScan("com.backend.kfc.dao.mysql.mapper//mapper所在的路径")
@ServletComponentScan
public class PimBackendApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(PimBackendApplication.class, args);
	}
}

若配置中有特别的，如solr的设置，可增加@EnableConfigurationProperties(SolrConfigProperties.class)，其中SolrConfigProperties是对solr配置属性的类：
@ConfigurationProperties(prefix ="solr")//表示配置文件中的以solr开头的
public class SolrConfigProperties {
    private String zkHost;
    private String coreDoc;//对应solr_core_doc=****
……
}

默认的配置文件是在resource文件夹下的application.properties，如：
spring.servlet.multipart.maxFileSize=500Mb
spring.servlet.multipart.maxRequestSize=500Mb

mybatis.config-locations=classpath:mybatis.xml
mybatis.mapper-locations=classpath:mybatis/*.xml	//*/

logging.level.com.pim.superadmin.model.mapper=debug

,不同环境的相同配置都写在此文件中。如果不同的环境有不同设置的属性，可另外在resource文件夹下创建config 包，包中包含application-localtotest1.properties等文件，在application.properties中的spring.profiles.active=×××生效时，将从×××读取所需的属性配置。如有：
spring.datasource.url = jdbc:mysql://test1-×××:3306/pim?useUnicode=true&characterEncoding=utf8
spring.datasource.username = dbcool
spring.datasource.password = 12345678

spring.redis.host-name=52.83.186.231
spring.redis.port=3333

short_long_url=http://52.83.164.247:8085
对于比较分散的特殊配置，可另外写一个类，比如EnvConfigBean：
@Component
public class EnvConfigBean {
	
	private static final String ENV_PROP_ACTIVE_PROFILE = "spring.profiles.active";
	private static final String SHORT_LONG_URL_HOST = "short_long_url";

	@Autowired
	private Environment env;

	private String runTimeEnv;
	private String shortLongUrlHost;

	@PostConstruct
	void init() {
		runTimeEnv = env.getProperty(ENV_PROP_ACTIVE_PROFILE);
		shortLongUrlHost = env.getProperty(SHORT_LONG_URL_HOST);
	}

	public String getRunTimeEnv() {
		return runTimeEnv;
	}
	public void setRunTimeEnv(String runTimeEnv) {
		this.runTimeEnv = runTimeEnv;
	}
	public String getShortLongUrlHost() {
		return shortLongUrlHost;
	}
	public void setShortLongUrlHost(String shortLongUrlHost) {
		this.shortLongUrlHost = shortLongUrlHost;
	}
}

则对于EnvConfigBean的调用，如：
……
@Autowired
private Client httpClient;

@Autowired
private EnvConfigBean envConfigBean;

private String SHORT_LONG_URL_HOST = null;

@PostConstruct
void init() {
	SHORT_LONG_URL_HOST = envConfigBean.getShortLongUrlHost();
}
……

对于项目中用的client是使用org.springframework.web.client.RestTemplate自己包装的RestClient类。具体RestClient类和RestTemplate的学习，见另外文档client。

