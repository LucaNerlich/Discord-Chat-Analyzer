package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.TreeMap;

public class MostAttachmentsRanking extends Ranking {

    @Getter
    private long attachmentsSent;
    @Getter
    private TreeMap<AuthorData, Long> mostAttachments;

    public MostAttachmentsRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateAttachments(authorDataCollection);
        countAttachments(authorDataCollection);
    }

    private void calculateAttachments(Collection<AuthorData> authorDataCollection) {
        mostAttachments = new TreeMap<>(new AuthorData.AuthorDataAttachmentsCountComparator());
        authorDataCollection.forEach(authorData -> mostAttachments.put(authorData, authorData.getAttachmentsSent()));
    }

    private void countAttachments(Collection<AuthorData> authorDataCollection) {
        attachmentsSent = authorDataCollection.stream()
                .mapToLong(AuthorData::getAttachmentsSent)
                .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MOST_ATTACHMENTS;
    }
}
