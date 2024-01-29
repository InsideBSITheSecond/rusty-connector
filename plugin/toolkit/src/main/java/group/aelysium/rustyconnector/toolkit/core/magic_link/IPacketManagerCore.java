package group.aelysium.rustyconnector.toolkit.core.magic_link;

import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPacketManagerCore extends Service {
    /**
     * Returns a map containing all active reply endpoints.
     * These endpoints can be used to resolve waiting message responses.
     * Don't mess with this if you don't know what it does!
     */
    Map<UUID, CompletableFuture<Packet>> activeReplyEndpoints();

    Packet.Builder newPacketBuilder();
}
