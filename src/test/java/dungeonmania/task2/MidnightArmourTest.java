package dungeonmania.task2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

public class MidnightArmourTest {
    @Test
    @Tag("15-1")
    @DisplayName("Test building Midnight Armour with a Sword and a Sun Stone")
    public void buildMidnightArmour() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest", "c_midnightArmourTest");

        // Move right to pick up Sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Move right to pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Attempt to build Midnight Armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size()); // Sword should be consumed
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size()); // Sun Stone should be consumed
    }

    @Test
    @Tag("15-3")
    @DisplayName("Test building Midnight Armour throws exception when zombies are present")
    public void testCannotBuildMidnightArmourWithZombies() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse res = controller.newGame("d_midnightArmourTest_zombiesPresent",
                "c_midnightArmour_battleEffect");

        // Pick up Sword and Sun Stone
        res = controller.tick(Direction.RIGHT); // Picks up sword
        res = controller.tick(Direction.RIGHT); // Picks up sun stone

        // Attempt to build Midnight Armour (should fail due to zombies)
        assertThrows(InvalidActionException.class, () -> controller.build("sceptre"));
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
    }

    @Test
    @Tag("15-1")
    @DisplayName("Test Midnight Armour adds attack and defense bonus in battle")
    public void testMidnightArmourBattleEffect() throws InvalidActionException {
        DungeonManiaController controller = new DungeonManiaController();
        String config = "c_midnightArmour_battleEffect";
        DungeonResponse res = controller.newGame("d_midnightArmourBattleEffect", config);

        // Pick up Sword and Sun Stone to build Midnight Armour
        res = controller.tick(Direction.RIGHT); // Picks up sword
        res = controller.tick(Direction.RIGHT); // Picks up sun stone

        // Build Midnight Armour
        res = controller.build("midnight_armour");
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // Move to encounter the enemy for battle
        res = controller.tick(Direction.RIGHT);

        // Check that Midnight Armour affects the player's stats during battle
        BattleResponse battle = res.getBattles().get(0);
        double playerBaseAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));
        double armourAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", config));
        double enemyAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("mercenary_attack", config));
        double armourDefense = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_defence", config));

        RoundResponse firstRound = battle.getRounds().get(0);

        // Verify player's enhanced attack due to armour
        assertEquals((playerBaseAttack + armourAttack) / 5, -firstRound.getDeltaEnemyHealth(), 0.001);

        // Verify reduced damage taken by player due to armour defense
        double expectedPlayerDamage = (enemyAttack - armourDefense) / 10;
        assertEquals(expectedPlayerDamage, -firstRound.getDeltaCharacterHealth(), 0.001);

        // Check if the mercenary has died by confirming it’s no longer present in entities
        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "mercenary") == 0);

        // Verify that Midnight Armour remains in the player's inventory after the battle
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
    }
}
