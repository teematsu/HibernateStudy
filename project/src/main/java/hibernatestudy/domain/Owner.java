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
public class Owner {
    @Id @GeneratedValue
    @Getter private int id;
    
    @Getter @Setter private String name;
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST) // cascadeから意図的にREMOVEを除外
    @JoinColumn(name = "owner_id") // nullable = falseでも、親無しのOwnedが存在できない点を除いて、問題なく動作する。
    @Getter private List<Owned> children = new ArrayList<>();
    
    
    protected Owner() {}
    
    public Owner(String name) {
        this.name = name;
    }
}
