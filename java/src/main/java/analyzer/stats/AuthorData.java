package analyzer.stats;

import analyzer.models.Author;
import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the analyzed data for a single Author.
 */
@Getter
@Setter
public class AuthorData {

    private transient Author author;
    private transient LocalDate earliestLocalDate;
    private transient long totalWordCount = 0; // More memory efficient than storing all counts

    private String authorId;
    private String firstMessageSent;
    private double averageWordsPerMessage;
    private long messagesSent = 0;
    private long embedsSent = 0;
    private long attachmentsSent = 0;
    private long sumEmojisReceived = 0;
    private long timesMentioned = 0;
    private Map<Emoji, Integer> emojisReceived = new TreeMap<>(new Emoji.EmojiComparator());

    // Social graph data - who this user mentions and who mentions them
    private Map<String, Integer> mentionsSent = new ConcurrentHashMap<>();
    private Map<String, Integer> mentionsReceived = new ConcurrentHashMap<>();

    public void addWordCount(int wordCount) {
        totalWordCount += wordCount;
    }

    public long getWordCountSum() {
        return totalWordCount;
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

    public void addMentionSent(String mentionedUserId) {
        mentionsSent.merge(mentionedUserId, 1, Integer::sum);
    }

    public void addMentionReceived(String mentionerUserId) {
        mentionsReceived.merge(mentionerUserId, 1, Integer::sum);
    }

    public long getTotalMentionsSent() {
        return mentionsSent.values().stream().mapToLong(Integer::longValue).sum();
    }

    public long getTotalMentionsReceived() {
        return mentionsReceived.values().stream().mapToLong(Integer::longValue).sum();
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

    @Override
    public String toString() {
        return author.getNickname();
    }

    public String getLocalDateAsString(LocalDate localDate) {
        return localDate.getDayOfMonth() +
            "." +
            localDate.getMonthValue() +
            "." +
            localDate.getYear();
    }

    private void setFirstMessageSent() {
        firstMessageSent = getLocalDateAsString(earliestLocalDate);
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

    public static class AuthorDataMentionsSentComparator implements Comparator<AuthorData> {
        @Override
        public int compare(AuthorData o1, AuthorData o2) {
            final Long mentionsSent1 = o1.getTotalMentionsSent();
            final Long mentionsSent2 = o2.getTotalMentionsSent();
            final int compare = mentionsSent2.compareTo(mentionsSent1);

            if (compare == 0) {
                return 1;
            } else {
                return compare;
            }
        }
    }
}
