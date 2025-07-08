package analyzer.examples;

import analyzer.Analyzer;
import analyzer.models.channel.Channel;
import analyzer.utils.SocialGraphUtils;

import java.io.IOException;
import java.util.List;

/**
 * Example class demonstrating social graph visualization features
 */
public class SocialGraphExample {
    
    public static void main(String[] args) {
        try {
            // Load your channels (replace with your actual data loading method)
            // List<Channel> channels = loadYourChannels(); // You'll need to implement this
            
            // Create analyzer
            // Analyzer analyzer = new Analyzer(channels);
            
            // Generate various visualizations
            // demonstrateVisualizations(analyzer);
            
            System.out.println("Replace the commented code above with your actual channel loading logic");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void demonstrateVisualizations(Analyzer analyzer) throws IOException {
        System.out.println("=== SOCIAL GRAPH VISUALIZATION DEMO ===\n");
        
        // 1. Text-based visualization
        System.out.println("1. TEXT VISUALIZATION:");
        System.out.println("----------------------");
        String textViz = analyzer.generateTextVisualization();
        System.out.println(textViz);
        
        // 2. Network statistics
        System.out.println("\n2. NETWORK STATISTICS:");
        System.out.println("----------------------");
        SocialGraphUtils.NetworkStatistics stats = analyzer.getNetworkStatistics();
        System.out.println("Total Users: " + stats.getTotalUsers());
        System.out.println("Total Mentions: " + stats.getTotalMentions());
        System.out.println("Average Connections per User: " + String.format("%.2f", stats.getAverageConnectionsPerUser()));
        
        // 3. Most connected users
        System.out.println("\n3. MOST CONNECTED USERS:");
        System.out.println("-------------------------");
        List<SocialGraphUtils.UserConnection> mostConnected = analyzer.getMostConnectedUsers();
        for (int i = 0; i < Math.min(5, mostConnected.size()); i++) {
            SocialGraphUtils.UserConnection user = mostConnected.get(i);
            System.out.printf("%d. %s - %d total connections\n", 
                i + 1, user.getNickname(), user.getTotalConnections());
        }
        
        // 4. Mutual relationships
        System.out.println("\n4. MUTUAL RELATIONSHIPS:");
        System.out.println("-------------------------");
        List<SocialGraphUtils.MutualMentionRelationship> mutualRels = analyzer.getMutualMentionRelationships();
        for (int i = 0; i < Math.min(5, mutualRels.size()); i++) {
            SocialGraphUtils.MutualMentionRelationship rel = mutualRels.get(i);
            System.out.printf("%d. User pair with %d total mentions\n", 
                i + 1, rel.getTotalMentions());
        }
        
        // 5. Generate HTML visualization (manual)
        System.out.println("\n5. GENERATING HTML VISUALIZATION...");
        analyzer.generateHTMLVisualization("output/social-graph.html");
        System.out.println("HTML visualization saved to: output/social-graph.html");
        
        // 6. Export for external tools (manual)
        System.out.println("\n6. EXPORTING FOR EXTERNAL TOOLS...");
        analyzer.exportGephiFormat("output/social-graph.gexf");
        System.out.println("Gephi format saved to: output/social-graph.gexf");
        
        analyzer.exportGraphMLFormat("output/social-graph.graphml");
        System.out.println("GraphML format saved to: output/social-graph.graphml");
        
        System.out.println("\n=== AUTOMATIC OUTPUT FILES ===");
        System.out.println("When running the main analyzer, these files are automatically generated:");
        System.out.println("- social-graph-analysis.txt (text visualization)");
        System.out.println("- social-graph.html (interactive HTML visualization)");
        System.out.println("- ranking-mention-network.json (mention network data)");
        System.out.println("- ranking-most-mentions-sent.json (top mentioners)");
        System.out.println("- ranking-social-graph-matrix.json (complete social matrix)");
        
        System.out.println("\n=== VISUALIZATION DEMO COMPLETE ===");
    }
} 