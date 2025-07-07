package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import analyzer.utils.ComparatorUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AvgWordCountRanking extends Ranking {

    private static final String OUTPUT_FILE_NAME = "logs/ranking-most-attachments.json";
    @Getter
    @Setter
    private Map<Double, String> averageWordsPerMessage = new TreeMap<>(ComparatorUtils.descendingWithTieBreaker(d -> d));

    public AvgWordCountRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateAvgWordCount(authorDataList);
    }

    // round to configurable precision points
    private static double round(double value) {
        double scale = Math.pow(10, AnalyzerConfig.DECIMAL_PRECISION);
        return Math.round(value * scale) / scale;
    }

    private void calculateAvgWordCount(List<AuthorData> authorDataList) {
        authorDataList
                .stream()
                .filter(authorData -> authorData.getMessagesSent() >= AnalyzerConfig.MIN_MESSAGES_FOR_AVG_WORD_COUNT)
                .forEach(authorData -> {
                    final double wordCountSum = authorData.getWordCountSum();
                    final double messagesSent = authorData.getMessagesSent();

                    if (wordCountSum > 0 && messagesSent > 0) {
                        authorData.setAverageWordsPerMessage(round(wordCountSum / messagesSent));
                        averageWordsPerMessage.put(authorData.getAverageWordsPerMessage(), authorData.getAuthor().getNickname());
                    }
                });
    }

    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
