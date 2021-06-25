package analyzer.models.message;

import analyzer.models.Author;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private String id;
    private String type;
    private String timestamp;
    private String timestampEdited;
    private String callEndedTimestamp;
    private boolean isPinned;
    private String content;
    private Author author;
    private Attachment[] attachments;
    private Embeds[] embeds;
    private Reaction[] reactions;
    private Mention[] mentions;
    private Reference reference;
}
