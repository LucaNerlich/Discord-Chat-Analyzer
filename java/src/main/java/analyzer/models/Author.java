package analyzer.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Author {
    
    private String id;
    private String name;
    private String discriminator;
    private String nickname;
    private String color;
    private boolean isBot;
    private String avatarUrl;
    private String url;
    private String iconUrl;
}
