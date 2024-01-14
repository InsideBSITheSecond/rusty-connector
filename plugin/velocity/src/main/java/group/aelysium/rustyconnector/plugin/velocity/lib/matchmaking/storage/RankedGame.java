package group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage;

import group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage.player_rank.RandomizedPlayerRank;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.StorageService;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.ScorecardHolder;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IRankedGame;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IRankedPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IScoreCard;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.player_rank.IPlayerRank;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IStorageService;
import org.eclipse.serializer.collections.lazy.LazyHashMap;

import java.util.Map;
import java.util.UUID;

/**
 * A RankedGame is a representation of all variations of a player's rank within a specific gamemode.
 * If, over the time of this game existing, it has ranked players based on both ELO and WIN_RATE, both of those
 * ranks are saved here and can be retrieved.
 */
public class RankedGame extends Card.WithKey<String> implements IRankedGame {
    protected String name;
    protected IScoreCard.IRankSchema.Type<?> rankingSchema;
    protected ScorecardHolder scorecards = new ScorecardHolder();

    public RankedGame(String name, IScoreCard.IRankSchema.Type<?> schema) {
        this.name = name;
        this.rankingSchema = schema;
    }

    public String key() {
        return this.name;
    }

    public String name() {
        return this.name;
    }

    public IRankedPlayer rankedPlayer(IStorageService storage, UUID uuid, boolean allowNull) {
        ScoreCard scorecard = this.scorecards.get(uuid);
        if(scorecard == null)
            if(allowNull) {
                ScoreCard newScorecard = new ScoreCard();
                this.scorecards.put(uuid, newScorecard);

                storage.store(this.scorecards);

                scorecard = newScorecard;
            } else return null;

        IPlayerRank<?> rank = scorecard.fetch((StorageService) storage, this.rankingSchema);

        return RankedPlayer.from(uuid, rank);
    }

    public IPlayerRank<?> playerRank(IStorageService storage, IPlayer player) throws IllegalStateException {
        if(rankingSchema == IScoreCard.IRankSchema.RANDOMIZED) return new RandomizedPlayerRank();

        ScoreCard scorecard = this.scorecards.get(player.uuid());
        if(scorecard == null) {
            ScoreCard fresh = new ScoreCard();

            this.scorecards.put(player.uuid(), fresh);
            storage.store(this.scorecards);

            scorecard = fresh;
        }

        IPlayerRank<?> playerRank = scorecard.fetch((StorageService) storage, this.rankingSchema);
        if(playerRank == null) {
            try {
                IPlayerRank<?> fresh = this.rankingSchema.get().getDeclaredConstructor().newInstance();

                scorecard.store((StorageService) storage, fresh);

                playerRank = fresh;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return playerRank;
    }

    public void quantizeRankSchemas(StorageService storage) {
        for (ScoreCard scorecard : this.scorecards.values()) {
            scorecard.quantize(storage, this.rankingSchema);
        }
    }

    @Override
    public boolean attributeEquals(Attribute<?> attribute) {
        return false;
    }

    @Override
    public Altercator<?> alter() {
        return null;
    }
}
