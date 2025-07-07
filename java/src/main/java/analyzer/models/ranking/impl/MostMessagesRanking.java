package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;


public class MostMessagesRanking extends Ranking {

    @Getter
    private long messagesSent;
    @Getter
    private TreeMap<AuthorData, Long> mostMessages;

    public MostMessagesRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateMessageRanking(authorDataCollection);
        countMessages(authorDataCollection);
    }

    private void calculateMessageRanking(Collection<AuthorData> authorDataCollection) {
        mostMessages = new TreeMap<>(new AuthorData.AuthorDataMessagesCountComparator());
        authorDataCollection.forEach(authorData -> mostMessages.put(authorData, authorData.getMessagesSent()));
    }

    private void countMessages(Collection<AuthorData> authorDataCollection) {
        messagesSent = authorDataCollection.stream()
                .mapToLong(AuthorData::getMessagesSent)
                .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MOST_MESSAGES;
    }
}
