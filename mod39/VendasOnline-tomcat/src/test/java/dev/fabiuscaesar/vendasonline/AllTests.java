/**
 * 
 */
package dev.fabiuscaesar.vendasonline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dev.fabiuscaesar.vendasonline.dao.ClienteDAOTest;
import dev.fabiuscaesar.vendasonline.dao.EstoqueDAOTest;
import dev.fabiuscaesar.vendasonline.dao.ProdutoDAOTest;
import dev.fabiuscaesar.vendasonline.dao.VendaDAOTest;
import dev.fabiuscaesar.vendasonline.service.ClienteServiceTest;
import dev.fabiuscaesar.vendasonline.service.EstoqueServiceTest;
import dev.fabiuscaesar.vendasonline.service.ProdutoServiceTest;
import dev.fabiuscaesar.vendasonline.service.VendaServiceTest;

/**
 * @author FabiusCaesar
 * @date 14 de out. de 2025
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ClienteDAOTest.class,
	EstoqueDAOTest.class,
    ProdutoDAOTest.class,
    VendaDAOTest.class,
    ClienteServiceTest.class,
    EstoqueServiceTest.class,
    ProdutoServiceTest.class,
    VendaServiceTest.class
})
public class AllTests {

}
