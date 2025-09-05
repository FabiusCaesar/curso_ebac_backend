/**
 * 
 */
package dev.fabiuscaesar.infra;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author FabiusCaesar
 * @date 2 de set. de 2025
 */

public class JPAUtil {
	
	private static final EntityManagerFactory EMF = 
            Persistence.createEntityManagerFactory("carrosPU");
	
	private JPAUtil() {}
	
	public static EntityManagerFactory getEntityManagerFactory() {
		return EMF;
	}
	
	public static void shutDown() {
		if (EMF.isOpen()) {
			EMF.close();
		}
	}

}
