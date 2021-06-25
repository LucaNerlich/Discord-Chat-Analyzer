package analyzer;

import analyzer.models.channel.Channel;
import com.google.gson.Gson;
import org.springframework.util.StopWatch;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Discord Chat Analyzer");
        try (Reader reader = Files.newBufferedReader(Paths.get("logs/allfreundlichbeta.json"))) {
            Gson gson = new Gson();
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            Channel channel = gson.fromJson(reader, Channel.class);
            
            stopWatch.stop();
            System.out.println("Parsing Json took: " + stopWatch.getTotalTimeSeconds() + " seconds.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Ending Discord Chat Analyzer");
    }
}
