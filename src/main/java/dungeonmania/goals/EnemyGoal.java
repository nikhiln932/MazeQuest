package dungeonmania.goals;

import dungeonmania.Game;

public class EnemyGoal implements Goal {
    private int requiredKills;

    public EnemyGoal(int requiredKills) {
        this.requiredKills = requiredKills;
    }

    public boolean achieved(Game game) {
        boolean sufficientKills = game.getKillCount() >= requiredKills;
        boolean spawnersDestroyed = game.areAllZombieSpawnersDestroyed();
        return sufficientKills && spawnersDestroyed;
    }

    @Override
    public String toString(Game game) {
        return achieved(game) ? "" : ":enemy";
    }
}
