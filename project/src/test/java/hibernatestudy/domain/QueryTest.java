package hibernatestudy.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
public class QueryTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void newなentityをパラメータとしてクエリ() {
        Child child = new Child("ひろし", 10);
        TypedQuery<Child> query = em.createQuery("SELECT c FROM Child c WHERE c = :child", Child.class);
        query.setParameter("child", child);
        
        query.getResultList();
    }

    @Test
    public void newで自前設定のidを持つentityをパラメータとしてクエリ() {
        Child child = new Child("ひろし", 10);
        child.setId(123);
        TypedQuery<Child> query = em.createQuery("SELECT c FROM Child c WHERE c = :child", Child.class);
        query.setParameter("child", child);
        
        query.getResultList();
    }

    @Test
    public void removedなentityをパラメータとしてクエリ() {
        Child child = new Child("ひろし", 10);
        em.persist(child);
        em.flush();
        em.remove(child);
        
        TypedQuery<Child> query = em.createQuery("SELECT c FROM Child c WHERE c = :child", Child.class);
        query.setParameter("child", child);
        
        List<Child> actual = query.getResultList();
        assertThat(actual.size(), is(0));
    }
}
