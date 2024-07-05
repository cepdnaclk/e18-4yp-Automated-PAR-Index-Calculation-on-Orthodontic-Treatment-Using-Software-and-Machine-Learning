package com.orthodontics.filemanagement;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") // Allow CORS for all endpoints

                //.allowedOrigins("http://localhost:5173") // Allow requests from this origin
                .allowedOrigins("*")

                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
                .allowedHeaders("*"); // Allow all headers
    }
}
