import processing.core.PImage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class WorldModel {

    private static final int PROPERTY_KEY = 0;

    private static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_NAME = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    private static final String MINER_KEY = "miner";
    private static final int MINER_NUM_PROPERTIES = 7;
    private static final int MINER_NAME = 1;
    private static final int MINER_LIMIT = 4;
    private static final int MINER_COL = 2;
    private static final int MINER_ROW = 3;
    private static final int MINER_RATE = 5;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_NAME = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final String ORE_KEY = "ore";
    private static final int ORE_NUM_PROPERTIES = 5;
    private static final int ORE_NAME = 1;
    private static final int ORE_COL = 2;
    private static final int  ORE_ROW = 3;

    private static final String SMITH_KEY = "blacksmith";
    private static final int SMITH_NUM_PROPERTIES = 7;
    private static final int SMITH_NAME = 1;
    private static final int SMITH_COL = 2;
    private static final int SMITH_ROW = 3;
    private static final int SMITH_LIMIT = 4;
    private static final int SMITH_RATE = 5;

    private static final String VEIN_KEY = "vein";
    private static final int VEIN_NUM_PROPERTIES = 6;
    private static final int VEIN_NAME = 1;
    private static final int VEIN_RATE = 4;
    private static final int VEIN_COL = 2;
    private static final int VEIN_ROW = 3;

    private final String src_path;
    private final String world_file;

    private Background[][] background;

    private final int num_rows;
    private final int num_cols;

    private WorldEntity[][] occupancy;

    private ArrayList<WorldEntity> entities;
    private OrderedList action_queue;


    public WorldModel(String src_path, String world_file, int num_rows, int num_cols, Background background) {
        this.src_path = src_path;
        this.world_file = world_file;
        this.background = new Background[num_rows][num_cols];
        this.num_rows = num_rows;
        this.num_cols = num_cols;
        this.occupancy = new WorldEntity[num_rows][num_cols];
        this.entities = new ArrayList<>();
        this.action_queue = new OrderedList();

        for (int x = 0; x < num_cols; x++) {
            for (int y = 0; y < num_rows; y++) {
                this.background[y][x] = background;
            }
        }
    }

    public boolean within_bounds(Point pt) {
        return (pt.getX() >= 0 && pt.getX() < num_cols && pt.getY() >= 0 && pt.getY() < num_rows);
    }

    public boolean is_occupied(Point pt) {
        return (this.within_bounds(pt) && this.occupancy[pt.getY()][pt.getX()] != null);
    }

    public void add_entity(WorldEntity entity) {
        Point pt = entity.getPosition();
        if (this.within_bounds(pt)) {
            WorldEntity old_entity = this.occupancy[pt.getY()][pt.getX()];
            if (old_entity != null) {
                try {
                    Actor actor = (Actor) old_entity;
//                    for (ActTime action : actor.get_pending_actions()) {
//                        actor.remove_pending_action(action);
//                    }
                } catch (ClassCastException e) {
                    // do nothing
                }
            }
            this.occupancy[pt.getY()][pt.getX()] = entity;
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

        if (oftype.size() != 0) {
            WorldEntity closest = oftype.get(0);

            for (WorldEntity entity : oftype) {
                if (distance_sq(entity.getPosition(), pt) < distance_sq(closest.getPosition(), pt)) {
                    closest = entity;
                }
            }

            return closest;
        } else {
            return null;
        }
    }

    public static double distance_sq(Point p1, Point p2) {
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
                + (p1.getY() - p2.getY())*(p1.getY() - p2.getY());
    }

    public ArrayList<Point> move_entity(WorldEntity entity, Point pt) {
        ArrayList<Point> tiles = new ArrayList<Point>();

        if (this.within_bounds(pt)) {
            Point old_pt = entity.getPosition();
            this.occupancy[old_pt.getY()][old_pt.getX()] = null;
            tiles.add(old_pt);
            this.occupancy[pt.getY()][pt.getX()] = entity;
            tiles.add(pt);
            entity.setPosition(pt);
        }

        return tiles;
    }

    public void remove_entity(WorldEntity entity) {
        this.remove_entity_at(entity.getPosition());
    }

    public void remove_entity_at(Point pt) {
        if (this.within_bounds(pt) && this.occupancy[pt.getY()][pt.getX()] != null) {
            WorldEntity entity = this.occupancy[pt.getY()][pt.getX()];
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.occupancy[pt.getY()][pt.getX()] = null;
        }
    }

    public Background get_background(Point pt) {
        if (this.within_bounds(pt)) {
            return this.background[pt.getY()][pt.getX()];
        } else {
            return null;
        }
    }

    public void set_background(Point pt, Background background) {
        if (this.within_bounds(pt)) {
            this.background[pt.getY()][pt.getX()] = background;
        }
    }

    public WorldEntity get_tile_occupant(Point pt) {
        if (this.within_bounds(pt)) {
            return this.occupancy[pt.getY()][pt.getX()];
        } else {
            return null;
        }
    }

    public ArrayList<WorldEntity> getEntities() {
        return entities;
    }

    public int getNum_rows() {
        return num_rows;
    }

    public int getNum_cols() {
        return num_cols;
    }

    public PImage get_background_image(Point pt) {
        if (within_bounds(pt)) {
            return this.background[pt.getY()][pt.getX()].getImgs().get(0);
        } else {
            return null;
        }
    }

    public void load_world(ImageStore i_store, boolean run) throws IOException {
        File file = new File(this.src_path + this.world_file);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = null;

        while ((line = br.readLine()) != null) {
            String[] properties = line.split(" ");
            if (properties.length != 0) {
                if (properties[PROPERTY_KEY].compareTo(BGND_KEY) == 0) {
                    add_background_from_file(properties, i_store);
                } else {
                    add_entity_from_file(properties, i_store, run);
                }
            }
        }

        br.close();
    }

    private void add_entity_from_file(String[] properties, ImageStore i_store, boolean run) {
        WorldEntity new_entity = create_from_properties(properties, i_store);
        if (new_entity != null) {
            this.add_entity(new_entity);
            if (run) {
                try {
                    Actor actor = (Actor) new_entity;
                    actor.schedule_entity(this, i_store);
                } catch (ClassCastException e) {
                    // do nothing
                }
            }
        }
    }

    public void add_background_from_file(String[] properties, ImageStore i_store) {
        if (properties.length >= BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]), Integer.parseInt(properties[BGND_ROW]));
            String name = properties[BGND_NAME];
            this.set_background(pt, new Background(name, i_store.get_images(name)));
        }
    }

    private WorldEntity create_from_properties(String[] properties, ImageStore i_store) {
        String key = properties[PROPERTY_KEY];
        if (properties.length != 0) {
            if (key.compareTo(MINER_KEY) == 0) {
                return create_miner(properties, i_store);
            } else if (key.compareTo(VEIN_KEY) == 0) {
                return create_vein(properties, i_store);
            } else if (key.compareTo(ORE_KEY) == 0) {
                return create_ore(properties, i_store);
            } else if (key.compareTo(SMITH_KEY) == 0) {
                return create_smith(properties, i_store);
            } else if (key.compareTo(OBSTACLE_KEY) == 0) {
                return create_obstacle(properties, i_store);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private WorldEntity create_miner(String[] properties, ImageStore i_store) {
        if (properties.length == MINER_NUM_PROPERTIES) {
            Miner miner = new MinerNotFull(properties[MINER_NAME],
                    new Point(Integer.parseInt(properties[MINER_COL]), Integer.parseInt(properties[MINER_ROW])),
                    Integer.parseInt(properties[MINER_RATE]),
                    Integer.parseInt(properties[MINER_LIMIT]),
                    i_store.get_images(MINER_KEY), this);
            return miner;
        } else {
            return null;
        }
    }

    private WorldEntity create_vein(String[] properties, ImageStore i_store) {
        if (properties.length == VEIN_NUM_PROPERTIES) {
            Vein vein = new Vein(properties[VEIN_NAME],
                    new Point(Integer.parseInt(properties[VEIN_COL]), Integer.parseInt(properties[VEIN_ROW])),
                    Integer.parseInt(properties[VEIN_RATE]),
                    i_store.get_images(VEIN_KEY));
            return vein;
        } else {
            return null;
        }
    }

    private WorldEntity create_ore(String[] properties, ImageStore i_store) {
        if (properties.length == ORE_NUM_PROPERTIES) {
            Ore ore = new Ore(properties[ORE_NAME],
                    new Point(Integer.parseInt(properties[ORE_COL]), Integer.parseInt(properties[ORE_ROW])),
                    i_store.get_images(ORE_KEY));
            return ore;
        } else {
            return null;
        }
    }

    private WorldEntity create_smith(String[] properties, ImageStore i_store) {
        if (properties.length == SMITH_NUM_PROPERTIES) {
            Blacksmith smith= new Blacksmith(properties[SMITH_NAME],
                    new Point(Integer.parseInt(properties[SMITH_COL]), Integer.parseInt(properties[SMITH_ROW])),
                    Integer.parseInt(properties[SMITH_LIMIT]),
                    Integer.parseInt(properties[SMITH_RATE]),
                    i_store.get_images(SMITH_KEY));
            return smith;
        } else {
            return null;
        }
    }

    private WorldEntity create_obstacle(String[] properties, ImageStore i_store) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Obstacle obstacle= new Obstacle(properties[OBSTACLE_NAME],
                    new Point(Integer.parseInt(properties[OBSTACLE_COL]), Integer.parseInt(properties[OBSTACLE_ROW])),
                    i_store.get_images(OBSTACLE_KEY));
            return obstacle;
        } else {
            return null;
        }
    }

    private void create_spongebob(Point pt, ImageStore i_store) {
        System.out.println("Spongebob created!");
        Spongebob spongebob = new Spongebob("spongebob", pt, 200, i_store.get_images("spongebob"), this);
        spongebob.schedule_spongebob(this, System.currentTimeMillis(), i_store);
        add_entity(spongebob);
    }

    private void create_patrick(Point pt, ImageStore i_store) {
        System.out.println("Patrick created!");
        Patrick patrick = new Patrick("patrick", pt, 200, i_store.get_images("patrick"), this);
        patrick.schedule_patrick(this, System.currentTimeMillis(), i_store);
        add_entity(patrick);
    }

    private void create_all_krabs(ImageStore i_store) {
        System.out.println("Creating all Mr. Krabs!");
        ArrayList<WorldEntity> entities_to_remove = new ArrayList<>();
        for (WorldEntity entity : entities) {
            if (entity.getClass() == Blacksmith.class) {
                entities_to_remove.add(entity);
            }
        }

        for (int i = 0; i < entities_to_remove.size(); i++) {
            Point entity_pt = entities_to_remove.get(i).getPosition();
            remove_entity(entities_to_remove.get(i));
            Krabs krabs = new Krabs("krabs", entity_pt, 14, 2400, i_store.get_images("krabs"));
            add_entity(krabs);
        }
    }

    public static Point find_open_around(WorldModel world, Point pt, int distance) {
        for (int dy = -distance; dy < distance + 1; dy++) {
            for (int dx = -distance; dx < distance + 1; dx++) {
                Point new_pt = new Point(pt.getX() + dx, pt.getY() + dy);

                if (world.within_bounds(new_pt) && !(world.is_occupied(new_pt))) {
                    return new_pt;
                }
            }
        }
        return null;
    }

    public void activateSpongebob(Point pt, ImageStore i_store) {
        if (space_for_spongebob(pt)) {
            System.out.println("Spongebob activated!");
            System.out.println("At x: " + pt.getX() + ", y: " + pt.getY());

            int start_x = pt.getX();
            int start_y = pt.getY();
            int size = 2;

            for (int x = start_x - size; x <= start_x + size + 1; x++) {
                for (int y = start_y - size; y <= start_y + size; y++) {
                    add_background_from_file(new String[]{"background", "sand", String.valueOf(x), String.valueOf(y)}, i_store);
                }
            }


            Point spongebob_pt = new Point(pt.getX(), pt.getY() + 1);
            Point patrick_pt = new Point(pt.getX() + 1, pt.getY() + 1);

            create_spongebob(spongebob_pt, i_store);
            create_patrick(patrick_pt, i_store);
            create_pineapple(pt, i_store);
            create_all_krabs(i_store);


        }
    }

    private void create_pineapple(Point pt, ImageStore i_store) {
        Point bottomleft = new Point(pt.getX(), pt.getY());
        Point middleleft = new Point(pt.getX(), pt.getY() - 1);
        Point topleft = new Point(pt.getX(), pt.getY() - 2);
        Point bottomright = new Point(pt.getX() + 1, pt.getY());
        Point middleright = new Point(pt.getX() + 1, pt.getY() - 1);
        Point topright = new Point(pt.getX() + 1, pt.getY() - 2);

        PineapplePiece bottomleft_piece = new PineapplePiece("bottomleft pineapple", bottomleft, i_store.get_images("pineapple_bottomleft"));
        PineapplePiece middleleft_piece = new PineapplePiece("middleleft pineapple", middleleft, i_store.get_images("pineapple_middleleft"));
        PineapplePiece topleft_piece = new PineapplePiece("topleft pineapple", topleft, i_store.get_images("pineapple_topleft"));
        PineapplePiece bottomright_piece = new PineapplePiece("bottomright pineapple", bottomright, i_store.get_images("pineapple_bottomright"));
        PineapplePiece middleright_piece = new PineapplePiece("middleright pineapple", middleright, i_store.get_images("pineapple_middleright"));
        PineapplePiece topright_piece = new PineapplePiece("topright pineapple", topright, i_store.get_images("pineapple_topright"));

        add_entity(bottomleft_piece);
        add_entity(middleleft_piece);
        add_entity(topleft_piece);
        add_entity(bottomright_piece);
        add_entity(middleright_piece);
        add_entity(topright_piece);
    }

    private boolean space_for_spongebob(Point pt) {
        Point spongebob_pt = new Point(pt.getX(), pt.getY() + 1);
        Point patrick_pt = new Point(pt.getX() + 1, pt.getY() + 1);
        Point bottomleft = new Point(pt.getX(), pt.getY());
        Point middleleft = new Point(pt.getX(), pt.getY() - 1);
        Point topleft = new Point(pt.getX(), pt.getY() - 2);
        Point bottomright = new Point(pt.getX() + 1, pt.getY());
        Point middleright = new Point(pt.getX() + 1, pt.getY() - 1);
        Point topright = new Point(pt.getX() + 1, pt.getY() - 2);
        return !is_occupied(spongebob_pt) && !is_occupied(patrick_pt) && !is_occupied(bottomleft) && !is_occupied(bottomright)
                && !is_occupied(middleleft) && !is_occupied(middleright) && !is_occupied(topleft) && !is_occupied(topright);
    }
}