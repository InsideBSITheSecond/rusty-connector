package group.aelysium.rustyconnector.plugin.velocity.lib.family;

import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.plugin.velocity.lib.whitelist.Whitelist;
import group.aelysium.rustyconnector.plugin.velocity.lib.load_balancing.LoadBalancer;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.toolkit.velocity.family.Metadata;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Family implements group.aelysium.rustyconnector.toolkit.velocity.family.Family<MCLoader, Player, LoadBalancer> {
    protected final String id;
    protected Metadata metadata;
    protected Settings<MCLoader, LoadBalancer, Whitelist> settings;

    protected Family(String id, Settings<MCLoader, LoadBalancer, Whitelist> settings, Metadata metadata) {
        this.id = id;
        this.settings = settings;
        this.metadata = metadata;
    }

    public String id() {
        return this.id;
    }

    public Component displayName() {
        return this.settings.displayName();
    }

    public MCLoader findServer(@NotNull ServerInfo serverInfo) {
        return this.registeredServers().stream()
                .filter(server -> server.serverInfo().equals(serverInfo)
                ).findFirst().orElse(null);
    }

    public void addServer(MCLoader server) {
        this.settings.loadBalancer().add(server);
    }

    public void removeServer(MCLoader server) {
        this.settings.loadBalancer().remove(server);
    }

    public Whitelist whitelist() {
        return (Whitelist) this.settings.whitelist().get();
    }

    public List<com.velocitypowered.api.proxy.Player> players(int max) {
        List<com.velocitypowered.api.proxy.Player> players = new ArrayList<>();

        for (MCLoader server : this.registeredServers()) {
            if(players.size() > max) break;

            players.addAll(server.registeredServer().getPlayersConnected());
        }

        return players;
    }

    public List<com.velocitypowered.api.proxy.Player> players() {
        List<com.velocitypowered.api.proxy.Player> players = new ArrayList<>();

        for (MCLoader server : this.registeredServers()) {
            players.addAll(server.registeredServer().getPlayersConnected());
        }

        return players;
    }

    public List<MCLoader> registeredServers() {
        List<MCLoader> servers = new ArrayList<>();
        servers.addAll(this.settings.loadBalancer().servers());
        servers.addAll(this.settings.loadBalancer().lockedServers());
        return servers;
    }
    public boolean containsServer(ServerInfo serverInfo) {
        return !(this.findServer(serverInfo) == null);
    }

    public long playerCount() {
        AtomicLong newPlayerCount = new AtomicLong();
        this.settings.loadBalancer().servers().forEach(server -> newPlayerCount.addAndGet(server.playerCount()));

        return newPlayerCount.get();
    }

    public LoadBalancer loadBalancer() {
        return this.settings.loadBalancer();
    }

    public Family parent() {
        return (Family) this.settings.parent().get(true);
    }

    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Family that = (Family) o;
        return Objects.equals(id, that.id);
    }
}