package dungeonmania.entities.logicalEntities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class Wire extends Conductor {
    private List<Conductor> conductors = new ArrayList<>();

    public Wire(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public void subscribe(Conductor c) {
        conductors.add(c);
    }

    public void notifyConductors(boolean state) {
        setActivated(state);
        for (Conductor c : conductors) {
            if (c.isActivated() != isActivated()) {
                c.notifyConductors(isActivated());
            }
        }
    }
}
