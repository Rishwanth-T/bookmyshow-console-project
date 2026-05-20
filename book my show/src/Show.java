import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Show extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int movieId;
    private int theatreId;
    private LocalDateTime startTime;
    private double ticketPrice;
    
    public Show(int id, int movieId, int theatreId, LocalDateTime startTime, double ticketPrice) {
        super(id);
        this.movieId = movieId;
        this.theatreId = theatreId;
        this.startTime = startTime;
        this.ticketPrice = ticketPrice;
    }
    
    public int getMovieId() {
        return movieId;
    }
    
    public int getTheatreId() {
        return theatreId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public double getTicketPrice() {
        return ticketPrice;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%d] %s | Price: %.2f", getId(), startTime.format(formatter), ticketPrice);
    }
}
