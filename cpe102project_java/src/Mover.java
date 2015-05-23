import processing.core.PImage;

import java.util.*;

public abstract class Mover extends AnimatedActor {

    private final int rate;
    private final AStarNode[][] nodes;
    private ArrayList<Point> path;

    public Mover(String name, Point position, int rate, ArrayList<PImage> imgs, WorldModel world) {
        super(name, position, imgs);
        this.rate = rate;
        this.nodes = new AStarNode[world.getNum_rows()][world.getNum_cols()];
        this.path = new ArrayList<>();

        for (int x = 0; x < world.getNum_cols(); x++) {
            for (int y = 0; y < world.getNum_rows(); y++) {
                nodes[y][x] = new AStarNode(new Point(x, y));
            }
        }
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

    public ArrayList<Point> getPath() {
        return path;
    }

    public void setPath(ArrayList<Point> path) {
        this.path = path;
    }

    public void aStar(Point start, Point goal, WorldModel world, Class dest_class) {
        List<AStarNode> open_set = new ArrayList<>();
        List<AStarNode> closed_set = new LinkedList<>();

        // Reset and prepare for new search
        for (int x = 0; x < world.getNum_cols(); x++) {
            for (int y = 0; y < world.getNum_rows(); y++) {
                nodes[y][x] = new AStarNode(new Point(x, y));
            }
        }
        path = new ArrayList<>();

        nodes[start.getY()][start.getX()].setG_score(0);
        nodes[start.getY()][start.getX()].setF_score(nodes[start.getY()][start.getX()].getG_score() + start.distanceTo(goal));
        open_set.add(nodes[start.getY()][start.getX()]);

        while (open_set.size() > 0) {
            AStarNode current = lowestFOpenSet(open_set);
            if (current.getPt().getX() == goal.getX() && current.getPt().getY() == goal.getY()) {
                reconstruct_path(current);
                return;
            }

            open_set.remove(current);
            closed_set.add(current);
            for (AStarNode neighbor : getNeighbors(current, nodes, world, dest_class)) {
                if (closed_set.contains(neighbor)) {
                    continue;
                }

                int tentative_g_score = current.getG_score() + 1;

                if (!open_set.contains(neighbor) || tentative_g_score < neighbor.getG_score()) {
                    neighbor.setOrigin(current);
                    neighbor.setG_score(tentative_g_score);
                    neighbor.setF_score(neighbor.getG_score() + neighbor.getPt().distanceTo(goal));
                    if (!open_set.contains(neighbor)) {
                        open_set.add(neighbor);
                    }
                }
            }
        }

        path = new ArrayList<>();
    }

    private static ArrayList<AStarNode> getNeighbors(AStarNode current, AStarNode[][] nodes, WorldModel world, Class dest_class) {
        ArrayList<AStarNode> neighbors = new ArrayList<>();

        Point current_pt = current.getPt();
        int current_x = current_pt.getX();
        int current_y = current_pt.getY();

        Point up = new Point(current_x, current_y - 1);
        Point right = new Point(current_x + 1, current_y);
        Point down = new Point(current_x, current_y + 1);
        Point left = new Point(current_x - 1, current_y);

        if (world.within_bounds(up) && (world.get_tile_occupant(up) == null || world.get_tile_occupant(up).getClass() == dest_class)) {
            neighbors.add(nodes[up.getY()][up.getX()]);
        }
        if (world.within_bounds(right) && (world.get_tile_occupant(right) == null || world.get_tile_occupant(right).getClass() == dest_class)) {
            neighbors.add(nodes[right.getY()][right.getX()]);
        }
        if (world.within_bounds(down) && (world.get_tile_occupant(down) == null || world.get_tile_occupant(down).getClass() == dest_class)) {
            neighbors.add(nodes[down.getY()][down.getX()]);
        }
        if (world.within_bounds(left) && (world.get_tile_occupant(left) == null || world.get_tile_occupant(left).getClass() == dest_class)) {
            neighbors.add(nodes[left.getY()][left.getX()]);
        }

        return neighbors;
    }

    private static AStarNode lowestFOpenSet(List<AStarNode> open_set) {
        AStarNode lowest = open_set.get(0);

        for (int i = 1; i < open_set.size(); i++) {
            AStarNode current = open_set.get(i);
            if (lowest.getF_score() > current.getF_score()) {
                lowest = current;
            }
        }

        return lowest;
    }

    public void reconstruct_path(AStarNode current) {
        ArrayList<Point> total_path = new ArrayList<>();
        total_path.add(current.getPt());

        while (current.getOrigin() != null) {
            total_path.add(current.getOrigin().getPt());
            current = current.getOrigin();
        }

        path = total_path;
    }
}
