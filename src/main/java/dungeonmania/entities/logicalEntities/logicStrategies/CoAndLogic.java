package dungeonmania.entities.logicalEntities.logicStrategies;

import java.util.List;

import dungeonmania.entities.logicalEntities.Conductor;

public class CoAndLogic implements LogicStrategy {
    private int prevActiveConductors = 0;
    private boolean on;

    @Override
    public boolean evaluate(List<Conductor> adjacentConductors) {
        int numAdjActive = (int) adjacentConductors.stream().filter(Conductor::isActivated).count();
        int numAdj = adjacentConductors.size();
        if (prevActiveConductors == numAdjActive) {
            return on;
        } else if (numAdjActive == numAdj && numAdj >= 2 && prevActiveConductors == 0) {
            prevActiveConductors = numAdjActive;
            return on = true;
        } else {
            prevActiveConductors = numAdjActive;
            return on = false;
        }
    }
}
