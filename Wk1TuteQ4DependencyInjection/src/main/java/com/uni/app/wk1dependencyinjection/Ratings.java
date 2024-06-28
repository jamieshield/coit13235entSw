package com.uni.app.wk1dependencyinjection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
@Component public class Ratings implements RatingRepository {
    private final List<Rating> ratings = new ArrayList();
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
