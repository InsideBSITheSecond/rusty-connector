package group.aelysium.rustyconnector.toolkit.velocity.matchmaking.storage.player_rank;

import group.aelysium.rustyconnector.toolkit.velocity.storage.IStorageService;

public interface IWinLossPlayerRank extends IPlayerRank<Double> {
    /**
     * Marks a win for this player.
     * This will also store the new rank in the remote storage.
     * @param storage The remote storage to save the win in.
     */
    <TMySQLStorage extends IStorageService> void markWin(TMySQLStorage storage);

    /**
     * Marks a loss for this player.
     * This will also store the new rank in the remote storage.
     * @param storage The remote storage to save the loss in.
     */
    <TMySQLStorage extends IStorageService> void markLoss(TMySQLStorage storage);

    Double rank();
}
