package analyzer;

import analyzer.models.Author;
import analyzer.models.channel.Channel;
import analyzer.models.message.Message;
import analyzer.models.message.reaction.Emoji;
import analyzer.models.message.reaction.Reaction;
import analyzer.stats.EmojiCounter;
import analyzer.stats.Statistic;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class Analyzer {
    
    private final Channel channel;
    private Statistic statistic;
    private List<EmojiCounter> emojiCounters;
    
    public Analyzer(final Channel channel) {
        this.channel = channel;
        if (channel != null) {
            this.statistic = analyze(channel);
        }
        emojiCounters = createEmojiCounter(statistic);
    }
    
    public List<EmojiCounter> createEmojiCounter(Statistic statistic) {
        final List<EmojiCounter> result = new ArrayList<>();
        
        if (statistic != null) {
            final Map<Author, Map<Emoji, Integer>> authorEmojiMap = statistic.getAuthorEmojiMap();
            final Set<Map.Entry<Author, Map<Emoji, Integer>>> entries = authorEmojiMap.entrySet();
            entries.forEach(authorEmojiMapping -> result.add(new EmojiCounter(authorEmojiMapping.getKey(), authorEmojiMapping.getValue())));
        }
        return result;
    }
    
    private Statistic analyze(final Channel channel) {
        final Statistic result = new Statistic();
        final Message[] messages = channel.getMessages();
        
        for (Message message : messages) {
            final Author author = message.getAuthor();
            final Reaction[] reactions = message.getReactions();
            for (Reaction reaction : reactions) {
                final Emoji emoji = reaction.getEmoji();
                final short count = reaction.getCount();
                result.addEmoji(author, emoji, count);
            }
        }
        
        return result;
    }
}
