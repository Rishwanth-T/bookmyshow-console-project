import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CatalogService {
    private DataStore dataStore;
    
    public CatalogService(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    public List<String> getCities() {
        return dataStore.getCities();
    }
    
    public List<Theatre> getTheatresByCity(String city) {
        return dataStore.getTheatresByCity(city);
    }
    
    public List<Movie> getAllMovies() {
        return dataStore.getAllMovies();
    }
    
    public List<Show> getShowsByMovieAndTheatre(int movieId, int theatreId) {
        return dataStore.getShowsByMovieAndTheatre(movieId, theatreId);
    }
    
    public List<Show> getShowsByMovie(int movieId) {
        return dataStore.getShowsByMovie(movieId);
    }
    
    public List<Show> searchShowsByMovieName(String movieName) {
        List<Show> result = new ArrayList<>();
        for (Movie movie : dataStore.getAllMovies()) {
            if (movie.getName().toLowerCase().contains(movieName.toLowerCase())) {
                result.addAll(dataStore.getShowsByMovie(movie.getId()));
            }
        }
        return result;
    }
    
    public List<Show> searchShowsByMovieNameAndDate(String movieName, LocalDate date) {
        List<Show> result = new ArrayList<>();
        List<Show> shows = searchShowsByMovieName(movieName);
        for (Show show : shows) {
            if (show.getStartTime().toLocalDate().equals(date)) {
                result.add(show);
            }
        }
        return result;
    }
    
    public String getSeatMap(int showId) {
        Show show = dataStore.getShow(showId);
        if (show == null) {
            return "Invalid show ID";
        }
        
        Theatre theatre = dataStore.getTheatre(show.getTheatreId());
        Movie movie = dataStore.getMovie(show.getMovieId());
        boolean[] seatStatus = dataStore.getSeatStatus(showId);
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== SEAT MAP ==========\n");
        sb.append("Movie: ").append(movie.getName()).append("\n");
        sb.append("Theatre: ").append(theatre.getName()).append("\n");
        sb.append("Show Time: ").append(show.getStartTime()).append("\n");
        sb.append("Price per Seat: Rs. ").append(show.getTicketPrice()).append("\n\n");
        
        sb.append("Screen this way >>>>\n\n");
        
        int seatsPerRow = theatre.getSeatsPerRow();
        int totalRows = (seatStatus.length + seatsPerRow - 1) / seatsPerRow;
        int seatIndex = 0;
        
        sb.append("   ");
        for (int col = 1; col <= seatsPerRow; col++) {
            sb.append(String.format("%2d ", col));
        }
        sb.append("\n");
        
        for (int row = 0; row < totalRows; row++) {
            char rowLetter = (char) ('A' + row);
            sb.append(rowLetter).append("  ");
            
            for (int col = 0; col < seatsPerRow; col++, seatIndex++) {
                if (seatIndex < seatStatus.length && seatStatus[seatIndex]) {
                    sb.append(" x ");
                } else if (seatIndex < seatStatus.length) {
                    sb.append(" _ ");
                } else {
                    sb.append("   ");
                }
            }
            sb.append("\n");
        }
        
        sb.append("\n_ = Available  |  x = Booked\n");
        sb.append("==============================\n");
        
        return sb.toString();
    }
    
    public List<Theatre> getAllTheatres() {
        return dataStore.getAllTheatres();
    }
}
