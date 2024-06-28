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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
            <version>1.18.32</version>
        </dependency>
    </dependencies>
 */
package com.uni.app;
// prep for Wk2_1to1Bi_lockers_jdk11
import java.util.Objects;
import lombok.Data;
@Data
class Employee { // Child
    private String name;
    Locker locker;
    void setLocker(Locker locker) {
        this.locker=locker;
        if (!Objects.equals(locker.getEmployee(),this)) { locker.setEmployee(this); }
    }
}
@Data
class Locker { // Parent
    private String rowAndNumber;
    private Employee employee; // rob from 1 (child:Emp), give to many (parent:Locker)
    void setEmployee(Employee employee) {
        this.employee=employee;
        if (!Objects.equals(employee.getLocker(),this)) { employee.setLocker(this); }
    }
} 
public class App {
    public static void main(String[] args) {
        Employee empl = new Employee();
        empl.setName("Danielle");
        
        Employee ann = new Employee();
        ann.setName("Ann");
        
        Locker locker = new Locker();
        locker.setRowAndNumber("B3");

        locker.setEmployee(empl);
        System.out.println(empl.getLocker().getRowAndNumber());
        
        ann.setLocker(locker);
        System.out.println(locker.getEmployee().getName());
    }
}
