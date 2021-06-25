package analyzer.models.message.embed;

import analyzer.models.Author;
import analyzer.models.message.Field;
import analyzer.models.message.Thumbnail;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Embed {
    private String title;
    private String url;
    private String timestamp;
    private String description;
    private String color;
    private Author author;
    private Thumbnail thumbnail;
    private Image image;
    private Footer footer;
    private Field[] fields;
}
