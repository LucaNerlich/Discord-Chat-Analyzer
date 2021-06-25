package analyzer.models.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mention {
    private String id;
    private String name;
    private String discriminator;
    private String nickname;
    private boolean isBot;
}
