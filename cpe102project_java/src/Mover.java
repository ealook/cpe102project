import processing.core.PImage;

import java.util.ArrayList;

public abstract class Mover extends AnimatedActor {

    private final int rate;

    public Mover(String name, Point position, int rate, ArrayList<PImage> imgs) {
        super(name, position, imgs);
        this.rate = rate;
    }

    public int get_rate() {
        return rate;
    }

    public abstract void schedule_entity(WorldModel world, ImageStore i_store);

    public static int sign(int x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
