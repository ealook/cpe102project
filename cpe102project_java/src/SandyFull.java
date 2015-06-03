import processing.core.PImage;

import java.util.ArrayList;

public class SandyFull extends Sandy {

    public SandyFull(String name, Point position, int rate, int resource_limit, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, rate, resource_limit, resource_limit, imgs, world);
    }

    @Override
    public ActOperation create_sandy_action(WorldModel world, ImageStore i_store) {
        return this.create_miner_full_action(world, i_store);
    }

    private ActOperation create_miner_full_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            this.setPath(new ArrayList<>());
            Point entity_pt = this.getPosition();
            Krabs krabs = (Krabs) world.find_nearest(entity_pt, Krabs.class);
            FinderPair search = this.sandy_to_krabs(world, krabs);
            ArrayList<Point> tiles = new ArrayList<>();
            tiles.add(search.getPt());

            Sandy new_entity = this;
            if (search.isFound()) {
                new_entity = this.try_transform_sandy(world, this.try_transform_sandy_full(world));
            }

            new_entity.schedule_action(new_entity.create_sandy_action(world, i_store), current_ticks + new_entity.get_rate());

            return tiles;
        };

        return action[0];
    }


    public SandyOperation try_transform_sandy_full(WorldModel world) {

        SandyOperation sandyMaker = () -> {
            SandyNotFull new_entity = new SandyNotFull(this.getName(), this.getPosition(),
                    this.get_rate(), this.get_resource_limit(), this.getImgs(), world);

            return new_entity;
        };

        return sandyMaker;
    }

}
