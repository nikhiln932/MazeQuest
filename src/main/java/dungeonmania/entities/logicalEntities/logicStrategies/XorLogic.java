package dungeonmania.entities.logicalEntities.logicStrategies;

import java.util.List;

import dungeonmania.entities.logicalEntities.Conductor;

public class XorLogic implements LogicStrategy {
    @Override
    public boolean evaluate(List<Conductor> adjacentConductors) {
        return adjacentConductors.stream().filter(Conductor::isActivated).count() == 1;
    }
}
