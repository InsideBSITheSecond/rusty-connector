package group.aelysium.rustyconnector.plugin.velocity.lib.family;

import group.aelysium.rustyconnector.core.lib.lang.ASCIIAlphabet;
import group.aelysium.rustyconnector.core.lib.lang.Lang;
import group.aelysium.rustyconnector.core.lib.lang.LanguageResolver;
import group.aelysium.rustyconnector.core.lib.lang.printable.LangPrinter;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.event_handlers.EventDispatch;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.ranked_family.RankedFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.scalar_family.RootFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.scalar_family.ScalarFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.static_family.StaticFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.plugin.velocity.lib.whitelist.Whitelist;
import group.aelysium.rustyconnector.toolkit.velocity.events.player.FamilyPreJoinEvent;
import group.aelysium.rustyconnector.toolkit.velocity.family.IFamily;
import group.aelysium.rustyconnector.toolkit.velocity.family.Metadata;
import group.aelysium.rustyconnector.toolkit.velocity.family.scalar_family.IRootFamily;
import group.aelysium.rustyconnector.toolkit.velocity.load_balancing.ILoadBalancer;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.server.IMCLoader;
import group.aelysium.rustyconnector.toolkit.velocity.util.AddressUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public abstract class Family implements IFamily {
    protected final String id;
    protected final Metadata metadata;
    protected final Settings settings;

    protected Family(String id, Settings settings, Metadata metadata) {
        this.id = id;
        this.settings = settings;
        this.metadata = metadata;
    }

    public String id() {
        return this.id;
    }

    public String displayName() {
        return this.settings.displayName();
    }

    public String idOrDisplayName() {
        if(!this.displayName().isEmpty()) return this.displayName();
        return this.id;
    }

    public IMCLoader findServer(@NotNull UUID uuid) {
        // Using MCLoader.Reference should be faster since it uses ServerService#fetch which is backed by HashMap.
        try {
            IMCLoader mcLoader = new IMCLoader.Reference(uuid).get();
            if(mcLoader.family().equals(this)) return mcLoader;
            else return null;
        } catch (Exception ignore) {}

        // If the MCLoader can't be found via MCLoader.Reference, try searching the family itself.
        return this.registeredServers().stream()
                .filter(server -> server.uuid().equals(uuid)
                ).findFirst().orElse(null);
    }

    public void addServer(@NotNull IMCLoader server) {
        this.settings.loadBalancer().add(server);
    }

    public void removeServer(@NotNull IMCLoader server) {
        this.settings.loadBalancer().remove(server);
    }

    public Whitelist whitelist() {
        try { return this.settings.whitelist().get(); } catch (Exception ignore) { return null; }
    }

    public void balance() {
        this.settings.loadBalancer().completeSort();
    }

    public List<com.velocitypowered.api.proxy.Player> players(int max) {
        List<com.velocitypowered.api.proxy.Player> players = new ArrayList<>();

        for (IMCLoader server : this.registeredServers()) {
            if(players.size() > max) break;

            players.addAll(server.registeredServer().getPlayersConnected());
        }

        return players;
    }

    public List<com.velocitypowered.api.proxy.Player> players() {
        List<com.velocitypowered.api.proxy.Player> players = new ArrayList<>();

        for (IMCLoader server : this.registeredServers()) {
            players.addAll(server.registeredServer().getPlayersConnected());
        }

        return players;
    }

    public List<IMCLoader> registeredServers() {
        return this.loadBalancer().servers();
    }
    public boolean containsServer(UUID uuid) {
        try {
            return new IMCLoader.Reference(uuid).get().family().equals(this);
        } catch (Exception ignore) {}

        // If the MCLoader can't be found via MCLoader.Reference, try searching the family itself.
        return this.registeredServers().stream().anyMatch(server -> server.uuid().equals(uuid));
    }

    public long playerCount() {
        AtomicLong newPlayerCount = new AtomicLong();
        this.settings.loadBalancer().servers().forEach(server -> newPlayerCount.addAndGet(server.playerCount()));

        return newPlayerCount.get();
    }

    public ILoadBalancer<IMCLoader> loadBalancer() {
        return this.settings.loadBalancer();
    }

    public Family parent() {
        return this.settings.parent().get(true);
    }

    public Metadata metadata() {
        return this.metadata;
    }

    public Request connect(IPlayer player) {
        EventDispatch.UnSafe.fireAndForget(new FamilyPreJoinEvent(this, player));

        return this.settings.connector().connect(player);
    }

    public Optional<IMCLoader> smartFetch() {
        return this.settings.connector().fetchAny();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Family that = (Family) o;
        return Objects.equals(id, that.id);
    }

    public class Print extends LangPrinter {
        public Print() { super(Tinder.get().lang().resolver()); }

        public final Component chip() {
            String type = "UNKNOWN";
            NamedTextColor color = NamedTextColor.GRAY;
            if(Family.this instanceof RootFamily) {
                type = "ROOT";
                color = NamedTextColor.GOLD;
            }
            if(Family.this instanceof ScalarFamily) {
                type = "SCALAR";
                color = NamedTextColor.BLUE;
            }
            if(Family.this instanceof StaticFamily) {
                type = "STATIC";
                color = NamedTextColor.GREEN;
            }
            if(Family.this instanceof RankedFamily) {
                type = "RANKED";
                color = NamedTextColor.YELLOW;
            }

            return resolver.get(
                    "proxy.family.chip",
                    LanguageResolver.tagHandler("family_id", Family.this.id()),
                    LanguageResolver.tagHandler("family_type", type),
                    LanguageResolver.tagHandler("player_count", Family.this.playerCount()),
                    LanguageResolver.tagHandler("server_count", Family.this.registeredServers().size()),
                    LanguageResolver.tagHandler("display_name", Family.this.displayName())
                    ).color(color);
        }

        private Component lockedServers() {
            Component servers = text("");

            if(Family.this.registeredServers().size() == 0) return resolver.get("proxy.family.generic.servers.no_registered_servers");
            if(Family.this.loadBalancer().size(true) == 0) return resolver.get("proxy.family.generic.servers.no_locked_servers");

            List<IMCLoader> serverList = Family.this.loadBalancer().lockedServers();

            for (IMCLoader imcLoader : serverList) {
                if(!(imcLoader instanceof MCLoader mcLoader)) continue;

                servers = servers.append(mcLoader.new Print().chip().color(GRAY)).append(newline());
            }

            return servers;
        }
        private Component unlockedServers() {
            Component servers = text("");
            int i = 0;

            if(Family.this.registeredServers().size() == 0) return resolver.get("proxy.family.generic.servers.no_registered_servers");
            if(Family.this.loadBalancer().size(false) == 0) return resolver.get("proxy.family.generic.servers.no_unlocked_servers");

            List<IMCLoader> serverList = Family.this.loadBalancer().openServers();

            for (IMCLoader imcLoader : serverList) {
                if(!(imcLoader instanceof MCLoader mcLoader)) continue;

                if(Family.this.loadBalancer().index() == i)
                    servers = servers.append(mcLoader.new Print().chip().color(GREEN)).append(newline());
                else
                    servers = servers.append(mcLoader.new Print().chip().color(GRAY)).append(newline());

                i++;
            }

            return servers;
        }

        protected final Component profile(Component familyParameters, boolean lockedServers) {
            Component servers;
            if(lockedServers) servers = lockedServers();
            else servers = unlockedServers();

            return Lang.WindowBuilder.create()
                    .header(Family.this.id, AQUA)
                    .section(familyParameters)
                    .section(
                            text("family <family id> sort", GOLD),
                            resolver.get("proxy.family.generic.command_descriptions.sort"),
                            Lang.SPACING,
                            text("family <family id> resetIndex", GOLD),
                            resolver.get("proxy.family.generic.command_descriptions.reset_index"),
                            Lang.SPACING,
                            text("family <family id> locked", GOLD),
                            resolver.get("proxy.family.generic.command_descriptions.locked"),
                            Lang.SPACING,
                            text("family <family id> players", GOLD),
                            resolver.get("proxy.family.generic.command_descriptions.locked"),
                            Lang.SPACING,
                            text("family <family id> parties", GOLD),
                            resolver.get("proxy.family.generic.command_descriptions.locked")
                    )
                    .section(servers)
                    .build();
        }

        public static Component families() {
            Tinder api = Tinder.get();
            Component families = text("");
            for (IFamily ifamily : api.services().family().dump()) {
                if(!(ifamily instanceof Family family)) continue;

                families = families.append(family.new Print().chip());
            }

            return Lang.WindowBuilder.create()
                    .header("families", AQUA)
                    .section(
                            api.lang().resolver().getArray("proxy.family.description"),
                            families
                    )
                    .section(
                            text("/rc family <family id>",DARK_AQUA),
                            api.lang().resolver().get("proxy.family.details_usage")
                    )
                    .build();
        }
    }
}
