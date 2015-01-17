package hibernatestudy.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Owned {
    @Id @GeneratedValue
    @Getter @Setter private int id;
    
    @Getter @Setter private String name;
    
    protected Owned() {}

    public Owned(String name) {
        this.name = name;
    }
}
