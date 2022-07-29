import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class SiteMap extends RecursiveTask<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteMap.class);
    private static final Marker EXCEPTIONS_MARKER = MarkerFactory.getMarker("EXCEPTIONS");
    private final String link;
    private Document doc = null;
    private int tabulationCount = 0;
    Set<String> linksCollection = new HashSet<>();

    public SiteMap(String link) {
        if (link.charAt(link.length() - 1) != '/') {
            link += "/";
        }
        this.link = link;
        linksCollection.add(link);
        //        System.out.println(link);
        try {
            doc = Jsoup.connect(link).maxBodySize(0).get();
        } catch (IOException e) {
            LOGGER.error(EXCEPTIONS_MARKER, "Не смогло подключиться по ссылке", e);
            e.printStackTrace();
        }


    }

    public SiteMap(String link, int tabulationCount) {
        this(link);
        this.tabulationCount = tabulationCount;
    }

    @Override
    protected String compute() {
        if (doc != null) {
            Elements links = doc.select("a");
            String regex = link + "[^?#/]+/$";
            List<SiteMap> taskList = new ArrayList<>();
            links.forEach(href -> {
                if (!href.hasAttr("type")) {
                    String link = href.attr("abs:href");
                    if (link.matches(regex) && !linksCollection.contains(link)) {
                        linksCollection.add(link);
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SiteMap task = new SiteMap(link, tabulationCount + 1);
                        taskList.add(task);
                        task.fork();
                    }
                }
            });

            StringBuilder result = new StringBuilder();
            result.append("\t".repeat(tabulationCount));
            result.append(link).append(System.lineSeparator());
            for (SiteMap task : taskList) {
                String taskResult = task.join();
                if (taskResult != null) {
                    result.append(task.join());
                }
            }
            return result.toString();

        } else {
            System.out.println("Ошибка получения кода. Проверьте правильность ссылки");
        }
        return null;
    }

}
