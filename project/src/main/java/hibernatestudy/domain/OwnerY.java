package hibernatestudy.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
public class OwnerY {
    @Id @GeneratedValue
    @Getter private int id;
    
    @Getter @Setter private String name;
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ownery_id")
    @Getter private List<OwnedByXOrY> children = new ArrayList<>();
    
    
    protected OwnerY() {}
    
    public OwnerY(String name) {
        this.name = name;
    }
}
