package dungeonmania.task2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SunStoneTest {
    @Test
    @Tag("4-2")
    @DisplayName("Test player can pick up a Sun Stone and add to inventory")
    public void pickUpSunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SunStoneTest_pickUp", "c_SunStoneTest_pickUp");

        // Assert that Sun Stone exists in the dungeon and is not in inventory
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Move player to pick up the Sun Stone
        res = dmc.tick(Direction.RIGHT);

        // Check that the Sun Stone is now in the player's inventory and no longer in the dungeon
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
    }

    @Test
    @Tag("4-3")
    @DisplayName("Test player can use a Sun Stone to open and walk through a door")
    public void useSunStoneWalkThroughOpenDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SunStoneTest_useToOpenDoor", "c_SunStoneTest_useToOpenDoor");

        // Check Sun Stone is in the dungeon and not in inventory initially
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up the Sun Stone
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());

        // Use Sun Stone to open door and confirm it remains in inventory
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("4-8")
    @DisplayName("Test player can craft a Shield using Sun Stone and then a Sceptre with new resources")
    public void craftShieldAndSceptreWithSunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SunStoneTest_crafting", "c_SunStoneTest_crafting");

        // Pick up Sun Stone and initial resources for crafting Shield
        res = dmc.tick(Direction.RIGHT); // Picks up Sun Stone
        res = dmc.tick(Direction.RIGHT); // Picks up first Wood
        res = dmc.tick(Direction.RIGHT); // Picks up second Wood

        // Confirm initial inventory state
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Craft a Shield (Sun Stone should remain in inventory)
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size()); // Sun Stone should remain
        assertEquals(0, TestUtils.getInventory(res, "wood").size()); // Wood should be consumed

        // Pick up additional resources for crafting Sceptre
        res = dmc.tick(Direction.RIGHT); // Picks up additional Wood
        res = dmc.tick(Direction.RIGHT); // Picks up Treasure

        // Confirm inventory state after picking up new resources
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Craft a Sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size()); // Sun Stone remains if used as substitute
        assertEquals(0, TestUtils.getInventory(res, "treasure").size()); // Treasure used up in crafting
        assertEquals(0, TestUtils.getInventory(res, "wood").size()); // Wood used up
    }

    @Test
    @Tag("13-5")
    @DisplayName("Test achieving a treasure goal using a Sun Stone")
    public void treasureGoalWithSunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_treasureGoalWithSunStone", "c_SunStoneTest_crafting");

        // Assert that treasure goal is initially not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // Move player to pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Assert that treasure goal is now met
        assertEquals("", TestUtils.getGoals(res));
    }
}
