//Wk1_pom_jdk11
/*<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.32</version>
        </dependency>
    </dependencies>
*/


package com.uni.app;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
@Data @NoArgsConstructor @RequiredArgsConstructor class Movie {
    @NonNull private String title;
    private Integer rating;
}
@Data @NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor class Book {
    @NonNull private String title;
    private Integer rating;
}
public class App {
    public static void main(String[] args) { 
        Movie m = new Movie("Frankenstein");
        m.setRating(6);
        Book b1 = new Book();
        Book b2 = new Book("Frankenstein",6);
        Book b3 = new Book("Frankenstein");
        
    }
}

/*
package com.uni.app;
import lombok.Data;
import lombok.NonNull;
@Data class Movie {
    @NonNull private String title;
    private Integer rating;
}
public class App {
    public static void main(String[] args) { 
        Movie m = new Movie("Frankenstein");
        m.setRating(6);
    }
}
*/