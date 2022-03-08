package com.shulichenko.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class StartupApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(GraylogHttpForwarder.class);

    private final DataProcessor dataProcessor;

    public StartupApplicationRunner(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.isEmpty()) {
            logger.error("Missing mandatory file path argument");
            System.exit(1);
        }

        if (nonOptionArgs.size() > 1) {
            logger.error("Too many non-optional arguments");
            System.exit(1);
        }

        var filePath = nonOptionArgs.get(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            dataProcessor.process(reader);
        } catch (IOException e) {
            logger.error("File read error: {}", e.getMessage());
        }
    }
}
