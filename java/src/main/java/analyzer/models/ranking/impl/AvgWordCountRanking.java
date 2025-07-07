package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import analyzer.utils.ComparatorUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class AvgWordCountRanking extends Ranking {

    @Getter
    @Setter
    private Map<Double, String> averageWordsPerMessage = new TreeMap<>(ComparatorUtils.descendingWithTieBreaker(d -> d));

    public AvgWordCountRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateAvgWordCount(authorDataCollection);
    }

    // round to configurable precision points
    private static double round(double value) {
        double scale = Math.pow(10, AnalyzerConfig.DECIMAL_PRECISION);
        return Math.round(value * scale) / scale;
    }

    private void calculateAvgWordCount(Collection<AuthorData> authorDataCollection) {
        authorDataCollection
                .parallelStream() // Use parallel processing for better performance
                .filter(authorData -> authorData.getMessagesSent() >= AnalyzerConfig.MIN_MESSAGES_FOR_AVG_WORD_COUNT)
                .filter(authorData -> authorData.getWordCountSum() > 0) // Early filter
                .forEach(authorData -> {
                    final double wordCountSum = authorData.getWordCountSum();
                    final double messagesSent = authorData.getMessagesSent();

                    authorData.setAverageWordsPerMessage(round(wordCountSum / messagesSent));
                    averageWordsPerMessage.put(authorData.getAverageWordsPerMessage(), authorData.getAuthor().getNickname());
                });
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_AVG_WORD_COUNT;
    }
}
