import processing.core.PImage;

import java.util.ArrayList;

public abstract class AnimatedActor extends Actor {

    public AnimatedActor(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
    }

    public abstract void schedule_entity(WorldModel world, ImageStore i_store);
}
