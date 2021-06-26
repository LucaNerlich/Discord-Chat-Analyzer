package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;

import java.util.List;

public class AvgWordCountRanking extends Ranking {
    
    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-avg-word-count.json";
    
    // todo print wordcount to json
    
    public AvgWordCountRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateAvgWordCount(authorDataList);
    }
    
    private void calculateAvgWordCount(List<AuthorData> authorDataList) {
        authorDataList.forEach(authorData -> {
            final double wordCountSum = authorData.getWordCountSum();
            final double messagesSent = authorData.getMessagesSent();
            
            if (wordCountSum > 0 && messagesSent > 0) {
                authorData.setAverageWordsPerMessage(wordCountSum / messagesSent);
            }
        });
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
