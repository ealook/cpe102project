import processing.core.PImage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Quake extends AnimatedActor {

    private static final long QUAKE_DURATION = 1100;

    public Quake(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_quake(world, System.currentTimeMillis());
    }


    private void schedule_quake(WorldModel world, long ticks) {
        this.schedule_action(this.create_entity_death_action(world), ticks + QUAKE_DURATION);
    }


    private ActOperation create_entity_death_action(WorldModel world) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            Point pt = this.getPosition();
            world.remove_entity(this);
            tiles.add(pt);

            return tiles;
        };

        return action[0];
    }
}
