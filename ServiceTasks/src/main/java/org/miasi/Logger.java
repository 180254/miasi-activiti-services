package org.miasi;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Logger {

    private int loggerId = new Random().nextInt(1000);
    private String loggerName;

    private Logger(String loggerName) {
        this.loggerName = loggerName;
    }

    public static Logger forClass(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    public void log(Object message) {
        try {
            Path path = Paths.get("ST_Log_" + loggerName + ".txt");
            String message2 = loggerId + "-" + message.toString();

            if (message instanceof Throwable) {
                message2 = message2 + "\r\n" + ExceptionUtils.getStackTrace((Throwable) message);
            }

            List<String> strings = Collections.singletonList(message2);
            Files.write(path, strings, UTF_8, APPEND, CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
