package analyzer.models.ranking.impl;

import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;

public class MostAttachmentsRanking extends Ranking {
    
    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-most-attachments.json";
    
    @Getter
    private long attachmentsSent;
    @Getter
    private TreeMap<AuthorData, Long> mostAttachments;
    
    public MostAttachmentsRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateAttachments(authorDataList);
        countAttachments(authorDataList);
    }
    
    private void calculateAttachments(List<AuthorData> authorDataList) {
        mostAttachments = new TreeMap<>(new AuthorData.AuthorDataAttachmentsCountComparator());
        authorDataList.forEach(authorData -> mostAttachments.put(authorData, authorData.getAttachmentsSent()));
    }
    
    private void countAttachments(List<AuthorData> authorDataList) {
        for (AuthorData authorData : authorDataList) {
            attachmentsSent = attachmentsSent + authorData.getAttachmentsSent();
        }
    }
    
    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
