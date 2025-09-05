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

import dev.fabiuscaesar.domain.Carro;
import dev.fabiuscaesar.domain.Marca;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 3 de set. de 2025
 */

public class CarroTest {
	
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
	public void devePersistirCarroComMarca() {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// arrange marca
		Marca m = new Marca();
		m.setNome("Benz & Cie");
		em.persist(m);
		
		// arrange carro
		Carro c = new Carro();
		c.setMarca(m); // Many-to-One: Carro.marca é um objeto Marca
		c.setModelo("Benz Patent-Motorwagen");
		c.setAno(1886);
		em.persist(c);
		
        tx.commit();

        Long idMarcaGerada = m.getId();
        assertNotNull(idMarcaGerada);
        
        Long idCarroGerado = c.getId();
        assertNotNull(idCarroGerado);
        
        // garante que o próximo acesso venha do banco e não do cache do EM
        em.clear();
        
        Marca marcaReloaded = em.find(Marca.class, idMarcaGerada);
        Carro carroReloaded = em.find(Carro.class, idCarroGerado);
        
        assertNotNull(marcaReloaded);
        assertEquals("Benz & Cie", marcaReloaded.getNome());
        
        assertNotNull(carroReloaded);
        assertEquals("Benz Patent-Motorwagen", carroReloaded.getModelo());
        assertEquals(m.getNome(), carroReloaded.getMarca().getNome());
        assertEquals(Integer.valueOf(1886), carroReloaded.getAno());
	}
}
