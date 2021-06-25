package analyzer.models.message.reaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Emoji {
    private String id;
    private String name;
    private boolean isAnimated;
    private String imageUrl;
}
