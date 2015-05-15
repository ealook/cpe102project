import processing.core.PImage;

import java.util.ArrayList;

public class OreBlob extends Mover {

    public OreBlob(String name, Point position, int rate, ArrayList<PImage> imgs) {
        super(name, position, rate, imgs);
    }

    @Override
    public void schedule_entity(WorldModel world, ImageStore i_store) {
        this.schedule_blob(world, System.currentTimeMillis(), i_store);
    }

    public void schedule_blob(WorldModel world, long ticks, ImageStore i_store) {
        this.schedule_action(this.create_ore_blob_action(world, i_store), ticks + this.get_rate());
    }

    private ActOperation create_ore_blob_action(WorldModel world, ImageStore i_store) {

        ActOperation[] action = {null};

        action[0] = (long current_ticks) -> {

            this.remove_pending_action(action[0]);

            ArrayList<Point> tiles = new ArrayList<>();

            Point entity_pt = this.getPosition();

            Vein vein = (Vein) world.find_nearest(entity_pt, Vein.class);
            FinderPair search = this.blob_to_vein(world, vein);

            long next_time = current_ticks + this.get_rate();
            if (search.isFound()) {
                Quake quake = new Quake("quake", search.getPt(), i_store.get_images("quake"));
                quake.schedule_entity(world, i_store);
                world.add_entity(quake);
                next_time = current_ticks + this.get_rate() * 2;
            }

            this.schedule_action(this.create_ore_blob_action(world, i_store), next_time);

            return tiles;
        };
        return action[0];
    }

    private FinderPair blob_to_vein(WorldModel world, Vein vein) {
        Point entity_pt = this.getPosition();
        if (vein == null) {
            return new FinderPair(entity_pt, false);
        }
        Point vein_pt = vein.getPosition();
        if (entity_pt.adjacent_to(vein_pt)) {
            world.remove_entity(vein);
            return new FinderPair(vein_pt, true);
        } else {
            Point new_pt = blob_next_position(world, entity_pt, vein_pt);
            WorldEntity old_entity = world.get_tile_occupant(new_pt);
            if (old_entity != null && old_entity.getClass() == Ore.class) {
                world.remove_entity(old_entity);
            }
            return new FinderPair(world.move_entity(this, new_pt).get(1), false);
        }
    }

    public static Point blob_next_position(WorldModel world, Point entity_pt, Point dest_pt) {
        int horiz = sign(dest_pt.getX() - entity_pt.getX());
        Point new_pt = new Point(entity_pt.getX() + horiz, entity_pt.getY());

        if (horiz == 0 || (world.is_occupied(new_pt) && world.get_tile_occupant(new_pt).getClass() != Ore.class)) {
            int vert = sign(dest_pt.getY() - entity_pt.getY());
            new_pt = new Point(entity_pt.getX(), entity_pt.getY() + vert);

            if (vert == 0 || (world.is_occupied(new_pt) && world.get_tile_occupant(new_pt).getClass() != Ore.class)) {
                new_pt = entity_pt;
            }
        }

        return  new_pt;
    }
}
