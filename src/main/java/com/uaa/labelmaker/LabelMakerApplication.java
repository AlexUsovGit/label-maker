package com.uaa.labelmaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LabelMakerApplication

{

    public static void main(String[] args)
    {
        Logger logger = LoggerFactory.getLogger(LabelMakerApplication.class);

        SpringApplication.run(LabelMakerApplication.class, args);
        logger.info("\n\n\nLABEL MAKER STARTED\n\n\n");
    }

}
