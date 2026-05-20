import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Booking extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String customerName;
    private int showId;
    private List<String> bookedSeats;
    private LocalDateTime bookingTime;
    private double totalPrice;
    
    public Booking(int id, String customerName, int showId, List<String> bookedSeats, double totalPrice) {
        super(id);
        this.customerName = customerName;
        this.showId = showId;
        this.bookedSeats = new ArrayList<>(bookedSeats);
        this.bookingTime = LocalDateTime.now();
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }
    
    public int getShowId() {
        return showId;
    }
    
    public List<String> getBookedSeats() {
        return new ArrayList<>(bookedSeats);
    }
    
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Booking #%d | Customer: %s | Show: %d | Seats: %s | Total: %.2f | Booked: %s", 
            getId(), customerName, showId, bookedSeats, totalPrice, bookingTime.format(formatter));
    }
}
