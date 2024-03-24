package group.aelysium.rustyconnector.toolkit.core.magic_link;

import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPacketManagerCore extends Service {
    /**
     * Returns a map containing all active reply packets.
     * These packets can be used to send packet responses.
     * Don't mess with this if you don't know what it does!
     */
    Map<UUID, Packet> activeReplyEndpoints();

    Packet.Builder newPacketBuilder();
}
