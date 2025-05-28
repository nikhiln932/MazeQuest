package dungeonmania.goals;

import dungeonmania.Game;

public class ExitGoal implements Goal {
    public ExitGoal() {
    }

    @Override
    public boolean achieved(Game game) {
        return game.isPlayerOnExit();
    }

    @Override
    public String toString(Game game) {
        return achieved(game) ? "" : ":exit";
    }
}
