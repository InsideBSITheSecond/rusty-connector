package group.aelysium.rustyconnector.core.lib.lang;

import group.aelysium.rustyconnector.core.central.PluginLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.newlines;

@SuppressWarnings("ConstantConditions")
public class Lang {
    public final static String attachedWordmark = "RustyConnector:";

    public interface Message {
        Component build();

        default void send(PluginLogger sender) {
            sender.send(PlainTextComponentSerializer.plainText().serialize(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build()
                    ))
            );
        }
    }
    public interface ParameterizedMessage1<A1> {
        Component build(A1 arg1);

        default void send(PluginLogger sender, A1 arg1) {
            sender.send(PlainTextComponentSerializer.plainText().serialize(
                    join(
                            JoinConfiguration.separator(newline()),
                            text(attachedWordmark),
                            build(arg1)
                    ))
            );
        }
    }
    public interface ParameterizedMessage2<A1, A2> {
        Component build(A1 arg1, A2 arg2);

        default void send(PluginLogger sender, A1 arg1, A2 arg2) {
            sender.send(PlainTextComponentSerializer.plainText().serialize(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2)
                    ))
            );
        }
    }
    public interface ParameterizedMessage3<A1, A2, A3> {
        Component build(A1 arg1, A2 arg2, A3 arg3);

        default void send(PluginLogger sender, A1 arg1, A2 arg2, A3 arg3) {
            sender.send(PlainTextComponentSerializer.plainText().serialize(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2, arg3)
                    ))
            );
        }
    }
    public interface ParameterizedMessage4<A1, A2, A3, A4> {
        Component build(A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(PluginLogger sender, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.send(PlainTextComponentSerializer.plainText().serialize(
                    join(
                            newlines(),
                            text(attachedWordmark),
                            build(arg1, arg2, arg3, arg4)
                    ))
            );
        }
    }
}




