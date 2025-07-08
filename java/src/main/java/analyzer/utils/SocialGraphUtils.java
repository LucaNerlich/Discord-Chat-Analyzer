package analyzer.utils;

import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SocialGraphUtils {

    /**
     * Calculates the most connected users in the social graph based on total connections
     */
    public static List<UserConnection> getMostConnectedUsers(Collection<AuthorData> authorDataCollection) {
        return authorDataCollection.stream()
            .map(authorData -> new UserConnection(
                authorData.getAuthorId(),
                authorData.getAuthor().getNickname(),
                authorData.getMentionsSent().size(),
                authorData.getMentionsReceived().size(),
                authorData.getTotalMentionsSent(),
                authorData.getTotalMentionsReceived()
            ))
            .sorted((a, b) -> Integer.compare(b.getTotalConnections(), a.getTotalConnections()))
            .collect(Collectors.toList());
    }

    /**
     * Finds mutual mention relationships (users who mention each other)
     */
    public static List<MutualMentionRelationship> findMutualMentionRelationships(
        final Collection<AuthorData> authorDataCollection) {
        final List<MutualMentionRelationship> mutualRelationships = new ArrayList<>();
        final Map<String, AuthorData> userIdToAuthorData = authorDataCollection.stream()
            .collect(Collectors.toMap(AuthorData::getAuthorId, data -> data));

        for (AuthorData authorData : authorDataCollection) {
            final String userId = authorData.getAuthorId();

            // Check each user this person mentions
            for (Map.Entry<String, Integer> mentionEntry : authorData.getMentionsSent().entrySet()) {
                final String mentionedUserId = mentionEntry.getKey();
                final int mentionsSentCount = mentionEntry.getValue();

                // Check if the mentioned user also mentions this user back
                final AuthorData mentionedUser = userIdToAuthorData.get(mentionedUserId);
                if (mentionedUser != null) {
                    final Integer mentionsReceivedCount = mentionedUser.getMentionsSent().get(userId);
                    if (mentionsReceivedCount != null && mentionsReceivedCount > 0) {
                        // Mutual relationship found - avoid duplicates by only adding if userId < mentionedUserId
                        if (userId.compareTo(mentionedUserId) < 0) {
                            // Get user names for display
                            final String user1Name = authorData.getAuthor().getName();
                            final String user2Name = mentionedUser.getAuthor().getName();

                            mutualRelationships.add(new MutualMentionRelationship(
                                userId,
                                mentionedUserId,
                                user1Name,
                                user2Name,
                                mentionsSentCount,
                                mentionsReceivedCount
                            ));
                        }
                    }
                }
            }
        }

        return mutualRelationships.stream()
            .sorted((a, b) -> Integer.compare(b.getTotalMentions(), a.getTotalMentions()))
            .collect(Collectors.toList());
    }

    /**
     * Exports social graph data in a format suitable for network visualization tools
     */
    public static SocialGraphExport exportSocialGraphData(Collection<AuthorData> authorDataCollection) {
        final List<Node> nodes = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();

        // Create mapping from ID to name for lookup
        final Map<String, String> idToNameMap = new HashMap<>();
        for (AuthorData authorData : authorDataCollection) {
            idToNameMap.put(authorData.getAuthorId(), authorData.getAuthor().getName());
        }

        // Create nodes (using names as IDs for visualization)
        for (AuthorData authorData : authorDataCollection) {
            nodes.add(new Node(
                authorData.getAuthor().getName(),
                authorData.getAuthor().getName(),
                authorData.getTotalMentionsSent(),
                authorData.getTotalMentionsReceived()
            ));
        }

        // Create edges (converting IDs to names)
        for (AuthorData authorData : authorDataCollection) {
            final String sourceName = authorData.getAuthor().getName();

            for (Map.Entry<String, Integer> mentionEntry : authorData.getMentionsSent().entrySet()) {
                final String targetId = mentionEntry.getKey();
                final String targetName = idToNameMap.getOrDefault(targetId, "Unknown User");
                final int weight = mentionEntry.getValue();

                edges.add(new Edge(sourceName, targetName, weight));
            }
        }

        return new SocialGraphExport(nodes, edges);
    }

    /**
     * Calculates basic network statistics
     */
    public static NetworkStatistics calculateNetworkStatistics(Collection<AuthorData> authorDataCollection) {
        final int totalUsers = authorDataCollection.size();
        final long totalMentions = authorDataCollection.stream()
            .mapToLong(AuthorData::getTotalMentionsSent)
            .sum();

        final int totalConnections = authorDataCollection.stream()
            .mapToInt(authorData -> authorData.getMentionsSent().size())
            .sum();

        final double averageConnectionsPerUser = totalUsers > 0 ? (double) totalConnections / totalUsers : 0;
        final double averageMentionsPerUser = totalUsers > 0 ? (double) totalMentions / totalUsers : 0;

        return new NetworkStatistics(
            totalUsers,
            totalMentions,
            totalConnections,
            averageConnectionsPerUser,
            averageMentionsPerUser
        );
    }

    // Supporting classes for social graph analysis

    @Getter
    public static class UserConnection {
        private final String userId;
        private final String nickname;
        private final int outgoingConnections;
        private final int incomingConnections;
        private final long totalMentionsSent;
        private final long totalMentionsReceived;

        public UserConnection(String userId, String nickname, int outgoingConnections,
                              int incomingConnections, long totalMentionsSent, long totalMentionsReceived) {
            this.userId = userId;
            this.nickname = nickname;
            this.outgoingConnections = outgoingConnections;
            this.incomingConnections = incomingConnections;
            this.totalMentionsSent = totalMentionsSent;
            this.totalMentionsReceived = totalMentionsReceived;
        }

        public int getTotalConnections() {
            return outgoingConnections + incomingConnections;
        }

    }

    public record MutualMentionRelationship(String user1Id, String user2Id, String user1Name, String user2Name,
                                            int user1ToUser2Mentions, int user2ToUser1Mentions) {

        public int getTotalMentions() {
            return user1ToUser2Mentions + user2ToUser1Mentions;
        }
    }

    public record Node(String id, String label, long mentionsSent, long mentionsReceived) {
    }

    public record Edge(String source, String target, int weight) {
    }

    public record SocialGraphExport(List<Node> nodes, List<Edge> edges) {
    }

    public record NetworkStatistics(int totalUsers, long totalMentions, int totalConnections,
                                    double averageConnectionsPerUser, double averageMentionsPerUser) {
    }
}
