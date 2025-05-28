package dungeonmania.entities.logicalEntities.logicStrategies;

import java.util.List;

import dungeonmania.entities.logicalEntities.Conductor;

public interface LogicStrategy {
    public boolean evaluate(List<Conductor> adjacentConductors);
}
