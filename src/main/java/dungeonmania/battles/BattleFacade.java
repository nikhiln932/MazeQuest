package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.BuffApplier;
import dungeonmania.entities.collectables.Useable;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getBattleStatistics().getHealth();
        double initialEnemyHealth = enemy.getBattleStatistics().getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);

        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<InventoryItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);

        Potion effectivePotion = player.getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = player.applyBuff(playerBuff);
        } else {
            for (InventoryItem item : player.getInventory().getEntities(InventoryItem.class)) {
                if (item instanceof BuffApplier && !(item instanceof Potion)) {
                    playerBuff = ((BuffApplier) item).applyBuff(playerBuff);
                    battleItems.add(item);
                    if (item instanceof Useable) {
                        ((Useable) item).use(game);
                    }
                }
            }
        }

        List<Mercenary> mercs = game.getMap().getEntities(Mercenary.class);
        for (Mercenary merc : mercs) {
            if (!merc.isAllied())
                continue;
            playerBuff = BattleStatistics.applyBuff(playerBuff, merc.getBattleStatistics());
        }

        // 2. Battle the two stats
        BattleStatistics playerBaseStatistics = player.getBattleStatistics();
        BattleStatistics enemyBaseStatistics = enemy.getBattleStatistics();
        BattleStatistics playerBattleStatistics = BattleStatistics.applyBuff(playerBaseStatistics, playerBuff);
        BattleStatistics enemyBattleStatistics = enemyBaseStatistics;
        if (!playerBattleStatistics.isEnabled() || !enemyBaseStatistics.isEnabled())
            return;
        List<BattleRound> rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);

        // 3. update health to the actual statistics
        player.getBattleStatistics().setHealth(playerBattleStatistics.getHealth());
        enemy.getBattleStatistics().setHealth(enemyBattleStatistics.getHealth());

        // 4. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(enemyString,
                rounds.stream().map(ResponseBuilder::getRoundResponse).collect(Collectors.toList()),
                battleItems.stream().map(Entity.class::cast).map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth, initialEnemyHealth));
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }
}
