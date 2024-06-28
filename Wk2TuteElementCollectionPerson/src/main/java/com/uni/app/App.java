/*<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
            <version>1.18.32</version>
        </dependency>
    </dependencies>
Table: PERSON
| ID | NAME |
|  3 |  Ann |

Table: PHONE
| ID |    NUMBER |
|  1 | 041838383 |
|  2 | 041838382 |

Table: PERSON_NUMBERS
| PERSON_ID | NUMBERS_ID |
|         3 |          1 |
|         3 |          2 |
*/
package com.uni.app;

// coderunner jdk11 h2 prototype

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.core.annotation.Order;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
// more, e.g. lombok

/*
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
*/
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Entity @Data class Phone {
    @Id @GeneratedValue private Long id;
    private String number;
    //@OneToMany // many phones ; phone is owner
    //private Person person;
}
@Entity @Data class Person {
    @Id @GeneratedValue private Long id;
    private String name;
    //@ElementCollection private List<String> numbers = new ArrayList<>(); // set
    @OneToMany private List<Phone> numbers = new ArrayList<>(); // set
}
interface PhoneRepository extends JpaRepository<Phone, Long> { } // JPA injects repo based on, e.g. @Entity
interface PersonRepository extends JpaRepository<Person, Long> { } // JPA injects repo based on, e.g. @Entity
@SpringBootApplication public class App {
    public static void main( String[] args ) { SpringApplication.run(App.class, args);  }

   @Transactional
   @Data @Component class AppInit implements ApplicationRunner {
   @NonNull private final PhoneRepository phoneRepository; // DI via lombok constructor
   @NonNull private final PersonRepository personRepository; // DI via lombok constructor
   @Override public void run(ApplicationArguments args) throws Exception {
     Phone ph1=new Phone();
     ph1.setNumber("041838383");
     phoneRepository.save(ph1);
     Phone ph2=new Phone();
     ph2.setNumber("041838382");
     phoneRepository.save(ph2);
     Person ann = new Person();
     ann.setName("Ann");
     ann.getNumbers().add(ph1);
     ann.getNumbers().add(ph2);
     //ann.getNumbers().add(ph2); // duplicate
     personRepository.save(ann);
     List<Person> people = personRepository.findAll();
     for (Person p : people) { 
         System.out.print(p.getName()+":");   
         for (Phone ph : p.getNumbers()) { System.out.print(ph.getNumber()+";"); }
         System.out.println();   }   }   }  }
// jpa_bi
@Component
class RepositoryHelper {
    @PersistenceContext private EntityManager em; // @NonNull not needed?
    // SHOW TABLES for h2 dbms
    String P1="SELECT %s_NAME FROM INFORMATION_SCHEMA.%sS";
    String P2=" WHERE TABLE_SCHEMA = SCHEMA()";
    String PC=String.format(P1, "COLUMN", "COLUMN")+P2;
    String PT=String.format(P1, "TABLE", "TABLE")+P2;
    @Transactional
    public List<String> showTables() {
        Query query = this.em.createNativeQuery(PT);
        return query.getResultList();
    }
    @Transactional
    public List<String> showColumns(String table) {
        // https://www.h2database.com/html/systemtables.html
        Query query = this.em.createNativeQuery(PC+" AND TABLE_NAME = '"+table+"'");
        return query.getResultList();
    }
    @Transactional
    public List<String> sql(String sql) {
        Query query = this.em.createNativeQuery(sql);
        return query.getResultList();
    }
}

@Component @Data class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  /**
   * This event is executed as late as conceivably possible to indicate that 
   * the application is ready to service requests.
   * https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
   */
  @NonNull private final RepositoryHelper repositoryHelper; // @NonNull not needed?
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {

            int SEP=0;
        
        for (String table: repositoryHelper.showTables()) {
                System.out.println();
                System.out.println("Table: "+table);
                List<String> columns=repositoryHelper.showColumns(table);
                List<List<String>> tab= new ArrayList();
                List<Integer> widths=new ArrayList();
                System.out.printf("|");
                for (String column: columns) {
                    int width=column.length();
                    List<String> col=repositoryHelper.sql("SELECT "+column+" FROM "+table);
                    tab.add(col);
                    for (Object row : col) {
                        width=Math.max(width, row.toString().length());
                    }
                    widths.add(width+SEP);
                    System.out.printf(" %"+(width+SEP)+"s |",column);
                }
                System.out.println();
                int rowCount=tab.get(0).size();
                for (int r=0; r<rowCount; r++) {
                    System.out.printf("|");
                    for (int c=0; c<columns.size(); c++) {
                        System.out.printf(" %"+widths.get(c)+"s |",tab.get(c).get(r));
                    }
                    System.out.println();
                }
        }
    

    return;
  }
}


/*@Data @Component 
class AppInitShowTables implements ApplicationRunner {
    @NonNull private final RepositoryHelper repositoryHelper; // @NonNull not needed?
    public void run(ApplicationArguments args) throws Exception {
        int SEP=0;
        
        for (String table: repositoryHelper.showTables()) {
                System.out.println();
                System.out.println("Table: "+table);
                List<String> columns=repositoryHelper.showColumns(table);
                List<List<String>> tab= new ArrayList();
                List<Integer> widths=new ArrayList();
                System.out.printf("|");
                for (String column: columns) {
                    int width=column.length();
                    List<String> col=repositoryHelper.sql("SELECT "+column+" FROM "+table);
                    tab.add(col);
                    for (Object row : col) {
                        width=Math.max(width, row.toString().length());
                    }
                    widths.add(width+SEP);
                    System.out.printf(" %"+(width+SEP)+"s |",column);
                }
                System.out.println();
                int rowCount=tab.get(0).size();
                for (int r=0; r<rowCount; r++) {
                    System.out.printf("|");
                    for (int c=0; c<columns.size(); c++) {
                        System.out.printf(" %"+widths.get(c)+"s |",tab.get(c).get(r));
                    }
                    System.out.println();
                }
        }
    }
}
*/