package dungeonmania.goals;

import dungeonmania.Game;

public class BoulderGoal implements Goal {
    public BoulderGoal() {
    }

    @Override
    public boolean achieved(Game game) {
        return game.areAllSwitchesActivated();
    }

    @Override
    public String toString(Game game) {
        return achieved(game) ? "" : ":boulders";
    }
}
