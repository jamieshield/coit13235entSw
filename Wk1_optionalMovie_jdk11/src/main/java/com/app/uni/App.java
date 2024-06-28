//Wk1_optionalMovie_jdk11
/*<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.6.15</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.32</version>
        </dependency>
    </dependencies>
*/
package com.app.uni;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
@Data class Rating {
    private final String title;
    private final Integer rating;
}
interface RatingRepository {
    public Optional<Integer> findRating(String title);
}
@Component @Data class Ratings implements RatingRepository {
    private List<Rating> ratings = new ArrayList();
    public Ratings() {
        ratings.add(new Rating("Frankenstein",6));
        ratings.add(new Rating("Pride and Predujice",8));
    }
    public Optional<Integer> findRating(String title) {
        for (Rating rating : ratings) {
             if (rating.getTitle().equals(title)) {
                 return Optional.of(rating.getRating());
             }
        }
        return Optional.empty();
    }
}
@SpringBootApplication
public class App {
    public static void main(String[] args) { 
        SpringApplication.run(App.class, args);
    }        
}
@Component @Data class RatingService implements ApplicationRunner {
    private final RatingRepository ratings;
    public void run(ApplicationArguments args) {
        System.out.println(ratings.findRating("Frankenstein").orElseThrow());
        Optional<Integer> rating=ratings.findRating("Moby Dick");
        if (rating.isPresent()) {
            System.out.println(rating.get());
        }
    }
}