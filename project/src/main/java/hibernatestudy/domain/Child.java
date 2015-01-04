package hibernatestudy.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Child {
    @Id @GeneratedValue
    @Getter private int id;
    
    @Getter @Setter private String name;
    
    @Getter @Setter private int age;

    protected Child() {}

    public Child(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
