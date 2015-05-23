import processing.core.PImage;

import java.util.ArrayList;

public class MinerNotFull extends Miner {

    public MinerNotFull(String name, Point position, int rate, int resource_limit, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, resource_limit, 0, imgs, world);
    }

    public ActOperation create_miner_action(WorldModel world, ImageStore i_store) {
        return this.create_miner_not_full_action(world, i_store);
    }

    private ActOperation create_miner_not_full_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            this.setPath(new ArrayList<>());
            Point entity_pt = this.getPosition();
            Ore ore = (Ore) world.find_nearest(entity_pt, Ore.class);
            FinderPair search = this.miner_to_ore(world, ore);
            ArrayList<Point> tiles = new ArrayList<>();
            tiles.add(search.getPt());

            Miner new_entity = this;
            if (search.isFound()) {
                new_entity = this.try_transform_miner(world, this.try_transform_miner_not_full(world));
            }

            new_entity.schedule_action(new_entity.create_miner_action(world, i_store), current_ticks + new_entity.get_rate());

            return tiles;
        };

        return action[0];
    }

    public String entity_string() {
        return "miner " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.get_resource_limit()
                + " " + this.get_rate();
    }

    public MinerOperation try_transform_miner_not_full(WorldModel world) {

        MinerOperation minerMaker = () -> {
            if (this.resource_count < this.resource_limit) {
                return this;
            } else {

                MinerFull new_entity = new MinerFull(this.getName(), this.getPosition(),
                        this.get_rate(), this.get_resource_limit(), this.getImgs(), world);

                return new_entity;
            }
        };

        return minerMaker;
    }
}
