package org.miasi.common;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Logger {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
            Path path = Paths.get(String.format("ST_Log_%s.txt", loggerName));

            String timestamp = LocalDateTime.now().format(formatter);
            String message2 = String.format("%d-%s-%s", loggerId, timestamp, message.toString());

            if (message instanceof Throwable) {
                message2 = String.format("%s\r\n%s",
                        message2,
                        ExceptionUtils.getStackTrace((Throwable) message));
            }

            List<String> strings = Collections.singletonList(message2);
            Files.write(path, strings, UTF_8, APPEND, CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
