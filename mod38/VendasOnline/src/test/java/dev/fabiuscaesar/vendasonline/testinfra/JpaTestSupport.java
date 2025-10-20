/**
 * 
 */
package dev.fabiuscaesar.vendasonline.testinfra;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

public abstract class JpaTestSupport {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void initEMF() {
        // Usa a PU de testes (H2)
        emf = Persistence.createEntityManagerFactory("test");
    }

    @AfterClass
    public static void closeEMF() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Before
    public void openEM() {
        em = emf.createEntityManager();
    }

    @After
    public void closeEM() {
        if (em != null && em.isOpen()) em.close();
    }
}
