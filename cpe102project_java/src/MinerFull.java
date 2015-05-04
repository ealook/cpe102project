
public class MinerFull extends Miner {

    public MinerFull(String name, Point position, int rate, int resource_limit) {
        super(name, position, rate, resource_limit, resource_limit);
    }

    public Miner try_transform_miner_full(WorldModel world) {

        MinerNotFull new_entity = new MinerNotFull(this.getName(), this.getPosition(),
                this.get_rate(), this.get_resource_limit());

        return new_entity;
    }

}
