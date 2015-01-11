package hibernatestudy.domain;

import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
public class EntityLifecycleTest {
    @PersistenceContext
    private EntityManager em;

    private List<FooEntity> findFooByName(String name) {
        TypedQuery<FooEntity> query = em.createQuery("SELECT e FROM FooEntity e WHERE e.name = :name", FooEntity.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    private List<BarEntity> findBarByName(String name) {
        TypedQuery<BarEntity> query = em.createQuery("SELECT e FROM BarEntity e WHERE e.name = :name", BarEntity.class);
        query.setParameter("name", name);
        return query.getResultList();
    }
    
    private FooEntity makeDetachedFoo(String name) {
        FooEntity foo = new FooEntity(name);
        em.persist(foo);
        em.flush();
        em.detach(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
        return foo;
    }

    private BarEntity makeDetachedBar(Integer id, String name) {
        BarEntity bar = new BarEntity(id, name);
        em.persist(bar);
        em.flush();
        em.detach(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));
        return bar;
    }
    
    /*
     * @GeneratedValueなidを持つインスタンスを新規作成し、idをセットしないまま、操作を適用
     */
    
    @Test
    public void generatedValueのidでid未設定のインスタンスをpersist() {
        FooEntity foo = new FooEntity("テスト");
        em.persist(foo);
        em.flush();

        List<FooEntity> foundEntities = findFooByName("テスト");
        assertThat(foundEntities.size(), is(1));
    }
    
    @Test
    public void generatedValueのidでid未設定のインスタンスをremove() {
        FooEntity foo = new FooEntity("テスト");
        em.remove(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
        
        // 例外出ずに終了
    }

    @Test
    public void generatedValueのidでid未設定のインスタンスをdetach() {
        FooEntity foo = new FooEntity("テスト");
        em.detach(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
        
        // 例外出ずに終了
    }

    @Test
    public void generatedValueのidでid未設定のインスタンスをmerge() {
        FooEntity foo = new FooEntity("テスト");
        FooEntity mergedFoo = em.merge(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
        assertThat(em.contains(mergedFoo), is(true));
        
        List<FooEntity> foundEntities = findFooByName("テスト");
        assertThat(foundEntities.size(), is(1));
    }
    
    /*
     * @GeneratedValueなidを持つインスタンスを新規作成し、idをセットした後に、操作を適用
     */

    @Test
    public void generatedValueのidでid設定済みのインスタンスをpersist() {
        FooEntity foo = new FooEntity("テスト");
        foo.setId(9823);
        try {
            em.persist(foo);              
                // 以下のメッセージの例外が出る。detachedなエンティティとみなされているようだ。
                // javax.persistence.PersistenceException: org.hibernate.PersistentObjectException: detached entity passed to persist: hibernatestudy.domain.FooEntity
        } catch (PersistenceException e) {
            return;
        }
        fail();
    }

    @Test
    public void generatedValueのidでid設定済みのインスタンスをremove() {
        FooEntity foo = new FooEntity("テスト");
        foo.setId(9823);
        try {
            em.remove(foo);              
                // 以下のメッセージの例外が出る。detachedなエンティティとみなされているようだ。
                // java.lang.IllegalArgumentException: Removing a detached instance hibernatestudy.domain.FooEntity#9823
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }
    
    @Test
    public void generatedValueのidでid設定済みのインスタンスをdetach() {
        FooEntity foo = new FooEntity("テスト");
        foo.setId(9823);
        em.detach(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
    }
    
    @Test
    public void generatedValueのidでid設定済みのインスタンスをmerge() {
        FooEntity foo = new FooEntity("テスト");
        foo.setId(9823);

        FooEntity mergedFoo = em.merge(foo);
        em.flush();
        assertThat(em.contains(foo), is(false));
        assertThat(em.contains(mergedFoo), is(true));
        assertThat(mergedFoo.getId(), is(not(9823))); 
            // 新しいエンティティが追加されるが、 @GeneratedValueなので、idは書き換えられる。
        
        List<FooEntity> foundEntities = findFooByName("テスト");
        assertThat(foundEntities.size(), is(1));
    }

    /*
     * @GeneratedValueなidを持つdetachedなインスタンスに、操作を適用
     */

    @Test
    public void generatedValueのidでdetachedなインスタンスをpersist() {
        FooEntity foo = makeDetachedFoo("テスト");
        try {
            em.persist(foo);
        } catch(PersistenceException e) {
            // javax.persistence.PersistenceException: org.hibernate.PersistentObjectException: detached entity passed to persist: hibernatestudy.domain.FooEntity
            return;
        }
        fail();
    }
    
    @Test
    public void generatedValueのidでdetachedなインスタンスをremove() {
        FooEntity foo = makeDetachedFoo("テスト");
        try {
            em.remove(foo);
        } catch(IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Removing a detached instance hibernatestudy.domain.FooEntity#1
            return;
        }
        fail();
    }
    
    @Test
    public void generatedValueのidでdetachedなインスタンスをdetach() {
        FooEntity foo = makeDetachedFoo("テスト");
        em.detach(foo);
        // 例外発生せず終了
    }
    
    @Test
    public void generatedValueのidでdetachedなインスタンスをmerge() {
        FooEntity foo = makeDetachedFoo("テスト");
        FooEntity mergedFoo = em.merge(foo);
        assertThat(em.contains(foo), is(false));
        assertThat(em.contains(mergedFoo), is(true));
    }
    
    /*
     * @GeneratedValueを指定しないidを持つインスタンスを新規に作成し、idをセットしないまま、操作を適用
     */
    
    @Test
    public void 非generatedValueのidでid未設定のインスタンスをpersist() {
        BarEntity bar = new BarEntity(null, "東京都千代田区一ツ橋");
        try {
            em.persist(bar);
            // javax.persistence.PersistenceException: org.hibernate.id.IdentifierGenerationException: ids for this class must be manually assigned before calling save(): hibernatestudy.domain.Area
        } catch (PersistenceException e) {
            return;
        }
        fail();
    }

    @Test
    public void 非generatedValueのidでid未設定のインスタンスをremove() {
        BarEntity bar = new BarEntity(null, "東京都千代田区一ツ橋");
        em.remove(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));
        
        // 例外出ずに終了
    }
    
    @Test
    public void 非generatedValueのidでid未設定のインスタンスをdetach() {
        BarEntity bar = new BarEntity(null, "東京都千代田区一ツ橋");
        em.detach(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));
        
        // 例外出ずに終了
    }
    
    @Test
    public void 非generatedValueのidでid未設定のインスタンスをmerge() {
        BarEntity bar = new BarEntity(null, "東京都千代田区一ツ橋");
        try {
            em.merge(bar);
        } catch (PersistenceException e) {
            // javax.persistence.PersistenceException: org.hibernate.id.IdentifierGenerationException: ids for this class must be manually assigned before calling save(): hibernatestudy.domain.BarEntity
            //e.printStackTrace();
            return;
        }
        fail();
    }
    
    /*
     * @GeneratedValueを指定しないidを持つインスタンスを新規に作成し、idをセット後に、操作を適用
     */    
    
    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをpersist() {
        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        em.persist(bar);
        
        List<BarEntity> foundEntities = findBarByName("東京都千代田区一ツ橋");
        assertThat(foundEntities.size(), is(1));
    }

    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをpersist_同じidのentityが存在() {
        BarEntity preExistentBar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        em.persist(preExistentBar);
        em.flush();

        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        try {
            em.persist(bar);
        } catch(EntityExistsException e) {
            // javax.persistence.EntityExistsException: A different object with the same identifier value was already associated with the session : [hibernatestudy.domain.BarEntity#1010003]
            return;
        }
        fail();
        
    }
    
    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをremove() {
        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        em.remove(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));

        // 例外出ずに終了
    }
    
    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをremove_同じidのエンティティが存在() {
        BarEntity preExistentBar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        em.persist(preExistentBar);
        em.flush();

        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        try {
            em.remove(bar);
        } catch(IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Removing a detached instance hibernatestudy.domain.BarEntity#1010003
            return;
        }
        fail();
    }
    
    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをdetach() {
        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        em.detach(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));
        
        // 例外出ずに終了
    }
    
    @Test
    public void 非generatedValueのidでid設定済みのインスタンスをmerge() {
        BarEntity bar = new BarEntity(1010003, "東京都千代田区一ツ橋");
        BarEntity mergedBar = em.merge(bar);
        em.flush();
        
        assertThat(em.contains(bar), is(false));
        assertThat(em.contains(mergedBar), is(true));
        assertThat(mergedBar.getId(), is(1010003)); 
        
        List<BarEntity> foundEntities = findBarByName("東京都千代田区一ツ橋");
        assertThat(foundEntities.size(), is(1));
    }
    

    /*
     * @GeneratedValueを指定しないidを持つdetachedなインスタンスに、操作を適用
     */    
    
    @Test
    public void 非generatedValueのidでdetachedなインスタンスをpersist() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");

        em.persist(bar);
        assertThat(em.contains(bar), is(true));
            // persistまでは動いてしまう。
        try {
            em.flush();
        } catch (PersistenceException e) {
            // ID重複でDBの例外
            // javax.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException: could not execute statement
            e.printStackTrace();
            return;
        }
        fail();
    }

    
    @Test
    public void 非generatedValueのidでdetachedなインスタンスをpersist_クエリをはさむ() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");

        BarEntity found = em.find(BarEntity.class, 1010003);
        assertThat(found, is(not(nullValue())));

        try {
            em.persist(bar);
        } catch(PersistenceException e) {
            // javax.persistence.EntityExistsException: A different object with the same identifier value was already associated with the session : [hibernatestudy.domain.Area#1010003]
            //e.printStackTrace();
            return;
        }
        fail();
    }

    @Test
    public void 非generatedValueのidでdetachedなインスタンスをpersist_ただし当該entityは削除済み() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");

        Query query = em.createQuery("DELETE BarEntity e WHERE id=:id");
        query.setParameter("id", bar.getId());
        query.executeUpdate();

        em.persist(bar);
        assertThat(em.contains(bar), is(true));
        List<BarEntity> foundEntities = findBarByName("東京都千代田区一ツ橋");
        assertThat(foundEntities.size(), is(1));
    }
    
    @Test
    public void 非generatedValueのidでdetachedなインスタンスをremove() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");

        try {
            em.remove(bar);
        } catch (IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Removing a detached instance hibernatestudy.domain.BarEntity#1010003
            e.printStackTrace();
            return;
        }
        fail();
    }

    @Test
    public void 非generatedValueのidでdetachedなインスタンスをremove_ただし当該entityは削除済み() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");
        
        Query query = em.createQuery("DELETE BarEntity e WHERE id=:id");
        query.setParameter("id", bar.getId());
        query.executeUpdate();

        em.remove(bar);
        
        // 例外発生せず完了
    }

    @Test
    public void 非generatedValueのidでdetachedなインスタンスをdetach() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");
        em.detach(bar);
        em.flush();
        assertThat(em.contains(bar), is(false));
        
        // 例外出ずに終了
    }
    
    @Test
    public void 非generatedValueのidでdetachedなインスタンスをmerge() {
        BarEntity bar = makeDetachedBar(1010003, "東京都千代田区一ツ橋");
        BarEntity mergedBar = em.merge(bar);
        assertThat(em.contains(bar), is(false));
        assertThat(em.contains(mergedBar), is(true));
    }
}
