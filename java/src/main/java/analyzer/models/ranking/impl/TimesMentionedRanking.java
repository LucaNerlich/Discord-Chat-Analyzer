package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;


public class TimesMentionedRanking extends Ranking {

    @Getter
    private long countMentions;
    @Getter
    private TreeMap<AuthorData, Long> timesMentioned;

    public TimesMentionedRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateMentionRanking(authorDataCollection);
        countMentions(authorDataCollection);
    }

    private void calculateMentionRanking(Collection<AuthorData> authorDataCollection) {
        timesMentioned = new TreeMap<>(new AuthorData.AuthorDataMentionsCountComparator());
        authorDataCollection.forEach(authorData -> timesMentioned.put(authorData, authorData.getTimesMentioned()));
    }

    private void countMentions(Collection<AuthorData> authorDataCollection) {
        countMentions = authorDataCollection.stream()
            .mapToLong(AuthorData::getTimesMentioned)
            .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_TIMES_MENTIONED;
    }
}
