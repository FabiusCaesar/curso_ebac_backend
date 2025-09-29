/**
 * 
 */
package br.com.rpires.infra;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author FabiusCaesar
 * @date 13 de set. de 2025
 */

public final class JPAUtil {
    private static final String PU_NAME = "exemploPU";
    private static final EntityManagerFactory EMF = buildEMF();
    private JPAUtil() { }

    private static EntityManagerFactory buildEMF() {
        try {
            return Persistence.createEntityManagerFactory(PU_NAME);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Falha ao inicializar JPA (PU=" + PU_NAME + "): " + e.getMessage());
        }
    }
    public static EntityManager getEntityManager() { return EMF.createEntityManager(); }
    public static void closeFactory() { if (EMF.isOpen()) EMF.close(); }
}
