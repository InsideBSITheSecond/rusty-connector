package group.aelysium.rustyconnector.plugin.velocity.lib.storage;

import group.aelysium.rustyconnector.toolkit.velocity.family.static_family.IServerResidence;
import group.aelysium.rustyconnector.toolkit.velocity.friends.PlayerPair;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.IPlayerRank;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IStorageRoot;

import java.util.*;

public class Database implements IStorageRoot {
    private final Set<PlayerPair> friends = new LazyHashSet<>();
    private final Map<IServerResidence.Key, IServerResidence.MCLoaderEntry> residence = new LazyHashMap<>();

    private final Map<IPlayer.RankKey, IPlayerRank> ranks = new LazyHashMap<>();



    public Map<String, Map<UUID, IServerResidence.MCLoaderEntry>> residence() {
        return residence;
    }

    public Optional<IPlayerRank> fetchRank(IPlayer.RankKey key) {
        IPlayerRank rank = this.ranks.get(key);
        if(rank == null) return Optional.empty();
        return Optional.of(rank);
    }
    public void saveRank(IPlayer.RankKey key, IPlayerRank rank) {
        XThreads.executeSynchronized(()-> {
            ranks.put(key, rank);
    
            storage.store(this.ranks);
        });
    }
    public void deleteRank(IPlayer.RankKey key) {
        XThreads.executeSynchronized(()-> {
            IPlayerRank rank = ranks.remove(key);

            storage.store(this.ranks);

            return rank == null;
        });
    }

    public void deleteGame(String gameId) {
        List<IPlayer.RankKey> delete = new ArrayList<>();

        this.ranks.forEach((k, v) -> {
            if(!k.gameId().equals(gameId)) return;
            delete.add(k);
        });

        XThreads.executeSynchronized(()-> {
            delete.forEach(this.ranks::remove);

            storage.store(this.ranks);
        });
    }
}