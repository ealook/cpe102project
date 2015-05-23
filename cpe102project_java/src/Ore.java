import processing.core.PImage;

import java.util.ArrayList;

public class Ore extends Actor {

    private static final int BLOB_RATE_SCALE = 4;
    private final int rate;

    public Ore(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
        this.rate = 5000;
    }

    public int get_rate() {
        return rate;
    }

    public String entity_string() {
        return "ore" + " " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.rate;
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_ore(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_ore(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_ore_transformation_action(world, i_store), ticks + this.get_rate());
    }

    private ActOperation create_ore_transformation_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            OreBlob blob = new OreBlob(this.getName() + " -- blob", this.getPosition(), this.get_rate() / BLOB_RATE_SCALE, i_store.get_images("blob"), world);
            blob.schedule_blob(world, current_ticks, i_store);
            world.remove_entity(this);
            world.add_entity(blob);

            tiles.add(blob.getPosition());

            return tiles;
        };
        return action[0];
    }
}
