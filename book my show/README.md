# Console BookMyShow Duplicate (Java)

A menu-driven console application that simulates core BookMyShow flows:
- Customer login by name
- Browse city, movie, and showtime
- Search shows by movie name and date
- View seat map
- Book seats
- View bookings
- Cancel booking
- Admin menu to add movies, theatres, and shows at runtime
- Printable ticket invoice in the console
- Persist bookings between runs (`bookings.ser`)

## Project Layout

- `src/Main.java` - app entrypoint
- `src/ConsoleApp.java` - menu/UI flow
- `src/DataStore.java` - seeded data and in-memory state
- `src/CatalogService.java` - city/movie/show listing
- `src/BookingService.java` - booking and cancellation logic
- `src/PersistenceService.java` - file persistence
- `src/SmokeTest.java` - minimal runnable test harness

## Quick Run (Windows cmd)

```bat
cd /d "c:\Users\5072239\Desktop\full stack\book my show\book my show"
javac src\*.java
java -cp src Main
```

## Run Smoke Test (Windows cmd)

```bat
cd /d "c:\Users\5072239\Desktop\full stack\book my show\book my show"
javac src\*.java
java -cp src SmokeTest
```

## Notes

- Seat booking is guarded against double-booking within one app process.
- Admin-added catalog data and bookings are persisted in `bookings.ser`.
- This app is designed as a local console MVP.
- No external dependencies are required, so no build manifest is included.
