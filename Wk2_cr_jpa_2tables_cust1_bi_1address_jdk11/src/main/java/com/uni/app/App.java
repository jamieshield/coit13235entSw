package com.uni.app;
// coderunner jpa_2tables_cust1_bi_1_jdk11
/*
javax.? renamed jakarta.? in 2019 (Oracle license issues?) Spring 2.7 (jdk11) uses javax. Spring 3+ (jdk21) should use jakarta
import jakarta.persistence.Entity;
 */
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Entity @Data  @NoArgsConstructor @RequiredArgsConstructor class Address {
    @Id @GeneratedValue
    private Long id;
    @NonNull private String city;
    @OneToOne(mappedBy = "address") private Customer customer;
    //public String toString() { return city;  } // print city prints address prints city...
}
@Entity @Data @NoArgsConstructor @RequiredArgsConstructor class Customer {
    @Id @GeneratedValue
    private Long id;
    @NonNull private String name; // can't use non-nulls as need default & default overrides 
    @NonNull @OneToOne private Address address;
}
interface CustomerRepository extends JpaRepository<Customer, Long> {}
interface AddressRepository extends JpaRepository<Address, Long> {}
@SpringBootApplication public class App {
    public static void main(String[] args) 
    {
        SpringApplication.run(App.class, args);
    }
}
@Order(1)
@Transactional @Component @Data class AppInit implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    public void run(String... args) throws Exception {
        Address address = new Address("Hobart");
        Customer cust = new Customer("John",address);
        // Next line commented just to test relation is bidirectional
        //address.setCustomer(cust); // in-memory entities inconsistent with db without this
        addressRepository.save(address);
        customerRepository.save(cust);
        List<Customer> custs = customerRepository.findAll();
        for (Customer c : custs) {
            // current entities are inconsistent with db
            System.out.println("c->a: "+c.getAddress()); // customer=null 
            // print Address works due to null Customer
        }
        List<Address> addresses = addressRepository.findAll();
        for (Address a : addresses) {
            System.out.println("Bi a->c: "+a.getCustomer()); // null // bi
        }
    }
}
@Order(2)
@Transactional @Component @Data class AppInit2 implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    public void run(String... args) throws Exception {
        List<Address> addresses = addressRepository.findAll();
        for (Address a : addresses) {
            System.out.println("Bi a->c: "+a.getCustomer().getName()); // bi
        }
    }
}
// https://stackoverflow.com/questions/30874214/how-to-access-entity-manager-with-spring-boot-and-spring-data
@Service
class RepositoryHelper {
    @PersistenceContext private EntityManager em;
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
@Component @Data
class AppInitShowTables implements CommandLineRunner {
    private final RepositoryHelper repositoryHelper;
    public void run(String... args) throws Exception {
        int SEP=0;
        for (String table: repositoryHelper.showTables()) {
                System.out.println("\nTable: "+table);
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