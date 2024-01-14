package group.aelysium.rustyconnector.plugin.velocity.lib.players;

import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.StorageService;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.PlayerHolder;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.core.card.CardController;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.IRankedPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Player extends Card.WithKey<UUID> implements IPlayer, CardController.Alter {
    protected UUID uuid;
    protected String username;
    protected long firstLogin;
    protected long lastLogin;

    protected Player(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID uuid() {
        this.catchIllegalCall();

        return this.uuid;
    }
    public String username() {
        this.catchIllegalCall();

        return this.username;
    }

    @Override
    public boolean attributeEquals(Attribute<?> attribute) {
        this.catchIllegalCall();

        return switch (attribute.name()) {
            case "uuid" -> this.uuid.equals(attribute.attribute());
            case "username" -> this.username.equals(attribute.attribute());
            default -> false;
        };
    }

    @Override
    public UUID key() {
        this.catchIllegalCall();

        return this.uuid;
    }

    public void sendMessage(Component message) {
        this.catchIllegalCall();

        try {
            this.resolve().orElseThrow().sendMessage(message);
        } catch (Exception ignore) {}
    }

    public void disconnect(Component reason) {
        this.catchIllegalCall();

        try {
            this.resolve().orElseThrow().disconnect(reason);
        } catch (Exception ignore) {}
    }

    public Optional<com.velocitypowered.api.proxy.Player> resolve() {
        this.catchIllegalCall();

        return Tinder.get().velocityServer().getPlayer(this.uuid);
    }

    @Override
    public boolean online() {
        this.catchIllegalCall();

        return resolve().isPresent();
    }

    public Optional<MCLoader> server() {
        this.catchIllegalCall();

        try {
            com.velocitypowered.api.proxy.Player resolvedPlayer = this.resolve().orElseThrow();
            UUID mcLoaderUUID = UUID.fromString(resolvedPlayer.getCurrentServer().orElseThrow().getServerInfo().getName());

            MCLoader mcLoader = new MCLoader.Reference(mcLoaderUUID).get();

            return Optional.of(mcLoader);
        } catch (Exception ignore) {}
        return Optional.empty();
    }

    public Optional<IRankedPlayer> rank(String gamemode) {
        this.catchIllegalCall();

        StorageService storage = Tinder.get().services().storage();
        try {
            return Optional.of(storage.database().getGame(gamemode).orElseThrow().rankedPlayer(storage, this.uuid, false));
        } catch (NoSuchElementException ignore) {}
        return Optional.empty();
    }

    @Override
    public boolean equals(Object object) {
        this.catchIllegalCall();

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Player that = (Player) object;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public String toString() {
        this.catchIllegalCall();

        return "<Player uuid="+this.uuid.toString()+" username="+this.username+">";
    }

    @Override
    public Altercator alter() {
        this.catchIllegalCall();

        return new Altercator(this);
    }


    public static class Builder extends CardController.Create.Creator<Player> {
        private UUID uuid;
        private String username;

        public Builder(PlayerHolder owner) {
            super(owner);
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        @Override
        public ReadyForInsert<Player> prepare() {
            return new ReadyForInsert<>(this.owner, new Player(this.uuid, this.username));
        }
    }
    public static class Altercator extends CardController.Alter.Altercator<Player> {
        protected Altercator(@NotNull Player card) {
            super(card);
        }

        public Altercator username(String username) {
            this.card.username = username;
            return this;
        }
    }
}