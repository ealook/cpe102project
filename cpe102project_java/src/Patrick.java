import processing.core.PImage;

import java.util.ArrayList;

public class Patrick extends Mover {

    public Patrick(String name, Point position, int rate, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, imgs, world);
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_patrick(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_patrick(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_patrick_action(world, i_store), ticks + this.get_rate());
    }

    private ActOperation create_patrick_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            this.setPath(new ArrayList<>());
            Point entity_pt = this.getPosition();
            Miner miner = (MinerNotFull) world.find_nearest(entity_pt, MinerNotFull.class);
            if (miner == null) {
                miner = (MinerFull) world.find_nearest(entity_pt, MinerFull.class);
            }
            FinderPair search;
            if (miner != null) {
                search = this.patrick_to_miner(world, miner, miner.getClass());
            } else {
                search = new FinderPair(this.getPosition(), false);
            }


            long next_time = current_ticks + this.get_rate();
            if (search.isFound()) {
                Sandy sandy = new SandyNotFull("sandy", search.getPt(), 600, 1, i_store.get_images("sandy"), world);
                sandy.schedule_entity(world, i_store);
                world.add_entity(sandy);
                next_time = current_ticks + this.get_rate() * 2;
            }

            this.schedule_action(this.create_patrick_action(world, i_store), next_time);

            return tiles;
        };
        return action[0];
    }

    private FinderPair patrick_to_miner(WorldModel world, Miner miner, Class miner_type) {
        Point entity_pt = this.getPosition();
        if (miner == null) {
            return new FinderPair(entity_pt, false);
        }
        Point miner_pt = miner.getPosition();
        if (entity_pt.adjacent_to(miner_pt)) {
            world.remove_entity(miner);
            return new FinderPair(miner_pt, true);
        } else {
            Point new_pt = patrick_next_position(world, entity_pt, miner_pt, miner_type);
            WorldEntity old_entity = world.get_tile_occupant(new_pt);
            if (old_entity != null && old_entity.getClass() == Ore.class) {
                world.remove_entity(old_entity);
            }
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    protected Point patrick_next_position(WorldModel world, Point entity_pt, Point dest_pt, Class dest_class) {
        this.aStar(entity_pt, dest_pt, world, dest_class);
        if (this.getPath().size() > 1) {
            return this.getPath().get(this.getPath().size() - 2);
        } else {
            return entity_pt;
        }
    }
}
