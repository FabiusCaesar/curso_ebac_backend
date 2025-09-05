/**
 * 
 */
package dev.fabiuscaesar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.domain.Acessorio;
import dev.fabiuscaesar.domain.Carro;
import dev.fabiuscaesar.domain.Marca;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 4 de set. de 2025
 */

public class CarroAcessorioTest {
	
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
	public void deveAssociarAcessoriosAoCarro() {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// arrange marca
		Marca m = new Marca();
		m.setNome("Benz & Cie");
		em.persist(m);
		
		// arrange acessorio 1
		Acessorio a1 = new Acessorio();
		a1.setNome("Banco em couro");
		a1.setDescricao("Revestimento do banco feito em couro legítimo");
		em.persist(a1);
		
		// arrange acessorio 2
		Acessorio a2 = new Acessorio();
		a2.setNome("Buzina");
		a2.setDescricao("Buzina a gás");
		em.persist(a2);
				
		// arrange carro
		Carro c = new Carro();
		c.setMarca(m); // Many-to-One: Carro.marca é um objeto Marca
		c.setModelo("Benz Patent-Motorwagen");
		c.setAno(1886);
		Set<Acessorio> accs = new java.util.HashSet<>();
		accs.add(a1);
		accs.add(a2);
		c.setAcessorios(accs);

		em.persist(c);
		
        tx.commit();

        Long idCarroGerado = c.getId();
        assertNotNull(idCarroGerado);
        
        // garante que o próximo acesso venha do banco e não do cache do EM
        em.clear();
        
        Carro carroReloaded = em.find(Carro.class, idCarroGerado);
        
        assertNotNull(carroReloaded);
        assertNotNull(carroReloaded.getAcessorios());
        assertEquals(2, carroReloaded.getAcessorios().size());
        
        boolean temBancoDeCouro = carroReloaded.getAcessorios().stream().anyMatch(x -> "Banco em couro".equals(x.getNome()));
        boolean temBuzina = carroReloaded.getAcessorios().stream().anyMatch(x -> "Buzina".equals(x.getNome()));
        assertTrue(temBancoDeCouro && temBuzina);
	}
}
