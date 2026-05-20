import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<Integer, Movie> movies;
    private Map<Integer, Theatre> theatres;
    private Map<Integer, Show> shows;
    private List<Booking> bookings;
    private Map<Integer, boolean[]> showSeatStatus; // Track booked seats per show
    
    private int nextMovieId;
    private int nextTheatreId;
    private int nextShowId;
    private int nextBookingId;
    
    public DataStore() {
        this.movies = new HashMap<>();
        this.theatres = new HashMap<>();
        this.shows = new HashMap<>();
        this.bookings = new ArrayList<>();
        this.showSeatStatus = new HashMap<>();
        
        this.nextMovieId = 1;
        this.nextTheatreId = 1;
        this.nextShowId = 1;
        this.nextBookingId = 1;
        
        seedInitialData();
    }
    
    private void seedInitialData() {
        // Add sample movies
        addMovie("Avengers: Endgame", "Action", 181, "English");
        addMovie("The Shawshank Redemption", "Drama", 142, "English");
        addMovie("Inception", "Sci-Fi", 148, "English");
        addMovie("Dangal", "Sports", 161, "Hindi");
        addMovie("Pushpa", "Action", 179, "Telugu");
        
        // Add sample theatres
        addTheatre("PVR Cinemas", "Bangalore", 150, 10);
        addTheatre("INOX", "Mumbai", 200, 12);
        addTheatre("Cinepolis", "Delhi", 120, 10);
        addTheatre("Ticket Factory", "Hyderabad", 100, 10);
        
        // Add sample shows
        addShow(1, 1, LocalDateTime.of(2026, 5, 6, 14, 30), 250.0);
        addShow(1, 1, LocalDateTime.of(2026, 5, 6, 18, 0), 300.0);
        addShow(2, 2, LocalDateTime.of(2026, 5, 6, 16, 0), 200.0);
        addShow(3, 3, LocalDateTime.of(2026, 5, 7, 20, 0), 280.0);
        addShow(4, 4, LocalDateTime.of(2026, 5, 7, 19, 30), 220.0);
    }
    
    // Movie operations
    public void addMovie(String name, String genre, int duration, String language) {
        Movie movie = new Movie(nextMovieId++, name, genre, duration, language);
        movies.put(movie.getId(), movie);
    }
    
    public Movie getMovie(int id) {
        return movies.get(id);
    }
    
    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }
    
    // Theatre operations
    public void addTheatre(String name, String city, int totalSeats, int seatsPerRow) {
        Theatre theatre = new Theatre(nextTheatreId++, name, city, totalSeats, seatsPerRow);
        theatres.put(theatre.getId(), theatre);
    }
    
    public Theatre getTheatre(int id) {
        return theatres.get(id);
    }
    
    public List<Theatre> getAllTheatres() {
        return new ArrayList<>(theatres.values());
    }
    
    public List<Theatre> getTheatresByCity(String city) {
        List<Theatre> result = new ArrayList<>();
        for (Theatre theatre : theatres.values()) {
            if (theatre.getCity().equalsIgnoreCase(city)) {
                result.add(theatre);
            }
        }
        return result;
    }
    
    // Show operations
    public void addShow(int movieId, int theatreId, LocalDateTime startTime, double ticketPrice) {
        if (!movies.containsKey(movieId) || !theatres.containsKey(theatreId)) {
            System.out.println("Invalid movie or theatre ID");
            return;
        }
        
        Show show = new Show(nextShowId, movieId, theatreId, startTime, ticketPrice);
        shows.put(show.getId(), show);
        
        // Initialize seat status for this show (false = available, true = booked)
        Theatre theatre = theatres.get(theatreId);
        showSeatStatus.put(show.getId(), new boolean[theatre.getTotalSeats()]);
        
        nextShowId++;
    }
    
    public Show getShow(int id) {
        return shows.get(id);
    }
    
    public List<Show> getAllShows() {
        return new ArrayList<>(shows.values());
    }
    
    public List<Show> getShowsByMovie(int movieId) {
        List<Show> result = new ArrayList<>();
        for (Show show : shows.values()) {
            if (show.getMovieId() == movieId) {
                result.add(show);
            }
        }
        return result;
    }
    
    public List<Show> getShowsByMovieAndTheatre(int movieId, int theatreId) {
        List<Show> result = new ArrayList<>();
        for (Show show : shows.values()) {
            if (show.getMovieId() == movieId && show.getTheatreId() == theatreId) {
                result.add(show);
            }
        }
        return result;
    }
    
    // Booking operations
    public int bookSeats(String customerName, int showId, List<String> seats, double totalPrice) {
        if (!shows.containsKey(showId)) {
            return -1; // Invalid show
        }
        
        Booking booking = new Booking(nextBookingId++, customerName, showId, seats, totalPrice);
        bookings.add(booking);
        
        // Mark seats as booked
        boolean[] seatStatus = showSeatStatus.get(showId);
        for (String seatCode : seats) {
            int seatIndex = seatCodeToIndex(seatCode, shows.get(showId).getTheatreId());
            if (seatIndex >= 0 && seatIndex < seatStatus.length) {
                seatStatus[seatIndex] = true;
            }
        }
        
        return booking.getId();
    }
    
    public List<Booking> getBookingsByCustomer(String customerName) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomerName().equalsIgnoreCase(customerName)) {
                result.add(booking);
            }
        }
        return result;
    }
    
    public Booking getBooking(int id) {
        for (Booking booking : bookings) {
            if (booking.getId() == id) {
                return booking;
            }
        }
        return null;
    }
    
    public boolean cancelBooking(int bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            return false;
        }
        
        // Free up the seats
        boolean[] seatStatus = showSeatStatus.get(booking.getShowId());
        for (String seatCode : booking.getBookedSeats()) {
            int seatIndex = seatCodeToIndex(seatCode, shows.get(booking.getShowId()).getTheatreId());
            if (seatIndex >= 0 && seatIndex < seatStatus.length) {
                seatStatus[seatIndex] = false;
            }
        }
        
        bookings.remove(booking);
        return true;
    }
    
    // Seat management
    public boolean[] getSeatStatus(int showId) {
        return showSeatStatus.get(showId);
    }
    
    public boolean isSeatAvailable(int showId, String seatCode) {
        boolean[] seatStatus = showSeatStatus.get(showId);
        if (seatStatus == null) {
            return false;
        }
        
        int seatIndex = seatCodeToIndex(seatCode, shows.get(showId).getTheatreId());
        return seatIndex >= 0 && seatIndex < seatStatus.length && !seatStatus[seatIndex];
    }
    
    private int seatCodeToIndex(String seatCode, int theatreId) {
        // Seat format: "A1", "A2", "B1", etc.
        if (seatCode.length() < 2) {
            return -1;
        }
        
        char row = seatCode.charAt(0);
        try {
            int col = Integer.parseInt(seatCode.substring(1));
            Theatre theatre = theatres.get(theatreId);
            int rowIndex = row - 'A';
            int colIndex = col - 1;
            
            if (rowIndex < 0 || colIndex < 0 || colIndex >= theatre.getSeatsPerRow()) {
                return -1;
            }
            
            return rowIndex * theatre.getSeatsPerRow() + colIndex;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
    
    public List<String> getCities() {
        List<String> cities = new ArrayList<>();
        for (Theatre theatre : theatres.values()) {
            if (!cities.contains(theatre.getCity())) {
                cities.add(theatre.getCity());
            }
        }
        return cities;
    }
}
