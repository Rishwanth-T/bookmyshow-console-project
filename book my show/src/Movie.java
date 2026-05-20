import java.io.Serializable;

public class Movie extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String genre;
    private int durationMinutes;
    private String language;
    
    public Movie(int id, String name, String genre, int durationMinutes, String language) {
        super(id);
        this.name = name;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public String getLanguage() {
        return language;
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s | Genre: %s | Duration: %d min | Language: %s", 
            getId(), name, genre, durationMinutes, language);
    }
}
