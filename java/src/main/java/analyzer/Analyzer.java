package analyzer;

import analyzer.models.Author;
import analyzer.models.channel.Channel;
import analyzer.models.message.Message;
import analyzer.models.message.reaction.Emoji;
import analyzer.models.message.reaction.Reaction;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class Analyzer {
    
    private final Map<Author, AuthorData> authorDataMap;
    private static final int MIN_AMOUNT_MESSAGES = 10;
    
    public Analyzer(final List<Channel> channels) {
        this.authorDataMap = new TreeMap<>(new Author.AuthorComparator());
        
        if (channels != null) {
            for (Channel channel : channels) {
                analyzeChannel(channel);
                analyzeAuthor(authorDataMap);
                removeAuthors();
            }
        }
    }
    
    private void removeAuthors() {
        authorDataMap.values().removeIf(
                authorData -> authorData.getMessagesSent() < MIN_AMOUNT_MESSAGES
        );
    }
    
    private void analyzeAuthor(Map<Author, AuthorData> authorDataMap) {
        authorDataMap.entrySet().parallelStream().forEach(this::countEmojis);
    }
    
    private void countEmojis(Map.Entry<Author, AuthorData> entry) {
        final AuthorData authorData = entry.getValue();
        authorData.setSumEmojisReceived(
                authorData.getEmojisReceived()
                        .values()
                        .stream()
                        .mapToLong(value -> value)
                        .sum()
        );
    }
    
    private void analyzeChannel(final Channel channel) {
        for (Message message : channel.getMessages()) {
            final Author author = message.getAuthor();
            
            if (!authorDataMap.containsKey(author)) {
                final AuthorData authorData = new AuthorData();
                populateAuthorDataMap(authorData, message);
            } else {
                populateAuthorDataMap(authorDataMap.get(author), message);
            }
        }
    }
    
    private void populateAuthorDataMap(AuthorData authorData, Message message) {
        analyzeMessage(authorData, message);
        authorData.setAuthorId(message.getAuthor().getId());
        authorData.setAuthor(message.getAuthor());
        authorData.setEarliestLocalDate(message.getTimestamp());
        authorDataMap.put(message.getAuthor(), authorData);
    }
    
    private void analyzeMessage(AuthorData authorData, Message message) {
        authorData.incrementMessages();
        analyzeReactions(authorData, message.getReactions());
    }
    
    private void analyzeReactions(AuthorData authorData, Reaction[] reactions) {
        for (Reaction reaction : reactions) {
            final Emoji emoji = reaction.getEmoji();
            final int count = reaction.getCount();
            
            final Map<Emoji, Integer> emojisRecieved = authorData.getEmojisReceived();
            if (emojisRecieved.containsKey(emoji)) {
                emojisRecieved.put(emoji, emojisRecieved.get(emoji) + count);
            } else {
                emojisRecieved.put(emoji, count);
            }
        }
    }
}
