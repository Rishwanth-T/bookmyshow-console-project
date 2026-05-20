import java.io.Serializable;

public class Theatre extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String city;
    private int totalSeats;
    private int seatsPerRow;
    
    public Theatre(int id, String name, String city, int totalSeats, int seatsPerRow) {
        super(id);
        this.name = name;
        this.city = city;
        this.totalSeats = totalSeats;
        this.seatsPerRow = seatsPerRow;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCity() {
        return city;
    }
    
    public int getTotalSeats() {
        return totalSeats;
    }
    
    public int getSeatsPerRow() {
        return seatsPerRow;
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%d seats)", getId(), name, city, totalSeats);
    }
}
