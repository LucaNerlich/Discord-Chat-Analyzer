package analyzer.models.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment {
    private String id;
    private String url;
    private String fileName;
    private long fileSizeBytes;
}
