
public class MinerNotFull extends Miner {

    public MinerNotFull(String name, Point position, int rate, int resource_limit) {
        super(name, position, rate, resource_limit, 0);
    }

    public String entity_string() {
        return "miner " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.get_resource_limit()
                + " " + this.get_rate();
    }

    public Miner try_transform_miner_not_full(WorldModel world) {

        if (this.resource_count < this.resource_limit) {
            return this;
        } else {

            MinerFull new_entity = new MinerFull(this.getName(), this.getPosition(),
                    this.get_rate(), this.get_resource_limit());

            return new_entity;
        }
    }
}
