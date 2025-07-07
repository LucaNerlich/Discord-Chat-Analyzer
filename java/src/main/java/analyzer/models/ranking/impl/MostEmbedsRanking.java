package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;

public class MostEmbedsRanking extends Ranking {

    @Getter
    private long embedsSent;
    @Getter
    private TreeMap<AuthorData, Long> mostEmbeds;

    public MostEmbedsRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateEmbeds(authorDataCollection);
        countEmbeds(authorDataCollection);
    }

    private void calculateEmbeds(Collection<AuthorData> authorDataCollection) {
        mostEmbeds = new TreeMap<>(new AuthorData.AuthorDataEmbedsCountComparator());
        authorDataCollection.forEach(authorData -> mostEmbeds.put(authorData, authorData.getEmbedsSent()));
    }

    private void countEmbeds(Collection<AuthorData> authorDataCollection) {
        embedsSent = authorDataCollection.stream()
                .mapToLong(AuthorData::getEmbedsSent)
                .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MOST_EMBEDS;
    }
}
