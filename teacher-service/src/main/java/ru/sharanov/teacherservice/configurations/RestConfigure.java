package ru.sharanov.teacherservice.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("url")
public class RestConfigure {
    private String school;
    private String student;
}
