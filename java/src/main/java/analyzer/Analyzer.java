package analyzer;

import analyzer.config.AnalyzerConfig;
import analyzer.models.Author;
import analyzer.models.channel.Channel;
import analyzer.models.message.Attachment;
import analyzer.models.message.Mention;
import analyzer.models.message.Message;
import analyzer.models.message.embed.Embed;
import analyzer.models.message.reaction.Emoji;
import analyzer.models.message.reaction.Reaction;
import analyzer.models.ranking.Ranking;
import analyzer.models.ranking.RankingFactory;
import analyzer.models.ranking.RankingType;
import analyzer.stats.AuthorData;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class Analyzer {

    private final Map<Author, AuthorData> authorDataMap;

    public Analyzer(final List<Channel> channels) {
        this.authorDataMap = new TreeMap<>(new Author.AuthorComparator());

        if (channels != null) {
            for (Channel channel : channels) {
                analyzeChannel(channel);
                analyzeAuthor(authorDataMap);
                removeAuthors();
            }
        }
    }

    public Ranking getRanking(RankingType rankingType) {
        return RankingFactory.createRanking(rankingType, new LinkedList<>(authorDataMap.values()));
    }

    private void removeAuthors() {
        authorDataMap.values().removeIf(
                authorData -> authorData.getMessagesSent() < AnalyzerConfig.MIN_AMOUNT_MESSAGES
        );
    }

    private void analyzeAuthor(Map<Author, AuthorData> authorDataMap) {
        authorDataMap.entrySet().parallelStream().forEach(this::countEmojis);
    }

    private void countEmojis(Map.Entry<Author, AuthorData> entry) {
        final AuthorData authorData = entry.getValue();
        authorData.setSumEmojisReceived(
                authorData.getEmojisReceived()
                        .values()
                        .stream()
                        .mapToLong(value -> value)
                        .sum()
        );
    }

    private void analyzeChannel(final Channel channel) {
        for (Message message : channel.getMessages()) {
            final Author author = message.getAuthor();

            if (!authorDataMap.containsKey(author)) {
                final AuthorData authorData = new AuthorData();
                populateAuthorDataMap(authorData, message);
            } else {
                populateAuthorDataMap(authorDataMap.get(author), message);
            }
        }
    }

    private void populateAuthorDataMap(AuthorData authorData, Message message) {
        analyzeMessage(authorData, message);
        authorData.setAuthorId(message.getAuthor().getId());
        authorData.setAuthor(message.getAuthor());
        authorData.setEarliestLocalDate(message.getTimestamp());
        authorDataMap.put(message.getAuthor(), authorData);
    }

    private void analyzeMessage(AuthorData authorData, Message message) {
        authorData.incrementMessages();
        analyzeContent(authorData, message);
        analyzeEmbeds(authorData, message);
        analyzeAttachments(authorData, message);
        analyzeMentions(authorData, message);
        analyzeReactions(authorData, message.getReactions());
    }

    private void analyzeMentions(AuthorData authorData, Message message) {
        final Mention[] mentions = message.getMentions();
        if (mentions != null && mentions.length > 0) {
            authorData.incrementTimesMentioned();
        }
    }

    private void analyzeAttachments(AuthorData authorData, Message message) {
        final Attachment[] attachments = message.getAttachments();
        if (attachments != null && attachments.length > 0) {
            authorData.incrementAttachments();
        }
    }

    private void analyzeEmbeds(AuthorData authorData, Message message) {
        final Embed[] embeds = message.getEmbeds();
        if (embeds != null && embeds.length > 0) {
            authorData.incrementEmbeds();
        }
    }

    private void analyzeContent(AuthorData authorData, Message message) {
        final String content = message.getContent();
        if (StringUtils.isNotBlank(content)) {
            String[] words = content.split("\\s+");
            authorData.addWordCount(words.length);
        }
    }

    private void analyzeReactions(AuthorData authorData, Reaction[] reactions) {
        for (Reaction reaction : reactions) {
            final Emoji emoji = reaction.getEmoji();
            final int count = reaction.getCount();

            final Map<Emoji, Integer> emojisRecieved = authorData.getEmojisReceived();
            if (emojisRecieved.containsKey(emoji)) {
                emojisRecieved.put(emoji, emojisRecieved.get(emoji) + count);
            } else {
                emojisRecieved.put(emoji, count);
            }
        }
    }
}
