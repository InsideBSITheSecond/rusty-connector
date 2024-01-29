package group.aelysium.rustyconnector.toolkit.core.magic_link.messenger;

import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

import java.net.ConnectException;
import java.util.Optional;

public interface IMessengerConnector extends Service {
    /**
     * Gets the connection to the remote resource.
     * @return {@link MessengerConnection}
     */
    Optional<MessengerConnection> connection();

    /**
     * Connect to the remote resource.
     *
     * @return A {@link MessengerConnection}.
     * @throws ConnectException If there was an issue connecting to the remote resource.
     */
    MessengerConnection connect() throws ConnectException;
}
