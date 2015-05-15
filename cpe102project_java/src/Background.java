import processing.core.PImage;

import java.util.ArrayList;

public class Background extends WorldObject {

    ArrayList<PImage> imgs;



    public Background(String name, ArrayList<PImage> imgs) {
        super(name);
        this.imgs = imgs;

    }

    public ArrayList<PImage> getImgs() {
        return imgs;
    }

}
