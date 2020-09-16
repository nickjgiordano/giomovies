package movie.management.app;

/**
 * used to return a movie result after being processed
 */
public class Movie {
    private String id, title, date, year, rating, votes, poster, director;
    
    public Movie(String id, String title, String date, String year, String rating, String votes, String poster, String director) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.year = year;
        this.rating = rating;
        this.votes = votes;
        this.poster = poster;
        this.director = director;
    }

    public String getId() {return id;}
    public String getTitle() {return title;}
    public String getDate() {return date;}
    public String getYear() {return year;}
    public String getRating() {return rating;}
    public String getVotes() {return votes;}
    public String getPoster() {return poster;}
    public String getDirector() {return director;}
    
    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDate(String date) {this.date = date;}
    public void setYear(String year) {this.year = year;}
    public void setRating(String rating) {this.rating = rating;}
    public void setVotes(String votes) {this.votes = votes;}
    public void setPoster(String poster) {this.poster = poster;}
    public void setDirector(String director) {this.director = director;}
}
