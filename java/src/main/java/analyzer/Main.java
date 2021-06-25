package analyzer;

import analyzer.models.channel.Channel;
import analyzer.stats.EmojiCounter;
import com.google.gson.Gson;
import org.springframework.util.StopWatch;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final String REAL_LOG = "logs/allfreundlichbeta.json";
    private static final String TEST_LOG = "logs/test.json";
    
    private static final String OUTPUT_FILE = "logs/output.json";
    
    
    public static void main(String[] args) {
        System.out.println("Starting Discord Chat Analyzer");
        try (Reader reader = Files.newBufferedReader(Paths.get(REAL_LOG))) {
            Gson gson = new Gson();
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            Channel channel = gson.fromJson(reader, Channel.class);
            stopWatch.stop();
            System.out.println("Parsing Json took: " + stopWatch.getTotalTimeSeconds() + " seconds.");
            
            Analyzer analyzer = new Analyzer(channel);
            final List<EmojiCounter> emojiCounters = analyzer.getEmojiCounters();
            
            stopWatch.start();
            try (Writer writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE))) {
                gson.toJson(emojiCounters, writer);
            }
            stopWatch.stop();
            System.out.println("Creating output Json took: " + stopWatch.getTotalTimeSeconds() + " seconds.");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Ending Discord Chat Analyzer");
    }
}
