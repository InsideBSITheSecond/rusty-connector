package group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage;

import group.aelysium.rustyconnector.plugin.velocity.lib.storage.StorageService;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IScoreCard;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.player_rank.IPlayerRank;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ScoreCard is a representation of a player's entire ranked game history.
 * All ranks associated with a player should be able to be fetched using their scorecard.
 */
public class ScoreCard extends Card.WithKey<UUID> implements IScoreCard {
    protected final Map<Class<? extends IPlayerRank<?>>, IPlayerRank<?>> ranks = new ConcurrentHashMap<>();

    public <TPlayerRank extends IPlayerRank<?>> void store(TPlayerRank rank) {
        this.ranks.put(rank.type().get(), rank);

        this.parent().store(ranks);
    }

    @SuppressWarnings("unchecked")
    public <TPlayerRank extends IPlayerRank<?>> TPlayerRank fetch(IRankSchema.Type<?> schema) {
        try {

            TPlayerRank rank = (TPlayerRank) this.ranks.get(schema.get());
            if (rank == null) {
                TPlayerRank newRank = (TPlayerRank) schema.get().getDeclaredConstructor().newInstance();
                this.ranks.put(schema.get(), newRank);

                storage.store(this.ranks);

                return newRank;
            }

            return rank;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void quantize(StorageService storage, IRankSchema.Type<?> schema) {
        try {
            IPlayerRank<?> rank = this.ranks.get(schema.get());

            this.ranks.clear();
            this.ranks.put(schema.get(), rank);

            storage.store(this.ranks);
        } catch (Exception ignore) {
        }

        throw new IllegalStateException();
    }

    @Override
    public boolean attributeEquals(Attribute<?> attribute) {
        return false;
    }

    @Override
    public UUID key() {
        return null;
    }

    @Override
    public Altercator<?> alter() {
        return null;
    }

    @Override
    public <TPlayerRank extends IPlayerRank<?>> void store(TPlayerRank rank) {

    }

    @Override
    public <TPlayerRank extends IPlayerRank<?>> TPlayerRank fetch(IRankSchema.Type<?> schema) {
        return null;
    }
}
