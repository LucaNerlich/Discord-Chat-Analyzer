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

        // Process folders in parallel for better performance
        AnalyzerConfig.LOG_FOLDER_PATHS.parallelStream().forEach(folderPath -> {
            ExceptionHandler.logInfo("Processing folder: " + folderPath);

            final List<Channel> channels = fileService.parseJsonToChannels(folderPath);
            final Analyzer analyzer = new Analyzer(channels);
            final String outputDir = fileService.createOutputDirectory(folderPath);

            // Write Author Data
            fileService.writeAuthorData(analyzer.getAuthorDataMap(), outputDir);

            // Write Rankings in parallel
            writeRankings(analyzer, fileService, outputDir);

            // Write Social Graph Visualizations
            writeSocialGraphVisualizations(analyzer, fileService, outputDir);

            ExceptionHandler.logInfo("Completed analysis for folder: " + folderPath);
        });

        ExceptionHandler.logInfo("All analyses completed successfully");
    }

    private static void writeRankings(Analyzer analyzer, FileService fileService, String outputDir) {
        ExecutorService executorService = Executors.newFixedThreadPool(AnalyzerConfig.THREAD_POOL_SIZE);
        try {
            Arrays.stream(RankingType.values())
                    .forEach(rankingType ->
                            executorService.execute(() -> {
                                final Ranking ranking = analyzer.getRanking(rankingType);
                                fileService.writeRanking(ranking, outputDir);
                            }));
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "writing rankings");
        } finally {
            executorService.shutdown();
        }
    }

    private static void writeSocialGraphVisualizations(Analyzer analyzer, FileService fileService, String outputDir) {
        try {
            // Generate and write text visualization
            String textVisualization = analyzer.generateTextVisualization();
            fileService.writeSocialGraphTextVisualization(textVisualization, outputDir);

            // Generate and write HTML visualization
            String htmlContent = analyzer.generateHTMLContent();
            fileService.writeSocialGraphHTMLVisualization(htmlContent, outputDir);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, "writing social graph visualizations");
        }
    }


}
