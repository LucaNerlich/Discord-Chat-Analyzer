package analyzer;

import analyzer.models.Statistic;
import analyzer.models.channel.Channel;
import com.google.gson.Gson;
import org.springframework.util.StopWatch;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String REAL_LOG = "logs/allfreundlichbeta.json";
    private static final String TEST_LOG = "logs/test.json";
    
    public static void main(String[] args) {
        System.out.println("Starting Discord Chat Analyzer");
        try (Reader reader = Files.newBufferedReader(Paths.get(REAL_LOG))) {
            Gson gson = new Gson();
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            Channel channel = gson.fromJson(reader, Channel.class);
            Analyzer analyzer = new Analyzer(channel);
            final Statistic statistic = analyzer.getStatistic();
            
            stopWatch.stop();
            System.out.println("Parsing Json took: " + stopWatch.getTotalTimeSeconds() + " seconds.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Ending Discord Chat Analyzer");
    }
}
