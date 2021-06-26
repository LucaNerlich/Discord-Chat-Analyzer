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
            return o2.compareTo(o1);
        }
    }
    
    private void calculateAvgWordCount(List<AuthorData> authorDataList) {
        authorDataList.forEach(authorData -> {
            final double wordCountSum = authorData.getWordCountSum();
            final double messagesSent = authorData.getMessagesSent();
            
            if (wordCountSum > 0 && messagesSent > 0) {
                authorData.setAverageWordsPerMessage(wordCountSum / messagesSent);
                averageWordsPerMessage.put(authorData.getAverageWordsPerMessage(), authorData.getAuthor().getNickname());
            }
        });
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
