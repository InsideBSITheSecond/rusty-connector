package group.aelysium.rustyconnector.core.lib.messenger.implementors.rabbit_mq;

import group.aelysium.rustyconnector.core.lib.crypt.AESCryptor;
import group.aelysium.rustyconnector.core.lib.messenger.MessengerSubscriber;
import group.aelysium.rustyconnector.toolkit.core.logger.PluginLogger;
import group.aelysium.rustyconnector.toolkit.core.message_cache.ICacheableMessage;
import group.aelysium.rustyconnector.toolkit.core.message_cache.IMessageCacheService;
import group.aelysium.rustyconnector.toolkit.core.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketIdentification;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketListener;

import java.util.List;
import java.util.Map;

public class WebsocketSubscriber extends MessengerSubscriber {
    public WebsocketSubscriber(AESCryptor cryptor, IMessageCacheService<? extends ICacheableMessage> cache, PluginLogger logger, Packet.Node senderUUID, Map<PacketIdentification, List<PacketListener<? extends Packet.Wrapper>>> listeners) {
        super(cryptor, cache, logger, senderUUID, listeners);
    }

    public void onMessage(String message) {
        this.onMessage(message);
    }
}