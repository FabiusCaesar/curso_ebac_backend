package dev.fabiuscaesar.vendasonline.config;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Transactional
@Interceptor
@Priority(Interceptor.Priority.APPLICATION) // ativa também por prioridade (além do beans.xml)
public class TxInterceptor {

    @Inject
    private EntityManager em;

    @AroundInvoke
    public Object manage(InvocationContext ctx) throws Exception {
        EntityTransaction tx = em.getTransaction();
        boolean owner = false;
        try {
            if (!tx.isActive()) {
                tx.begin();
                owner = true;
            }
            Object result = ctx.proceed();
            if (owner) {
                tx.commit();
            }
            return result;
        } catch (Exception e) {
            if (owner && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
