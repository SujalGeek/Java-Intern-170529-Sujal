// package com.example.apigateway.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.reactive.config.CorsRegistry;
// import org.springframework.web.reactive.config.WebFluxConfigurer;

// @Configuration
// public class CorsConfig {
    
//     @Bean
//     public WebFluxConfigurer corsConfigurer(){

//         return new WebFluxConfigurer() {
            
//             @Override
//             public void addCorsMappings(CorsRegistry corsRegistry)
//             {
//                 corsRegistry.addMapping("/**")
//                 .allowedOrigins("http://localhost:5173")
//                 .allowedMethods("GET","POST","PUT","DELETE")
//                 .allowedHeaders("*")
//                 .allowCredentials(true);
//             }
//         };
//     }
// }
