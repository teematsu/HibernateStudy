package hibernatestudy.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class BarEntity {
    @Id
    @Getter @Setter private Integer id;
    
    @Getter @Setter private String name;
    
    protected BarEntity(){}
    
    public BarEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
