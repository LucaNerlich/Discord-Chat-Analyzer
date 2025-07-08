package analyzer.models.ranking.impl;

import analyzer.config.AnalyzerConfig;
import analyzer.models.message.reaction.Emoji;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MostCommonReactionRanking extends Ranking {

    @Getter
    private long reactionsGiven;
    @Getter
    @Setter
    private TreeMap<Emoji, Integer> mostCommonReaction = new TreeMap<>(new Emoji.EmojiCountComparator());

    public MostCommonReactionRanking(Collection<AuthorData> authorDataCollection) {
        super(authorDataCollection);
        calculateMostCommonReaction(authorDataCollection);
        countReactions();
    }

    private void countReactions() {
        reactionsGiven = mostCommonReaction.values().stream()
            .mapToLong(Integer::intValue)
            .sum();
    }

    private void calculateMostCommonReaction(Collection<AuthorData> authorDataCollection) {
        Map<Emoji, Integer> emojiCount = new HashMap<>();
        authorDataCollection.forEach(authorData -> {
            final Map<Emoji, Integer> emojisReceived = authorData.getEmojisReceived();
            emojisReceived.forEach((key, value) -> {
                // Use more efficient merge operation
                emojiCount.merge(key, value, Integer::sum);
            });
        });

        // write emoji count
        for (Map.Entry<Emoji, Integer> emoji : emojiCount.entrySet()) {
            emoji.getKey().setCount(emoji.getValue());
        }
        mostCommonReaction.putAll(emojiCount);
    }

    @Override
    public String getOutputFileName() {
        return AnalyzerConfig.RANKING_MOST_COMMON_REACTION;
    }
}
