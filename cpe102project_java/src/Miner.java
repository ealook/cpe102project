import processing.core.PImage;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class Miner extends Mover {

    public final int resource_limit;
    public int resource_count;

    public Miner(String name, Point position, int rate, int resource_limit, int resource_count, ArrayList<PImage> imgs) {
        super(name, position, rate, imgs);
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

    public Miner try_transform_miner(WorldModel world, MinerOperation minerMaker) {
        Miner new_entity = minerMaker.run();
        if (this != new_entity) {
            world.remove_entity_at(this.getPosition());
            world.add_entity(new_entity);
        }

        return new_entity;
    }

    public FinderPair miner_to_ore(WorldModel world, Ore ore) {
        Point entity_pt = this.getPosition();
        if (ore == null) {
            return new FinderPair(entity_pt, false);
        }
        Point ore_pt = ore.getPosition();
        if (entity_pt.adjacent_to(ore_pt)) {
            this.set_resource_count(1 + this.get_resource_count());
            world.remove_entity(ore);
            return new FinderPair(ore_pt, true);
        } else {
            Point new_pt = next_position(world, entity_pt, ore_pt);
            world.move_entity(this, new_pt);
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    public FinderPair miner_to_smith(WorldModel world, Blacksmith smith) {
        Point entity_pt = this.getPosition();
        if (smith == null) {
            return new FinderPair(entity_pt, false);
        }
        Point smith_pt = smith.getPosition();
        if (entity_pt.adjacent_to(smith_pt)) {
            smith.set_resource_count(smith.get_resource_count() + this.get_resource_count());
            this.set_resource_count(0);
            return new FinderPair(null, true);
        } else {
            Point new_pt = next_position(world, entity_pt, smith_pt);
            world.move_entity(this, new_pt);
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_miner(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_miner(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_miner_action(world, i_store), ticks + this.get_rate());
    }

    public abstract ActOperation create_miner_action(WorldModel world, ImageStore i_store);

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
}
