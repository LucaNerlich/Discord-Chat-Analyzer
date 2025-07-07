package analyzer.models.ranking;

import analyzer.models.ranking.impl.*;
import analyzer.stats.AuthorData;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RankingFactory {

    private static final Map<RankingType, Function<List<AuthorData>, Ranking>> RANKING_CREATORS = Map.of(
            RankingType.MOST_MESSAGES, MostMessagesRanking::new,
            RankingType.ACCOUNT_AGE, AccountAgeRanking::new,
            RankingType.MOST_COMMON_REACTION, MostCommonReactionRanking::new,
            RankingType.AVG_WORD_COUNT, AvgWordCountRanking::new,
            RankingType.MOST_EMBEDS, MostEmbedsRanking::new,
            RankingType.MOST_ATTACHMENTS, MostAttachmentsRanking::new,
            RankingType.TIMES_MENTIONED, TimesMentionedRanking::new
    );

    public static Ranking createRanking(RankingType rankingType, List<AuthorData> authorDataList) {
        Function<List<AuthorData>, Ranking> creator = RANKING_CREATORS.get(rankingType);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown ranking type: " + rankingType);
        }
        return creator.apply(authorDataList);
    }
}
