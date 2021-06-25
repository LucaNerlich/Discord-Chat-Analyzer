package analyzer.stats;

import analyzer.models.Author;
import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the analyzed data.
 */
@Getter
@Setter
public class Statistic {
    
    // who receives which emoji and how often
    private Map<Author, Map<Emoji, Integer>> authorEmojiMap = new HashMap<>();
    
    public void addAuthor(Author author) {
        if (!authorEmojiMap.containsKey(author)) {
            authorEmojiMap.put(author, new HashMap<>());
        }
    }
    
    public void addEmoji(Author author, Emoji emoji, int count) {
        if (!authorEmojiMap.containsKey(author)) {
            addAuthor(author, emoji, count);
        } else {
            final Map<Emoji, Integer> emojiIntegerMap = authorEmojiMap.get(author);
            if (emojiIntegerMap.containsKey(emoji)) {
                final Integer countOld = emojiIntegerMap.get(emoji);
                emojiIntegerMap.put(emoji, countOld + count);
            } else {
                emojiIntegerMap.put(emoji, count);
            }
        }
    }
    
    private void addAuthor(Author author, Emoji emoji, int count) {
        addAuthor(author);
        authorEmojiMap.get(author).put(emoji, count);
    }
}
