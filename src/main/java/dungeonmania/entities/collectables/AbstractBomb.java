package dungeonmania.entities.collectables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class AbstractBomb extends InventoryItem {
    public enum State {
        SPAWNED, INVENTORY, PLACED
    }

    public static final int DEFAULT_RADIUS = 1;
    private State state;
    private int radius;

    public AbstractBomb(Position position, int radius) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
    }

    public abstract void onOverlap(GameMap map, Entity entity);

    public int getRadius() {
        return radius;
    }

    public abstract void onPutDown(GameMap map, Position p);

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void explode(GameMap map) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        for (int i = x - getRadius(); i <= x + getRadius(); i++) {
            for (int j = y - getRadius(); j <= y + getRadius(); j++) {
                map.destroyEntitiesOnPosition(i, j);
            }
        }
    }

}
