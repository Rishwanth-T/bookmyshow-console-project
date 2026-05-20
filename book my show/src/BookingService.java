import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private DataStore dataStore;
    private CatalogService catalogService;
    
    public BookingService(DataStore dataStore, CatalogService catalogService) {
        this.dataStore = dataStore;
        this.catalogService = catalogService;
    }
    
    public boolean isValidSeatCode(String seatCode, int showId) {
        if (seatCode.length() < 2) {
            return false;
        }
        
        char row = seatCode.charAt(0);
        try {
            int col = Integer.parseInt(seatCode.substring(1));
            Show show = dataStore.getShow(showId);
            Theatre theatre = dataStore.getTheatre(show.getTheatreId());
            
            int rowIndex = row - 'A';
            int colIndex = col - 1;
            
            if (rowIndex < 0 || colIndex < 0 || colIndex >= theatre.getSeatsPerRow()) {
                return false;
            }
            
            int totalRows = (theatre.getTotalSeats() + theatre.getSeatsPerRow() - 1) / theatre.getSeatsPerRow();
            return rowIndex < totalRows;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public List<String> validateSeatsForBooking(int showId, List<String> seatCodes) {
        List<String> invalidSeats = new ArrayList<>();
        
        for (String seatCode : seatCodes) {
            if (!isValidSeatCode(seatCode, showId)) {
                invalidSeats.add(seatCode + " (invalid format)");
            } else if (!dataStore.isSeatAvailable(showId, seatCode)) {
                invalidSeats.add(seatCode + " (already booked)");
            }
        }
        
        return invalidSeats;
    }
    
    public int bookSeats(String customerName, int showId, List<String> seatCodes) {
        // Validate all seats
        List<String> invalidSeats = validateSeatsForBooking(showId, seatCodes);
        if (!invalidSeats.isEmpty()) {
            System.out.println("\nInvalid seats:");
            for (String seat : invalidSeats) {
                System.out.println("  - " + seat);
            }
            return -1;
        }
        
        Show show = dataStore.getShow(showId);
        double totalPrice = show.getTicketPrice() * seatCodes.size();
        
        int bookingId = dataStore.bookSeats(customerName, showId, seatCodes, totalPrice);
        
        if (bookingId == -1) {
            System.out.println("Booking failed. Invalid show ID.");
            return -1;
        }
        
        return bookingId;
    }
    
    public List<Booking> getCustomerBookings(String customerName) {
        return dataStore.getBookingsByCustomer(customerName);
    }
    
    public Booking getBooking(int bookingId) {
        return dataStore.getBooking(bookingId);
    }
    
    public boolean cancelBooking(int bookingId) {
        Booking booking = dataStore.getBooking(bookingId);
        if (booking == null) {
            return false;
        }
        return dataStore.cancelBooking(bookingId);
    }
    
    public String generateBookingTicket(int bookingId) {
        Booking booking = dataStore.getBooking(bookingId);
        if (booking == null) {
            return "Booking not found";
        }
        
        Show show = dataStore.getShow(booking.getShowId());
        Theatre theatre = dataStore.getTheatre(show.getTheatreId());
        Movie movie = dataStore.getMovie(show.getMovieId());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        StringBuilder ticket = new StringBuilder();
        ticket.append("\n");
        ticket.append("╔════════════════════════════════════════╗\n");
        ticket.append("║         BOOKMYSHOW TICKET              ║\n");
        ticket.append("╚════════════════════════════════════════╝\n");
        ticket.append("\n");
        ticket.append("Booking ID:        #").append(bookingId).append("\n");
        ticket.append("Customer Name:     ").append(booking.getCustomerName()).append("\n");
        ticket.append("\n");
        ticket.append("------- SHOW DETAILS -------\n");
        ticket.append("Movie:             ").append(movie.getName()).append("\n");
        ticket.append("Theatre:           ").append(theatre.getName()).append("\n");
        ticket.append("City:              ").append(theatre.getCity()).append("\n");
        ticket.append("Show Time:         ").append(show.getStartTime().format(formatter)).append("\n");
        ticket.append("\n");
        ticket.append("------- SEATS -------\n");
        ticket.append("Seats:             ").append(String.join(", ", booking.getBookedSeats())).append("\n");
        ticket.append("Number of Seats:   ").append(booking.getBookedSeats().size()).append("\n");
        ticket.append("\n");
        ticket.append("------- PRICING -------\n");
        ticket.append("Price per Seat:    Rs. ").append(String.format("%.2f", show.getTicketPrice())).append("\n");
        ticket.append("Total Amount:      Rs. ").append(String.format("%.2f", booking.getTotalPrice())).append("\n");
        ticket.append("\n");
        ticket.append("Booked On:         ").append(booking.getBookingTime().format(formatter)).append("\n");
        ticket.append("\n");
        ticket.append("════════════════════════════════════════\n");
        ticket.append("Thank you for booking with BookMyShow!\n");
        ticket.append("════════════════════════════════════════\n");
        
        return ticket.toString();
    }
}
