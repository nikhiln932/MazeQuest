package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.logicalEntities.Conductor;
import dungeonmania.entities.logicalEntities.Logical;
import dungeonmania.entities.logicalEntities.logicStrategies.LogicStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends Entity implements Logical {
    private List<Conductor> conductors = new ArrayList<>();
    private LogicStrategy strategy;
    private boolean open = false;

    public SwitchDoor(Position position) {
        super(position.asLayer(Entity.DOOR_LAYER));
    }

    public SwitchDoor(Position position, LogicStrategy strategy) {
        super(position.asLayer(Entity.DOOR_LAYER));
        this.strategy = strategy;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return false;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void tick() {
        open = strategy.evaluate(conductors);
    }

    public void subscribe(Conductor c) {
        conductors.add(c);
    }

}
