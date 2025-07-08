package analyzer.utils;

import analyzer.models.ranking.impl.MentionNetworkRanking;
import analyzer.models.ranking.impl.SocialGraphMatrixRanking;
import analyzer.stats.AuthorData;

import java.util.*;
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
            Collection<AuthorData> authorDataCollection) {

        List<MutualMentionRelationship> mutualRelationships = new ArrayList<>();
        Map<String, AuthorData> userIdToAuthorData = authorDataCollection.stream()
                .collect(Collectors.toMap(AuthorData::getAuthorId, data -> data));

        for (AuthorData authorData : authorDataCollection) {
            String userId = authorData.getAuthorId();

            // Check each user this person mentions
            for (Map.Entry<String, Integer> mentionEntry : authorData.getMentionsSent().entrySet()) {
                String mentionedUserId = mentionEntry.getKey();
                int mentionsSentCount = mentionEntry.getValue();

                // Check if the mentioned user also mentions this user back
                AuthorData mentionedUser = userIdToAuthorData.get(mentionedUserId);
                if (mentionedUser != null) {
                    Integer mentionsReceivedCount = mentionedUser.getMentionsSent().get(userId);
                    if (mentionsReceivedCount != null && mentionsReceivedCount > 0) {
                        // Mutual relationship found - avoid duplicates by only adding if userId < mentionedUserId
                        if (userId.compareTo(mentionedUserId) < 0) {
                            mutualRelationships.add(new MutualMentionRelationship(
                                    userId,
                                    mentionedUserId,
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
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // Create nodes
        for (AuthorData authorData : authorDataCollection) {
            nodes.add(new Node(
                    authorData.getAuthorId(),
                    authorData.getAuthor().getName(),
                    authorData.getTotalMentionsSent(),
                    authorData.getTotalMentionsReceived()
            ));
        }

        // Create edges
        for (AuthorData authorData : authorDataCollection) {
            String sourceId = authorData.getAuthorId();

            for (Map.Entry<String, Integer> mentionEntry : authorData.getMentionsSent().entrySet()) {
                String targetId = mentionEntry.getKey();
                int weight = mentionEntry.getValue();

                edges.add(new Edge(sourceId, targetId, weight));
            }
        }

        return new SocialGraphExport(nodes, edges);
    }

    /**
     * Calculates basic network statistics
     */
    public static NetworkStatistics calculateNetworkStatistics(Collection<AuthorData> authorDataCollection) {
        int totalUsers = authorDataCollection.size();
        long totalMentions = authorDataCollection.stream()
                .mapToLong(AuthorData::getTotalMentionsSent)
                .sum();

        int totalConnections = authorDataCollection.stream()
                .mapToInt(authorData -> authorData.getMentionsSent().size())
                .sum();

        double averageConnectionsPerUser = totalUsers > 0 ? (double) totalConnections / totalUsers : 0;
        double averageMentionsPerUser = totalUsers > 0 ? (double) totalMentions / totalUsers : 0;

        return new NetworkStatistics(
                totalUsers,
                totalMentions,
                totalConnections,
                averageConnectionsPerUser,
                averageMentionsPerUser
        );
    }

    // Supporting classes for social graph analysis

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

        // Getters
        public String getUserId() { return userId; }
        public String getNickname() { return nickname; }
        public int getOutgoingConnections() { return outgoingConnections; }
        public int getIncomingConnections() { return incomingConnections; }
        public long getTotalMentionsSent() { return totalMentionsSent; }
        public long getTotalMentionsReceived() { return totalMentionsReceived; }
    }

    public static class MutualMentionRelationship {
        private final String user1Id;
        private final String user2Id;
        private final int user1ToUser2Mentions;
        private final int user2ToUser1Mentions;

        public MutualMentionRelationship(String user1Id, String user2Id,
                                       int user1ToUser2Mentions, int user2ToUser1Mentions) {
            this.user1Id = user1Id;
            this.user2Id = user2Id;
            this.user1ToUser2Mentions = user1ToUser2Mentions;
            this.user2ToUser1Mentions = user2ToUser1Mentions;
        }

        public int getTotalMentions() {
            return user1ToUser2Mentions + user2ToUser1Mentions;
        }

        // Getters
        public String getUser1Id() { return user1Id; }
        public String getUser2Id() { return user2Id; }
        public int getUser1ToUser2Mentions() { return user1ToUser2Mentions; }
        public int getUser2ToUser1Mentions() { return user2ToUser1Mentions; }
    }

    public static class Node {
        private final String id;
        private final String label;
        private final long mentionsSent;
        private final long mentionsReceived;

        public Node(String id, String label, long mentionsSent, long mentionsReceived) {
            this.id = id;
            this.label = label;
            this.mentionsSent = mentionsSent;
            this.mentionsReceived = mentionsReceived;
        }

        // Getters
        public String getId() { return id; }
        public String getLabel() { return label; }
        public long getMentionsSent() { return mentionsSent; }
        public long getMentionsReceived() { return mentionsReceived; }
    }

    public static class Edge {
        private final String source;
        private final String target;
        private final int weight;

        public Edge(String source, String target, int weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        // Getters
        public String getSource() { return source; }
        public String getTarget() { return target; }
        public int getWeight() { return weight; }
    }

    public static class SocialGraphExport {
        private final List<Node> nodes;
        private final List<Edge> edges;

        public SocialGraphExport(List<Node> nodes, List<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }

        // Getters
        public List<Node> getNodes() { return nodes; }
        public List<Edge> getEdges() { return edges; }
    }

    public static class NetworkStatistics {
        private final int totalUsers;
        private final long totalMentions;
        private final int totalConnections;
        private final double averageConnectionsPerUser;
        private final double averageMentionsPerUser;

        public NetworkStatistics(int totalUsers, long totalMentions, int totalConnections,
                               double averageConnectionsPerUser, double averageMentionsPerUser) {
            this.totalUsers = totalUsers;
            this.totalMentions = totalMentions;
            this.totalConnections = totalConnections;
            this.averageConnectionsPerUser = averageConnectionsPerUser;
            this.averageMentionsPerUser = averageMentionsPerUser;
        }

        // Getters
        public int getTotalUsers() { return totalUsers; }
        public long getTotalMentions() { return totalMentions; }
        public int getTotalConnections() { return totalConnections; }
        public double getAverageConnectionsPerUser() { return averageConnectionsPerUser; }
        public double getAverageMentionsPerUser() { return averageMentionsPerUser; }
    }
}
