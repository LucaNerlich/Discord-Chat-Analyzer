package analyzer.models.ranking.impl;

import analyzer.models.message.reaction.Emoji;
import analyzer.models.ranking.Ranking;
import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MostCommonReactionRanking extends Ranking {

    private static final transient String OUTPUT_FILE_NAME = "logs/ranking-most-common-reaction.json";

    @Getter
    private long reactionsGiven;
    @Getter
    @Setter
    private TreeMap<Emoji, Integer> mostCommonReaction = new TreeMap<>(new Emoji.EmojiCountComparator());

    public MostCommonReactionRanking(List<AuthorData> authorDataList) {
        super(authorDataList);
        calculateMostCommonReaction(authorDataList);
        countReactions();
    }

    private void countReactions() {
        mostCommonReaction.forEach((key, value) -> reactionsGiven = reactionsGiven + value);
    }

    private void calculateMostCommonReaction(List<AuthorData> authorDataList) {
        Map<Emoji, Integer> emojiCount = new HashMap<>();
        authorDataList.forEach(authorData -> {
            final Map<Emoji, Integer> emojisReceived = authorData.getEmojisReceived();
            emojisReceived.forEach((key, value) -> {
                if (emojiCount.containsKey(key)) {
                    emojiCount.put(key, emojiCount.get(key) + value);
                } else {
                    emojiCount.put(key, value);
                }
            });
        });

        // write emoji count
        for (Map.Entry<Emoji, Integer> emoji : emojiCount.entrySet()) {
            emoji.getKey().setCount(emoji.getValue());
        }
        mostCommonReaction.putAll(emojiCount);
    }

    @Override
    public String getOutputFilePath() {
        return OUTPUT_FILE_NAME;
    }
}
