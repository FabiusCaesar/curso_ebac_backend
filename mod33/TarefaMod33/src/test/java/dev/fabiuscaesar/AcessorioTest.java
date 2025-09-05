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

import dev.fabiuscaesar.domain.Acessorio;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 3 de set. de 2025
 */

public class AcessorioTest {
	
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
	public void devePersistirELerAcessorio() {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// arrange
		Acessorio a = new Acessorio();
		a.setNome("Banco em couro");
		a.setDescricao("Revestimento do banco feito em couro legítimo");
		em.persist(a);
				
        tx.commit();

        Long idAcessorioGerado = a.getId();
        assertNotNull(idAcessorioGerado);
        
        // garante que o próximo acesso venha do banco e não do cache do EM
        em.clear();
        
        Acessorio acessorioReloaded = em.find(Acessorio.class, idAcessorioGerado);
        
        assertNotNull(acessorioReloaded);
        assertEquals("Banco em couro", acessorioReloaded.getNome());
        assertEquals("Revestimento do banco feito em couro legítimo", acessorioReloaded.getDescricao());
	}
}
