/*
@startuml
class Employee<@Data> {
--
  - String name
  + setLocker(Locker l)
}
class Locker<@Data> {
--
  - String rowAndNumber
  + Employee getEmployee()
  FK employee_id
}
Employee "1" -- "1..*" Locker : used_by <
@enduml

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
// coderunner Wk2_1to1Bi_lockers_jdk11
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
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Entity @Data
class Employee { // Child
    @Id @GeneratedValue private Long id;
    private String name;
    // @OneToMany lazy loaded by default - use @Transactional
    @OneToMany(mappedBy="employee") private List<Locker> lockers=new ArrayList<>(); // parent of bi (persisted indirectly)
    void setLocker(Locker locker) { // addLocker
        this.lockers.add(locker);
        if (!Objects.equals(locker.getEmployee(),this)) { locker.setEmployee(this); }
    }
}
@Entity @Data
class Locker { // Parent
    @Id @GeneratedValue private Long id;
    private String rowAndNumber;
    // Locker is owner/parent of Locker 1 -- 0..1 Employee : used_by
    // Future: Locker 1..* -- 0..1 Employee : used_by
    @ManyToOne private Employee employee; // rob from 1 (child:Emp), give to many (parent:Locker)
    void setEmployee(Employee employee) {
        this.employee=employee;
        if (!employee.getLockers().contains(this)) { employee.setLocker(this); }
    }
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
@Component @Data @Order(1)
class AppInit implements ApplicationRunner {
    private final EmployeeRepository employeeRepository; // auto injected?
    private final LockerRepository lockerRepository; // auto injected?
    public void run(ApplicationArguments args) throws Exception {
        Employee empl = new Employee();
        empl.setName("Danielle");
        employeeRepository.save(empl);
        Employee ann = new Employee();
        ann.setName("Ann");
        employeeRepository.save(ann);
        Locker locker = new Locker();
        locker.setRowAndNumber("B3");
        locker.setEmployee(empl);
        ann.setLocker(locker);
        lockerRepository.save(locker);
        List<Locker> lockers = lockerRepository.findAll();
        for (Locker l : lockers) {
            System.out.println(l.getEmployee().getName());
        }
    }
}
@Order(2)
@Component @Data @Transactional
class AppInit2 implements ApplicationRunner {
    private final EmployeeRepository employeeRepository; // auto injected?
    public void run(ApplicationArguments args) throws Exception {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee empl : employees) {
            for (Locker locker : empl.getLockers()) { // lazy: @Transactional
             System.out.println(locker.getRowAndNumber());
            }
        }
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
