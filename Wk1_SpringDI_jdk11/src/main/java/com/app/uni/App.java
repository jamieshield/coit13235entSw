//Wk1_SpringDI_jdk11
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
package com.uni.app;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
@Data class Rating {
    private final String title;
    private final Integer rating;
}
interface RatingRepository {
    public Integer findRating(String title);
}
@Data class RatingsComponent implements RatingRepository {
    private List<Rating> ratings = new ArrayList();
    public RatingsComponent() {
        ratings.add(new Rating("Frankenstein",6));
        ratings.add(new Rating("Pride and Predujice",8));
    }
    public Integer findRating(String title) {
        for (Rating rating : ratings) {
             if (rating.getTitle().equals(title)) {
                 return rating.getRating();
             }
        }
        return null;
    }
}

public class App {
    static class AA implements ApplicationArguments {
        @Override public String[] getSourceArgs() { return null;  }
        @Override public Set<String> getOptionNames() { return null; }
        @Override public boolean containsOption(String name) { return true; }
        @Override public List<String> getOptionValues(String name) { return null; }
        @Override public List<String> getNonOptionArgs() { return null;  }
    }
    public static void main(String[] args) { 
        System.out.println("Jim's Spring");
        System.out.println("============");
        System.out.println("Emulating Spring");
        System.out.println("Spring creates singleton components:");
        System.out.println(" = RatingsComponent - a Repository for Rating(s)");
        RatingRepository ratingsComponent_singleton=new RatingsComponent();
        System.out.println(" = RatingServiceComponent [inject dep: singleton RatingsComponent]");
        ApplicationRunner ratingServiceComponent_singleton=new RatingServiceComponent(ratingsComponent_singleton);
        ApplicationArguments aArgs=new AA();
        try {
            ratingServiceComponent_singleton.run(aArgs);
        } catch (Exception e) {}
    }        
}
@Data class RatingServiceComponent implements ApplicationRunner {
    private final RatingRepository ratings;
    public void run(ApplicationArguments args) {
        System.out.println(ratings.findRating("Frankenstein"));
        Integer rating=ratings.findRating("Moby Dick");
        if (rating!=null) {
            System.out.println(rating);
        }
    }
}