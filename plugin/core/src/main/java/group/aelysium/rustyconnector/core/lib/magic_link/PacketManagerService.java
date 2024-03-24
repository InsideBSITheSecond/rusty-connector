package group.aelysium.rustyconnector.core.lib.magic_link;

import group.aelysium.rustyconnector.core.lib.cache.TimeoutCache;
import group.aelysium.rustyconnector.toolkit.core.magic_link.IPacketManagerCore;
import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.IServiceableService;
import group.aelysium.rustyconnector.toolkit.velocity.util.LiquidTimestamp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PacketManagerService<Flame extends IServiceableService<?>> implements IPacketManagerCore {
    private final TimeoutCache<UUID, Packet> packetsAwaitingReply;
    protected final Flame flame;

    public PacketManagerService(Flame flame) {
        this.packetsAwaitingReply = new TimeoutCache<>(LiquidTimestamp.from(10, TimeUnit.SECONDS));
        this.flame = flame;
    }

    public Packet.Builder newPacketBuilder() {
        return new Packet.Builder(this.flame);
    }

    public Map<UUID, Packet> activeReplyEndpoints() {
        return this.packetsAwaitingReply;
    }

    @Override
    public void kill() {
        try {
            this.packetsAwaitingReply.clear();
            this.packetsAwaitingReply.close();
        } catch (Exception ignore) {}
    }
}
