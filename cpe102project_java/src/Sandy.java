import processing.core.PImage;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class Sandy extends Mover {

    public final int resource_limit;
    public int resource_count;

    public Sandy(String name, Point position, int rate, int resource_limit, int resource_count, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, imgs, world);
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

    public Sandy try_transform_sandy(WorldModel world, SandyOperation sandyMaker) {
        Sandy new_entity = sandyMaker.run();
        if (this != new_entity) {
            world.remove_entity_at(this.getPosition());
            world.add_entity(new_entity);
        }

        return new_entity;
    }

    public FinderPair sandy_to_krabbypatty(WorldModel world, Krabbypatty krabbypatty) {
        Point entity_pt = this.getPosition();
        if (krabbypatty == null) {
            return new FinderPair(entity_pt, false);
        }
        Point krabbypatty_pt = krabbypatty.getPosition();
        if (entity_pt.adjacent_to(krabbypatty_pt)) {
            this.set_resource_count(1 + this.get_resource_count());
            world.remove_entity(krabbypatty);
            return new FinderPair(krabbypatty_pt, true);
        } else {
            Point new_pt = next_position(world, entity_pt, krabbypatty_pt, Krabbypatty.class);
            world.move_entity(this, new_pt);
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    public FinderPair sandy_to_krabs(WorldModel world, Krabs krabs) {
        Point entity_pt = this.getPosition();
        if (krabs == null) {
            return new FinderPair(entity_pt, false);
        }
        Point smith_pt = krabs.getPosition();
        if (entity_pt.adjacent_to(smith_pt)) {
            krabs.set_resource_count(krabs.get_resource_count() + this.get_resource_count());
            this.set_resource_count(0);
            return new FinderPair(null, true);
        } else {
            Point new_pt = next_position(world, entity_pt, smith_pt, Krabs.class);
            world.move_entity(this, new_pt);
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_miner(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_miner(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_sandy_action(world, i_store), ticks + this.get_rate());
    }

    public abstract ActOperation create_sandy_action(WorldModel world, ImageStore i_store);

    protected Point next_position(WorldModel world, Point entity_pt, Point dest_pt, Class dest_class) {
        this.aStar(entity_pt, dest_pt, world, dest_class);
        if (this.getPath().size() > 1) {
            return this.getPath().get(this.getPath().size() - 2);
        } else {
            return entity_pt;
        }
    }
}
