package hibernatestudy.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class FooEntity {
    @Id
    @GeneratedValue
    @Getter @Setter private Integer id;
    
    @Getter @Setter private String name;
    
    protected FooEntity(){}
    
    public FooEntity(String name) {
        this.name = name;
    }
}
