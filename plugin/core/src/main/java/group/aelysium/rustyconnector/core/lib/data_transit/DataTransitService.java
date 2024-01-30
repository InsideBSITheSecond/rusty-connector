package group.aelysium.rustyconnector.core.lib.data_transit;

import group.aelysium.rustyconnector.toolkit.core.magic_link.packet.Packet;
import group.aelysium.rustyconnector.core.lib.exception.BlockedMessageException;
import group.aelysium.rustyconnector.core.lib.exception.NoOutputException;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class DataTransitService implements Service {
    private final int maxLength;

    public DataTransitService(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Validate a message.
     * @param message The message to check.
     * @throws BlockedMessageException If the message should be blocked.
     */
    public void validate(Packet message) throws BlockedMessageException, NoOutputException {
        if(message.messageVersion() > Packet.protocolVersion())
            throw new BlockedMessageException("The incoming message contained a protocol version greater than expected! " + message.messageVersion() + " > " + Packet.protocolVersion() + ". Make sure you are using the same version of RustyConnector on your proxy and sub-servers!");
        if(message.messageVersion() < Packet.protocolVersion())
            throw new BlockedMessageException("The incoming message contained a protocol version that was less than expected! " + message.messageVersion() + " < " + Packet.protocolVersion() + ". Make sure you are using the same version of RustyConnector on your proxy and sub-servers!");

        if(message.toString().length() > this.maxLength)
            throw new BlockedMessageException("The message is to long!");
    }

    @Override
    public void kill() {
    }
}
