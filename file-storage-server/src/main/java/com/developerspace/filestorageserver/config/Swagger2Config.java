package com.developerspace.filestorageserver.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Value("${swagger.show}")
    private boolean swaggerShow;

    @Value("${project.version}")
    private String serviceVersion;

    /**
     * 无法扫描接口
     **/
    @Bean
    public Docket createRestApi() {

        List<Parameter> pars = new ArrayList<Parameter>();
        ParameterBuilder accessTokenBuilder = new ParameterBuilder();
        //请求头显示框，不添加则不显示
        accessTokenBuilder.name("AccessToken").description("请求头AccessToken")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(accessTokenBuilder.build());
        ParameterBuilder refreshTokenBuilder = new ParameterBuilder();
        //请求头显示框，不添加则不显示
        refreshTokenBuilder.name("RefreshToken").description("请求头RefreshToken")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(refreshTokenBuilder.build());


        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class));
        log.info("swaggerShow="+swaggerShow);
        builder = swaggerShow ? builder.paths(PathSelectors.any()) : builder.paths(PathSelectors.none());
        return builder.build().globalOperationParameters(pars);
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OSS存储服务")
                .description("用于上传图片、访问图片" )
                .version(serviceVersion)
                .build();
    }
}