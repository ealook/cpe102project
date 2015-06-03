import processing.core.PImage;

import java.util.ArrayList;

public class Spongebob extends Mover {

    public Spongebob(String name, Point position, int rate, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, imgs, world);
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_spongebob(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_spongebob(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_spongebob_action(world, i_store), ticks + this.get_rate());
    }

    private ActOperation create_spongebob_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            this.setPath(new ArrayList<>());
            Point entity_pt = this.getPosition();
            OreBlob oreBlob = (OreBlob) world.find_nearest(entity_pt, OreBlob.class);
            FinderPair search = this.spongebob_to_vein(world, oreBlob);

            long next_time = current_ticks + this.get_rate();
            if (search.isFound()) {
                Gary gary = new Gary("gary", search.getPt(), 1000, i_store.get_images("gary"), world);
                gary.schedule_entity(world, i_store);
                world.add_entity(gary);
                next_time = current_ticks + this.get_rate() * 2;
            }

            this.schedule_action(this.create_spongebob_action(world, i_store), next_time);

            return tiles;
        };
        return action[0];
    }

    private FinderPair spongebob_to_vein(WorldModel world, OreBlob oreBlob) {
        Point entity_pt = this.getPosition();
        if (oreBlob == null) {
            return new FinderPair(entity_pt, false);
        }
        Point ore_blob_pt = oreBlob.getPosition();
        if (entity_pt.adjacent_to(ore_blob_pt)) {
            world.remove_entity(oreBlob);
            return new FinderPair(ore_blob_pt, true);
        } else {
            Point new_pt = spongebob_next_position(world, entity_pt, ore_blob_pt, OreBlob.class);
            WorldEntity old_entity = world.get_tile_occupant(new_pt);
            if (old_entity != null && old_entity.getClass() == Ore.class) {
                world.remove_entity(old_entity);
            }
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    protected Point spongebob_next_position(WorldModel world, Point entity_pt, Point dest_pt, Class dest_class) {
        this.aStar(entity_pt, dest_pt, world, dest_class);
        if (this.getPath().size() > 1) {
            return this.getPath().get(this.getPath().size() - 2);
        } else {
            return entity_pt;
        }
    }
}
