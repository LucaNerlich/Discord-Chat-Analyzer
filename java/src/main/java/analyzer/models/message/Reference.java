package analyzer.models.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reference {
    private String messageId;
    private String channelId;
    private String guildId;
}
