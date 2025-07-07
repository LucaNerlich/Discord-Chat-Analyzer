package analyzer.service;

import analyzer.config.AnalyzerConfig;
import analyzer.models.channel.Channel;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import analyzer.utils.ExceptionHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileService {

    private final Gson gson;

    public FileService() {
        this.gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .setPrettyPrinting()
                .create();
    }

    /**
     * Reads and parses all JSON log files into Channel objects
     */
    public List<Channel> parseJsonToChannels() {
        List<String> logPaths = readLogPaths();
        List<Channel> channels = Collections.synchronizedList(new ArrayList<>());

        ExceptionHandler.logInfo("Processing " + logPaths.size() + " log files");

        logPaths.parallelStream().forEach(logFilePath -> {
            try {
                Channel channel = parseChannelFromFile(logFilePath);
                if (channel != null) {
                    channels.add(channel);
                }
            } catch (Exception e) {
                ExceptionHandler.handleFileProcessingException(e, logFilePath);
            }
        });

        ExceptionHandler.logInfo("Successfully processed " + channels.size() + " channels");
        return channels;
    }

    /**
     * Writes author data to JSON file
     */
    public void writeAuthorData(Map<?, AuthorData> authorDataMap) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(AnalyzerConfig.OUTPUT_FILE_AUTHORS))) {
            gson.toJson(authorDataMap, writer);
            ExceptionHandler.logInfo("Author data written to: " + AnalyzerConfig.OUTPUT_FILE_AUTHORS);
        } catch (IOException e) {
            ExceptionHandler.handleIOException(e, "writing author data");
        }
    }

    /**
     * Writes ranking data to JSON file
     */
    public void writeRanking(Ranking ranking) {
        if (ranking == null) {
            ExceptionHandler.logWarning("Attempted to write null ranking");
            return;
        }

        try (Writer writer = Files.newBufferedWriter(Paths.get(ranking.getOutputFilePath()))) {
            gson.toJson(ranking, writer);
            ExceptionHandler.logInfo("Ranking written to: " + ranking.getOutputFilePath());
        } catch (IOException e) {
            ExceptionHandler.handleIOException(e, "writing ranking: " + ranking.getClass().getSimpleName());
        }
    }

    private Channel parseChannelFromFile(String logFilePath) {
        try (Reader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
            return gson.fromJson(reader, Channel.class);
        } catch (Exception e) {
            ExceptionHandler.handleFileProcessingException(e, logFilePath);
            return null;
        }
    }

    private List<String> readLogPaths() {
        List<String> logPaths = new ArrayList<>();

        for (String folderPath : AnalyzerConfig.LOG_FOLDER_PATHS) {
            try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {
                List<String> result = walk
                        .map(Path::toString)
                        .filter(f -> f.endsWith(".json"))
                        .toList();
                logPaths.addAll(result);
            } catch (IOException e) {
                ExceptionHandler.handleIOException(e, "reading log paths from: " + folderPath);
            }
        }

        return logPaths;
    }
}
