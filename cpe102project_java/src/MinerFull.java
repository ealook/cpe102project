import processing.core.PImage;

import java.util.ArrayList;

public class MinerFull extends Miner {

    public MinerFull(String name, Point position, int rate, int resource_limit, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, resource_limit, resource_limit, imgs, world);
    }

    @Override
    public ActOperation create_miner_action(WorldModel world, ImageStore i_store) {
        return this.create_miner_full_action(world, i_store);
    }

    private ActOperation create_miner_full_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            this.setPath(new ArrayList<>());
            Point entity_pt = this.getPosition();
            Blacksmith smith = (Blacksmith) world.find_nearest(entity_pt, Blacksmith.class);
            FinderPair search = this.miner_to_smith(world, smith);
            ArrayList<Point> tiles = new ArrayList<>();
            tiles.add(search.getPt());

            Miner new_entity = this;
            if (search.isFound()) {
                new_entity = this.try_transform_miner(world, this.try_transform_miner_full(world));
            }

            new_entity.schedule_action(new_entity.create_miner_action(world, i_store), current_ticks + new_entity.get_rate());

            return tiles;
        };

        return action[0];
    }


    public MinerOperation try_transform_miner_full(WorldModel world) {

        MinerOperation minerMaker = () -> {
            MinerNotFull new_entity = new MinerNotFull(this.getName(), this.getPosition(),
                    this.get_rate(), this.get_resource_limit(), this.getImgs(), world);

            return new_entity;
        };

        return minerMaker;
    }

}
