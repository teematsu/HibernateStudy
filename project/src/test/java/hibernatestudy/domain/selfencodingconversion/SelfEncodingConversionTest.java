package hibernatestudy.domain.selfencodingconversion;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml", "classpath:/testApplicationContext.xml"})
@Transactional
public class SelfEncodingConversionTest {
    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private BookRepo bookRepo;
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void エンティティ読み出し() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO book(id, title, price) VALUES(1, 'あいうえお', 1000)");

        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();
        
        Book actual = bookRepo.findById(1L);
        assertThat(actual.getTitle(), is("あいうえお"));
        assertThat(actual.getPrice(), is(1000));
    }
    
}
