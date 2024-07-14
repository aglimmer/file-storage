package com.developerspace.filestorageserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;

/**
 * WebMvc配置内容：
 * 1.跨域请求配置，通过拦截器或过滤器设置可以访问的IP、允许的方法、请求头
 * 2.序列化器，配置日期返回给前端的序列化方法
 * @Author wangz
 * @Date 2021-04-06 0:46
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LocalDateConverter());
        registry.addConverter(new LocalTimeConverter());
        registry.addConverter(new LocalDateTimeConverter());
        registry.addConverter(new DateConverter());

    }

    @ConditionalOnBean
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        om.registerModule(javaTimeModule);
        return om;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    public static class LocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
    public static class DateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        }
    }
    public static class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
    public static class LocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }

    @Bean
    public HandlerInterceptor accessInterceptor() {
        //return new AccessInterceptor();
        return new HandlerInterceptor(){
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE,HEAD,PATCH");
                //若前端传送了自定义的请求头而没有在这里面设置，则会提示跨域请求错误：Access to XMLHttpRequest at 'http://localhost:8080/token' from origin 'http://127.0.0.1:5500' has been blocked by CORS policy: Request header field token is not allowed by Access-Control-Allow-Headers in preflight response.
                //response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, AccessToken, RefreshToken, X-Requested-With");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
                response.setHeader("Access-Control-Allow-Credentials", "true"); // 前端请求设置credentials: 'include', 这里就必须设置为true
                response.setHeader("Access-Control-Max-Age", "3600");
                // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
                if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
                    response.setStatus(HttpStatus.OK.value());
                    return false;
                }
                return true;
            }
        };

    }

    /**
     * 放行swagger-ui静态资源
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.accessInterceptor())
                .excludePathPatterns("/error")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/api-docs/**", "/swagger-ui.html/**");
    }
    @Bean
    public FilterRegistrationBean<CustomCorsFilter> customCorsFilterFilterRegistrationBean(){
        FilterRegistrationBean<CustomCorsFilter> registrationBean = new FilterRegistrationBean<>(this.customCorsFilter());
        registrationBean.setOrder(-1);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    @Bean
    public CustomCorsFilter customCorsFilter(){
        return new CustomCorsFilter();
    }

   public static class CustomCorsFilter implements Filter{
       @Override
       public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
           HttpServletResponse response = (HttpServletResponse) servletResponse;
           HttpServletRequest request = (HttpServletRequest) servletRequest;
           response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
           response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE,HEAD,PATCH");
           // 若前端传送了自定义的请求头而没有在这里面设置，某些情况下则会提示跨域请求错误：not allowed by Access-Control-Allow-Headers in preflight response.
           response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
           response.setHeader("Access-Control-Allow-Credentials", "true"); // 前端请求设置credentials: 'include', 这里就必须设置为true
           response.setHeader("Access-Control-Max-Age", "3600"); // 设置 Access-Control-Max-Age 为 3600，表示预检请求的结果可以缓存 3600 秒（即 1 小时），在此期间，浏览器不需要为相同的跨域请求再次发送预检请求
           filterChain.doFilter(servletRequest,servletResponse);
       }
   }
    @Slf4j
    public static class AccessInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            //跨域访问设置：response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            //或者：httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
            Enumeration<String> headerNames =request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                log.info("header----{},{}",headerName,request.getHeader(headerName));
            }
            log.info("file-server----preHandle---uri={}",request.getRequestURL());
            //避免服务中的跨域配置与网关的跨域配置冲突，若不包含请求头：x-forwarded-port，则说明是不是网关转发
            if(request.getHeader("x-forwarded-port")==null) {
                log.info("x-forwarded-port={}",request.getHeader("x-forwarded-port"));
                //跨域请求
                response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE,HEAD,PATCH");
                //若前端传送了自定义的请求头而没有在这里面设置，则会提示跨域请求错误：Access to XMLHttpRequest at 'http://localhost:8080/token' from origin 'http://127.0.0.1:5500' has been blocked by CORS policy: Request header field token is not allowed by Access-Control-Allow-Headers in preflight response.
                //response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, AccessToken, RefreshToken, X-Requested-With");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Max-Age", "3600");

                //若要允许所有请求头，则使用如下
                //response.setHeader("Access-Control-Allow-Headers","*");
            }else {
                //跨域请求
                response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE,HEAD,PATCH");
                //若前端传送了自定义的请求头而没有在这里面设置，则会提示跨域请求错误：Access to XMLHttpRequest at 'http://localhost:8080/token' from origin 'http://127.0.0.1:5500' has been blocked by CORS policy: Request header field token is not allowed by Access-Control-Allow-Headers in preflight response.
                //response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, AccessToken, RefreshToken, X-Requested-With");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Max-Age", "3600");
            }
            //log.info("Origin="+request.getHeader("Origin"));
            //log.info("uri="+request.getRequestURI());
            //log.info("url="+request.getRequestURL());
            //String token = request.getHeader("AUTHORIZATION");
            //log.info("token="+token);
            /**
             * 前端请求设置credentials: 'include', 这里就必须设置为true
             * Response to preflight request doesn't pass access control check:
             * The value of the 'Access-Control-Allow-Credentials' header in the response is '' which
             * must be 'true' when the request's credentials mode is 'include'.
             **/
            //response.setHeader("Access-Control-Allow-Credentials", "true");
            // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
            if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
                response.setStatus(HttpStatus.OK.value());
                return false;
            }
            return true;
        }
    }

}
