package dungeonmania.entities.logicalEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.AbstractBomb;
import dungeonmania.entities.logicalEntities.logicStrategies.LogicStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LogicBomb extends AbstractBomb implements Logical {
    public static final int DEFAULT_RADIUS = 1;
    private List<Conductor> conductors = new ArrayList<>();
    private LogicStrategy strategy;
    private boolean explode = false;

    public LogicBomb(Position position, int radius, LogicStrategy strategy) {
        super(position, radius);
        this.strategy = strategy;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (getState() != State.SPAWNED)
            return;
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this))
                return;
            map.destroyEntity(this);
        }
        setState(State.INVENTORY);
    }

    public void onPutDown(GameMap map, Position p) {
        setPosition(p);
        map.addEntity(this);
        setState(State.PLACED);
        List<Position> adjPosList = getPosition().getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<Entity> entities = map.getEntities(node).stream().filter(e -> (e instanceof Conductor))
                    .collect(Collectors.toList());
            entities.stream().map(Conductor.class::cast).forEach(s -> this.subscribe(s));
        });
    }

    @Override
    public void tick() {
        if (strategy.evaluate(conductors) && getState() == State.PLACED) {
            explode = true;
        }
    }

    public void checkExplode(GameMap map) {
        if (explode) {
            explode(map);
        }
    }

    public void subscribe(Conductor c) {
        conductors.add(c);
    }

}
