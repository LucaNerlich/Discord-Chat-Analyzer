package analyzer;

import analyzer.models.Author;
import analyzer.models.Ranking;
import analyzer.models.channel.Channel;
import analyzer.stats.AuthorData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String TEST_LOG = "logs/test.json";
    private static final String OUTPUT_FILE_AUTHORS = "logs/output-all.json";
    private static final String OUTPUT_FILE_RANKING = "logs/output-ranking.json";
    
    
    public static void main(String[] args) {
        System.out.println("Starting Discord Chat Analyzer");
        
        final Gson gson = new Gson();
        Analyzer analyzer = new Analyzer(parseJsonToChannels());
        
        
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        writeAllAuthorsToFile(gson, analyzer);
        writeRankingToFile(gson, analyzer);
        stopWatch.stop();
        System.out.println("Creating output Json took: " + stopWatch.getTotalTimeSeconds() + " seconds.");
        
        System.out.println("Ending Discord Chat Analyzer");
    }
    
    private static void writeRankingToFile(Gson gson, Analyzer analyzer) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE_RANKING))) {
            final Map<Author, AuthorData> authorDataMap = analyzer.getAuthorDataMap();
            final Ranking ranking = new Ranking(new LinkedList<>(authorDataMap.values()));
            gson.toJson(ranking, writer);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    private static void writeAllAuthorsToFile(Gson gson, Analyzer analyzer) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE_AUTHORS))) {
            gson.toJson(analyzer.getAuthorDataMap(), writer);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    private static List<Channel> parseJsonToChannels() {
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        
        final List<Channel> channels = new ArrayList<>();
        final List<String> logs = readLogs();
        
        logs.parallelStream().forEach(logFilePath -> {
            try (Reader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
                final StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                final Channel channel = gson.fromJson(reader, Channel.class);
                channels.add(channel);
                stopWatch.stop();
                
                DecimalFormat df = new DecimalFormat("###.###");
                System.out.println("Parsing Json took: " + df.format(stopWatch.getTotalTimeSeconds()) + " seconds. Channel: " + channel.getChannel().getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        return channels;
    }
    
    private static List<String> readLogs() {
        List<String> logPaths = new ArrayList<>();
        
        final List<String> folderPaths = List.of(
                "logs/enklave",
                "logs/thepod"
        );
        for (String folder : folderPaths) {
            try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
                List<String> result = walk.map(Path::toString)
                        .filter(f -> f.endsWith(".json"))
                        .collect(Collectors.toList());
                logPaths.addAll(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logPaths;
    }
}
