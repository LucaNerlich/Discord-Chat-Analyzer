package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;

@Getter
public class MostMentionsSentRanking extends Ranking {

    private long totalMentionsSent;
    private TreeMap<AuthorData, Long> mostMentionsSent;

    public MostMentionsSentRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateMentionsSentRanking(authorDataCollection);
        countTotalMentionsSent(authorDataCollection);
    }

    private void calculateMentionsSentRanking(Collection<AuthorData> authorDataCollection) {
        mostMentionsSent = new TreeMap<>(new AuthorData.AuthorDataMentionsSentComparator());
        authorDataCollection.forEach(authorData -> 
            mostMentionsSent.put(authorData, authorData.getTotalMentionsSent())
        );
    }

    private void countTotalMentionsSent(Collection<AuthorData> authorDataCollection) {
        totalMentionsSent = authorDataCollection.stream()
                .mapToLong(AuthorData::getTotalMentionsSent)
                .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MOST_MENTIONS_SENT;
    }
} 