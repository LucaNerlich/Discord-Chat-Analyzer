package analyzer.models.channel;

import analyzer.models.DateRange;
import analyzer.models.Guild;
import analyzer.models.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Channel {

    private Guild guild;
    private ChannelInfo channel;
    private DateRange dateRange;
    private Message[] messages;
}
