package analyzer.stats;

import analyzer.models.Author;
import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds the analyzed data for a single Author.
 */
@Getter
@Setter
public class AuthorData {
    
    private transient Author author;
    private transient LocalDate earliestLocalDate;
    private transient List<Integer> wordsPerMessage = new ArrayList<>();
    
    private String authorId;
    private String firstMessageSent;
    private double averageWordsPerMessage;
    private long messagesSent = 0;
    private long embedsSent = 0;
    private long attachmentsSent = 0;
    private long sumEmojisReceived = 0;
    private long timesMentioned = 0;
    private Map<Emoji, Integer> emojisReceived = new TreeMap<>(new Emoji.EmojiComparator());
    
    public void addWordCount(int wordCount) {
        wordsPerMessage.add(wordCount);
    }
    
    public int getWordCountSum() {
        return wordsPerMessage.stream().mapToInt(Integer::intValue).sum();
    }
    
    public void incrementMessages() {
        messagesSent++;
    }
    
    public void incrementTimesMentioned() {
        timesMentioned++;
    }
    
    public void incrementAttachments() {
        attachmentsSent++;
    }
    
    public void incrementEmbeds() {
        embedsSent++;
    }
    
    public void setEarliestLocalDate(String timestamp) {
        //  "timestamp": "2018-10-18T08:52:29.781+00:00",
        final String yearMonthDay = timestamp.substring(0, 10);
        LocalDate localDate = LocalDate.parse(yearMonthDay);
        if (earliestLocalDate == null || earliestLocalDate.isAfter(localDate)) {
            earliestLocalDate = localDate;
        }
        setFirstMessageSent();
    }
    
    public static class AuthorDataMessagesCountComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long messagesSent1 = o1.getMessagesSent();
            final Long messagesSent2 = o2.getMessagesSent();
            final int compare = messagesSent2.compareTo(messagesSent1);
            
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    public static class AuthorDataMentionsCountComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long timesMentioned1 = o1.getTimesMentioned();
            final Long timesMentioned2 = o2.getTimesMentioned();
            final int compare = timesMentioned2.compareTo(timesMentioned1);
    
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    public static class AuthorDataEmbedsCountComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long embedsSent1 = o1.getEmbedsSent();
            final Long embedsSent2 = o2.getEmbedsSent();
            final int compare = embedsSent2.compareTo(embedsSent1);
    
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    public static class AuthorDataAttachmentsCountComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long attachmentsSent1 = o1.getAttachmentsSent();
            final Long attachmentsSent2 = o2.getAttachmentsSent();
            final int compare = attachmentsSent2.compareTo(attachmentsSent1);
    
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    public static class AuthorDataFirstMessageComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final int compare = o1.getEarliestLocalDate().compareTo(o2.getEarliestLocalDate());
            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
    
    @Override
    public String toString() {
        return author.getNickname();
    }
    
    public String getLocalDateAsString(LocalDate localDate) {
        return new StringBuilder()
                .append(localDate.getDayOfMonth())
                .append(".")
                .append(localDate.getMonthValue())
                .append(".")
                .append(localDate.getYear())
                .toString();
    }
    
    private void setFirstMessageSent() {
        firstMessageSent = getLocalDateAsString(earliestLocalDate);
    }
}
