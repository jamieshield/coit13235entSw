/*<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.32</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
*/
package com.uni.app;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
@Data class Rating {
    @NonNull private String title;
    @NonNull private Integer rating;
}
interface Repository {
    Optional<Integer> rating(String title);
}
@Component @Data class MovieRepository implements Repository {
    private Rating[] ratings={new Rating("Frankenstein",6),new Rating("Pride and Prejudice",8)};
    @Override
    public Optional<Integer> rating(String title) {
        for (Rating rating: ratings) {
            if (rating.getTitle().compareTo(title)==0) {
                return Optional.of(rating.getRating());
            }
        }
        return Optional.empty();
    }
}
@Component @Data class RatingService implements ApplicationRunner {
    private final MovieRepository movieRepository;    
    public void run(ApplicationArguments args) throws Exception {
            Optional rating=movieRepository.rating("Frankenstein");
            if (rating.isPresent()) {
                System.out.println(rating.get());
            } else System.out.println("Frank not found");
        }
}
@SpringBootApplication public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
