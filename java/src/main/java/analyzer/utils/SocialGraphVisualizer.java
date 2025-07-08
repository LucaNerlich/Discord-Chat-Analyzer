package analyzer.utils;

import analyzer.stats.AuthorData;
import analyzer.utils.SocialGraphUtils.Edge;
import analyzer.utils.SocialGraphUtils.MutualMentionRelationship;
import analyzer.utils.SocialGraphUtils.NetworkStatistics;
import analyzer.utils.SocialGraphUtils.Node;
import analyzer.utils.SocialGraphUtils.SocialGraphExport;
import analyzer.utils.SocialGraphUtils.UserConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SocialGraphVisualizer {

    /**
     * Generate a simple text-based visualization of the social graph
     */
    public static String generateTextVisualization(Collection<AuthorData> authorDataCollection) {
        StringBuilder sb = new StringBuilder();

        // Network overview
        NetworkStatistics stats = SocialGraphUtils.calculateNetworkStatistics(authorDataCollection);
        sb.append("=== SOCIAL GRAPH ANALYSIS ===\n\n");
        sb.append("Network Statistics:\n");
        sb.append("- Total Users: ").append(stats.totalUsers()).append("\n");
        sb.append("- Total Mentions: ").append(stats.totalMentions()).append("\n");
        sb.append("- Total Connections: ").append(stats.totalConnections()).append("\n");
        sb.append("- Avg Connections/User: ").append(String.format("%.2f", stats.averageConnectionsPerUser())).append("\n");
        sb.append("- Avg Mentions/User: ").append(String.format("%.2f", stats.averageMentionsPerUser())).append("\n\n");

        // Most connected users
        List<UserConnection> mostConnected = SocialGraphUtils.getMostConnectedUsers(authorDataCollection);
        sb.append("=== MOST CONNECTED USERS ===\n");
        for (int i = 0; i < Math.min(10, mostConnected.size()); i++) {
            UserConnection user = mostConnected.get(i);
            sb.append(String.format("%2d. %-20s (Connections: %d, Sent: %d, Received: %d)\n",
                i + 1, user.getNickname(), user.getTotalConnections(),
                user.getTotalMentionsSent(), user.getTotalMentionsReceived()));
        }

        // Mutual relationships
        List<MutualMentionRelationship> mutualRelationships = SocialGraphUtils.findMutualMentionRelationships(authorDataCollection);
        sb.append("\n=== TOP MUTUAL RELATIONSHIPS ===\n");
        for (int i = 0; i < Math.min(10, mutualRelationships.size()); i++) {
            MutualMentionRelationship rel = mutualRelationships.get(i);
            sb.append(String.format("%2d. %s→%s: %d, %s→%s: %d (Total: %d)\n",
                i + 1,
                rel.user1Name(), rel.user2Name(), rel.user1ToUser2Mentions(),
                rel.user2Name(), rel.user1Name(), rel.user2ToUser1Mentions(),
                rel.getTotalMentions()));
        }

        // Simple network diagram
        sb.append("\n=== NETWORK DIAGRAM ===\n");
        sb.append(generateSimpleNetworkDiagram(authorDataCollection));

        return sb.toString();
    }

    /**
     * Generate a simple ASCII network diagram
     */
    private static String generateSimpleNetworkDiagram(Collection<AuthorData> authorDataCollection) {
        StringBuilder sb = new StringBuilder();

        List<UserConnection> topUsers = SocialGraphUtils.getMostConnectedUsers(authorDataCollection)
            .stream()
            .limit(8)  // Show top 8 users to keep diagram readable
            .collect(Collectors.toList());

        for (UserConnection user : topUsers) {
            sb.append(String.format("%-15s", user.getNickname()));

            // Find who this user mentions most
            AuthorData userData = authorDataCollection.stream()
                .filter(ad -> ad.getAuthorId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);

            if (userData != null && !userData.getMentionsSent().isEmpty()) {
                String topMentioned = userData.getMentionsSent().entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> {
                        // Find nickname for this user ID
                        return authorDataCollection.stream()
                            .filter(ad -> ad.getAuthorId().equals(entry.getKey()))
                            .findFirst()
                            .map(ad -> ad.getAuthor().getNickname())
                            .orElse("Unknown");
                    })
                    .orElse("None");

                int mentionCount = userData.getMentionsSent().values().stream()
                    .max(Integer::compareTo)
                    .orElse(0);

                sb.append(" ──→ ").append(topMentioned).append(" (").append(mentionCount).append(")");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate HTML content for visualization (as string)
     */
    public static String generateHTMLContent(Collection<AuthorData> authorDataCollection) {
        SocialGraphExport graphData = SocialGraphUtils.exportSocialGraphData(authorDataCollection);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Discord Social Graph</title>\n");
        html.append("<script type=\"text/javascript\" src=\"https://unpkg.com/vis-network/standalone/umd/vis-network.min.js\"></script>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("#network { width: 100%; height: 600px; border: 1px solid #ccc; }\n");
        html.append(".stats { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }\n");
        html.append(".stats h3 { margin-top: 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<h1>Discord Social Graph Visualization</h1>\n");

        // Add network statistics
        NetworkStatistics stats = SocialGraphUtils.calculateNetworkStatistics(authorDataCollection);
        html.append("<div class=\"stats\">\n");
        html.append("<h3>Network Statistics</h3>\n");
        html.append("<p><strong>Total Users:</strong> ").append(stats.totalUsers()).append("</p>\n");
        html.append("<p><strong>Total Mentions:</strong> ").append(stats.totalMentions()).append("</p>\n");
        html.append("<p><strong>Total Connections:</strong> ").append(stats.totalConnections()).append("</p>\n");
        html.append("<p><strong>Average Connections per User:</strong> ").append(String.format("%.2f", stats.averageConnectionsPerUser())).append("</p>\n");
        html.append("</div>\n");

        html.append("<div id=\"network\"></div>\n");

        html.append("<script>\n");
        html.append("var nodes = new vis.DataSet([\n");

        // Add nodes
        for (int i = 0; i < graphData.nodes().size(); i++) {
            Node node = graphData.nodes().get(i);
            html.append("  {id: '").append(node.id()).append("', ");
            html.append("label: '").append(escapeJs(node.label())).append("', ");
            html.append("value: ").append(node.mentionsSent() + node.mentionsReceived()).append(", ");
            html.append("title: 'Sent: ").append(node.mentionsSent()).append(", Received: ").append(node.mentionsReceived()).append("'}");
            if (i < graphData.nodes().size() - 1) html.append(",");
            html.append("\n");
        }

        html.append("]);\n\n");
        html.append("var edges = new vis.DataSet([\n");

        // Add edges
        for (int i = 0; i < graphData.edges().size(); i++) {
            Edge edge = graphData.edges().get(i);
            html.append("  {from: '").append(edge.source()).append("', ");
            html.append("to: '").append(edge.target()).append("', ");
            html.append("value: ").append(edge.weight()).append(", ");
            html.append("title: '").append(edge.weight()).append(" mentions', ");
            html.append("arrows: 'to'}");
            if (i < graphData.edges().size() - 1) html.append(",");
            html.append("\n");
        }

        html.append("]);\n\n");

        html.append("var container = document.getElementById('network');\n");
        html.append("var data = { nodes: nodes, edges: edges };\n");
        html.append("var options = {\n");
        html.append("  nodes: {\n");
        html.append("    shape: 'dot',\n");
        html.append("    scaling: { min: 10, max: 30 },\n");
        html.append("    font: { size: 12, color: '#000000' },\n");
        html.append("    borderWidth: 2,\n");
        html.append("    color: { background: '#97C2FC', border: '#2B7CE9' }\n");
        html.append("  },\n");
        html.append("  edges: {\n");
        html.append("    width: 0.15,\n");
        html.append("    color: { inherit: 'from' },\n");
        html.append("    smooth: { type: 'continuous' },\n");
        html.append("    scaling: { min: 1, max: 5 }\n");
        html.append("  },\n");
        html.append("  physics: {\n");
        html.append("    stabilization: { iterations: 150 },\n");
        html.append("    barnesHut: { gravitationalConstant: -8000, springConstant: 0.001, springLength: 200 }\n");
        html.append("  },\n");
        html.append("  interaction: { hover: true, tooltipDelay: 200 }\n");
        html.append("};\n\n");

        html.append("var network = new vis.Network(container, data, options);\n");
        html.append("</script>\n");
        html.append("</body>\n</html>");

        return html.toString();
    }

    /**
     * Generate an interactive HTML visualization using vis.js and save to file
     */
    public static void generateHTMLVisualization(Collection<AuthorData> authorDataCollection, String outputPath) throws IOException {
        String htmlContent = generateHTMLContent(authorDataCollection);

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(htmlContent);
        }
    }

    /**
     * Export data in Gephi format (.gexf)
     */
    public static void exportGephiFormat(Collection<AuthorData> authorDataCollection, String outputPath) throws IOException {
        SocialGraphExport graphData = SocialGraphUtils.exportSocialGraphData(authorDataCollection);

        StringBuilder gexf = new StringBuilder();
        gexf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        gexf.append("<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\">\n");
        gexf.append("<meta lastmodifieddate=\"").append(new Date()).append("\">\n");
        gexf.append("<creator>Discord Chat Analyzer</creator>\n");
        gexf.append("<description>Social Graph from Discord Chat Analysis</description>\n");
        gexf.append("</meta>\n");
        gexf.append("<graph mode=\"static\" defaultedgetype=\"directed\">\n");

        // Attributes
        gexf.append("<attributes class=\"node\">\n");
        gexf.append("<attribute id=\"0\" title=\"mentionsSent\" type=\"long\"/>\n");
        gexf.append("<attribute id=\"1\" title=\"mentionsReceived\" type=\"long\"/>\n");
        gexf.append("</attributes>\n");

        // Nodes
        gexf.append("<nodes>\n");
        for (Node node : graphData.nodes()) {
            gexf.append("<node id=\"").append(node.id()).append("\" label=\"").append(escapeXml(node.label())).append("\">\n");
            gexf.append("<attvalues>\n");
            gexf.append("<attvalue for=\"0\" value=\"").append(node.mentionsSent()).append("\"/>\n");
            gexf.append("<attvalue for=\"1\" value=\"").append(node.mentionsReceived()).append("\"/>\n");
            gexf.append("</attvalues>\n");
            gexf.append("</node>\n");
        }
        gexf.append("</nodes>\n");

        // Edges
        gexf.append("<edges>\n");
        int edgeId = 0;
        for (Edge edge : graphData.edges()) {
            gexf.append("<edge id=\"").append(edgeId++).append("\" source=\"").append(edge.source())
                .append("\" target=\"").append(edge.target()).append("\" weight=\"").append(edge.weight()).append("\"/>\n");
        }
        gexf.append("</edges>\n");

        gexf.append("</graph>\n");
        gexf.append("</gexf>");

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(gexf.toString());
        }
    }

    /**
     * Export data in GraphML format
     */
    public static void exportGraphMLFormat(Collection<AuthorData> authorDataCollection, String outputPath) throws IOException {
        SocialGraphExport graphData = SocialGraphUtils.exportSocialGraphData(authorDataCollection);

        StringBuilder graphML = new StringBuilder();
        graphML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        graphML.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
        graphML.append("<key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");
        graphML.append("<key id=\"mentionsSent\" for=\"node\" attr.name=\"mentionsSent\" attr.type=\"long\"/>\n");
        graphML.append("<key id=\"mentionsReceived\" for=\"node\" attr.name=\"mentionsReceived\" attr.type=\"long\"/>\n");
        graphML.append("<key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"int\"/>\n");
        graphML.append("<graph id=\"SocialGraph\" edgedefault=\"directed\">\n");

        // Nodes
        for (Node node : graphData.nodes()) {
            graphML.append("<node id=\"").append(node.id()).append("\">\n");
            graphML.append("<data key=\"label\">").append(escapeXml(node.label())).append("</data>\n");
            graphML.append("<data key=\"mentionsSent\">").append(node.mentionsSent()).append("</data>\n");
            graphML.append("<data key=\"mentionsReceived\">").append(node.mentionsReceived()).append("</data>\n");
            graphML.append("</node>\n");
        }

        // Edges
        int edgeId = 0;
        for (Edge edge : graphData.edges()) {
            graphML.append("<edge id=\"e").append(edgeId++).append("\" source=\"").append(edge.source())
                .append("\" target=\"").append(edge.target()).append("\">\n");
            graphML.append("<data key=\"weight\">").append(edge.weight()).append("</data>\n");
            graphML.append("</edge>\n");
        }

        graphML.append("</graph>\n");
        graphML.append("</graphml>");

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(graphML.toString());
        }
    }

    private static String escapeJs(String text) {
        return text.replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String escapeXml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
