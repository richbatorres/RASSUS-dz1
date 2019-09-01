package hr.fer.serverProject;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "hr.fer.serverProject")
public class ApplicationConfiguration {
	@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }
}
