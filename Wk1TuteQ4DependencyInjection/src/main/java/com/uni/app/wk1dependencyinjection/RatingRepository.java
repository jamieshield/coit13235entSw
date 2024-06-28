package com.uni.app.wk1dependencyinjection;
import java.util.Optional;
public interface RatingRepository {
    public Optional<Integer> findRating(String title);
}
