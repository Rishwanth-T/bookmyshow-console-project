// Interface contract: All entities must have a unique ID
public interface Entity {
    int getId();
}

// Abstract base class: Provides shared id field and getId() implementation
abstract class BaseEntity implements Entity {
    private final int id;  // Immutable ID field (final = cannot be changed)
    
    public BaseEntity(int id) {
        this.id = id;
    }
    
    @Override  // Implements Entity interface contract
    public int getId() {
        return id;
    }
}