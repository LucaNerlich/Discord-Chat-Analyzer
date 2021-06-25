package analyzer.models.message.reaction;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Emoji {
    private String id;
    private String name;
    private boolean isAnimated;
    private String imageUrl;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Emoji emoji = (Emoji) o;
        return isAnimated == emoji.isAnimated && Objects.equals(id, emoji.id) && Objects.equals(name, emoji.name) && Objects.equals(imageUrl, emoji.imageUrl);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, isAnimated, imageUrl);
    }
}
