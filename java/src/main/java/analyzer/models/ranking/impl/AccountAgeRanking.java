package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;

public class AccountAgeRanking extends Ranking {
    
    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-account-age.json";
    
    @Getter
    private TreeMap<AuthorData, String> joinedServer;
    
    public AccountAgeRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateAccountAgeRanking(authorDataList);
    }
    
    private void calculateAccountAgeRanking(List<AuthorData> authorDataList) {
        joinedServer = new TreeMap<>(new AuthorData.AuthorDataFirstMessageComparator());
        authorDataList.stream()
                .filter(authorData -> authorData.getMessagesSent() >= 10)
                .forEach(authorData -> joinedServer.put(authorData, authorData.getLocalDateAsString(authorData.getEarliestLocalDate())));
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
