package hibernatestudy.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml", "classpath:/testApplicationContext.xml"})
@Transactional
public class OneToOneRelationTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void 関連元new_関連先new_関連元をpersist() {
        Main main = new Main("メイン");
        Sub sub = new Sub("サブ");
        
        main.setSub(sub);
        
        // 操作
        em.persist(main);
        
        // 検証

        // Subもmanagedになる。
        assertThat(em.contains(sub), is(true));
        
        // Mainはmanagedになっている。
        assertThat(em.contains(main), is(true));
        

        // 検索結果にはSubが反映される。
        TypedQuery<Sub> query = em.createQuery("SELECT e FROM Sub e WHERE e.name=:name", Sub.class);
        query.setParameter("name", "サブ");
        List<Sub> found = query.getResultList();
        assertThat(found.size(), is(1));
        
    }


    @Test
    public void 関連元new_関連先managed_関連元をpersist() {
        // managedなSubを作成
        Sub sub = new Sub("サブ");
        em.persist(sub);
        em.flush();
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(true));

        // 
        Main main = new Main("メイン");
        main.setSub(sub);
        
        // 操作
        em.persist(main);

        // Mainはmanagedになっている。
        assertThat(em.contains(main), is(true));

        // 検索結果にはSubとMainの関連が反映される。
        TypedQuery<Main> query = em.createQuery("SELECT e FROM Main e WHERE e.sub.name=:name", Main.class);
        query.setParameter("name", "サブ");
        List<Main> found = query.getResultList();
        assertThat(found.size(), is(1));
    }
    
    @Test
    public void 関連元new_関連先removed_関連元をpersist() {
        // removedなSubを作成
        Sub sub = new Sub("サブ");
        em.persist(sub);
        em.flush();
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(true));
        em.remove(sub);
//        em.flush(); // flushするとsubはdetachedになってしまうようだ。
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(false));
        Long removedSubId = sub.getId();
        
        // 
        Main main = new Main("メイン");
        main.setSub(sub);
        
        // 操作
        em.persist(main);

        // Subもmanagedになっている。
        assertThat(em.contains(sub), is(true));

        // Mainはmanagedになっている。
        assertThat(em.contains(main), is(true));
 
        // 検索結果にはSubとMainの関連が反映される。
        TypedQuery<Main> query = em.createQuery("SELECT e FROM Main e WHERE e.sub.name=:name", Main.class);
        query.setParameter("name", "サブ");
        List<Main> found = query.getResultList();
        assertThat(found.size(), is(1));
        // id はremoveする前と同じ
        assertThat(found.get(0).getSub().getId(), is(removedSubId)); 
    }
    
    @Test
    public void 関連元new_関連先removedかつflush_関連元をpersist() {
        // removedなSubを作成
        Sub sub = new Sub("サブ");
        em.persist(sub);
        em.flush();
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(true));
        em.remove(sub);
        em.flush(); // flushするとsubはdetachedになってしまうようだ。

        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(false));
        
        // 
        Main main = new Main("メイン");
        main.setSub(sub);
        
        // 操作
        try {
            em.persist(main); // subがdetachedとなっているため例外となる。
        } catch (PersistenceException e) {
            return;
        }
        fail();
    }

    @Test
    public void 関連元new_関連先removedかつquery発行_関連元をpersist() {
        // removedなSubを作成
        Sub sub = new Sub("サブ");
        em.persist(sub);
        em.flush();
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(true));
        em.remove(sub);
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(false));

        // クエリを発行
        TypedQuery<Sub> subQuery = em.createQuery("SELECT e FROM Sub e WHERE e.name=:name", Sub.class);
        subQuery.setParameter("name", "サブ");
        subQuery.getResultList();

        
        // 
        Main main = new Main("メイン");
        main.setSub(sub);
        
        // 操作
        try {
            em.persist(main); // subがdetachedとなっているため例外となる。
        } catch (PersistenceException e) {
            return;
        }
        fail();
    }
    
    @Test
    public void 関連元new_関連先detached_関連元をpersist() {
        // detachedなSubを作成
        Sub sub = new Sub("サブ");
        em.persist(sub);
        em.flush();
        em.detach(sub);
        assertThat(sub.getId(), is(not(nullValue())));
        assertThat(em.contains(sub), is(false));

        // 
        Main main = new Main("メイン");
        main.setSub(sub);
        
        // 操作
        try {
            em.persist(main);  // ここで例外でるはず
            fail();
        } catch (Exception e) {
            if (! (e instanceof PersistenceException)) {
                fail();
            }
        }
    }
}
