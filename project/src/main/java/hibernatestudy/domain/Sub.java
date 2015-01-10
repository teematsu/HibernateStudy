package hibernatestudy.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Sub implements Serializable {
    @Id
    @GeneratedValue
    @Getter Long id;
    
    @Getter @Setter private String name;

    public Sub(String name) {
        this.name = name;
    }
}
