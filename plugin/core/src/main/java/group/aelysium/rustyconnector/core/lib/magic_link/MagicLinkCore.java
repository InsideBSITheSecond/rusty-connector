package group.aelysium.rustyconnector.core.lib.magic_link;

import group.aelysium.rustyconnector.core.lib.messenger.MessengerConnector;
import group.aelysium.rustyconnector.toolkit.core.magic_link.ICoreMagicLinkService;
import group.aelysium.rustyconnector.toolkit.core.magic_link.IPacketManagerCore;
import group.aelysium.rustyconnector.toolkit.core.magic_link.messenger.MessengerConnection;
import group.aelysium.rustyconnector.toolkit.core.magic_link.messenger.IMessengerConnector;

import java.net.ConnectException;
import java.util.Optional;

public abstract class MagicLinkCore implements ICoreMagicLinkService {
    protected IMessengerConnector redisConnector;
    protected IPacketManagerCore packetManager;

    public MagicLinkCore(IMessengerConnector redisConnector) {
        this.redisConnector = redisConnector;
    }

    public IPacketManagerCore packetManager() {
        return this.packetManager;
    }

    /**
     * Get the {@link MessengerConnection} created from this {@link MessengerConnector}.
     * @return An {@link Optional} possibly containing a {@link group.aelysium.rustyconnector.core.lib.messenger.MessengerConnection}.
     */
    public Optional<MessengerConnection> connection() {
        return this.redisConnector.connection();
    }

    /**
     * Connect to the remote resource.
     * @return A {@link MessengerConnection}.
     * @throws ConnectException If there was an issue connecting to the remote resource.
     */
    public MessengerConnection connect() throws ConnectException {
        return this.redisConnector.connect();
    }

    @Override
    public void kill() {
        this.redisConnector.kill();
        this.packetManager.kill();
    }
}
