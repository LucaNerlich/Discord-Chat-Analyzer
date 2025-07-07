package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;

public class MostEmbedsRanking extends Ranking {

    private static final String OUTPUT_FILE_NAME = "logs/ranking-most-embeds.json";

    @Getter
    private long embedsSent;
    @Getter
    private TreeMap<AuthorData, Long> mostEmbeds;

    public MostEmbedsRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateEmbeds(authorDataList);
        countEmbeds(authorDataList);
    }

    private void calculateEmbeds(List<AuthorData> authorDataList) {
        mostEmbeds = new TreeMap<>(new AuthorData.AuthorDataEmbedsCountComparator());
        authorDataList.forEach(authorData -> mostEmbeds.put(authorData, authorData.getEmbedsSent()));
    }

    private void countEmbeds(List<AuthorData> authorDataList) {
        for (AuthorData authorData : authorDataList) {
            embedsSent = embedsSent + authorData.getEmbedsSent();
        }
    }

    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
