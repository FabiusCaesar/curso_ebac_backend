/**
 * 
 */
package dev.fabiuscaesar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
 * @date 5 de set. de 2025
 */

public class MarcaRelacionamentoTest {
	
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
	public void deveListarCarrosDeUmaMarca() {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// arrange marca
		Marca m = new Marca();
		m.setNome("Benz & Cie");
		em.persist(m);
				
		// arrange carro 1
		Carro c1 = new Carro();
		c1.setMarca(m);
		c1.setModelo("Benz Patent-Motorwagen");
		c1.setAno(1886);
		em.persist(c1);
		
		// arrange carro 2
		Carro c2 = new Carro();
		c2.setMarca(m);
		c2.setModelo("Benz Viktoria");
		c2.setAno(1892);
		em.persist(c2);
		
        tx.commit();
        
        Long idMarcaGerada = m.getId();
        assertNotNull(idMarcaGerada);

        Long idCarroGerado1 = c1.getId();
        assertNotNull(idCarroGerado1);
        
        Long idCarroGerado2 = c2.getId();
        assertNotNull(idCarroGerado2);
        
        // garante que o próximo acesso venha do banco e não do cache do EM
        em.clear();
        
        Marca marcaReloaded = em.find(Marca.class, idMarcaGerada);
        
        assertNotNull(marcaReloaded);
        assertNotNull(marcaReloaded.getCarros());
        assertEquals(2, marcaReloaded.getCarros().size());
        
        boolean temCarro1 = marcaReloaded.getCarros().stream().anyMatch(x -> "Benz Patent-Motorwagen".equals(x.getModelo()));
        boolean temCarro2 = marcaReloaded.getCarros().stream().anyMatch(x -> "Benz Viktoria".equals(x.getModelo()));
        assertTrue(temCarro1 && temCarro2);
	}
}
