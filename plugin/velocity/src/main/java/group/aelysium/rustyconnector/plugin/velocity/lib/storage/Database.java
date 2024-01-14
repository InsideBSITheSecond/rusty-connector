package group.aelysium.rustyconnector.plugin.velocity.lib.storage;

import group.aelysium.rustyconnector.plugin.velocity.lib.matchmaking.storage.RankedGame;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.FriendsHolder;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.PlayerHolder;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.RankedGameHolder;
import group.aelysium.rustyconnector.toolkit.velocity.family.static_family.IServerResidence;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.velocity.friends.PlayerPair;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IDatabase;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.collections.lazy.LazyHashSet;

import java.util.*;

public class Database implements IDatabase {
    private final PlayerHolder players = new PlayerHolder();
    private final FriendsHolder friends = new FriendsHolder();
    private final Map<String, Map<UUID, IServerResidence.MCLoaderEntry>> residence = new LazyHashMap<>();
    private final RankedGameHolder rankedGame = new RankedGameHolder();

    public PlayerHolder players() {
        return players;
    }
    public FriendsHolder friends() {
        return friends;
    }
    public RankedGameHolder rankedGames() {
        return rankedGame;
    }

    public Map<String, Map<UUID, IServerResidence.MCLoaderEntry>> residence() {
        return residence;
    }

    public Optional<RankedGame> getGame(String name) {
        return Optional.ofNullable(games.get(name));
    }
    public void saveGame(StorageService storage, RankedGame game) {
        games.put(game.name(), game);

        storage.store(this.games);
    }
    public boolean deleteGame(StorageService storage, String name) {
        RankedGame game = games.remove(name);

        storage.store(this.games);

        return game == null;
    }
}
