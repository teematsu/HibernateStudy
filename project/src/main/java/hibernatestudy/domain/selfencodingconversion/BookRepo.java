package hibernatestudy.domain.selfencodingconversion;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class BookRepo {
    @PersistenceContext
    private EntityManager em;
    
//    public void add(Book book) {
//        em.persist(book);
//    }
    
    public Book findById(Long id) {
        Query query =  em.createNativeQuery("SELECT *, encode(convert_to(title, 'UTF-8'), 'hex') AS hex_title FROM book WHERE id=:id", "BookMapping");
        query.setParameter("id", id);
        Book book = (Book)query.getSingleResult();
        return book;
    }
}
