package analyzer.models.ranking;

import analyzer.models.ranking.impl.*;
import analyzer.stats.AuthorData;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class RankingFactory {

    private static final Map<RankingType, Function<Collection<AuthorData>, Ranking>> RANKING_CREATORS = Map.of(
            RankingType.MOST_MESSAGES, MostMessagesRanking::new,
            RankingType.ACCOUNT_AGE, AccountAgeRanking::new,
            RankingType.MOST_COMMON_REACTION, MostCommonReactionRanking::new,
            RankingType.AVG_WORD_COUNT, AvgWordCountRanking::new,
            RankingType.MOST_EMBEDS, MostEmbedsRanking::new,
            RankingType.MOST_ATTACHMENTS, MostAttachmentsRanking::new,
            RankingType.TIMES_MENTIONED, TimesMentionedRanking::new,
            RankingType.MENTION_NETWORK, MentionNetworkRanking::new,
            RankingType.MOST_MENTIONS_SENT, MostMentionsSentRanking::new,
            RankingType.SOCIAL_GRAPH_MATRIX, SocialGraphMatrixRanking::new
    );

    public static Ranking createRanking(RankingType rankingType, Collection<AuthorData> authorDataCollection) {
        Function<Collection<AuthorData>, Ranking> creator = RANKING_CREATORS.get(rankingType);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown ranking type: " + rankingType);
        }
        return creator.apply(authorDataCollection);
    }
}
