package group.aelysium.rustyconnector.toolkit.core.magic_link.packet;

import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

public abstract class PacketPublisher implements Service {
    /**
     * Publish a new packet to the {@link PacketPublisher}.
     * @param packet The packet to publish.
     */
    protected abstract void publish(Packet packet);
    /**
     * Publish a new packet to the {@link PacketPublisher}.
     * This method is identical to calling {@link #publish(Packet) .publish(}{@link Packet.Wrapper#packet()}{@link #publish(Packet) )}
     * @param packet The packet wrapper to publish.
     */
    protected abstract void publish(Packet.Wrapper packet);
}
