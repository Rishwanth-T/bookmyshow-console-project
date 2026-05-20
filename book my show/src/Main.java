public class Main {
    public static void main(String[] args) {
        DataStore store = new DataStore();
        PersistenceService persistenceService = new PersistenceService("bookings.ser");
        persistenceService.load(store);

        ConsoleApp consoleApp = new ConsoleApp(store, persistenceService);
        consoleApp.run();
    }
}