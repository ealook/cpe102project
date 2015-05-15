import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Vein extends Actor {

    private final int rate;
    private final int resource_distance;

    public Vein(String name, Point position, int rate, ArrayList<PImage> imgs) {
        super(name, position, imgs);
        this.rate = rate;
        this.resource_distance = 1;
    }

    public int get_rate() {
        return rate;
    }

    public int get_resource_distance() {
        return resource_distance;
    }

    public String entity_string() {
        return "vein" + " " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.rate + " " + this.resource_distance;
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_vein(world, System.currentTimeMillis(), i_store);
    }

    private void schedule_vein(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_vein_action(world, i_store), ticks + this.get_rate());
    }

    public ActOperation create_vein_action(final WorldModel world, final ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            Point open_pt = world.find_open_around(world, this.getPosition(), this.get_resource_distance());

            if (open_pt != null) {
                Ore ore = new Ore("ore - " + this.getName() + " - " + current_ticks, open_pt, i_store.get_images("ore"));
                ore.schedule_entity(world, i_store);
                world.add_entity(ore);
                tiles = new ArrayList<>();
                tiles.add(open_pt);
            }

            this.schedule_action(this.create_vein_action(world, i_store), current_ticks + this.get_rate());

            return tiles;
        };
        return action[0];
    }
}
