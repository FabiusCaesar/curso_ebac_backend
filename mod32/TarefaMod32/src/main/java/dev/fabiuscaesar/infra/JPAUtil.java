/**
 * 
 */
package dev.fabiuscaesar.infra;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author FabiusCaesar
 * @date 27 de ago. de 2025
 */

public final class JPAUtil {
	
	private static final EntityManagerFactory EMF = 
            Persistence.createEntityManagerFactory("ExemploJPA");
	
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
