
import java.util.Optional;
public class App {
    
    
    static Integer ratingNull(String title) {
        String RATINGS="Frankenstein:6;Pride and Predujice:8";
        for (String bookRating : RATINGS.split(";")) {
            String[] br=bookRating.split(":");
            if (br[0].compareTo(title)==0) {
                return Integer.parseInt(br[1]);
            }
        }
        return null;
    }
    
    static Optional<Integer> rating(String title) {
        String RATINGS="Frankenstein:6;Pride and Predujice:8";
        for (String bookRating : RATINGS.split(";")) {
            String[] br=bookRating.split(":");
            if (br[0].compareTo(title)==0) {
                return Optional.of(Integer.parseInt(br[1]));
            }
        }
        return Optional.empty();
    }
    
    public static void main(String[] args) { 
        // https://www.baeldung.com/java-optional
        System.out.println(ratingNull("Frankenstein"));
        Integer rateNull=ratingNull("Moby Dick");
        if (rateNull!=null) {
            System.out.println(rateNull);
        }
        
        
        System.out.println(rating("Frankenstein").orElseThrow());
        Optional<Integer> rate=rating("Moby Dick");
        //if (rate.isPresent()) {
            System.out.println(rate.get());
        //}
        
                
        //SpringApplication.run(App.class, args); 
    }        
    
}
/*
@Component class AppInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Everything started.");
    }
}
*/