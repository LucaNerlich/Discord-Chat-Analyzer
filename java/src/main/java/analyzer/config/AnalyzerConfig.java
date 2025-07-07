package analyzer.config;

import java.util.List;

public final class AnalyzerConfig {

    // Analysis Configuration
    public static final int MIN_AMOUNT_MESSAGES = 10;
    public static final int MIN_MESSAGES_FOR_AVG_WORD_COUNT = 10;
    public static final int DECIMAL_PRECISION = 2;

    // File Paths
    public static final String OUTPUT_FILE_AUTHORS = "logs/output-all.json";
    public static final String RANKING_MOST_MESSAGES = "logs/ranking-most-messages.json";
    public static final String RANKING_ACCOUNT_AGE = "logs/ranking-account-age.json";
    public static final String RANKING_MOST_COMMON_REACTION = "logs/ranking-most-common-reaction.json";
    public static final String RANKING_AVG_WORD_COUNT = "logs/ranking-avg-word-count.json";
    public static final String RANKING_MOST_EMBEDS = "logs/ranking-most-embeds.json";
    public static final String RANKING_MOST_ATTACHMENTS = "logs/ranking-most-attachments.json";
    public static final String RANKING_TIMES_MENTIONED = "logs/ranking-times-mentioned.json";

    // Data Source Folders
    public static final List<String> LOG_FOLDER_PATHS = List.of(
            // Uncomment the folders you want to analyze
            "logs/m10z"
            // "logs/enklave",
            // "logs/thepod",
            // "logs/okcool",
            // "logs/stayforever",
            // "logs/hooked",
            // "logs/nextjs",
            // "logs/computerbase",
            // "logs/insertmoin",
            // "logs/m10z"
    );

    // Threading Configuration
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private AnalyzerConfig() {
        // Utility class - prevent instantiation
    }
}
