package hibernatestudy.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Main implements Serializable {
    @Id
    @GeneratedValue
    @Getter private Long id;
    
    @Getter @Setter private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "main_id")
    @Getter @Setter private Sub sub;

    public Main(String name) {
        this.name = name;
    }
    
}
