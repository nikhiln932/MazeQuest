package dungeonmania.entities.enemies.movement;

import java.util.List;
import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class RandomMovement implements MovementStrategy {
    public void move(Game game, Enemy enemy) {
        GameMap map = game.getMap();
        Position enemyPosition = enemy.getPosition();
        List<Position> pos = enemyPosition.getCardinallyAdjacentPositions();
        Position nextPos = null;
        Random randGen = new Random();

        if (pos.size() == 0) {
            nextPos = enemyPosition;
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
        }

        map.moveTo(enemy, nextPos);
    }
}
