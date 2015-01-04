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
import javax.persistence.OrderBy;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Parent {
    @Id @GeneratedValue
    @Getter private int id;
    
    @Getter @Setter private String name;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @OrderBy("age ASC")
    @Getter private List<Child> children = new ArrayList<>();
    
    
    protected Parent() {}
    
    public Parent(String name) {
        this.name = name;
    }
}
