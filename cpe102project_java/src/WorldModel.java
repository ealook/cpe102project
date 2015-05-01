import java.util.ArrayList;

public class WorldModel {

    private Background[][] background;
    private final int num_rows;
    private final int num_cols;
    private WorldEntity[][] occupancy;
    private ArrayList<WorldEntity> entities;


    public WorldModel(int num_rows, int num_cols, Background background) {
        this.background = new Background[num_rows][num_cols];
        this.num_rows = num_rows;
        this.num_cols = num_cols;
        this.occupancy = new WorldEntity[num_rows][num_cols];
        this.entities = new ArrayList<>();

        for (int x = 0; x < num_cols; x++) {
            for (int y = 0; y < num_rows; y++) {
                this.background[x][y] = background;
            }
        }
    }

    public boolean within_bounds(Point pt) {
        return (pt.getX() >= 0 && pt.getX() < num_cols && pt.getY() >= 0 && pt.getY() < num_rows);
    }

    public boolean is_occupied(Point pt) {
        return (this.within_bounds(pt) && this.occupancy[pt.getX()][pt.getY()] != null);
    }

    public void add_entity(WorldEntity entity) {
        Point pt = entity.getPosition();
        if (this.within_bounds(pt)) {
            WorldEntity old_entity = this.occupancy[pt.getX()][pt.getY()];
            if (old_entity != null) {
                //old_entity.clear_pending_actions()
            }
            this.occupancy[pt.getX()][pt.getY()] = entity;
            this.entities.add(entity);
        }
    }

    public WorldEntity find_nearest(Point pt, Class type) {

        ArrayList<WorldEntity> oftype = new ArrayList<>();

        for (WorldEntity entity : this.entities) {
            if (entity.getClass() == type) {
                oftype.add(entity);
            }
        }


        return nearest_entity(oftype, pt);
    }

    public static WorldEntity nearest_entity(ArrayList<WorldEntity> oftype, Point pt) {

        WorldEntity closest = oftype.get(0);

        for (WorldEntity entity : oftype) {
            if (distance_sq(entity.getPosition(), pt) < distance_sq(closest.getPosition(), pt)) {
                closest = entity;
            }
        }

        return closest;
    }

    public static double distance_sq(Point p1, Point p2) {
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
                + (p1.getY() - p2.getY())*(p1.getY() - p2.getY());
    }

    public ArrayList<Point> move_entity(WorldEntity entity, Point pt) {
        ArrayList<Point> tiles = new ArrayList<Point>();

        if (this.within_bounds(pt)) {
            Point old_pt = entity.getPosition();
            this.occupancy[old_pt.getX()][old_pt.getY()] = null;
            tiles.add(old_pt);
            this.occupancy[pt.getX()][pt.getY()] = entity;
            tiles.add(pt);
            entity.setPosition(pt);
        }

        return tiles;
    }

    public void remove_entity(WorldEntity entity) {
        this.remove_entity_at(entity.getPosition());
    }

    public void remove_entity_at(Point pt) {
        if (this.within_bounds(pt) && this.occupancy[pt.getX()][pt.getY()] != null) {
            WorldEntity entity = this.occupancy[pt.getX()][pt.getY()];
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.occupancy[pt.getX()][pt.getY()] = null;
        }
    }

    public Background get_background(Point pt) {
        if (this.within_bounds(pt)) {
            return this.background[pt.getX()][pt.getY()];
        } else {
            return null;
        }
    }

    public void set_background(Point pt, Background background) {
        if (this.within_bounds(pt)) {
            this.background[pt.getX()][pt.getY()] = background;
        }
    }

    public WorldEntity get_tile_occupant(Point pt) {
        if (this.within_bounds(pt)) {
            return this.occupancy[pt.getX()][pt.getY()];
        } else {
            return null;
        }
    }

    public ArrayList<WorldEntity> getEntities() {
        return entities;
    }

}