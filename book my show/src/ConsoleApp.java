import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private DataStore dataStore;
    private PersistenceService persistenceService;
    private CatalogService catalogService;
    private BookingService bookingService;
    private Scanner scanner;
    private String currentCustomer;
    
    public ConsoleApp(DataStore dataStore, PersistenceService persistenceService) {
        this.dataStore = dataStore;
        this.persistenceService = persistenceService;
        this.catalogService = new CatalogService(dataStore);
        this.bookingService = new BookingService(dataStore, catalogService);
        this.scanner = new Scanner(System.in);
        this.currentCustomer = null;
    }
    
    public void run() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       WELCOME TO BOOKMYSHOW            ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        boolean running = true;
        while (running) {
            if (currentCustomer == null) {
                running = showMainMenu();
            } else {
                running = showCustomerMenu();
            }
        }
            
        scanner.close();
        persistenceService.save(dataStore);
        System.out.println("\nThank you for using BookMyShow!");
    }
    
    private boolean showMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Login as Customer");
        System.out.println("2. Admin Menu");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();  
        
        switch (choice) {
            case "1":
                loginCustomer();
                return true;
            case "2":
                adminMenu();
                return true;
            case "0":
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }
    
    private void loginCustomer() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().trim();

        
        
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        
        currentCustomer = name;
        System.out.println("\nWelcome, " + currentCustomer + "!");
    }
    
    private boolean showCustomerMenu() {
        System.out.println("\n========== CUSTOMER MENU ==========");
        System.out.println("1. Browse Movies and Book Tickets");
        System.out.println("2. Search Shows by Movie Name and Date");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel Booking");
        System.out.println("0. Logout");
        //System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                browseAndBook();
                return true;
            case "2":
                searchAndBook();
                return true;
            case "3":
                viewMyBookings();
                return true;
            case "4":
                cancelBooking();
                return true;
            case "0":
                currentCustomer = null;
                System.out.println("Logged out successfully.");
                return true;
//            case "0":
//                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }
    
    private void browseAndBook() {
        List<String> cities = catalogService.getCities();
        if (cities.isEmpty()) {
            System.out.println("No cities available.");
            return;
        }

        String selectedCity = null;
        Movie selectedMovie = null;
        Theatre selectedTheatre = null;
        Show selectedShow = null;
        int step = 1;

        while (true) {
            switch (step) {
                case 1:
                    System.out.println("\n========== SELECT CITY ==========");
                    for (int i = 0; i < cities.size(); i++) {
                        System.out.println((i + 1) + ". " + cities.get(i));
                    }
                    System.out.println("0. Back");
                    System.out.print("Enter city number: ");

                    String cityInput = scanner.nextLine().trim();
                    if (cityInput.equals("0")) {
                        return;
                    }

                    int cityChoice;
                    try {
                        cityChoice = Integer.parseInt(cityInput);
                        if (cityChoice < 1 || cityChoice > cities.size()) {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        break;
                    }

                    selectedCity = cities.get(cityChoice - 1);
                    step = 2;
                    break;

                case 2:
                    List<Theatre> theatresInCity = catalogService.getTheatresByCity(selectedCity);
                    List<Movie> movies = new ArrayList<>();
                    List<Integer> movieIds = new ArrayList<>();
                    for (Show show : dataStore.getAllShows()) {
                        if (theatresInCity.stream().anyMatch(theatre -> theatre.getId() == show.getTheatreId())) {
                            if (!movieIds.contains(show.getMovieId())) {
                                movies.add(dataStore.getMovie(show.getMovieId()));
                                movieIds.add(show.getMovieId());
                            }
                        }
                    }

                    if (movies.isEmpty()) {
                        System.out.println("No movies available in " + selectedCity + ".");
                        return;
                    }

                    System.out.println("\n========== SELECT MOVIE ==========");
                    for (int i = 0; i < movies.size(); i++) {
                        Movie movie = movies.get(i);
                        System.out.println((i + 1) + ". " + movie.getName() + " | Genre: " + movie.getGenre() + " | Duration: " + movie.getDurationMinutes() + " min | Language: " + movie.getLanguage());
                    }
                    System.out.println("0. Back");
                    System.out.print("Enter movie number: ");

                    String movieInput = scanner.nextLine().trim();
                    if (movieInput.equals("0")) {
                        step = 1;
                        break;
                    }

                    int movieChoice;
                    try {
                        movieChoice = Integer.parseInt(movieInput);
                        if (movieChoice < 1 || movieChoice > movies.size()) {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        break;
                    }

                    selectedMovie = movies.get(movieChoice - 1);
                    step = 3;
                    break;

                case 3:
                    List<Theatre> theatresForMovie = new ArrayList<>();
                    for (Theatre theatre : catalogService.getTheatresByCity(selectedCity)) {
                        if (!catalogService.getShowsByMovieAndTheatre(selectedMovie.getId(), theatre.getId()).isEmpty()) {
                            theatresForMovie.add(theatre);
                        }
                    }

                    if (theatresForMovie.isEmpty()) {
                        System.out.println("No theatres have this movie in " + selectedCity + ".");
                        step = 2;
                        break;
                    }

                    System.out.println("\n========== SELECT THEATRE ==========");
                    for (int i = 0; i < theatresForMovie.size(); i++) {
                        Theatre theatre = theatresForMovie.get(i);
                        System.out.println((i + 1) + ". " + theatre.getName() + " - " + theatre.getCity() + " (" + theatre.getTotalSeats() + " seats)");
                    }
                    System.out.println("0. Back");
                    System.out.print("Enter theatre number: ");

                    String theatreInput = scanner.nextLine().trim();
                    if (theatreInput.equals("0")) {
                        step = 2;
                        break;
                    }

                    int theatreChoice;
                    try {
                        theatreChoice = Integer.parseInt(theatreInput);
                        if (theatreChoice < 1 || theatreChoice > theatresForMovie.size()) {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        break;
                    }

                    selectedTheatre = theatresForMovie.get(theatreChoice - 1);
                    step = 4;
                    break;

                case 4:
                    List<Show> shows = catalogService.getShowsByMovieAndTheatre(selectedMovie.getId(), selectedTheatre.getId());
                    if (shows.isEmpty()) {
                        System.out.println("No shows available for this movie in this theatre.");
                        step = 3;
                        break;
                    }

                    System.out.println("\n========== SELECT SHOW ==========");
                    for (int i = 0; i < shows.size(); i++) {
                        Show show = shows.get(i);
                        System.out.println((i + 1) + ". " + show.getStartTime() + " | Price: Rs. " + String.format("%.2f", show.getTicketPrice()));
                    }
                    System.out.println("0. Back");
                    System.out.print("Enter show number: ");

                    String showInput = scanner.nextLine().trim();
                    if (showInput.equals("0")) {
                        step = 3;
                        break;
                    }

                    int showChoice;
                    try {
                        showChoice = Integer.parseInt(showInput);
                        if (showChoice < 1 || showChoice > shows.size()) {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        break;
                    }

                    selectedShow = shows.get(showChoice - 1);
                    step = 5;
                    break;

                case 5:
                    boolean booked = bookSeatsForShow(selectedShow.getId());
                    if (!booked) {
                        step = 4;
                        break;
                    }
                    return;
            }
        }
    }
    
    private void searchAndBook() {
        System.out.print("\nEnter movie name (0 to go back): ");
        String movieName = scanner.nextLine().trim();
        if (movieName.equals("0")) {
            return;
        }
        
        System.out.print("Enter date (yyyy-MM-dd, 0 to go back): ");
        String dateStr = scanner.nextLine().trim();
        if (dateStr.equals("0")) {
            return;
        }
        
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }
        
        List<Show> shows = catalogService.searchShowsByMovieNameAndDate(movieName, date);
        
        if (shows.isEmpty()) {
            System.out.println("No shows found.");
            return;
        }
        
        System.out.println("\n========== AVAILABLE SHOWS ==========");
        for (int i = 0; i < shows.size(); i++) {
            Show show = shows.get(i);
            Movie movie = dataStore.getMovie(show.getMovieId());
            Theatre theatre = dataStore.getTheatre(show.getTheatreId());
            System.out.println((i + 1) + ". " + movie.getName() + " at " + theatre.getName() + " | " + show);
        }
        
        System.out.println("0. Back");
        System.out.print("Enter show number: ");
        
        String showInput = scanner.nextLine().trim();
        if (showInput.equals("0")) {
            return;
        }
        
        int showChoice;
        try {
            showChoice = Integer.parseInt(showInput);
            if (showChoice < 1 || showChoice > shows.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        Show selectedShow = shows.get(showChoice - 1);
        bookSeatsForShow(selectedShow.getId());
    }
    
    private boolean bookSeatsForShow(int showId) {
        System.out.println(catalogService.getSeatMap(showId));
        
        System.out.print("Enter number of seats to book (0 to go back): ");
        String seatsInput = scanner.nextLine().trim();
        if (seatsInput.equals("0")) {
            return false;
        }
        
        int numSeats;
        try {
            numSeats = Integer.parseInt(seatsInput);
            if (numSeats <= 0) {
                System.out.println("Invalid number of seats.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return false;
        }
        
        List<String> seatsToBook = new ArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            System.out.print("Enter seat " + (i + 1) + " (e.g., A1): ");
            String seatCode = scanner.nextLine().trim().toUpperCase();
            seatsToBook.add(seatCode);
        }
        
        int bookingId = bookingService.bookSeats(currentCustomer, showId, seatsToBook);
        
        if (bookingId != -1) {
            System.out.println("\n✓ Booking successful! Booking ID: " + bookingId);
            Booking booking = bookingService.getBooking(bookingId);
            System.out.println(bookingService.generateBookingTicket(bookingId));
            return true;
        } else {
            System.out.println("\n✗ Booking failed.");
            return false;
        }
    }
    
    private void viewMyBookings() {
        List<Booking> bookings = bookingService.getCustomerBookings(currentCustomer);
        
        if (bookings.isEmpty()) {
            System.out.println("\nYou have no bookings.");
            return;
        }
        
        System.out.println("\n========== YOUR BOOKINGS ==========");
        for (Booking booking : bookings) {
            Show show = dataStore.getShow(booking.getShowId());
            Movie movie = dataStore.getMovie(show.getMovieId());
            Theatre theatre = dataStore.getTheatre(show.getTheatreId());
        
            System.out.println("\nBooking #" + booking.getId());
            System.out.println("  Movie: " + movie.getName());
            System.out.println("  Theatre: " + theatre.getName() + " - " + theatre.getCity());
            System.out.println("  Show Time: " + show.getStartTime());
            System.out.println("  Seats: " + String.join(", ", booking.getBookedSeats()));
            System.out.println("  Total Price: Rs. " + booking.getTotalPrice());
        }
    }
    
    private void cancelBooking() {
        List<Booking> bookings = bookingService.getCustomerBookings(currentCustomer);
        
        if (bookings.isEmpty()) {
            System.out.println("\nYou have no bookings to cancel.");
            return;
        }
        
        System.out.println("\n========== CANCEL BOOKING ==========");
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            Show show = dataStore.getShow(booking.getShowId());
            Movie movie = dataStore.getMovie(show.getMovieId());
            
            System.out.println((i + 1) + ". Booking #" + booking.getId() + " - " + movie.getName() + " @ " + show.getStartTime());
        }   
        
        System.out.print("Enter booking number to cancel: ");
        
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > bookings.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        Booking bookingToCancel = bookings.get(choice - 1);
        
        System.out.print("Are you sure you want to cancel booking #" + bookingToCancel.getId() + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes")) {
            if (bookingService.cancelBooking(bookingToCancel.getId())) {
                System.out.println("✓ Booking cancelled successfully.");
            } else {
                System.out.println("✗ Failed to cancel booking.");
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }
    
    private void adminMenu() {
        boolean inAdminMenu = true;
        
        while (inAdminMenu) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. Add Movie");
            System.out.println("2. Add Theatre");
            System.out.println("3. Add Show");
            System.out.println("4. View All Bookings");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addMovie();
                    break;
                case "2":
                    addTheatre();
                    break;
                case "3":
                    addShow();
                    break;
                case "4":
                    viewAllBookings();
                    break;
                case "0":
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void addMovie() {
        System.out.print("\nEnter movie name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();
        
        System.out.print("Enter duration (minutes): ");
        int duration;
        try {
            duration = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration.");
            return;
        }
        
        System.out.print("Enter language: ");
        String language = scanner.nextLine().trim();
        
        dataStore.addMovie(name, genre, duration, language);
        System.out.println("✓ Movie added successfully.");
    }
    
    private void addTheatre() {
        System.out.print("\nEnter theatre name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter city: ");
        String city = scanner.nextLine().trim();
        
        System.out.print("Enter total seats: ");
        int totalSeats;
        try {
            totalSeats = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        
        System.out.print("Enter seats per row: ");
        int seatsPerRow;
        try {
            seatsPerRow = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        
        dataStore.addTheatre(name, city, totalSeats, seatsPerRow);
        System.out.println("✓ Theatre added successfully.");
    }
    
    private void addShow() {
        List<Movie> movies = dataStore.getAllMovies();
        System.out.println("\n========== SELECT MOVIE ==========");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i).getName());
        }
        System.out.print("Enter movie number: ");
        
        int movieChoice;
        try {
            movieChoice = Integer.parseInt(scanner.nextLine().trim());
            if (movieChoice < 1 || movieChoice > movies.size()) {
                System.out.println("Invalid choice.");
                return;     
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        Movie selectedMovie = movies.get(movieChoice - 1);
        
        List<Theatre> theatres = dataStore.getAllTheatres();
        System.out.println("\n========== SELECT THEATRE ==========");
        for (int i = 0; i < theatres.size(); i++) {
            System.out.println((i + 1) + ". " + theatres.get(i).getName());
        }
        System.out.print("Enter theatre number: ");
        
        int theatreChoice;
        try {
            theatreChoice = Integer.parseInt(scanner.nextLine().trim());
            if (theatreChoice < 1 || theatreChoice > theatres.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
        Theatre selectedTheatre = theatres.get(theatreChoice - 1);
        
        System.out.print("\nEnter show date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeStr = scanner.nextLine().trim();
        
        java.time.LocalDateTime startTime;
        try {
            startTime = java.time.LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format.");
            return;
        }
        
        System.out.print("Enter ticket price: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }
        
        dataStore.addShow(selectedMovie.getId(), selectedTheatre.getId(), startTime, price);
        System.out.println("✓ Show added successfully.");
    }
    
    private void viewAllBookings() {
        List<Booking> bookings = dataStore.getAllBookings();
        
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }
        
        System.out.println("\n========== ALL BOOKINGS ==========");
        for (Booking booking : bookings) {
            Show show = dataStore.getShow(booking.getShowId());
            Movie movie = dataStore.getMovie(show.getMovieId());
            Theatre theatre = dataStore.getTheatre(show.getTheatreId());
            
            System.out.println("\nBooking #" + booking.getId());
            System.out.println("  Customer: " + booking.getCustomerName());
            System.out.println("  Movie: " + movie.getName());
            System.out.println("  Theatre: " + theatre.getName());
            System.out.println("  Show Time: " + show.getStartTime());
            System.out.println("  Seats: " + String.join(", ", booking.getBookedSeats()));
            System.out.println("  Total Price: Rs. " + booking.getTotalPrice());
        }
    }
}
