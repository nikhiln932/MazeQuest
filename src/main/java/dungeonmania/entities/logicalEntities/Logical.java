package dungeonmania.entities.logicalEntities;

public abstract interface Logical {
    public void tick();

    public void subscribe(Conductor c);
}
