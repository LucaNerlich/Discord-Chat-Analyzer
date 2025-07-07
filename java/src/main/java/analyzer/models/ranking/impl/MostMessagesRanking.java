package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;


public class MostMessagesRanking extends Ranking {
    
    @Getter
    private long messagesSent;
    @Getter
    private TreeMap<AuthorData, Long> mostMessages;
    
    public MostMessagesRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateMessageRanking(authorDataList);
        countMessages(authorDataList);
    }
    
    private void calculateMessageRanking(List<AuthorData> authorDataList) {
        mostMessages = new TreeMap<>(new AuthorData.AuthorDataMessagesCountComparator());
        authorDataList.forEach(authorData -> mostMessages.put(authorData, authorData.getMessagesSent()));
    }
    
    private void countMessages(List<AuthorData> authorDataList) {
        for (AuthorData authorData : authorDataList) {
            messagesSent = messagesSent + authorData.getMessagesSent();
        }
    }
    
    @Override
    public String getOutputFilePath() {
        return AnalyzerConfig.RANKING_MOST_MESSAGES;
    }
}
