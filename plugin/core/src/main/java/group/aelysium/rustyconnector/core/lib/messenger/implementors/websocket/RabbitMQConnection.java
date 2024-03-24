package group.aelysium.rustyconnector.core.lib.messenger.implementors.websocket;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import group.aelysium.rustyconnector.core.lib.crypt.AESCryptor;
import group.aelysium.rustyconnector.core.lib.messenger.MessengerConnection;
import group.aelysium.rustyconnector.core.lib.model.FailService;
import group.aelysium.rustyconnector.toolkit.core.logger.PluginLogger;
import group.aelysium.rustyconnector.toolkit.core.message_cache.IMessageCacheService;
import group.aelysium.rustyconnector.toolkit.core.messenger.IMessengerConnection;
import group.aelysium.rustyconnector.toolkit.core.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketIdentification;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketListener;
import group.aelysium.rustyconnector.toolkit.velocity.util.LiquidTimestamp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RabbitMQConnection extends MessengerConnection implements IMessengerConnection, AutoCloseable {
    private RabbitMQSubscriber subscriber;
    private final Map<PacketIdentification, List<PacketListener<? extends Packet.Wrapper>>> listeners = new HashMap<>();
    private final ConnectionFactory resources;
    private boolean isAlive = false;
    private ExecutorService executorService;
    private final FailService failService;
    private final AESCryptor cryptor;
    private Connection connection;
    public Channel channel;

    public RabbitMQConnection(ConnectionFactory resources, AESCryptor cryptor) {
        super();
        this.resources = resources;

        this.failService = new FailService(5, LiquidTimestamp.from(2, TimeUnit.SECONDS));
        this.cryptor = cryptor;
    }

    protected void subscribe(IMessageCacheService<?> cache, PluginLogger logger, Packet.Node senderUUID) {
        if(!this.isAlive) return;

        if(this.subscriber == null)
            this.subscriber = new RabbitMQSubscriber(this.cryptor, cache, logger, senderUUID, listeners);

        this.executorService.submit(() -> {
            try {
                if (this.connection == null) {
                    this.connection = this.resources.newConnection();
                    this.channel = null;
                }
                if (this.channel == null) this.channel = this.connection.createChannel();

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    this.subscriber.onMessage(message);
                };
                channel.basicConsume("rustyconnector", true, deliverCallback, consumerTag -> { });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    RabbitMQConnection.this.failService.trigger("RedisService has failed to many times within the allowed amount of time! Please check the error messages and try again!");
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                }
            }

            RabbitMQConnection.this.subscribe(cache, logger, senderUUID);
        });
    }

    public void startListening(IMessageCacheService<?> cache, PluginLogger logger, Packet.Node senderUUID) {
        if(this.isAlive) throw new IllegalStateException("The RedisService is already running! You can't start it again! Shut it down with `.kill()` first and then try again!");
        this.executorService = Executors.newFixedThreadPool(2);

        this.isAlive = true;

        this.subscribe(cache, logger, senderUUID);
    }

    @Override
    public void kill() {
        this.isAlive = false;
        this.failService.kill();

        try {
            this.executorService.shutdown();
            try {
                if (!this.executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    this.executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.executorService.shutdownNow();
            }
        } catch (Exception ignore) {}
    }

    public void publish(Packet packet) {
        try {
            String signedPacket = this.cryptor.encrypt(packet.toString());

            if (this.connection == null) {
                this.connection = this.resources.newConnection();
                this.channel = null;
            }
            if (this.channel == null) this.channel = this.connection.createChannel();

            this.channel.queueDeclare("rustyconnector", false, false, false, null);
            channel.basicPublish("", "rustyconnector", null, signedPacket.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void publish(Packet.Wrapper wrapper) {
        publish(wrapper.packet());
    }

    @Override
    public <TPacketListener extends PacketListener<? extends Packet.Wrapper>> void listen(TPacketListener listener) {
        this.listeners.computeIfAbsent(listener.target(), s -> new ArrayList<>());

        this.listeners.get(listener.target()).add(listener);
    }

    @Override
    public void close() throws Exception {
        this.kill();
    }
}
