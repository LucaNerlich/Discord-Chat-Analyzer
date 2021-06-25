package analyzer.stats;

import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds the analyzed data for a single Author.
 */
@Getter
@Setter
public class AuthorData {
    
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
