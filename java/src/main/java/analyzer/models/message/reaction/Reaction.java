package analyzer.models.message.reaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reaction {
    private Emoji emoji;
    private short count;
}
