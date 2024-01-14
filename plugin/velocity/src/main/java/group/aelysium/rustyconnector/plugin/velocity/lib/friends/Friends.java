package group.aelysium.rustyconnector.plugin.velocity.lib.friends;

import group.aelysium.rustyconnector.plugin.velocity.lib.players.Player;
import group.aelysium.rustyconnector.plugin.velocity.lib.storage.card_holders.FriendsHolder;
import group.aelysium.rustyconnector.toolkit.core.card.Card;
import group.aelysium.rustyconnector.toolkit.core.card.CardController;
import group.aelysium.rustyconnector.toolkit.velocity.friends.PlayerPair;

public class Friends extends Card {
    private final PlayerPair players;

    public Friends(Player player1, Player player2) {
        this.players = PlayerPair.from(player1, player2);
    }

    public PlayerPair playerPair() {
        this.catchIllegalCall();
        return this.players;
    }

    @Override
    public boolean attributeEquals(Attribute<?> attribute) {
        this.catchIllegalCall();

        return this.players.player1().equals(attribute.attribute()) ||
               this.players.player2().equals(attribute.attribute());
    }

    @Override
    public Altercator<?> alter() {
        this.catchIllegalCall();

        return null;
    }

    public static class Builder extends CardController.Create.Creator<Friends> {
        private Player player1;
        private Player player2;

        public Builder(FriendsHolder owner) {
            super(owner);
        }

        public Builder player1(Player player1) {
            this.player1 = player1;
            return this;
        }

        public Builder player2(Player player2) {
            this.player2 = player2;
            return this;
        }

        @Override
        public ReadyForInsert<Friends> prepare() {
            return new ReadyForInsert<>(this.owner, new Friends(this.player1, this.player2));
        }
    }
}
