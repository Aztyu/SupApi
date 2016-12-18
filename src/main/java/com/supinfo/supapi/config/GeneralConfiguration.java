package com.supinfo.supapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.supinfo.supapi.dao.RailDao;
import com.supinfo.supapi.dao.UserDao;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.dao.ITrainDao;
import com.supinfo.supapi.interfaces.dao.IUserDao;
import com.supinfo.supapi.interfaces.job.IRailJob;
import com.supinfo.supapi.interfaces.job.ITrainJob;
import com.supinfo.supapi.interfaces.job.IUserJob;
import com.supinfo.supapi.job.RailJob;
import com.supinfo.supapi.job.UserJob;

@Configuration
@ComponentScan(basePackages="com.supinfo.suprail")
@EnableWebMvc
public class GeneralConfiguration extends WebMvcConfigurerAdapter{
	@Bean
    public ViewResolver getViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
     
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
     
    
    //Init les diffrents bean Java utiliser dans les AutoWired
    @Bean
    public IUserDao getUserDao() {
        return new UserDao();
    }
    
    @Bean
    public IUserJob getUserJob() {
        return new UserJob();
    }
    
    @Bean
    public IRailDao getRailDao() {
        return new RailDao();
    }
    
    @Bean
    public IRailJob getRailJob() {
        return new RailJob();
    }
}
