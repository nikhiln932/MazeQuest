package dungeonmania.task2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SceptreTest {
    @Test
    @Tag("14-1")
    @DisplayName("Test building a Sceptre with one and two Sun Stones")
    public void buildSceptreWithSunStones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildSceptreWithSunStones", "c_buildSceptreWithSunStones");

        // Move to pick up 1 Sun Stone and 2 Arrows
        res = dmc.tick(Direction.RIGHT); // Pick up first Sun Stone
        res = dmc.tick(Direction.RIGHT); // Pick up first Arrow
        res = dmc.tick(Direction.RIGHT); // Pick up second Arrow

        // Confirm inventory state
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Attempt to build Sceptre - should not succeed due to missing second Sun Stone
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());

        // Move to pick up an additional Sun Stone
        res = dmc.tick(Direction.RIGHT); // Pick up second Sun Stone

        // Confirm inventory state after picking up the second Sun Stone
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // Attempt to build Sceptre - should succeed this time
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size()); // Arrows should be consumed
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size()); // One Sun Stone should remain
    }

    @Test
    @Tag("14-2")
    @DisplayName("Test mind controlling a mercenary using a Sceptre")
    public void mindControlMercenaryWithSceptre() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mindControlMercenaryTest", "c_mindControlMercenaryTest");

        String mercId = TestUtils.getEntities(res, "mercenary").get(0).getId();

        // Pick up resources and build a Sceptre
        res = dmc.tick(Direction.RIGHT); // Picks up first Sun Stone
        res = dmc.tick(Direction.RIGHT); // Picks up second Sun Stone
        res = dmc.tick(Direction.RIGHT); // Picks up first Arrow
        res = dmc.tick(Direction.RIGHT); // Picks up second Arrow
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Initiate mind control interaction (each action above counts as a tick)
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Expected behavior for mind control: mercenary moves closer each tick
        // After 5 ticks, mercenary should end up adjacent to player
        for (int i = 0; i < 5; i++) {

            res = dmc.tick(Direction.UP);
            Position playerPos = getPlayerPos(res);
            Position mercPos = getMercPos(res);

            // Check that the mercenary is moving closer to the player
            assertEquals(new Position(playerPos.getX(), playerPos.getY() + 1), mercPos);
            assertEquals(0, res.getBattles().size()); // Ensure no battle occurs while controlled
        }

        // Check that after 5 total ticks, the mercenary is no longer controlled and hostile
        res = dmc.tick(Direction.DOWN); // Moving back to encounter mercenary
        assertTrue(res.getBattles().size() > 0); // Battle should occur
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
