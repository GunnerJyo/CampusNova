package com.campusnova;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.boot.context.properties.EnableConfigurationProperties; import com.campusnova.config.AiProperties;
@SpringBootApplication @EnableConfigurationProperties(AiProperties.class) public class CampusNovaApplication { public static void main(String[] args){ SpringApplication.run(CampusNovaApplication.class,args); } }
