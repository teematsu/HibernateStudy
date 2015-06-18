package hibernatestudy.domain.selfencodingconversion;

import java.io.UnsupportedEncodingException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;
import lombok.Getter;
import lombok.Setter;

@SqlResultSetMapping(name="BookMapping", 
  entities={ 
    @EntityResult(entityClass=Book.class, fields = {
        @FieldResult(name = "id", column = "id"),
        @FieldResult(name = "price", column = "price"),
        @FieldResult(name = "hexTitle", column = "hex_title"),
    }),
  }
)

@Entity
public class Book {
    @Id
    @GeneratedValue
    @Getter private Long id;
    
//    @Transient
//    @Getter @Setter private String title;
    @Getter @Setter private int price;

    @Column(name = "title")
    private String hexTitle;
    
    public String getTitle() {
        if (hexTitle == null) {
            return null;
        }
        else {
            try {
                byte[] bytes = DatatypeConverter.parseHexBinary(hexTitle);
                String title = new String(bytes, "UTF-8");
                return title;
            } catch (UnsupportedEncodingException ex) {
                // おきないはず
                throw new RuntimeException(ex);
            }
        }
    }
}
