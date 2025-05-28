package dungeonmania.entities.enemies.movement;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class AlliedMovement implements MovementStrategy {
    private boolean isAdjacentToPlayer;

    public AlliedMovement(boolean isInitiallyAdjacent) {
        this.isAdjacentToPlayer = isInitiallyAdjacent;
    }

    public void setAdjacency(boolean adjacency) {
        isAdjacentToPlayer = adjacency;
    }

    @Override
    public void move(Game game, Enemy enemy) {
        GameMap map = game.getMap();
        Player player = game.getPlayer();

        Position nextPos = isAdjacentToPlayer ? player.getPreviousDistinctPosition()
                : map.dijkstraPathFind(enemy.getPosition(), player.getPosition(), enemy);
        if (!isAdjacentToPlayer && Position.isAdjacent(player.getPosition(), nextPos))
            isAdjacentToPlayer = true;

        map.moveTo(enemy, nextPos);
    }
}
