/**
 * 
 */
package dev.fabiuscaesar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.domain.Marca;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 2 de set. de 2025
 */

public class MarcaTest {
	
	private EntityManager em;
	
	@Before
	public void setUp() {
		em = JPAUtil.getEntityManagerFactory().createEntityManager();
	}
	
	@After
	public void tearDown() {
		if (em != null && em.isOpen()) em.close();
	}
	
	@Test
	public void devePersistirEMaisTardeLerMarca() {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// arrange + act
		Marca m = new Marca();
		m.setNome("Fiat");
		em.persist(m);		
		
        tx.commit();

        Long idGerado = m.getId();
        assertNotNull(idGerado);
        
        // garante que o próximo acesso venha do banco e não do cache do EM
        em.clear();
        
        Marca reloaded = em.find(Marca.class, idGerado);
        
        assertNotNull(reloaded);
        assertEquals("Fiat", reloaded.getNome());
	}

}
