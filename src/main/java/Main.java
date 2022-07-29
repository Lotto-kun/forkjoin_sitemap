import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Marker EXCEPTIONS_MARKER = MarkerFactory.getMarker("EXCEPTIONS");
    private static final String PATH_TO_SAVE = "data/SiteMap.txt";
    private static final String URL_TO_PARSE = "https://skillbox.ru";
    public static void main(String[] args) {
        String map = new ForkJoinPool().invoke(new SiteMap(URL_TO_PARSE));
        System.out.println(map);
        Path path = Paths.get(PATH_TO_SAVE);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (Exception e) {
                LOGGER.error(EXCEPTIONS_MARKER, "ошибка создания директории назначения", e);
                e.printStackTrace();
            }
        }
        try {
            Files.writeString(path, map);
        } catch (IOException e) {
            LOGGER.error(EXCEPTIONS_MARKER, "Ошибка записи в файл", e);
            e.printStackTrace();
        }


    }
}
