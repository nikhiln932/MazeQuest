package dungeonmania.entities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.actionInterfaces.Overlap;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.AbstractBomb;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Useable;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, Overlap {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;
    private Set<PotionListener> potionListeners = new HashSet<>();

    private int collectedTreasureCount = 0;
    private int mindControlDurationLeft = 0;
    private Mercenary mindControlledEnemy = null;

    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState(this);
    }

    public int getCollectedTreasureCount() {
        return collectedTreasureCount;
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public Useable getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables(GameMap map) {
        return inventory.getBuildables(map);
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(true, entity, factory);
        if (item == null)
            return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied())
                    return;
            }
            map.getGame().battle(this, (Enemy) entity);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        if (item instanceof Treasure || item instanceof SunStone)
            collectedTreasureCount++;
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null)
            inventory.remove(item);
    }

    public void use(AbstractBomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            potionListeners.forEach(PotionListener::notifyNoPotion);
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        potionListeners.forEach(e -> e.notifyPotion(inEffective));
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void useSceptreOn(Mercenary mercenary) {
        // Check if player has a Sceptre

        // Set up mind control duration and controlled mercenary
        Sceptre sceptre = (Sceptre) inventory.getFirst(Sceptre.class); // Get the Sceptre
        mindControlDurationLeft = sceptre.getDuration(); // Set duration from Sceptre
        mindControlledEnemy = mercenary;

        // Apply mind control to mercenary
        mercenary.mindControl();

        inventory.remove(sceptre); // Uncomment if you want single-use functionality
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }

        if (mindControlDurationLeft > 0) {
            mindControlDurationLeft--;
            if (mindControlDurationLeft == 0) {
                releaseMindControl(); // Release control once duration ends
            }
        }
    }

    public void releaseMindControl() {
        if (mindControlledEnemy != null) {
            mindControlledEnemy.releaseMindControl(); // Reset mercenary to hostile
            mindControlledEnemy = null; // Clear the reference to the controlled enemy
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return state.applyBuff(origin);
    }

    public void registerPotionListener(PotionListener e) {
        potionListeners.add(e);

        if (getEffectivePotion() != null)
            e.notifyPotion(getEffectivePotion());
    }

    public void removePotionListener(PotionListener e) {
        potionListeners.remove(e);
    }

    public boolean hasSceptre() {
        return inventory.count(Sceptre.class) > 0;
    }

    public boolean hasSunStone() {
        return inventory.count(SunStone.class) > 0;
    }
}
