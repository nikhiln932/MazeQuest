package dungeonmania.task2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class LogicTest {
    @Test
    @Tag("2f-1")
    @DisplayName("Test turning on a basic light bulb")
    public void testBasicOrLightBulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicLightTest", "c_basicGoalsTest_enemy");
        // Start of game
        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 2);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 0);

        res = dmc.tick(Direction.UP);

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 1);

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 2);

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        // pushed the first boulder off the first switch but the second switch should keep the bulb on
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 2);

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 2);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 0);
    }

    @Test
    @Tag("2f-2")
    @DisplayName("Co_And Logic on lightbulb")
    public void testCoandLightBulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("aSwitchDoorTest", "c_basicGoalsTest_enemy");
        // Start of game
        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 0);

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        // doesnt turn on
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 0);

        // push off first switch
        res = dmc.tick(Direction.DOWN);

        // push activating one
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);

        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_off") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "light_bulb_on") == 1);

    }

    @Test
    @Tag("2f-3")
    @DisplayName("AND switch doors + boulder push through")
    public void testSwitchDoorAnd() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("aSwitchDoorTest", "c_basicGoalsTest_enemy");
        // Start of game
        List<EntityResponse> entities = res.getEntities();
        Position playerStart = TestUtils.getEntities(res, "player").get(0).getPosition();

        // boulder doesnt go through door
        res = dmc.tick(Direction.UP);
        assertEquals(playerStart, TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 2);
        res = dmc.tick(Direction.UP);
        Position prevPosition = TestUtils.getEntities(res, "player").get(0).getPosition();

        // the one that has and condition doesnt open
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 1);

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(playerStart, TestUtils.getEntities(res, "player").get(0).getPosition());
        // push boulder through door
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);

        assertEquals(prevPosition, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("2f-4")
    @DisplayName("Logic Bomb xor and")
    public void testLogicBombsXOr() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("aBombLogical", "c_basicGoalsTest_enemy");
        // Start of game
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        assertEquals(2, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // not exploded becuase its and
        assertEquals(2, TestUtils.getEntities(res, "bomb").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        //explode
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);

        // not exploded becuase its xor
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        // not exploded becuase its xor
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);

        // removed two adjacent so now only one, should explode
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
    }

    @Test
    @Tag("2f-5")
    @DisplayName("Close Switch Door")
    public void testSwitchDoorClose() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("aSwitchDoorTest", "c_basicGoalsTest_enemy");
        // Start of game
        List<EntityResponse> entities = res.getEntities();
        Position playerStart = TestUtils.getEntities(res, "player").get(0).getPosition();

        // boulder doesnt go through door
        res = dmc.tick(Direction.UP);
        assertEquals(playerStart, TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 2);
        res = dmc.tick(Direction.UP);

        // the one that has and condition doesnt open
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 1);

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(playerStart, TestUtils.getEntities(res, "player").get(0).getPosition());
        // push boulder through door
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 1);
        res = dmc.tick(Direction.LEFT);
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door_open") == 0);
        assertTrue(TestUtils.countEntityOfType(entities, "switch_door") == 2);
        Position prevPosition = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);

        assertEquals(prevPosition, TestUtils.getEntities(res, "player").get(0).getPosition());
    }
}
