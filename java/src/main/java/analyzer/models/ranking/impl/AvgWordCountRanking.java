package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AvgWordCountRanking extends Ranking {
    
    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-avg-word-count.json";
    
    @Getter
    @Setter
    private Map<Double, String> averageWordsPerMessage = new TreeMap<>(new AvgWordCountComparator());
    
    public AvgWordCountRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateAvgWordCount(authorDataList);
    }
    
    public static class AvgWordCountComparator implements Comparator<Double> {
        @Override
        public int compare(Double o1, Double o2) {
            final int compare = o2.compareTo(o1);
            
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    private void calculateAvgWordCount(List<AuthorData> authorDataList) {
        authorDataList
                .stream()
                .filter(authorData -> authorData.getMessagesSent() >= 10)
                .forEach(authorData -> {
                    final double wordCountSum = authorData.getWordCountSum();
                    final double messagesSent = authorData.getMessagesSent();
                    
                    if (wordCountSum > 0 && messagesSent > 0) {
                        authorData.setAverageWordsPerMessage(round(wordCountSum / messagesSent));
                        averageWordsPerMessage.put(authorData.getAverageWordsPerMessage(), authorData.getAuthor().getNickname());
                    }
                });
    }
    
    // round to 2 precision points
    private static double round(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
