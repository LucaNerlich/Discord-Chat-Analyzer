package analyzer.models;

import analyzer.models.message.reaction.Emoji;
import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class Ranking {
    
    private long allMessages;
    private transient List<AuthorData> authorDataList;
    private transient List<Emoji> emojiCountList;
    private TreeMap<AuthorData, Long> mostMessages;
    private TreeMap<AuthorData, String> oldestAccount;
    private TreeMap<Emoji, Integer> mostCommonReaction = new TreeMap<>(new Emoji.EmojiCountComparator());
    
    public Ranking(List<AuthorData> authorDataList) {
        this.authorDataList = authorDataList;
        countMessages();
        calculateMessageRanking();
        calculateAccountAgeRanking();
        calculateMostCommonReaction();
    }
    
    private void calculateMostCommonReaction() {
        Map<Emoji, Integer> emojiCount = new HashMap<>();
        authorDataList.forEach(authorData -> {
            final Map<Emoji, Integer> emojisReceived = authorData.getEmojisReceived();
            emojisReceived.forEach((key, value) -> {
                if (emojiCount.containsKey(key)) {
                    emojiCount.put(key, emojiCount.get(key) + value);
                } else {
                    emojiCount.put(key, value);
                }
            });
        });
        for (Map.Entry<Emoji, Integer> emoji : emojiCount.entrySet()) {
            emoji.getKey().setCount(emoji.getValue());
        }
        mostCommonReaction.putAll(emojiCount);
    }
    
    private void calculateAccountAgeRanking() {
        oldestAccount = new TreeMap<>(new AuthorData.AuthorDataFirstMessageComparator());
        authorDataList.forEach(authorData -> oldestAccount.put(authorData, authorData.getLocalDateAsString(authorData.getEarliestLocalDate())));
    }
    
    private void calculateMessageRanking() {
        mostMessages = new TreeMap<>(new AuthorData.AuthorDataMessagesCountComparator());
        authorDataList.forEach(authorData -> mostMessages.put(authorData, authorData.getMessagesSent()));
    }
    
    private void countMessages() {
        for (AuthorData authorData : authorDataList) {
            allMessages = allMessages + authorData.getMessagesSent();
        }
    }
}
