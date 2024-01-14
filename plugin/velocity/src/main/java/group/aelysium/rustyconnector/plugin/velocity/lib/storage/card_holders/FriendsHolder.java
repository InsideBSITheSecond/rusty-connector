package group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders;

import group.aelysium.rustyconnector.plugin.velocity.lib.friends.Friends;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.velocity.friends.PlayerPair;

import java.util.UUID;

public class FriendsHolder extends Card.Holder.Set<Friends> {
    @Override
    public Friends.Builder create() {
        return new Friends.Builder(this);
    }
}
