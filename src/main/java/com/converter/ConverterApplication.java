package com.converter;

import com.converter.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import static java.util.Objects.nonNull;

@SpringBootApplication
public class ConverterApplication {
    @Autowired
    private Mapper mapper;
    private static String source;
    private static String target;

    public static void main(String[] args) {
        if (nonNull(args) && args.length > 0) {
            source = args[0];

            if (args.length > 1) {
                target = args[1];
            } else {
                target = "target.html";
            }
        }
        SpringApplication.run(ConverterApplication.class, args);
    }

    @EventListener
    public void execute(ContextRefreshedEvent event) {
        mapper.convert("HELP.md", "target.HTML");
//        mapper.convert(source, target);
    }
}
