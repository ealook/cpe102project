
public class Miner extends Mover {

    public final int resource_limit;
    public int resource_count;

    public Miner(String name, Point position, int rate, int resource_limit, int resource_count) {
        super(name, position, rate);
        this.resource_limit = resource_limit;
        this.resource_count = resource_count;
    }

    public void set_resource_count(int n) {
        resource_count = n;
    }

    public int get_resource_count() {
        return resource_count;
    }

    public int get_resource_limit() {
        return resource_limit;
    }

}
