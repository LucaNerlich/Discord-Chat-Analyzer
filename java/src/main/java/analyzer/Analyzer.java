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
import analyzer.utils.SocialGraphUtils;
import analyzer.utils.SocialGraphVisualizer;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class Analyzer {

    private final Map<Author, AuthorData> authorDataMap;

    public Analyzer(final List<Channel> channels) {
        this.authorDataMap = new TreeMap<>(new Author.AuthorComparator());

        if (channels != null) {
            // Process all channels first
            channels.parallelStream().forEach(this::analyzeChannel);

            // Then do final calculations once
            finalizeAnalysis();
        }
    }

    /**
     * More efficient word counting without regex split
     */
    private static int countWordsEfficiently(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int count = 0;
        boolean inWord = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                inWord = false;
            } else {
                if (!inWord) {
                    count++;
                    inWord = true;
                }
            }
        }

        return count;
    }

    public Ranking getRanking(RankingType rankingType) {
        // Pass the collection directly instead of creating a new LinkedList
        return RankingFactory.createRanking(rankingType, authorDataMap.values());
    }

    /**
     * Get social graph utilities for analyzing mention relationships
     */
    public SocialGraphUtils.NetworkStatistics getNetworkStatistics() {
        return SocialGraphUtils.calculateNetworkStatistics(authorDataMap.values());
    }

    /**
     * Get the most connected users in the social graph
     */
    public List<SocialGraphUtils.UserConnection> getMostConnectedUsers() {
        return SocialGraphUtils.getMostConnectedUsers(authorDataMap.values());
    }

    /**
     * Find mutual mention relationships (users who mention each other)
     */
    public List<SocialGraphUtils.MutualMentionRelationship> getMutualMentionRelationships() {
        return SocialGraphUtils.findMutualMentionRelationships(authorDataMap.values());
    }

    /**
     * Export social graph data for visualization tools
     */
    public SocialGraphUtils.SocialGraphExport exportSocialGraphData() {
        return SocialGraphUtils.exportSocialGraphData(authorDataMap.values());
    }

    /**
     * Generate a text-based visualization of the social graph
     */
    public String generateTextVisualization() {
        return SocialGraphVisualizer.generateTextVisualization(authorDataMap.values());
    }

    /**
     * Generate HTML content for visualization (as string)
     */
    public String generateHTMLContent() {
        return SocialGraphVisualizer.generateHTMLContent(authorDataMap.values());
    }

    /**
     * Generate an interactive HTML visualization
     */
    public void generateHTMLVisualization(String outputPath) throws java.io.IOException {
        SocialGraphVisualizer.generateHTMLVisualization(authorDataMap.values(), outputPath);
    }

    /**
     * Export social graph in Gephi format
     */
    public void exportGephiFormat(String outputPath) throws java.io.IOException {
        SocialGraphVisualizer.exportGephiFormat(authorDataMap.values(), outputPath);
    }

    /**
     * Export social graph in GraphML format
     */
    public void exportGraphMLFormat(String outputPath) throws java.io.IOException {
        SocialGraphVisualizer.exportGraphMLFormat(authorDataMap.values(), outputPath);
    }

    private void removeAuthors() {
        authorDataMap.values().removeIf(
                authorData -> authorData.getMessagesSent() < AnalyzerConfig.MIN_AMOUNT_MESSAGES
        );
    }

    private void finalizeAnalysis() {
        // Calculate emoji sums for all authors
        authorDataMap.entrySet().parallelStream().forEach(this::countEmojis);

        // Remove authors with insufficient messages
        removeAuthors();
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

    private synchronized void analyzeChannel(final Channel channel) {
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
            
            // Track mention relationships for social graph
            final String mentionerId = message.getAuthor().getId();
            
            for (Mention mention : mentions) {
                final String mentionedId = mention.getId();
                
                // Record that this author mentioned someone
                authorData.addMentionSent(mentionedId);
                
                // Record that the mentioned person was mentioned by this author
                AuthorData mentionedAuthorData = findOrCreateAuthorData(mentionedId);
                mentionedAuthorData.addMentionReceived(mentionerId);
            }
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
            int wordCount = countWordsEfficiently(content);
            authorData.addWordCount(wordCount);
        }
    }

    private void analyzeReactions(AuthorData authorData, Reaction[] reactions) {
        if (reactions == null || reactions.length == 0) {
            return; // Early return for performance
        }

        final Map<Emoji, Integer> emojisReceived = authorData.getEmojisReceived();
        for (Reaction reaction : reactions) {
            final Emoji emoji = reaction.getEmoji();
            final int count = reaction.getCount();

            // More efficient than contains + get + put
            emojisReceived.merge(emoji, count, Integer::sum);
        }
    }

    private AuthorData findOrCreateAuthorData(String userId) {
        // Find existing author data by user ID
        for (Map.Entry<Author, AuthorData> entry : authorDataMap.entrySet()) {
            if (entry.getKey().getId().equals(userId)) {
                return entry.getValue();
            }
        }
        
        // If not found, create a new AuthorData with minimal author info
        // This handles cases where mentioned users don't have messages in the analyzed channels
        Author placeholderAuthor = new Author();
        placeholderAuthor.setId(userId);
        placeholderAuthor.setName("Unknown User " + userId);
        placeholderAuthor.setNickname("Unknown User " + userId);
        
        AuthorData newAuthorData = new AuthorData();
        newAuthorData.setAuthorId(userId);
        newAuthorData.setAuthor(placeholderAuthor);
        
        authorDataMap.put(placeholderAuthor, newAuthorData);
        return newAuthorData;
    }
}
