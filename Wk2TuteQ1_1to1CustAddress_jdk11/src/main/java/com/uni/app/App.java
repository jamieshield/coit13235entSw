

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
 */
package com.uni.app;
// coderunner Wk2_jpa_2tables_cust1_1address_jdk11

import java.util.ArrayList;
import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import javax.transaction.Transactional;
//...

/*
javax.? renamed jakarta.? in 2019 (Oracle license issues?)
Spring 2.7 (jdk11) should use javax
Spring 3+ (jdk21) should use jakarta
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
 */
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Entity @Data class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;
}
@Entity @Data class Address {
    @Id @GeneratedValue
    private Long id;
    private String city;
    @OneToOne private Customer customer; // unused but needed owner/parent
    /*
    public void setCustomer(Customer customer) {
        this.customer=customer;
    }*/
}
interface CustomerRepository extends JpaRepository<Customer, Long> {}
interface AddressRepository extends JpaRepository<Address, Long> {}
@SpringBootApplication
public class App {
    public static void main(String[] args) //throws ParseException // parse date
    {
        SpringApplication.run(App.class, args);
    }
}
@Component @Data class AppInit implements ApplicationRunner {
    private final CustomerRepository customerRepository; // auto injected?
    private final AddressRepository addressRepository; // auto injected?
    //private final EntityManager em; // auto injected?
    public void run(ApplicationArguments args) throws Exception {
        Customer cust = new Customer();
        cust.setName("Sabrina Carpenter");
        customerRepository.save(cust);
        
        Address address = new Address();
        address.setCity("Hobart");
        address.setCustomer(cust);
        addressRepository.save(address);
              
        List<Address> addresses = addressRepository.findAll();
        for (Address a: addresses) {
            System.out.println(a.getCustomer().getName());
        }
    }
}

// see wk2Persist a book for show tables code


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

  
   //* https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
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
                        width=Math.max(width, (row==null?"null":row).toString().length());
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

