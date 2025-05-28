package dungeonmania.entities.logicalEntities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logicalEntities.logicStrategies.LogicStrategy;
import dungeonmania.util.Position;

public class LightBulb extends Entity implements Logical {
    private List<Conductor> conductors = new ArrayList<>();
    private LogicStrategy strategy;

    private boolean on = false;

    public LightBulb(Position position) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
    }

    public LightBulb(Position position, LogicStrategy strategy) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        this.strategy = strategy;
    }

    public LogicStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(LogicStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void tick() {
        on = strategy.evaluate(conductors);
    }

    public void subscribe(Conductor c) {
        conductors.add(c);
    }

    public boolean isOn() {
        return on;
    }

}
