package com.supinfo.supapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.supinfo.supapi.dao.TrainDao;
import com.supinfo.supapi.dao.UserDao;
import com.supinfo.supapi.interfaces.dao.ITrainDao;
import com.supinfo.supapi.interfaces.dao.IUserDao;
import com.supinfo.supapi.interfaces.job.ITrainJob;
import com.supinfo.supapi.interfaces.job.IUserJob;
import com.supinfo.supapi.job.TrainJob;
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
     
    @Bean
    public IUserDao getUserDao() {
        return new UserDao();
    }
    
    @Bean
    public IUserJob getUserJob() {
        return new UserJob();
    }
    
    @Bean
    public ITrainDao getTrainDao() {
        return new TrainDao();
    }
    
    @Bean
    public ITrainJob getTrainJob() {
        return new TrainJob();
    }
}
