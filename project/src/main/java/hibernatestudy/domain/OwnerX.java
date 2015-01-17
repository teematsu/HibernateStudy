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
public class OwnerX {
    @Id @GeneratedValue
    @Getter private int id;
    
    @Getter @Setter private String name;
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ownerx_id")
    @Getter private List<OwnedByXOrY> children = new ArrayList<>();
    
    
    protected OwnerX() {}
    
    public OwnerX(String name) {
        this.name = name;
    }
}
