package hibernatestudy.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
public class OrphanRemovalTest {
    @PersistenceContext
    private EntityManager em;

    private Owner makeOwnerHavingChild() {
        Owner owner = new Owner("親");
        Owned owned = new Owned("子1");
        owner.getChildren().add(owned);
        em.persist(owner);
        em.flush();
        return owner;
    }
    
    @Test
    public void 子は単独で存在できる() {
        Owned owned = new Owned("子1");
        em.persist(owned);
        em.flush();
        List resultList = em.createQuery("SELECT e FROM Owned e WHERE e.name='子1'").getResultList();
        assertThat(resultList.size(), is(1));
    }

    @Test
    public void flushすると反映される() {
        Owner owner = makeOwnerHavingChild();
        Owned owned = owner.getChildren().get(0);
        
        assertThat(em.contains(owned), is(true));

        owner.getChildren().remove(owned);

        // 親から削除しただけでは、まだmanagedのままである。
        assertThat(em.contains(owned), is(true));
        
        em.flush();

        assertThat(em.contains(owned), is(false));
        
        List resultList = em.createQuery("SELECT e FROM Owned e WHERE e.name='子1'").getResultList();
        assertThat(resultList.size(), is(0));
    }

    @Test
    public void クエリの実行でも反映される() {
        Owner owner = makeOwnerHavingChild();
        Owned owned = owner.getChildren().get(0);
        
        assertThat(em.contains(owned), is(true));

        owner.getChildren().remove(owned);

        // 親から削除しただけでは、まだmanagedのままである。
        assertThat(em.contains(owned), is(true));
        
        List resultList = em.createQuery("SELECT e FROM Owned e WHERE e.name='子1'").getResultList();
        assertThat(resultList.size(), is(0));

        assertThat(em.contains(owned), is(false));
        
    }
    
    @Test
    public void 親を削除すると子も削除される() {
        Owner owner = makeOwnerHavingChild();
        Owned owned = owner.getChildren().get(0);

        em.remove(owner);

        // 親を削除すると、子もはやmanagedではない。
        assertThat(em.contains(owned), is(false));

        List resultList = em.createQuery("SELECT e FROM Owned e WHERE e.name='子1'").getResultList();
        assertThat(resultList.size(), is(0));
    }

    @Test
    public void 親のすげかえ() {
        Owner owner = makeOwnerHavingChild();
        Owned owned = owner.getChildren().get(0);
        
        Owner owner2 = new Owner("親2");
        em.persist(owner2);
        
        owner.getChildren().remove(owned);
        owner2.getChildren().add(owned);

        em.flush();
        
        List resultList = em.createQuery("SELECT e FROM Owner e JOIN e.children e2 WHERE e2.name='子1'").getResultList();
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0), is((Object)owner2));
        
        // JPA仕様では、可搬性上、アプリがしてはいけないことと説明されているが、Hibernateで動作している。
        // (JPA 2.0 Spec  2.9 Entity Relationships)
    }
    
    @Test
    public void 親のすげかえの途中でflush() {
        Owner owner = makeOwnerHavingChild();
        Owned owned = owner.getChildren().get(0);
        
        Owner owner2 = new Owner("親2");
        em.persist(owner2);
        
        owner.getChildren().remove(owned);
        em.flush();
        
        owner2.getChildren().add(owned);
        try {
            em.flush();
        } catch (PersistenceException e) {
            // javax.persistence.PersistenceException: org.hibernate.PersistentObjectException: detached entity passed to persist: hibernatestudy.domain.Owned
            return;
        }
        fail();
        
        // JPA仕様では、可搬性上、アプリがしてはいけないことと説明されているが、
        // (JPA 2.0 Spec  2.9 Entity Relationships)
        // Hibernateでは flushが間に入ると、このとおり例外となる
    }
    
    @Test
    public void 親2つから参照されているときに片方から削除する() {
        // 関連の情報はOwnedのほうに持たせているので、そもそも親2つからの参照は
        // テーブル上で表現できない。
        // Hibernateの動作上は、owner2からownedへの参照を同期した段階で
        // ownedのupdateが走り、テーブル上は owner2への関連が作られ、owner1 との関係が切れる。
        // Javaオブジェクト上の関連とテーブル上の関連に不整合が出る。
        // owner1からownedへの参照を削除したときには、owner2とownedの関係には影響を与えない。
        
        Owner owner1 = makeOwnerHavingChild();
        //owner1.getChildren().add(new Owned("子2"));
        
        Owned owned = owner1.getChildren().get(0);
        
        Owner owner2 = new Owner("親2");
        owner2.getChildren().add(owned);
        em.persist(owner2);
        em.flush();

        owner1.getChildren().remove(owned);
        em.flush();

        // 親を確認するとowner2
        List resultList2 = em.createQuery("SELECT e FROM Owner e JOIN e.children e2 WHERE e2.name='子1'").getResultList();
        assertThat(resultList2.size(), is(1));
        assertThat(resultList2.get(0), is((Object)owner2));
    }
    // nullable問題
}
