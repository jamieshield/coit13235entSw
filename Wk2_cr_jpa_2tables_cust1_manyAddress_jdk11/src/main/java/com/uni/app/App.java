package com.uni.app;
// coderunner jpa_2tables_cust1_manyAddress_jdk11
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import javax.transaction.Transactional;
//...

/*
javax.? renamed jakarta.? in 2019 (Oracle license issues?) Spring 2.7 (jdk11) uses javax. Spring 3+ (jdk21) should use jakarta
import jakarta.persistence.Entity;
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Entity @Data class Address {
    @Id @GeneratedValue //(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull private String city;
}
@Entity @Data class Store {
    @Id @GeneratedValue //(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull private String suburb;
}
@Entity @Data class Customer {
    @Id @GeneratedValue //(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull private String name;
    @NonNull @OneToMany private List<Address> addresses;
    @NonNull @ManyToMany private List<Store> stores;
}
interface CustomerRepository extends JpaRepository<Customer, Long> {}
interface AddressRepository extends JpaRepository<Address, Long> {}
interface StoreRepository extends JpaRepository<Store, Long> {}
@SpringBootApplication
public class App {
    public static void main(String[] args) 
    {
        SpringApplication.run(App.class, args);
    }
}
@Transactional @Component @Data class AppInit implements ApplicationRunner {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final StoreRepository storeRepository;
    public void run(ApplicationArguments args) throws Exception {
        Address address = new Address("Hobart");
        this.addressRepository.save(address);
        List<Address> addresses=new ArrayList(Arrays.asList(address));
        Store storeG = new Store("Glenorchy");
        Store storeM = new Store("Moonah");
        storeRepository.save(storeG);
        storeRepository.save(storeM);
        List<Store> stores=new ArrayList(Arrays.asList(storeG,storeM));
        Customer cust = new Customer("John",addresses,stores);
        this.customerRepository.save(cust);
        List<Customer> custs = customerRepository.findAll();
        for (Customer c : custs) {
            System.out.println(c);
        }
    }
}


// jpa_bi
@Component
class RepositoryHelper {
    @PersistenceContext private EntityManager em; // @NonNull not needed?
    // SHOW TABLES for h2 dbms
    String P1 = "SELECT %s_NAME FROM INFORMATION_SCHEMA.%sS";
    String P2 = " WHERE TABLE_SCHEMA = SCHEMA()";
    String PC = String.format(P1, "COLUMN", "COLUMN") + P2;
    String PT = String.format(P1, "TABLE", "TABLE") + P2;
    @Transactional
    public List<String> showTables() {
        Query query = this.em.createNativeQuery(PT);
        return query.getResultList();
    }
    @Transactional
    public List<String> showColumns(String table) {
        // https://www.h2database.com/html/systemtables.html
        Query query = this.em.createNativeQuery(PC + " AND TABLE_NAME = '" + table + "'");
        return query.getResultList();
    }
    @Transactional
    public List<String> sql(String sql) {
        Query query = this.em.createNativeQuery(sql);
        return query.getResultList();
    }
}
@Component @Data
class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
     // https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
    @NonNull private final RepositoryHelper repositoryHelper; // @NonNull not needed?
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        int SEP = 0;
        for (String table : repositoryHelper.showTables()) {
            System.out.println();
            System.out.println("Table: " + table);
            List<String> columns = repositoryHelper.showColumns(table);
            List<List<String>> tab = new ArrayList();
            List<Integer> widths = new ArrayList();
            System.out.printf("|");
            for (String column : columns) {
                int width = column.length();
                List<String> col = repositoryHelper.sql("SELECT " + column + " FROM " + table);
                tab.add(col);
                for (Object row : col) {
                    width = Math.max(width, (row == null ? "null" : row).toString().length());
                }
                widths.add(width + SEP);
                System.out.printf(" %" + (width + SEP) + "s |", column);
            }
            System.out.println();
            int rowCount = tab.get(0).size();
            for (int r = 0; r < rowCount; r++) {
                System.out.printf("|");
                for (int c = 0; c < columns.size(); c++) {
                    System.out.printf(" %" + widths.get(c) + "s |", tab.get(c).get(r));
                }
                System.out.println();
            }
        }
        return;
    }
}

