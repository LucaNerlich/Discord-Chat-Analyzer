package analyzer.models.message.reaction;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
public class Emoji {
    private String id;
    private String name;
    private boolean isAnimated;
    private String imageUrl;
    private transient Integer count;
    
    
    public static class EmojiCountComparator implements Comparator<Emoji> {
        @Override
        public int compare(Emoji o1, Emoji o2) {
            return o2.count.compareTo(o1.count);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Emoji emoji = (Emoji) o;
        if (StringUtils.hasText(emoji.name) && StringUtils.hasText(name)) {
            return name.equals(emoji.name);
        } else {
            return Objects.equals(id, emoji.id);
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, isAnimated, imageUrl);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static class EmojiComparator implements Comparator<Emoji> {
        
        // compare by name
        @Override
        public int compare(Emoji o1, Emoji o2) {
            int result = 0;
            
            if (StringUtils.hasText(o1.getName()) && StringUtils.hasText(o2.getName())) {
                result = o1.getName().compareToIgnoreCase(o2.getName());
            }
            
            return result;
        }
    }
}
