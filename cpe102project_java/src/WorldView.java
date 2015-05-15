import static java.lang.Math.max;
import static java.lang.Math.min;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphics;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

public class WorldView {

    private final WorldModel world;
    private Rect viewport;
    private final int tile_width;
    private final int tile_height;
    private final int num_rows;
    private final int num_cols;
    private PApplet parent;

    public WorldView(PApplet parent, int view_cols, int view_rows, WorldModel world, int tile_width, int tile_height) {
        this.parent = parent;
        this.viewport = new Rect(0, 0, view_cols, view_rows);
        this.world = world;
        this.tile_width = tile_width;
        this.tile_height = tile_height;
        this.num_rows = world.getNum_rows();
        this.num_cols = world.getNum_cols();
    }

    private void draw_background() {
        for (int y = 0; y < this.viewport.getHeight(); y++) {
            for (int x = 0; x < this.viewport.getWidth(); x++) {
                Point w_pt = viewport_to_world(this.viewport, new Point(x, y));
                PImage img = this.world.get_background_image(w_pt);
                parent.image(img, x * this.tile_width, y * this.tile_height);
            }
        }
    }

    private void draw_entities() {
        for (WorldEntity entity : this.world.getEntities()) {
            if (this.viewport.collidepoint(entity.getPosition().getX(), entity.getPosition().getY())) {
                Point v_pt = world_to_viewport(this.viewport, entity.getPosition());
                parent.image(entity.getCurrentImage(), v_pt.getX() * this.tile_width, v_pt.getY() * this.tile_height);
            }
        }
    }

    public void draw_viewport() {
        this.draw_background();
        this.draw_entities();
    }

    public void update_view(int dx, int dy) {
        this.viewport = create_shifted_viewport(viewport, dx, dy, this.num_rows, this.num_cols);
    }

    public void update_view_tiles(ArrayList<Point> tiles) {

        for (Point tile : tiles) {
            if (this.viewport.collidepoint(tile.getX(), tile.getY())) {
                Point v_pt = world_to_viewport(this.viewport, tile);
                PImage img = this.get_tile_image(v_pt);
                this.update_tile(v_pt, img);
            }
        }

        // update the tiles that have changed?
    }

    private Rect update_tile(Point view_tile_pt, PImage surface) {
        int abs_x = view_tile_pt.getX() * this.tile_width;
        int abs_y = view_tile_pt.getY() * this.tile_height;

        parent.image(surface, abs_x, abs_y);

        return new Rect(abs_x, abs_y, this.tile_width, this.tile_height);
    }

    private PImage get_tile_image(Point view_tile_pt) {
        Point pt = viewport_to_world(this.viewport, view_tile_pt);
        PImage bgnd = this.world.get_background_image(pt);
        WorldEntity occupant = this.world.get_tile_occupant(pt);
        if (occupant != null) {
            PGraphics temp = parent.createGraphics(this.tile_width, this.tile_height);
            temp.image(bgnd, 0, 0);
            temp.image(occupant.getCurrentImage(), 0, 0);
            return temp.get();
        } else {
            return bgnd;
        }
    }

    public static Point viewport_to_world(Rect viewport, Point pt) {
        return new Point(pt.getX() + viewport.getLeft(), pt.getY() + viewport.getTop());
    }

    public static Point world_to_viewport(Rect viewport, Point pt) {
        return new Point(pt.getX() - viewport.getLeft(), pt.getY() - viewport.getTop());
    }

    public static int clamp(int v, int low, int high) {
        return min(high, max(v, low));
    }

    public static Rect create_shifted_viewport(Rect viewport, int dx, int dy, int num_rows, int num_cols) {
        int new_x = clamp(viewport.getLeft() + dx, 0, num_cols - viewport.getWidth());
        int new_y = clamp(viewport.getTop() + dy, 0, num_rows - viewport.getHeight());

        return new Rect(new_x, new_y, viewport.getWidth(), viewport.getHeight());
    }
}
