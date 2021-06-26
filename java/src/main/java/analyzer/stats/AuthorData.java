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
    private long sumEmojisReceived = 0;
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
    
    public void incrementEmbdes() {
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
            return messagesSent2.compareTo(messagesSent1);
        }
    }
    
    public static class AuthorDataEmbedsCountComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long embedsSent1 = o1.getEmbedsSent();
            final Long embedsSent2 = o2.getEmbedsSent();
            return embedsSent2.compareTo(embedsSent1);
        }
    }
    
    public static class AuthorDataFirstMessageComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            return o1.getEarliestLocalDate().compareTo(o2.getEarliestLocalDate());
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
