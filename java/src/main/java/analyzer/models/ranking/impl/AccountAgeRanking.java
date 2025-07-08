package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;

public class AccountAgeRanking extends Ranking {

    @Getter
    private TreeMap<AuthorData, String> joinedServer;

    public AccountAgeRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateAccountAgeRanking(authorDataCollection);
    }

    // todo bug, somehow does not add a lot of accounts
    private void calculateAccountAgeRanking(Collection<AuthorData> authorDataCollection) {
        joinedServer = new TreeMap<>(new AuthorData.AuthorDataFirstMessageComparator());
        authorDataCollection.stream()
            .filter(authorData -> authorData.getMessagesSent() >= AnalyzerConfig.MIN_AMOUNT_MESSAGES)
            .forEach(authorData -> joinedServer.put(authorData, authorData.getLocalDateAsString(authorData.getEarliestLocalDate())));
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_ACCOUNT_AGE;
    }
}
