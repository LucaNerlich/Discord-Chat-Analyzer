package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
public class MentionNetworkRanking extends Ranking {

    private TreeMap<AuthorData, Long> mostMentionsSent;
    private Map<String, Map<String, Integer>> mentionMatrix; // mentioner -> mentioned -> count
    private List<MentionRelationship> topMentionRelationships;
    private long totalMentionConnections;

    public MentionNetworkRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateMentionNetwork(authorDataCollection);
    }

    private void calculateMentionNetwork(Collection<AuthorData> authorDataCollection) {
        calculateMentionsSentRanking(authorDataCollection);
        buildMentionMatrix(authorDataCollection);
        calculateTopRelationships();
        countTotalConnections();
    }

    private void calculateMentionsSentRanking(Collection<AuthorData> authorDataCollection) {
        mostMentionsSent = new TreeMap<>(new AuthorData.AuthorDataMentionsSentComparator());
        authorDataCollection.forEach(authorData ->
            mostMentionsSent.put(authorData, authorData.getTotalMentionsSent())
        );
    }

    private void buildMentionMatrix(Collection<AuthorData> authorDataCollection) {
        mentionMatrix = new HashMap<>();

        // Create mapping from ID to name for lookup
        Map<String, String> idToNameMap = new HashMap<>();
        for (AuthorData authorData : authorDataCollection) {
            idToNameMap.put(authorData.getAuthorId(), authorData.getAuthor().getName());
        }

        for (AuthorData authorData : authorDataCollection) {
            String authorName = authorData.getAuthor().getName();
            Map<String, Integer> mentionsFromThisUser = new HashMap<>();

            // Add all mentions sent by this user, converting IDs to names
            authorData.getMentionsSent().forEach((mentionedId, count) -> {
                String mentionedName = idToNameMap.getOrDefault(mentionedId, "Unknown User");
                mentionsFromThisUser.put(mentionedName, count);
            });

            if (!mentionsFromThisUser.isEmpty()) {
                mentionMatrix.put(authorName, mentionsFromThisUser);
            }
        }
    }

    private void calculateTopRelationships() {
        topMentionRelationships = mentionMatrix.entrySet().stream()
            .flatMap(entry -> entry.getValue().entrySet().stream()
                .map(mentionEntry -> new MentionRelationship(
                    entry.getKey(),   // Already a name now
                    mentionEntry.getKey(),  // Already a name now
                    mentionEntry.getValue())))
            .sorted((r1, r2) -> Integer.compare(r2.getCount(), r1.getCount()))
            .limit(20) // Top 20 relationships
            .collect(Collectors.toList());
    }

    private void countTotalConnections() {
        totalMentionConnections = mentionMatrix.values().stream()
            .mapToLong(mentionMap -> mentionMap.values().stream()
                .mapToLong(Integer::longValue)
                .sum())
            .sum();
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MENTION_NETWORK;
    }

    @Getter
    public static class MentionRelationship {
        private final String mentionerName;
        private final String mentionedName;
        private final int count;

        public MentionRelationship(String mentionerName, String mentionedName, int count) {
            this.mentionerName = mentionerName;
            this.mentionedName = mentionedName;
            this.count = count;
        }

        @Override
        public String toString() {
            return mentionerName + " -> " + mentionedName + " (" + count + " mentions)";
        }
    }
}
