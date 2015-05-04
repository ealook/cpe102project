import java.util.concurrent.Callable;

public class Miner extends Mover {

    public final int resource_limit;
    public int resource_count;

    public Miner(String name, Point position, int rate, int resource_limit, int resource_count) {
        super(name, position, rate);
        this.resource_limit = resource_limit;
        this.resource_count = resource_count;
    }

    public void set_resource_count(int n) {
        resource_count = n;
    }

    public int get_resource_count() {
        return resource_count;
    }

    public int get_resource_limit() {
        return resource_limit;
    }

    public Miner try_transform_miner(WorldModel world, Miner new_entity) {
        if (this != new_entity) {
            //this.clear_pending_actions(world);
            world.remove_entity_at(this.getPosition());
            world.add_entity(new_entity);
            //new_entity.schedule_animation(world);
        }

        return new_entity;
    }

    public boolean miner_to_ore(WorldModel world, Ore ore) {
        Point entity_pt = this.getPosition();
        if (ore == null) {
            return false;
        }
        Point ore_pt = ore.getPosition();
        if (entity_pt.adjacent_to(ore_pt)) {
            this.set_resource_count(1 + this.get_resource_count());
            //actions.remove_entity(world, ore);
            world.remove_entity(ore);
            return true;
        } else {
            Point new_pt = next_position(world, entity_pt, ore_pt);
            world.move_entity(this, new_pt);
            return false;
        }
    }

    public boolean miner_to_smith(WorldModel world, Blacksmith smith) {
        Point entity_pt = this.getPosition();
        if (smith == null) {
            return false;
        }
        Point smith_pt = smith.getPosition();
        if (entity_pt.adjacent_to(smith_pt)) {
            smith.set_resource_count(smith.get_resource_count() + this.get_resource_count());
            this.set_resource_count(0);
            return true;
        } else {
            Point new_pt = next_position(world, entity_pt, smith_pt);
            world.move_entity(this, new_pt);
            return false;
        }
    }

    public static Point next_position(WorldModel world, Point entity_pt, Point dest_pt) {
        int horiz = sign(dest_pt.getX() - entity_pt.getX());
        Point new_pt = new Point(entity_pt.getX() + horiz, entity_pt.getY());

        if (horiz == 0 || world.is_occupied(new_pt)) {
            int vert = sign(dest_pt.getY() - entity_pt.getY());
            new_pt = new Point(entity_pt.getX(), entity_pt.getY() + vert);

            if (vert == 0 || world.is_occupied(new_pt)) {
                new_pt = entity_pt;
            }
        }

        return  new_pt;
    }

    public static int sign(int x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
