package group.aelysium.rustyconnector.plugin.velocity.lib.server;

import group.aelysium.rustyconnector.core.lib.packets.BuiltInIdentifications;
import group.aelysium.rustyconnector.core.lib.packets.RankedGame;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.PacketParameter;
import group.aelysium.rustyconnector.toolkit.velocity.matchmaking.gameplay.ISession;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayer;
import group.aelysium.rustyconnector.toolkit.velocity.server.IRankedMCLoader;

import java.net.InetSocketAddress;
import java.util.*;

public class RankedMCLoader extends MCLoader implements IRankedMCLoader {
    protected ISession activeSession;

    public RankedMCLoader(UUID uuid, InetSocketAddress address, String displayName, int softPlayerCap, int hardPlayerCap, int weight, int timeout) {
        super(uuid, address, displayName, softPlayerCap, hardPlayerCap, weight, timeout);
    }

    public Optional<ISession> currentSession() {
        if(this.activeSession == null) return Optional.empty();
        return Optional.of(this.activeSession);
    }

    public void connect(ISession session) {
        for (IPlayer rankedPlayer : session.players())
            this.connect(rankedPlayer);

        Tinder.get().services().magicLink().packetManager().newPacketBuilder()
                .identification(BuiltInIdentifications.RANKED_GAME_READY)
                .parameter(RankedGame.Ready.Parameters.SESSION, new PacketParameter(session.toJSON()))
                .sendTo(Packet.Target.mcLoader(this.uuid()));

        this.activeSession = session;
        this.lock();
    }

    @Override
    public void leave(IPlayer player) {
        if(this.activeSession == null) return;

        this.activeSession.leave(player);
    }

    public void unlock() {
        this.activeSession = null;
        super.unlock();
    }
}
