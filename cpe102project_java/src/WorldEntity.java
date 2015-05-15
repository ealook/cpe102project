import processing.core.PImage;

import java.util.ArrayList;

public class WorldEntity extends WorldObject {

    private final ArrayList<PImage> imgs;
    private int current_img;
    private Point position;

    public WorldEntity(String name, Point position, ArrayList<PImage> imgs) {
        super(name);
        this.imgs = imgs;
        this.position = position;
        this.current_img = 0;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point point) {
        this.position = point;
    }

    public PImage getCurrentImage() {
        return imgs.get(current_img);
    }

    public void next_image() {
        this.current_img = (this.current_img + 1) % this.imgs.size();
    }

    public ArrayList<PImage> getImgs() {
        return imgs;
    }
}
