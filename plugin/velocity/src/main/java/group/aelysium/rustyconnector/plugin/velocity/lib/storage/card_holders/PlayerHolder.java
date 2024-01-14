package group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders;

import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.toolkit.core.card.Card;

import java.util.UUID;

public class PlayerHolder extends Card.Holder.Map<UUID, Player> {
    /**
     * Fetches a RustyConnector player from the provided Velocity player.
     * If no player is stored in storage, the player will be stored.
     * If a player was already stored in the storage, that player will be returned.
     *
     * This method will also update the player's username if it has been changed.
     * @param velocityPlayer The player to fetch.
     * @return {@link Player}
     */
    public Player from(com.velocitypowered.api.proxy.Player velocityPlayer) {
        try {
            Player player = this.fetch(velocityPlayer.getUniqueId()).orElseThrow();
            if(!player.username().equals(velocityPlayer.getUsername())) {
                player.alter()
                      .username(velocityPlayer.getUsername())
                      .commit();
                return player;
            }
        } catch (Exception ignore) {}

        return this.create()
                .uuid(velocityPlayer.getUniqueId())
                .username(velocityPlayer.getUsername())
                .prepare().createAndStore();
    }

    @Override
    public Player.Builder create() {
        return new Player.Builder(this);
    }
}
