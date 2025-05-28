package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public abstract class PlayerState {
    private Player player;

    PlayerState(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void transitionBase() {
        player.changeState(new BaseState(player));
    }

    public void transitionInvisible() {
        player.changeState(new InvisibleState(player));
    }

    public void transitionInvincible() {
        player.changeState(new InvincibleState(player));
    }

    public abstract BattleStatistics applyBuff(BattleStatistics origin);
}
