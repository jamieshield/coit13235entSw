/*
<dependencies>   
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
// coderunner Wk2_1to1UniNormal_lockers_jdk11
/*
javax.? renamed jakarta.? in 2019 (Oracle license issues?)
Spring 2.7 (jdk11) should use javax
Spring 3+ (jdk21) should use jakarta
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
 */
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import javax.transaction.Transactional;
//...
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne; // only 1 is needed
import javax.persistence.OneToMany; // only 1 is needed
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
// @NoArgs required by repos   // @RequiredArgs used by 
@Entity @Data
class Employee {
    @Id @GeneratedValue private Long id;
    private String name;
}
@Entity @Data
class Locker {
    @Id @GeneratedValue private Long id;
    private String rowAndNumber;
    // Locker is owner/parent of Locker 1 -- 0..1 Employee : used_by
    // Future: Locker 1..* -- 0..1 Employee : used_by
    @ManyToOne private Employee employee; // is child
} 
interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
interface LockerRepository extends JpaRepository<Locker, Long> {
}
@SpringBootApplication public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
@Component @Data
class AppInit implements ApplicationRunner {
    private final EmployeeRepository employeeRepository; // auto injected?
    private final LockerRepository lockerRepository; // auto injected?
    public void run(ApplicationArguments args) throws Exception {
        Employee empl = new Employee();
        empl.setName("Danielle");
        
        Locker locker = new Locker();
        locker.setRowAndNumber("B3");
        locker.setEmployee(empl);
        
        Locker spare = new Locker();
        spare.setRowAndNumber("B4");
        spare.setEmployee(empl);
        
        List<Locker> lockers = lockerRepository.findAll();
        for (Locker l : lockers) {
            System.out.println(l.getEmployee().getName());
        }
        
        lockerRepository.save(locker);
        lockerRepository.save(spare);
        employeeRepository.save(empl);
    }
}
// see wk2Persist a book for show tables code
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
    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     * https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
     */
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
// DELETE REST
//package com.uni.app;
// coderunner jpa_2tables_cust1_manyAddress_jdk11
/*
javax.? renamed jakarta.? in 2019 (Oracle license issues?)
Spring 2.7 (jdk11) should use javax
Spring 3+ (jdk21) should use jakarta
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
 */
/*
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
@Transactional @Component @Data class AppInit implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final StoreRepository storeRepository;
    public void run(String... args) throws Exception {
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
*/
