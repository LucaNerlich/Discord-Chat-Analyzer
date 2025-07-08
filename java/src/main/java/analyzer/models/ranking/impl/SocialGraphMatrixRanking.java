package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SocialGraphMatrixRanking extends Ranking {

    private Map<String, Map<String, Integer>> socialGraphMatrix;
    private Set<String> allUserIds;
    private Map<String, UserGraphStats> userGraphStats;

    public SocialGraphMatrixRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateSocialGraphMatrix(authorDataCollection);
    }

    private void calculateSocialGraphMatrix(Collection<AuthorData> authorDataCollection) {
        extractAllUserIds(authorDataCollection);
        buildSocialGraphMatrix(authorDataCollection);
        calculateUserGraphStats(authorDataCollection);
    }

    private void extractAllUserIds(Collection<AuthorData> authorDataCollection) {
        allUserIds = authorDataCollection.stream()
            .map(AuthorData::getAuthorId)
            .collect(Collectors.toSet());
    }

    private void buildSocialGraphMatrix(Collection<AuthorData> authorDataCollection) {
        socialGraphMatrix = new HashMap<>();
        
        // Initialize matrix with all users
        for (String userId : allUserIds) {
            socialGraphMatrix.put(userId, new HashMap<>());
        }
        
        // Populate matrix with mention data
        for (AuthorData authorData : authorDataCollection) {
            String authorId = authorData.getAuthorId();
            Map<String, Integer> authorMentions = socialGraphMatrix.get(authorId);
            
            // Add mentions sent by this user
            authorData.getMentionsSent().forEach(authorMentions::put);
        }
    }

    private void calculateUserGraphStats(Collection<AuthorData> authorDataCollection) {
        userGraphStats = new HashMap<>();
        
        for (AuthorData authorData : authorDataCollection) {
            String userId = authorData.getAuthorId();
            UserGraphStats stats = new UserGraphStats();
            
            // Calculate outgoing connections (mentions sent)
            stats.outgoingConnections = authorData.getMentionsSent().size();
            stats.totalMentionsSent = authorData.getTotalMentionsSent();
            
            // Calculate incoming connections (mentions received)
            stats.incomingConnections = authorData.getMentionsReceived().size();
            stats.totalMentionsReceived = authorData.getTotalMentionsReceived();
            
            // Calculate centrality score (simple metric: in + out connections)
            stats.centralityScore = stats.incomingConnections + stats.outgoingConnections;
            
            userGraphStats.put(userId, stats);
        }
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_SOCIAL_GRAPH_MATRIX;
    }

    @Getter
    public static class UserGraphStats {
        private int incomingConnections;
        private int outgoingConnections;
        private long totalMentionsSent;
        private long totalMentionsReceived;
        private int centralityScore;

        public UserGraphStats() {
            // Default constructor
        }

        @Override
        public String toString() {
            return String.format("In: %d, Out: %d, SentMentions: %d, ReceivedMentions: %d, Centrality: %d",
                    incomingConnections, outgoingConnections, totalMentionsSent, totalMentionsReceived, centralityScore);
        }
    }
} 