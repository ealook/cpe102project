import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestCases {

    private static final double DELTA = 0.00001;
    private Point p = new Point(1, 2);
    private Point p1 = new Point(1, 3);

    @Test
    public void testPoint() {
        assertEquals(p.getX(), 1, DELTA);
        assertEquals(p.getY(), 2, DELTA);
        assertTrue(p.adjacent_to(p1));
    }

    @Test
    public void testWorldObject() {
        WorldObject o1 = new WorldObject("billy");

        assertTrue(o1.getName().compareTo("billy") == 0);
    }

    @Test
    public void testBackground() {
        Background o1 = new Background("bob");

        assertTrue(o1.getName().compareTo("bob") == 0);
    }

    @Test
    public void testWorldEntity() {
        WorldEntity o1 = new WorldEntity("joe", p);

        assertTrue(o1.getName().compareTo("joe") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
    }

    @Test
    public void testObstacle() {
        Obstacle o1 = new Obstacle("steve", p1);

        assertTrue(o1.getName().compareTo("steve") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p));
    }

    @Test
    public void testBlacksmith() {
        Blacksmith o1 = new Blacksmith("david", p, 10, 1);

        assertTrue(o1.getName().compareTo("david") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_resource_count(), 0, DELTA);
        o1.set_resource_count(3);
        assertEquals(o1.get_resource_count(), 3, DELTA);
        assertEquals(o1.get_resource_distance(), 1, DELTA);
        assertEquals(o1.get_rate(), 1, DELTA);
        assertEquals(o1.get_resource_limit(), 10, DELTA);
        assertTrue(o1.entity_string().compareTo("blacksmith david 1 3 10 1 1") == 0);
    }

    @Test
    public void testActor() {
        Actor o1 = new Actor("chandler", p);

        assertTrue(o1.getName().compareTo("chandler") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
    }

    @Test
    public void testVein() {
        Vein o1 = new Vein("ethan", p, 11);

        assertTrue(o1.getName().compareTo("ethan") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_resource_distance(), 1, DELTA);
        assertEquals(o1.get_rate(), 11, DELTA);
        assertTrue(o1.entity_string().compareTo("vein ethan 1 3 11 1") == 0);
    }

    @Test
    public void testOre() {
        Ore o1 = new Ore("usa", p);

        assertTrue(o1.getName().compareTo("usa") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertTrue(o1.entity_string().compareTo("ore usa 1 3 5000") == 0);
    }

    @Test
    public void testAnimatedActor() {
        AnimatedActor o1 = new AnimatedActor("tristen", p);

        assertTrue(o1.getName().compareTo("tristen") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
    }

    @Test
    public void testQuake() {
        Quake o1 = new Quake("kat", p);

        assertTrue(o1.getName().compareTo("kat") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
    }

    @Test
    public void testMover() {
        Mover o1 = new Mover("ross", p, 100);

        assertTrue(o1.getName().compareTo("ross") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_rate(), 100, DELTA);
    }

    @Test
    public void testOreBlob() {
        OreBlob o1 = new OreBlob("sponge", p, 17);

        assertTrue(o1.getName().compareTo("sponge") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_rate(), 17, DELTA);
    }

    @Test
    public void testMiner() {
        Miner o1 = new Miner("mike", p, 3, 9, 4);

        assertTrue(o1.getName().compareTo("mike") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_resource_count(), 4, DELTA);
        o1.set_resource_count(3);
        assertEquals(o1.get_resource_count(), 3, DELTA);
        assertEquals(o1.get_rate(), 3, DELTA);
        assertEquals(o1.get_resource_limit(), 9, DELTA);
    }

    @Test
    public void testMinerNotFull() {
        MinerNotFull o1 = new MinerNotFull("michael", p, 3, 9);

        assertTrue(o1.getName().compareTo("michael") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_resource_count(), 0, DELTA);
        o1.set_resource_count(3);
        assertEquals(o1.get_resource_count(), 3, DELTA);
        assertEquals(o1.get_rate(), 3, DELTA);
        assertEquals(o1.get_resource_limit(), 9, DELTA);
        assertTrue(o1.entity_string().compareTo("miner michael 1 3 9 3") == 0);
    }

    @Test
    public void testMinerFull() {
        MinerFull o1 = new MinerFull("mi", p, 3, 9);

        assertTrue(o1.getName().compareTo("mi") == 0);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 2, DELTA);
        assertTrue(o1.getPosition().adjacent_to(p1));
        o1.setPosition(p1);
        assertEquals(o1.getPosition().getX(), 1, DELTA);
        assertEquals(o1.getPosition().getY(), 3, DELTA);
        assertEquals(o1.get_resource_count(), 9, DELTA);
        o1.set_resource_count(3);
        assertEquals(o1.get_resource_count(), 3, DELTA);
        assertEquals(o1.get_rate(), 3, DELTA);
        assertEquals(o1.get_resource_limit(), 9, DELTA);
    }

    @Test
    public void testWorldModel() {
        MinerNotFull m = new MinerNotFull("me", p, 1000, 3);
        MinerNotFull m2 = new MinerNotFull("not me", new Point(-1, -10), 1000, 4);

        WorldModel world = new WorldModel(10, 10, new Background("bgrd"));

        // Testing add_entity and is_occupied
        world.add_entity(m);
        assertTrue(world.is_occupied(p));
        assertFalse(world.is_occupied(p1));

        // Testing is_occupied false case
        world.add_entity(m2);
        assertFalse(world.is_occupied(new Point(-1, -10)));

        // Testing within_bounds true and false case
        assertFalse(world.within_bounds(new Point(100, 100)));
        assertTrue(world.within_bounds(new Point(0, 0)));

        // Testing is_occupied true and false cases
        world.move_entity(m, p1);
        assertTrue(world.is_occupied(p1));
        assertFalse(world.is_occupied(p));

        // Testing move_entity
        world.move_entity(m, new Point(100, 100));
        assertTrue(world.is_occupied(p1));
        assertFalse(world.is_occupied(new Point(100, 100)));

        // Testing remove_entity
        world.remove_entity(m);
        assertFalse(world.is_occupied(p1));

        // Testing get_background
        assertTrue(world.get_background(p).getName().compareTo("bgrd") == 0);
        assertTrue(world.get_background(new Point(100, 100)) == null);

        // Testing set_background
        world.set_background(p, new Background("new bgrd"));
        assertTrue(world.get_background(p).getName().compareTo("new bgrd") == 0);

        // Testing set_background in an out of bounds case
        world.set_background(new Point(100, 100), new Background("new bgrd"));
        assertTrue(world.get_background(new Point(100, 100)) == null);

        // Testing get_tile_occupant cases
        MinerNotFull m3 = new MinerNotFull("me3", p, 1000, 3);
        world.add_entity(m3);
        assertTrue(world.get_tile_occupant(p).getName().compareTo("me3") == 0);
        assertTrue(world.get_tile_occupant(new Point(100, 100)) == null);


        // Testing find_nearest
        MinerNotFull m4 = new MinerNotFull("m4", new Point(9,9), 1000, 3);
        MinerNotFull m5 = new MinerNotFull("m5", new Point(8,8), 1000, 3);
        MinerNotFull m6 = new MinerNotFull("m6", new Point(9,8), 1000, 3);
        world.add_entity(m4);
        world.add_entity(m5);
        world.add_entity(m6);
        assertTrue(world.find_nearest(new Point(9,9), MinerNotFull.class).getName().compareTo(m4.getName()) == 0);

        // Testing getEntities
        for (WorldEntity entity : world.getEntities()) {
            assertTrue(MinerNotFull.class.isInstance(entity));
        }
    }

}
