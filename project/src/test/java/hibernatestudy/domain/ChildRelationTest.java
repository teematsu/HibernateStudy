package hibernatestudy.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml", "classpath:/testApplicationContext.xml"})
@Transactional
public class ChildRelationTest {
    @PersistenceContext
    private EntityManager em;
    

    @Test
    public void 親をpersistすればクエリの結果に親は反映される() {
        Parent parent = new Parent("たろう");

        em.persist(parent);
        
        TypedQuery<Parent> query = em.createQuery("SELECT p FROM Parent p WHERE p.name=:name", Parent.class);
        query.setParameter("name", "たろう");
        Parent actual = query.getSingleResult();

        assertThat(actual.getName(), is("たろう"));
    }
    
    @Test
    public void 親をpersistすれば子もpersistされてクエリの結果に子も反映される() {
        Parent parent = new Parent("たろう");
        Child child = new Child("じろう", 10);
        parent.getChildren().add(child);

        em.persist(parent);
        
        assertThat(em.contains(child), is(true)); // childはmanged状態
        
        TypedQuery<Parent> query = em.createQuery("SELECT p FROM Parent p JOIN p.children c WHERE c.name=:name", Parent.class);
        query.setParameter("name", "じろう");
        Parent actual = query.getSingleResult();

        assertThat(actual.getName(), is("たろう"));
        
    }
    
    @Test
    public void managedな親にnewな子を追加し親子に関するクエリ結果に反映されている() {
        Parent parent = new Parent("たろう");
        Child child = new Child("じろう", 10);

        em.persist(parent);

        assertThat(em.contains(parent), is(true)); // parentはmanaged状態
        
        
        parent.getChildren().add(child);
        
        assertThat(em.contains(child), is(false)); // childはmanged状態にはなっていない
        

        TypedQuery<Parent> query = em.createQuery("SELECT p FROM Parent p JOIN p.children c WHERE c.name=:name", Parent.class);
        query.setParameter("name", "じろう");
        Parent actual = query.getSingleResult();

        assertThat(actual.getName(), is("たろう"));
        
        assertThat(em.contains(child), is(true)); // childはmanged状態
    }

    @Test
    public void managedな親にnewな子を追加し子に関するクエリ結果に反映されている() {
        Parent parent = new Parent("たろう");
        Child child = new Child("じろう", 10);

        em.persist(parent);

        assertThat(em.contains(parent), is(true)); // parentはmanaged状態
        
        parent.getChildren().add(child);
        
        assertThat(em.contains(child), is(false)); // childはmanged状態にはなっていない
        
        TypedQuery<Child> query0 = em.createQuery("SELECT c FROM Child c WHERE c.name=:name", Child.class);
        query0.setParameter("name", "じろう");
        Child actualChild = query0.getSingleResult();

        assertThat(actualChild.getName(), is("じろう"));
        
        assertThat(em.contains(child), is(true)); // childはmanged状態
    }
    
}
