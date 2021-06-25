package analyzer;

import analyzer.models.Author;
import analyzer.models.Statistic;
import analyzer.models.channel.Channel;
import analyzer.models.message.Message;
import analyzer.models.message.reaction.Emoji;
import analyzer.models.message.reaction.Reaction;
import lombok.Getter;

@Getter
public class Analyzer {
    
    private final Channel channel;
    private Statistic statistic;
    
    public Analyzer(final Channel channel) {
        this.channel = channel;
        if (channel != null) {
            this.statistic = analyze(channel);
        }
    }
    
    private Statistic analyze(final Channel channel) {
        final Statistic statistic = new Statistic();
        final Message[] messages = channel.getMessages();
        
        for (Message message : messages) {
            final Author author = message.getAuthor();
            final Reaction[] reactions = message.getReactions();
            for (Reaction reaction : reactions) {
                final Emoji emoji = reaction.getEmoji();
                final short count = reaction.getCount();
                statistic.addEmoji(author, emoji, count);
            }
        }
        
        return statistic;
    }
}
