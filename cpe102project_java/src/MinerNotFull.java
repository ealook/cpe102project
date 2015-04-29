
public class MinerNotFull extends Miner {

    public MinerNotFull(String name, Point position, int rate, int resource_limit) {
        super(name, position, rate, resource_limit, 0);
    }

    public String entity_string() {
        return "miner " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.get_resource_limit()
                + " " + this.get_rate();
    }

}
