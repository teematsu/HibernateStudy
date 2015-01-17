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
    public void newな親_newな子_親をpersist() {
        Parent parent = new Parent("たろう");
        Child child = new Child("じろう", 10);
        parent.getChildren().add(child);

        // newな親をpersist
        em.persist(parent);

        // 子もmanagedになる。
        assertThat(em.contains(child), is(true)); // childはmanged状態

        // その子は、子に関するクエリの結果に反映される
        TypedQuery<Child> query0 = em.createQuery("SELECT c FROM Child c WHERE c.name=:name", Child.class);
        query0.setParameter("name", "じろう");
        Child actualChild = query0.getSingleResult();

        assertThat(actualChild.getName(), is("じろう"));
        
    }
    
    @Test
    public void newな親_managedな子_親をpersist() {
        Child child = new Child("じろう", 10);
        // 子をpersist
        em.persist(child);
        // 子はmanagedになっている。
        assertThat(em.contains(child), is(true));

        // newな親にmanagedな子を追加
        Parent parent = new Parent("たろう");
        parent.getChildren().add(child);

        // newな親をpersist
        em.persist(parent);

        // その子は、親子関係に関するクエリの結果に反映される。
        TypedQuery<Parent> query = em.createQuery("SELECT p FROM Parent p JOIN p.children c WHERE c.name=:name", Parent.class);
        query.setParameter("name", "じろう");
        Parent actual = query.getSingleResult();
        assertThat(actual.getName(), is("たろう"));
    }

    @Test
    public void newな親_removedな子_親をpersist() {
        Child child = new Child("じろう", 10);
        // 子をpersist
        em.persist(child);
        // 子はmanagedになっている。
        assertThat(em.contains(child), is(true));
        
        int idBeforeRemoved = child.getId();
        
        // 子をあらかじめremove
        em.remove(child);
        // 子はもうmanagedではない。
        assertThat(em.contains(child), is(false));

        //em.flush(); // flushすると、子はdetachedになり、次のpersistで例外が出る。
        
        // newな親にremovedな子を追加
        Parent parent = new Parent("たろう");
        parent.getChildren().add(child);

        // newな親をpersist
        em.persist(parent);

        // 子はmanagedになっている。
        assertThat(em.contains(child), is(true));
        
        // その子は、親子関係に関するクエリの結果に反映される。
        TypedQuery<Parent> query = em.createQuery("SELECT p FROM Parent p JOIN p.children c WHERE c.name=:name", Parent.class);
        query.setParameter("name", "じろう");
        Parent actual = query.getSingleResult();
        assertThat(actual.getName(), is("たろう"));

        // 子のIDは、削除前と同じである。
        assertThat(child.getId(), is(idBeforeRemoved));
    }
    
    
    @Test
    public void managedな親_newな子を追加() {
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
    
    @Test
    public void managedな親_newな子を追加_親子joinしたクエリ() {
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
}
