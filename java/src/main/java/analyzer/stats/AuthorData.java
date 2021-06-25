package analyzer.stats;

import analyzer.models.Author;
import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds the analyzed data for a single Author.
 */
@Getter
@Setter
public class AuthorData {
    
    private transient Author author;
    private String authorId;
    private transient LocalDate earliestLocalDate;
    private String firstMessageSent;
    private long messagesSent = 0;
    private long sumEmojisReceived = 0;
    private Map<Emoji, Integer> emojisReceived = new TreeMap<>(new Emoji.EmojiComparator());
    
    public void incrementMessages() {
        messagesSent++;
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
    
    private void setFirstMessageSent() {
        firstMessageSent = new StringBuilder()
                .append(earliestLocalDate.getDayOfMonth())
                .append(".")
                .append(earliestLocalDate.getMonthValue())
                .append(".")
                .append(earliestLocalDate.getYear())
                .toString();
    }
}
