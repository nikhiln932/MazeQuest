package dungeonmania.entities.enemies.movement;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.util.Position;

import java.util.List;

public class SpiderPatternMovement implements MovementStrategy {
    private List<Position> movementTrajectory;
    private int nextPositionElement;
    private boolean forward;

    public SpiderPatternMovement(Position startPosition) {
        // Set up the circular or predefined movement trajectory around the initial position
        this.movementTrajectory = startPosition.getAdjacentPositions();
        this.nextPositionElement = 1; // Starting point in the trajectory
        this.forward = true; // Indicates movement direction along the trajectory
    }

    @Override
    public void move(Game game, Enemy enemy) {
        // Determine the next position along the trajectory
        Position nextPos = movementTrajectory.get(nextPositionElement);

        // Check for obstacles (e.g., Boulders) in the next position
        List<Entity> entities = game.getMap().getEntities(nextPos);
        if (entities != null && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            // Reverse direction if an obstacle is encountered
            forward = !forward;
            updateNextPosition();
            updateNextPosition(); // Move back two steps to continue in the opposite direction
        }

        // Update to the next position if it's clear
        nextPos = movementTrajectory.get(nextPositionElement);
        entities = game.getMap().getEntities(nextPos);
        if (entities == null || entities.size() == 0
                || entities.stream().allMatch(e -> e.canMoveOnto(game.getMap(), enemy))) {
            game.getMap().moveTo(enemy, nextPos);
            updateNextPosition();
        }

    }

    private void updateNextPosition() {
        if (forward) {
            nextPositionElement++;
            if (nextPositionElement == 8) {
                nextPositionElement = 0;
            }
        } else {
            nextPositionElement--;
            if (nextPositionElement == -1) {
                nextPositionElement = 7;
            }
        }
    }
}
