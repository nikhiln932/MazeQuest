package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.actionInterfaces.MovedAway;
import dungeonmania.entities.actionInterfaces.Overlap;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.logicalEntities.Conductor;
import dungeonmania.entities.logicalEntities.Wire;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor implements Overlap, MovedAway {
    private List<Bomb> bombs = new ArrayList<>();
    private List<Wire> wires = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (isActivated()) {
            activateBombs(map);
        }
    }

    public void subscribe(Conductor c) {
        wires.add((Wire) c);
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(true);
            notifyConductors(true);
            activateBombs(map);
        }
    }

    public void activateBombs(GameMap map) {
        for (Bomb b : bombs) {
            b.explode(map);
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(false);
            turnOffWires();
        }
    }

    @Override
    public void notifyConductors(boolean state) {
        if (isActivated()) {
            for (Wire w : wires) {
                if (!w.isActivated()) {
                    w.notifyConductors(true);
                }
            }
        }
    }

    public void turnOffWires() {
        for (Wire w : wires) {
            if (w.isActivated()) {
                w.notifyConductors(false);
            }
        }
    }

}
