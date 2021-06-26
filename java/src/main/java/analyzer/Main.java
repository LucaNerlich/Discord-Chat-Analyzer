package analyzer;

import analyzer.models.channel.Channel;
import analyzer.models.ranking.Ranking;
import analyzer.models.ranking.RankingType;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String TEST_LOG = "logs/test.json";
    private static final String OUTPUT_FILE_AUTHORS = "logs/output-all.json";
    
    
    public static void main(String[] args) throws InterruptedException {
        final Analyzer analyzer = new Analyzer(parseJsonToChannels());
        
        // Write Author Data
        writeAuthorData(analyzer);
        
        // Write Rankings in parallel
        try {
            writeRankings(analyzer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void writeRankings(Analyzer analyzer) throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(RankingType.values().length);
        Arrays.stream(RankingType.values())
                .forEach(rankingType ->
                        executorService.execute(() ->
                                writeRankingToFile(analyzer, rankingType)));
        executorService.shutdown();
    }
    
    private static void writeAuthorData(Analyzer analyzer) {
        final Gson gson = new Gson();
        final StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
        writeAllAuthorsToFile(gson, analyzer);
        System.out.println("Writing " + OUTPUT_FILE_AUTHORS + " took: " + stopWatch.getTotalTimeMillis() + " milliseconds.");
        stopWatch.stop();
    }
    
    private static void writeRankingToFile(Analyzer analyzer, RankingType rankingType) {
        final Gson gson = new Gson();
        final StopWatch stopWatch = new StopWatch();
        final Ranking ranking = analyzer.getRanking(rankingType);
        
        if (ranking != null) {
            stopWatch.start();
            try (Writer writer = Files.newBufferedWriter(Paths.get(ranking.getOutputFilePath()))) {
                gson.toJson(ranking, writer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            stopWatch.stop();
            System.out.println("Writing " + ranking.getOutputFilePath() + " took: " + stopWatch.getTotalTimeMillis() + " milliseconds.");
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
//                "logs/enklave",
//                "logs/thepod",
//                "logs/okcool",
//                "logs/stayforever"
//                "logs/hooked"
//                "logs/nextjs"
                "logs/computerbase"
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
