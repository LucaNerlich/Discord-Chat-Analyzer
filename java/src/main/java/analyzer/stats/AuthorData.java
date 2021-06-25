package analyzer.stats;

import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Holds the analyzed data for a single Author.
 */
@Getter
@Setter
public class AuthorData {
    
    private long messagesSent = 0;
    private Map<Emoji, Integer> emojisRecieved = new TreeMap<>(new Emoji.EmojiComparator());
    
    public void incrementMessages() {
        messagesSent++;
    }
}
