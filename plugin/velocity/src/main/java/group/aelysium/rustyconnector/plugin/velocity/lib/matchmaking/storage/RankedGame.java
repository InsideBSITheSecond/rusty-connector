package group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage;

import group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage.player_rank.RandomizedPlayerRank;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.MySQLStorage;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IRankedGame;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IScoreCard;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.player_rank.IPlayerRank;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IMySQLStorageService;

import java.util.HashMap;
import java.util.UUID;

/**
 * ScoreCard is a representation of a player's entire ranked game history.
 * All ranks associated with a player should be able to be fetched using their scorecard.
 */
public class RankedGame implements IRankedGame<Player> {
    protected String name;
    protected IScoreCard.IRankSchema.Type<?> rankingSchema;
    protected HashMap<UUID, ScoreCard> scorecards = new HashMap<>();

    public RankedGame(String name, IScoreCard.IRankSchema.Type<?> schema) {
        this.name = name;
        this.rankingSchema = schema;
    }

    public String name() {
        return this.name;
    }

    public <TPlayerRank extends IPlayerRank<?>, TMySQLStorage extends IMySQLStorageService> TPlayerRank rankedPlayer(TMySQLStorage storage, UUID uuid) {
        ScoreCard scorecard = this.scorecards.get(uuid);
        if(scorecard == null) {
            ScoreCard newScorecard = new ScoreCard();
            this.scorecards.put(uuid, newScorecard);

            storage.store(this.scorecards);

            scorecard = newScorecard;
        }

        TPlayerRank rank = scorecard.fetch((MySQLStorage) storage, this.rankingSchema);

        return (TPlayerRank) RankedPlayer.from(uuid, rank);
    }

    public <TPlayerRank extends IPlayerRank<?>, TMySQLStorage extends IMySQLStorageService> TPlayerRank playerRank(TMySQLStorage storage, Player player) throws IllegalStateException {
        if(rankingSchema == IScoreCard.IRankSchema.RANDOMIZED) return (TPlayerRank) new RandomizedPlayerRank();

        ScoreCard scorecard = this.scorecards.get(player.uuid());
        if(scorecard == null) {
            ScoreCard fresh = new ScoreCard();

            this.scorecards.put(player.uuid(), fresh);
            storage.store(this.scorecards);

            scorecard = fresh;
        }

        TPlayerRank playerRank = scorecard.fetch((MySQLStorage) storage, this.rankingSchema);
        if(playerRank == null) {
            try {
                TPlayerRank fresh = (TPlayerRank) this.rankingSchema.get().getDeclaredConstructor().newInstance();

                scorecard.store((MySQLStorage) storage, fresh);

                playerRank = fresh;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return playerRank;
    }

    public void quantizeRankSchemas(MySQLStorage storage) {
        for (ScoreCard scorecard : this.scorecards.values()) {
            scorecard.quantize(storage, this.rankingSchema);
        }
    }
}