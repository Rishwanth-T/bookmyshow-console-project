import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SmokeTest {
    public static void main(String[] args) {
        System.out.println("🧪 Starting BookMyShow Smoke Test...\n");
        
        // Initialize services
        DataStore dataStore = new DataStore();
        CatalogService catalogService = new CatalogService(dataStore);
        BookingService bookingService = new BookingService(dataStore, catalogService);
        
        try {
            // Test 1: Verify initial data
            System.out.println("✓ Test 1: Checking initial data...");
            assert catalogService.getAllMovies().size() > 0 : "No movies found";
            assert catalogService.getTheatresByCity("Bangalore").size() > 0 : "No theatres in Bangalore";
            System.out.println("  - Movies loaded: " + catalogService.getAllMovies().size());
            System.out.println("  - Cities loaded: " + catalogService.getCities().size());
            System.out.println("  ✓ Passed\n");
            
            // Test 2: Get shows for a movie
            System.out.println("✓ Test 2: Retrieving shows for a movie...");
            List<Show> shows = catalogService.getShowsByMovie(1);
            assert shows.size() > 0 : "No shows found for movie 1";
            System.out.println("  - Shows for Movie 1: " + shows.size());
            System.out.println("  ✓ Passed\n");
            
            // Test 3: View seat map
            System.out.println("✓ Test 3: Displaying seat map...");
            Show firstShow = shows.get(0);
            String seatMap = catalogService.getSeatMap(firstShow.getId());
            assert seatMap.contains("[O]") : "Seat map doesn't contain available seats";
            System.out.println(seatMap);
            System.out.println("  ✓ Passed\n");
            
            // Test 4: Book seats
            System.out.println(" Test 4: Booking seats...");
            List<String> seatsToBook = Arrays.asList("A1", "A2", "A3");
            int bookingId = bookingService.bookSeats("John Doe", firstShow.getId(), seatsToBook);
            assert bookingId > 0 : "Booking failed";
            System.out.println("  - Booking ID: " + bookingId);
            System.out.println("  Passed\n");
            
            // Test 5: View booking ticket
            System.out.println("✓ Test 5: Generating booking ticket...");
            String ticket = bookingService.generateBookingTicket(bookingId);
            assert ticket.contains("John Doe") : "Ticket doesn't contain customer name";
            assert ticket.contains("A1") : "Ticket doesn't contain booked seats";
            System.out.println(ticket);
            System.out.println("  ✓ Passed\n");
            
            // Test 6: Verify seats are booked
            System.out.println("✓ Test 6: Verifying seat status after booking...");
            assert !dataStore.isSeatAvailable(firstShow.getId(), "A1") : "Seat A1 should be booked";
            assert !dataStore.isSeatAvailable(firstShow.getId(), "A2") : "Seat A2 should be booked";
            assert dataStore.isSeatAvailable(firstShow.getId(), "B1") : "Seat B1 should be available";
            System.out.println("  - Booked seats correctly marked");
            System.out.println("  ✓ Passed\n");
            
            // Test 7: Get customer bookings
            System.out.println("✓ Test 7: Retrieving customer bookings...");
            List<Booking> customerBookings = bookingService.getCustomerBookings("John Doe");
            assert customerBookings.size() == 1 : "Should have 1 booking for John Doe";
            System.out.println("  - Customer bookings: " + customerBookings.size());
            System.out.println("  ✓ Passed\n");
            
            // Test 8: Search shows by movie name and date
            System.out.println("✓ Test 8: Searching shows by movie name and date...");
            List<Show> searchResults = catalogService.searchShowsByMovieName("Avengers");
            assert searchResults.size() > 0 : "Search for 'Avengers' returned no results";
            System.out.println("  - Search results: " + searchResults.size());
            System.out.println("  ✓ Passed\n");
            
            // Test 9: Cancel booking
            System.out.println("✓ Test 9: Cancelling booking...");
            boolean cancelled = bookingService.cancelBooking(bookingId);
            assert cancelled : "Failed to cancel booking";
            assert dataStore.isSeatAvailable(firstShow.getId(), "A1") : "Seat A1 should be available after cancellation";
            System.out.println("  - Booking cancelled successfully");
            System.out.println("  - Seats freed up");
            System.out.println("  ✓ Passed\n");
            
            // Test 10: Admin add movie
            System.out.println("✓ Test 10: Admin adding a new movie...");
            int initialMovieCount = catalogService.getAllMovies().size();
            dataStore.addMovie("Test Movie", "Test Genre", 120, "English");
            int newMovieCount = catalogService.getAllMovies().size();
            assert newMovieCount == initialMovieCount + 1 : "Movie count didn't increase";
            System.out.println("  - New movie added successfully");
            System.out.println("  ✓ Passed\n");
            
            System.out.println("════════════════════════════════════════");
            System.out.println("✓ All smoke tests passed successfully!");
            System.out.println("════════════════════════════════════════\n");
            
        } catch (AssertionError e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
