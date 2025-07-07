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
     * Reads and parses JSON log files from a specific folder into Channel objects
     */
    public List<Channel> parseJsonToChannels(String folderPath) {
        List<String> logPaths = readLogPathsFromFolder(folderPath);
        List<Channel> channels = Collections.synchronizedList(new ArrayList<>());
        
        ExceptionHandler.logInfo("Processing " + logPaths.size() + " log files from " + folderPath);
        
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
        
        ExceptionHandler.logInfo("Successfully processed " + channels.size() + " channels from " + folderPath);
        return channels;
    }

    /**
     * Writes author data to JSON file in the specified output directory
     */
    public void writeAuthorData(Map<?, AuthorData> authorDataMap, String outputDir) {
        String outputPath = createOutputPath(outputDir, AnalyzerConfig.OUTPUT_FILE_AUTHORS);
        createOutputDirectoryIfNotExists(outputDir);
        
        try (Writer writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            gson.toJson(authorDataMap, writer);
            ExceptionHandler.logInfo("Author data written to: " + outputPath);
        } catch (IOException e) {
            ExceptionHandler.handleIOException(e, "writing author data");
        }
    }

        /**
     * Writes ranking data to JSON file in the specified output directory
     */
    public void writeRanking(Ranking ranking, String outputDir) {
        if (ranking == null) {
            ExceptionHandler.logWarning("Attempted to write null ranking");
            return;
        }
        
        String outputPath = createOutputPath(outputDir, ranking.getOutputFileName());
        createOutputDirectoryIfNotExists(outputDir);
        
        try (Writer writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            gson.toJson(ranking, writer);
            ExceptionHandler.logInfo("Ranking written to: " + outputPath);
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

        private List<String> readLogPathsFromFolder(String folderPath) {
        List<String> logPaths = new ArrayList<>();
        
        try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {
            List<String> result = walk
                .filter(path -> path.toString().endsWith(".json"))
                .filter(path -> !containsOutputFolder(path))  // Exclude output directory files
                .map(Path::toString)
                .toList();
            logPaths.addAll(result);
        } catch (IOException e) {
            ExceptionHandler.handleIOException(e, "reading log paths from: " + folderPath);
        }
        
        return logPaths;
    }
    
    /**
     * Checks if the given path contains the output subfolder
     */
    private boolean containsOutputFolder(Path path) {
        for (Path part : path) {
            if (part.toString().equals(AnalyzerConfig.OUTPUT_SUBFOLDER)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates output directory path based on input folder name
     */
    public String createOutputDirectory(String inputFolderPath) {
        // Extract folder name from path (e.g., "logs/m10z" -> "m10z")
        String folderName = Paths.get(inputFolderPath).getFileName().toString();
        return inputFolderPath + "/" + AnalyzerConfig.OUTPUT_SUBFOLDER;
    }
    
    /**
     * Creates the full output path by combining output directory and filename
     */
    private String createOutputPath(String outputDir, String filename) {
        return outputDir + "/" + filename;
    }
    
    /**
     * Creates output directory if it doesn't exist
     */
    private void createOutputDirectoryIfNotExists(String outputDir) {
        try {
            Path path = Paths.get(outputDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                ExceptionHandler.logInfo("Created output directory: " + outputDir);
            }
        } catch (IOException e) {
            ExceptionHandler.handleIOException(e, "creating output directory: " + outputDir);
        }
    }
}
