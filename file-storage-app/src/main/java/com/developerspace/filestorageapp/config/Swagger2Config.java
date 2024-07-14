package com.developerspace.filestorageapp.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Value("${swagger.show}")
	private boolean swaggerShow = false;

	@Value("${project.version}")
	private String projectVersion = "version-1.0.0";

	//创建多个swagger文档分组
	@Bean
	public Docket docketB() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).groupName("Group-B");
	}

	/**
	 * Swagger核心的bean，进行一些重要配置，可以使用多个方法返回多个这样的实例
     * Environment对象读取配置文件中的spring.profiles.active指定的值，由spring进行注入，由此可动态控制是否启用swagger文档
     * 若不使用 Environment对象，则直接使用 public Docket docket()
	 **/

	@Bean
	public Docket docket(Environment env) {
        ////设置要显示的swagger环境
		//Profiles prof = Profiles.of("dev", "test");
        ////如果激活的环境与Profiles指定的环境一致返回true，设置flag表示是否启用swagger-ui文档
		//boolean flag = env.acceptsProfiles(prof);
        //或者通过配置注入参数
        boolean flag = swaggerShow;

        // enable为true，表示启动swagger，此时可以在浏览器中访问
        // RequestHandlerSelectors 选择要扫描的范围
        // RequestHandlerSelectors.basePackage("com.controller")  扫描指定的包
        // RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class) 指定方法上的注解
        // RequestHandlerSelectors.withClassAnnotation(Controller.class) 指定类上的注解
        // paths(PathSelectors.regex("/swagger/*")) 指定的正则表达式路径
        // 提示：若要让swagger识别interface上的注解，要使用withMethodAnnotation(ApiOperation.class)，其他扫描办法不起效
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.groupName("Group-A")
				.enable(flag)
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
                //.paths(PathSelectors.regex("/swagger/*"))
        ////除了使用.enable()设置，也可以使用以下办法表示是否扫描
        //builder = swaggerShow ? builder.paths(PathSelectors.any()) : builder.paths(PathSelectors.none());
	    return builder.build();

	    //// 设置par参数，可以让swagger-ui页面显示请求头表单域
        //ParameterBuilder authorization = new ParameterBuilder();
        //List<Parameter> pars = new ArrayList<Parameter>();
        //authorization.name("Authorization").description("请求头信息Authorization（即token）")
        //        .modelRef(new ModelRef("string")).parameterType("header")
        //        .required(false).build();
        //pars.add(authorization.build());
        //// 请求头显示框，不添加则不显示
        //return builder.build().globalOperationParameters(pars);
	}

	//   返回一个ApiInfo实例，配置了文档的版本信息
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("swagger标题")
				.description("swagger接口测试描述信息")
				.version(projectVersion)
				.termsOfServiceUrl("https://mp.csdn.net/console/article")
				.contact(new Contact("wang xxx", "https://mp.csdn.net/console/article", "222xxxxx@qq.com"))
				.license("swagger-licence-0.0")
				.build();
	}
}