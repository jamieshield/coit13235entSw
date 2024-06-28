package com.uni.app.wk1dependencyinjection;
import java.util.Optional;
import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
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
