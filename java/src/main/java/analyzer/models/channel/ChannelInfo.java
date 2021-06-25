package analyzer.models.channel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelInfo {
    private String id;
    private String type;
    private String categoryId;
    private String category;
    private String name;
    private String topic;
}
