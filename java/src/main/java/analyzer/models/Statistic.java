package analyzer.models;

import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds the analyzed data.
 */
@Getter
@Setter
public class Statistic {
    
    // who receives which emoji and how often
    
    private class StatsAuthor {
        private final String id;
        private final String name;
        
        public StatsAuthor(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StatsAuthor that = (StatsAuthor) o;
            return id.equals(that.id) && name.equals(that.name);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }
    
    private Map<Author, Map<Emoji, Integer>> authorEmojiMap = new HashMap<>();
    
    public void addAuthor(Author author) {
//        final StatsAuthor statsAuthor = getStatsAuthor(author);
        
        if (!authorEmojiMap.containsKey(author)) {
            authorEmojiMap.put(author, new HashMap<>());
        }
    }
    
    public void addEmoji(Author author, Emoji emoji, int count) {
//        final StatsAuthor statsAuthor = getStatsAuthor(author);
        
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
    
    private StatsAuthor getStatsAuthor(Author author) {
        return new StatsAuthor(author.getId(), author.getName());
    }
    
    private void addAuthor(Author author, Emoji emoji, int count) {
//        final StatsAuthor statsAuthor = getStatsAuthor(author);
        addAuthor(author);
        authorEmojiMap.get(author).put(emoji, count);
    }
}
