package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;


public class MostMessagesRanking extends Ranking {
    
    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-most-messages.json";
    
    @Getter
    private long allMessages;
    @Getter
    private TreeMap<AuthorData, Long> mostMessages;
    
    public MostMessagesRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        countMessages(authorDataList);
        calculateMessageRanking(authorDataList);
    }
    
    private void calculateMessageRanking(List<AuthorData> authorDataList) {
        mostMessages = new TreeMap<>(new AuthorData.AuthorDataMessagesCountComparator());
        authorDataList.forEach(authorData -> mostMessages.put(authorData, authorData.getMessagesSent()));
    }
    
    private void countMessages(List<AuthorData> authorDataList) {
        for (AuthorData authorData : authorDataList) {
            allMessages = allMessages + authorData.getMessagesSent();
        }
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
