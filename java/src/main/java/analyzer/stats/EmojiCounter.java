package analyzer.stats;

import analyzer.models.Author;
import analyzer.models.message.reaction.Emoji;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmojiCounter {
    private String author;
    private Map<Emoji, Integer> emojisReceived;
    
    public EmojiCounter(Author author, Map<Emoji, Integer> emojiCounterMap) {
        this.author = author.getNickname();
        this.emojisReceived = emojiCounterMap;
    }
}
