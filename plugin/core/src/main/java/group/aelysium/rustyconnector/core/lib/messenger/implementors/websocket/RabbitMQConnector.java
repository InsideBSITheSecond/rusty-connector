package group.aelysium.rustyconnector.core.lib.messenger.implementors.websocket;

import com.rabbitmq.client.ConnectionFactory;
import group.aelysium.rustyconnector.core.lib.crypt.AESCryptor;
import group.aelysium.rustyconnector.core.lib.messenger.MessengerConnector;
import group.aelysium.rustyconnector.toolkit.core.UserPass;
import group.aelysium.rustyconnector.toolkit.core.messenger.IMessengerConnection;

import java.net.ConnectException;
import java.net.InetSocketAddress;

public class RabbitMQConnector extends MessengerConnector {
    private static final ConnectionFactory resources = new ConnectionFactory();
    protected final String dataChannel;

    private RabbitMQConnector(AESCryptor cryptor, InetSocketAddress address, UserPass userPass, String dataChannel) {
        super(cryptor, address, userPass);
        resources.setHost(address.getHostName());
        resources.setPort(address.getPort());
        resources.setUsername(userPass.user());
        resources.setPassword(new String(userPass.password()));
        this.dataChannel = dataChannel;
    }

    @Override
    public IMessengerConnection connect() throws ConnectException {

        this.connection = new RabbitMQConnection(
            resources,
            this.cryptor
        );

        return this.connection;
    }

    /**
     * Creates a new {@link RabbitMQConnector} and returns it.
     * @param cryptor The cryptor to use when shipping messages.
     * @param spec The spec to load the connector with.
     * @return A {@link RabbitMQConnector}.
     */
    public static RabbitMQConnector create(AESCryptor cryptor, RabbitMQConnectorSpec spec) {
        return new RabbitMQConnector(cryptor, spec.address(), spec.userPass(), spec.dataChannel());
    }

    public record RabbitMQConnectorSpec(InetSocketAddress address, UserPass userPass, String dataChannel) { }


    @Override
    public void kill() {
        if(this.connection != null) this.connection.kill();
    }
}
