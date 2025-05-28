package dungeonmania.entities.enemies.movement;

import dungeonmania.Game;
import dungeonmania.entities.enemies.Enemy;

public interface MovementStrategy {
    void move(Game game, Enemy enemy);
}
