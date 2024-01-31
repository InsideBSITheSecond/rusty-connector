package group.aelysium.rustyconnector.core.lib;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReplyableCommand {
    protected void reply(CommandSource source, Component response) {
        source.sendMessage(response);
    }
    protected void reply(CommandSource source, String response) {
        source.sendMessage(Component.text(response));
    }
    protected void error(CommandSource source, String error) {
        source.sendMessage(Component.text(error, NamedTextColor.RED));
    }
}
