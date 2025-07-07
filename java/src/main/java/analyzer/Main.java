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
        final List<Channel> channels = fileService.parseJsonToChannels();
        final Analyzer analyzer = new Analyzer(channels);

        // Write Author Data
        fileService.writeAuthorData(analyzer.getAuthorDataMap());

        // Write Rankings in parallel
        writeRankings(analyzer, fileService);

        ExceptionHandler.logInfo("Analysis completed successfully");
    }

    private static void writeRankings(Analyzer analyzer, FileService fileService) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(AnalyzerConfig.THREAD_POOL_SIZE)) {
            Arrays.stream(RankingType.values())
                    .forEach(rankingType ->
                            executorService.execute(() -> {
                                final Ranking ranking = analyzer.getRanking(rankingType);
                                fileService.writeRanking(ranking);
                            }));
            executorService.shutdown();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "writing rankings");
        }
    }


}
