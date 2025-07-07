package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;


public class TimesMentionedRanking extends Ranking {

    @Getter
    private long countMentions;
    @Getter
    private TreeMap<AuthorData, Long> timesMentioned;

    public TimesMentionedRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateMentionRanking(authorDataList);
        countMentions(authorDataList);
    }

    private void calculateMentionRanking(List<AuthorData> authorDataList) {
        timesMentioned = new TreeMap<>(new AuthorData.AuthorDataMentionsCountComparator());
        authorDataList.forEach(authorData -> timesMentioned.put(authorData, authorData.getTimesMentioned()));
    }

    private void countMentions(List<AuthorData> authorDataList) {
        for (AuthorData authorData : authorDataList) {
            countMentions = countMentions + authorData.getTimesMentioned();
        }
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_TIMES_MENTIONED;
    }
}
