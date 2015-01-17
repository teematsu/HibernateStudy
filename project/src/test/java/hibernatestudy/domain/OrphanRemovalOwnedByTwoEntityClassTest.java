package hibernatestudy.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml", "classpath:/testApplicationContext.xml"})
@Transactional
public class OrphanRemovalOwnedByTwoEntityClassTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void test() {
        OwnerX x = new OwnerX("X1");
        OwnerY y = new OwnerY("Y1");
        OwnedByXOrY owned = new OwnedByXOrY("Owned1");
        //OwnedByXOrY owned2 = new OwnedByXOrY("Owned2");
        
        // 2つのentityからそれぞれ関連を持たせる
        x.getChildren().add(owned);
        //x.getChildren().add(owned2);
        y.getChildren().add(owned);
        em.persist(x);
        em.persist(y);
        em.flush();

        // 一方の関連を削除
        x.getChildren().remove(owned);
        em.flush();
        
        // まだownedは残っている。
        List resultList = em.createQuery("SELECT e FROM OwnedByXOrY e WHERE e.name='Owned1'").getResultList();
        assertThat(resultList.size(), is(1));
        
        // もう一方の関連を削除
        y.getChildren().remove(owned);
        em.flush();
        
        // もうownedは削除されている。
        List resultList2 = em.createQuery("SELECT e FROM OwnedByXOrY e WHERE e.name='Owned1'").getResultList();
        assertThat(resultList2.size(), is(0));
    }
}
