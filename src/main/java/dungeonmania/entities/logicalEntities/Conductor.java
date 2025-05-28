package dungeonmania.entities.logicalEntities;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends Entity {
    private boolean activated;

    public Conductor(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public abstract void notifyConductors(boolean state);

    public abstract void subscribe(Conductor c);

}
