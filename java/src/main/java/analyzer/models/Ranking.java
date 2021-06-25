package analyzer.models;

import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
public class Ranking {
    
    private long allMessages;
    private transient List<AuthorData> authorDataList;
    private TreeMap<AuthorData, Long> mostMessages;
    private TreeMap<AuthorData, LocalDate> oldestAccount;
    
    public Ranking(List<AuthorData> authorDataList) {
        this.authorDataList = authorDataList;
        countMessages();
        calculateMessageRanking();
        calculateAccountAgeRanking();
    }
    
    private void calculateAccountAgeRanking() {
        oldestAccount = new TreeMap<>(new AuthorData.AuthorDataFirstMessageComparator());
        authorDataList.forEach(authorData -> {
            oldestAccount.put(authorData, authorData.getEarliestLocalDate());
        });
    }
    
    private void calculateMessageRanking() {
        mostMessages = new TreeMap<>(new AuthorData.AuthorDataMessagesCountComparator());
        authorDataList.forEach(authorData -> {
            mostMessages.put(authorData, authorData.getMessagesSent());
        });
    }
    
    private void countMessages() {
        for (AuthorData authorData : authorDataList) {
            allMessages = allMessages + authorData.getMessagesSent();
        }
    }
}
