package analyzer;

import analyzer.config.AnalyzerConfig;
import analyzer.models.channel.Channel;
import analyzer.models.ranking.Ranking;
import analyzer.models.ranking.RankingType;
import analyzer.service.FileService;
import analyzer.utils.ExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

        public static void main(String[] args) {
        ExceptionHandler.logInfo("Starting Discord Chat Analysis");
        
        final FileService fileService = new FileService();
        
        // Process each folder separately
        for (String folderPath : AnalyzerConfig.LOG_FOLDER_PATHS) {
            ExceptionHandler.logInfo("Processing folder: " + folderPath);
            
            final List<Channel> channels = fileService.parseJsonToChannels(folderPath);
            final Analyzer analyzer = new Analyzer(channels);
            final String outputDir = fileService.createOutputDirectory(folderPath);

            // Write Author Data
            fileService.writeAuthorData(analyzer.getAuthorDataMap(), outputDir);

            // Write Rankings in parallel
            writeRankings(analyzer, fileService, outputDir);
            
            ExceptionHandler.logInfo("Completed analysis for folder: " + folderPath);
        }
        
        ExceptionHandler.logInfo("All analyses completed successfully");
    }

    private static void writeRankings(Analyzer analyzer, FileService fileService, String outputDir) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(AnalyzerConfig.THREAD_POOL_SIZE)) {
            Arrays.stream(RankingType.values())
                    .forEach(rankingType ->
                            executorService.execute(() -> {
                                final Ranking ranking = analyzer.getRanking(rankingType);
                                fileService.writeRanking(ranking, outputDir);
                            }));
            executorService.shutdown();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "writing rankings");
        }
    }


}
