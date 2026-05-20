import java.io.*;

public class PersistenceService {
    private String filePath;
    
    public PersistenceService(String filePath) {
        this.filePath = filePath;
    }
    
    public void load(DataStore dataStore) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            DataStore loadedStore = (DataStore) ois.readObject();
            
            // Copy data from loaded store to current store
            for (Movie movie : loadedStore.getAllMovies()) {
                dataStore.addMovie(movie.getName(), movie.getGenre(), movie.getDurationMinutes(), movie.getLanguage());
            }
            
            for (Theatre theatre : loadedStore.getAllTheatres()) {
                dataStore.addTheatre(theatre.getName(), theatre.getCity(), theatre.getTotalSeats(), theatre.getSeatsPerRow());
            }
            
            for (Show show : loadedStore.getAllShows()) {
                dataStore.addShow(show.getMovieId(), show.getTheatreId(), show.getStartTime(), show.getTicketPrice());
            }
            
            for (Booking booking : loadedStore.getAllBookings()) {
                dataStore.bookSeats(booking.getCustomerName(), booking.getShowId(), booking.getBookedSeats(), booking.getTotalPrice());
            }
            
            System.out.println("Data loaded successfully from " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }
    
    public void save(DataStore dataStore) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(dataStore);
            System.out.println("Data saved successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }
}


