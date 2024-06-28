package com.uni.app;

import java.util.Optional;

interface Repository {
    Optional<Integer> rating(String title);
}
class MovieRepository implements Repository {
    public Optional<Integer> rating(String title) {
        String RATINGS="Frankenstein:6;Pride and Predujice:8";
        for (String bookRating : RATINGS.split(";")) {
            String[] br=bookRating.split(":");
            if (br[0].compareTo(title)==0) {
                return Optional.of(Integer.parseInt(br[1]));
            }
        }
        return Optional.empty();
    }
}
public class App {
    public static void main(String[] args) { 
        MovieRepository movieRepository=new MovieRepository();
        System.out.println(movieRepository.rating("Frankenstein").orElseThrow());
        Optional<Integer> rate=movieRepository.rating("Moby Dick");
        if (rate.isPresent()) {
            System.out.println(rate.get());
        }
    }           
}
