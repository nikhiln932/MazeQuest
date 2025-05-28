package dungeonmania.entities.enemies.movement;

import dungeonmania.Game;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class RunAwayMovement implements MovementStrategy {
    public void move(Game game, Enemy enemy) {
        GameMap map = game.getMap();
        Position playerPosition = map.getPlayer().getPosition();
        Position enemyPosition = enemy.getPosition();

        Position plrDiff = Position.calculatePositionBetween(playerPosition, enemyPosition);
        Position moveX = (plrDiff.getX() >= 0) ? Position.translateBy(enemyPosition, Direction.RIGHT)
                : Position.translateBy(enemyPosition, Direction.LEFT);
        Position moveY = (plrDiff.getY() >= 0) ? Position.translateBy(enemyPosition, Direction.DOWN)
                : Position.translateBy(enemyPosition, Direction.UP);
        Position offset = enemyPosition;
        if (plrDiff.getY() == 0 && map.canMoveTo(enemy, moveX))
            offset = moveX;
        else if (plrDiff.getX() == 0 && map.canMoveTo(enemy, moveY))
            offset = moveY;
        else if (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY())) {
            if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else
                offset = enemyPosition;
        } else {
            if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else
                offset = enemyPosition;
        }
        map.moveTo(enemy, offset);
    }
}
