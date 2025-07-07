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

    // Output directory structure
    public static final String OUTPUT_SUBFOLDER = "output";

    // Data Source Folders
    public static final List<String> LOG_FOLDER_PATHS = List.of(
            "logs/m10z",
            "logs/dttd"
    );

    // Threading Configuration
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    // Performance Configuration
    public static final int JSON_PARSE_BATCH_SIZE = 100; // Process JSON files in batches
    public static final boolean ENABLE_PARALLEL_FOLDER_PROCESSING = true;
    public static final boolean ENABLE_PARALLEL_CHANNEL_PROCESSING = true;
    public static final int CHANNEL_PROCESSING_BATCH_SIZE = 50;

    private AnalyzerConfig() {
        // Utility class - prevent instantiation
    }
}
