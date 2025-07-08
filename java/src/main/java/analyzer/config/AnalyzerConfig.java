package analyzer.config;

import java.util.List;

public final class AnalyzerConfig {

    // Analysis Configuration
    public static final int MIN_AMOUNT_MESSAGES = 10;
    public static final int MIN_MESSAGES_FOR_AVG_WORD_COUNT = 10;
    public static final int DECIMAL_PRECISION = 2;

    // File Names (will be combined with output directory)
    public static final String OUTPUT_FILE_AUTHORS = "output-all.json";
    public static final String RANKING_MOST_MESSAGES = "ranking-most-messages.json";
    public static final String RANKING_ACCOUNT_AGE = "ranking-account-age.json";
    public static final String RANKING_MOST_COMMON_REACTION = "ranking-most-common-reaction.json";
    public static final String RANKING_AVG_WORD_COUNT = "ranking-avg-word-count.json";
    public static final String RANKING_MOST_EMBEDS = "ranking-most-embeds.json";
    public static final String RANKING_MOST_ATTACHMENTS = "ranking-most-attachments.json";
    public static final String RANKING_TIMES_MENTIONED = "ranking-times-mentioned.json";
    public static final String RANKING_MENTION_NETWORK = "ranking-mention-network.json";
    public static final String RANKING_MOST_MENTIONS_SENT = "ranking-most-mentions-sent.json";
    public static final String RANKING_SOCIAL_GRAPH_MATRIX = "ranking-social-graph-matrix.json";
    public static final String SOCIAL_GRAPH_TEXT_VISUALIZATION = "social-graph-analysis.txt";
    public static final String SOCIAL_GRAPH_HTML_VISUALIZATION = "social-graph.html";

    // Output directory structure
    public static final String OUTPUT_SUBFOLDER = "output";

    // Data Source Folders
    public static final List<String> LOG_FOLDER_PATHS = List.of(
        "logs/m10z",
        "logs/dttd"
    );

    // Threading Configuration
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private AnalyzerConfig() {
        // Utility class - prevent instantiation
    }
}
