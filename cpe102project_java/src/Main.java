import processing.core.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.Random;


public class Main extends PApplet {

    private static final boolean RUN_AFTER_LOAD = true;
    private final String SRC_PATH = "/Users/ethan/Documents/Cal Poly SLO/CPE 102/cpe102project/cpe102project_java/src/";
    private final String IMAGE_LIST_FILE_NAME = "imagelist";
    private final String WORLD_FILE = "gaia.sav";

    private Random random = new Random();


    private long next_time;
    private static final int ANIMATION_TIME = 100;
    private long next_random_spawn_time;
    private static final int RANDOM_SPAWN_TIME = 1200;

    private final int WORLD_WIDTH_SCALE = 2 / 2;
    private final int WORLD_HEIGHT_SCALE = 2 / 2;

    private final int SCREEN_WIDTH = 640 * 2;
    private final int SCREEN_HEIGHT = 480 * 2;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;

    private WorldModel world;
    private WorldView view;
    private ImageStore i_store = new ImageStore(this, SRC_PATH, IMAGE_LIST_FILE_NAME);

    public void setup() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT);

        try {
            i_store.load_images(32, 32);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int num_cols = SCREEN_WIDTH / TILE_WIDTH * WORLD_WIDTH_SCALE;
        int num_rows = SCREEN_HEIGHT / TILE_HEIGHT * WORLD_HEIGHT_SCALE;

        Background default_background = create_default_background(i_store.get_images(i_store.getDefaultImageName()));

        world = new WorldModel(SRC_PATH, WORLD_FILE, num_rows, num_cols, default_background);
        view = new WorldView(this, SCREEN_WIDTH / TILE_WIDTH, SCREEN_HEIGHT / TILE_HEIGHT, world, TILE_WIDTH, TILE_HEIGHT);

        try {
            world.load_world(i_store, RUN_AFTER_LOAD);
        } catch (IOException e) {
            e.printStackTrace();
        }

        view.update_view(0, 0);

        next_time = System.currentTimeMillis() + ANIMATION_TIME;
        next_random_spawn_time = System.currentTimeMillis() + RANDOM_SPAWN_TIME;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= next_time)
        {
            next_images();
            next_time = time + ANIMATION_TIME;
            make_moves();
        }
        if (time >= next_random_spawn_time) {
            int x = random.nextInt(SCREEN_WIDTH / TILE_WIDTH * WORLD_WIDTH_SCALE);
            int y = random.nextInt(SCREEN_HEIGHT / TILE_HEIGHT * WORLD_HEIGHT_SCALE);
            Point random_pt = new Point(x, y);
            if (!world.is_occupied(random_pt)) {
                Vein new_vein = new Vein("randomly spawned vein", random_pt, 9456, i_store.get_images("vein"));
                new_vein.schedule_entity(world, i_store);
                world.add_entity(new_vein);
            }

            next_random_spawn_time = time + RANDOM_SPAWN_TIME;
        }

        this.view.draw_viewport();
        draw_mover_path();
    }

    private void draw_mover_path() {
        int x = mouseX / TILE_WIDTH;
        int y = mouseY / TILE_HEIGHT;

        Point world_pt = view.viewport_to_world(new Point(x, y));

        if (world.is_occupied(world_pt)) {
            try {
                Mover mover = (Mover) world.get_tile_occupant(world_pt);
                ArrayList<Point> path = mover.getPath();
                for (int i = 0; i < path.size() - 1; i++) {
                    Point view_pt = view.world_to_viewport(path.get(i));
                    fill(255, 0, 0);
                    noStroke();
                    rect(view_pt.getX() * TILE_WIDTH + 3 * TILE_WIDTH / 8, view_pt.getY() * TILE_HEIGHT + 3 * TILE_HEIGHT / 8, TILE_WIDTH / 4, TILE_HEIGHT / 4);
                }
            } catch (ClassCastException e) {
                // do nothing
            }
        }


    }

    private void make_moves() {
        for (int x = 0; x < world.getNum_cols(); x++) {
            for (int y = 0; y < world.getNum_rows(); y++) {
                Point loc = new Point(x, y);
                if (world.is_occupied(loc)) {
                    try {
                        Actor actor = (Actor) world.get_tile_occupant(loc);
                        if (actor.isTime(System.currentTimeMillis())) {
                            ActTime action = actor.get_pending_actions().get(0);
                            action.getActOperation().run(System.currentTimeMillis());
                            break;
                        }
                    } catch (ClassCastException e) {
                        // do nothing
                    }
                }
            }
        }
    }

    public void keyPressed()
    {
        int dx = 0;
        int dy = 0;
        if (key == CODED) {
            switch (keyCode)
            {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.update_view(dx, dy);
        }
    }

    public void mousePressed() {
        int x = mouseX / TILE_WIDTH;
        int y = mouseY / TILE_HEIGHT;

        Point world_pt = view.viewport_to_world(new Point(x, y));
        world.activateSpongebob(world_pt, i_store);
    }

    private void next_images() {
        for (WorldEntity entity : this.world.getEntities()) {
            entity.next_image();
        }
    }

    private Background create_default_background(ArrayList<PImage> imgs) {
        return new Background(i_store.getDefaultImageName(), imgs);
    }

}
