package group.aelysium.rustyconnector.plugin.velocity.lib.friends;

import group.aelysium.rustyconnector.toolkit.velocity.friends.IFriendsDataEnclave;
import group.aelysium.rustyconnector.toolkit.velocity.friends.PlayerPair;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IStorageService;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.StorageService;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.Database;

import java.util.*;

public class FriendsDataEnclave implements IFriendsDataEnclave {
    private final StorageService storage;

    public FriendsDataEnclave(IStorageService storage) {
        this.storage = (StorageService) storage;
    }

    public Optional<List<PlayerPair>> findFriends(IPlayer player) {
        try {
            Database root = this.storage.database();

            List<PlayerPair> mappings = root.friends().stream()
                    .filter(friendMapping -> friendMapping.player1().equals(player) || friendMapping.player2().equals(player))
                    .toList();

            return Optional.of(mappings);
        } catch (Exception ignore) {}

        return Optional.empty();
    }

    public boolean areFriends(IPlayer player1, IPlayer player2) throws RuntimeException {
        Database root = this.storage.database();
        return root.friends().contains(PlayerPair.from(player1, player2));
    }

    public long getFriendCount(IPlayer player) {
        try {
            Database root = this.storage.database();

            return root.friends().stream().filter(friendMapping -> friendMapping.contains(player)).count();
        } catch (Exception ignore) {
        }

        return 0;
    }

    public Optional<PlayerPair> addFriend(IPlayer player1, IPlayer player2) {
        try {
            Database root = this.storage.database();

            PlayerPair playerPair = PlayerPair.from(player1, player2);

            Set<PlayerPair> networkFriends = root.friends();
            networkFriends.add(playerPair);

            this.storage.store(networkFriends);

            return Optional.of(playerPair);
        } catch (Exception ignore) {
        }

        return Optional.empty();
    }

    public void removeFriend(IPlayer player1, IPlayer player2) {
        try {
            Database root = this.storage.database();

            PlayerPair playerPair = PlayerPair.from(player1, player2);

            Set<PlayerPair> networkFriends = root.friends();
            networkFriends.remove(playerPair);

            this.storage.store(networkFriends);
        } catch (Exception ignore) {
        }
    }
}
