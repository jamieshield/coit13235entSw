
//Wk1_lombok_jdk11
/*<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.32</version>
        </dependency>
    </dependencies>
*/
package com.uni.app;
import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.isFinal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
@Data class Rating {
    private final String title; // or @NonNull
    private final Integer rating; // or @NonNull
}
@Data class Movie {
    @NonNull private String title;
    private Integer rating;
}
@Data @RequiredArgsConstructor @AllArgsConstructor class Book {
    private final String title;
    private Integer cost;
}
@Data @NoArgsConstructor @AllArgsConstructor class Pet { // No... or Required...
    private String name;
    private String type;
}
@Data @NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor class Icecream {
    @NonNull private String flavour;
    private Integer cost;
}

public class App {
    public static void main(String[] args) { 
        /* Rating: all fields required in constructor (setters are optional - use final or @NonNull)
        public Rating(String title, Integer rating) {
          this.title = title;
          this.rating = rating;  }    */
        Rating r=new Rating("Frankenstein",6);
        
        /* Movie: title is required. Generate setters (can't use final)
        public Movie(String title) { this.title = title;  }
        public void setTitle(String title) { this.title = title; } */
        Movie m = new Movie("Frankenstein");
        m.setTitle("Frankenstein recut");
        
        /* Book: title is required and disallow setTitle (use final)
           Generate constructors that take either just title or all args.
        public Book(String title) { this.title = title; }
        public Book(String title,Integer cost) { this.title = title; this.cost=cost; }  */
        Book b = new Book("Frankenstein");
        // Disallow: b.setTitle("Frankenstein recut");
        System.out.println(setDisallowed(b,"title"));
        b=new Book("Frankenstein",12);
        
        /* Pet: take all or no arguments in constructor
        public Pet(String name, String type) { this.name = name; this.type = type;  }
        public Pet() {} */
        Pet p = new Pet();
        p = new Pet("Mousetrap","Cat");
        
        /* Icrecream: constructors take none, just flavour or all args
        public Icecream(String flavour, Integer cost) { this.flavour = flavour; this.cost = cost; }
        public Icecream(String flavour) { this.flavour = flavour; }
        public Icecream() {}  */
        Icecream i = new Icecream();
        i = new Icecream("Chocolate");
        i = new Icecream("Chocolate",7);
        
    }
    static boolean setDisallowed(Object obj,String fieldName) {
        try { 
            Field field;
            field = obj.getClass().getDeclaredField(fieldName); 
            return isFinal(field.getModifiers());
        } catch (Exception e) {}
        return false;
    }
}
