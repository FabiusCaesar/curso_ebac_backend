/**
 * 
 */
package br.com.rpires.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.rpires.dao.generic.GenericJpaDAO;
import br.com.rpires.domain.Cliente;
import br.com.rpires.domain.Produto;
import br.com.rpires.domain.ProdutoQuantidade;
import br.com.rpires.domain.Venda;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;
import br.com.rpires.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 17 de set. de 2025
 */

public class VendaDAO extends GenericJpaDAO<Venda, String> implements IVendaDAO {

    public VendaDAO() {
        // campo-chave de negócio é "codigo"
        super(Venda.class, "codigo");
    }

    // Mantém o comportamento antigo: não permitir excluir venda por código
    @Override
    public void excluir(String valor) {
        throw new UnsupportedOperationException("OPERAÇÃO NÃO PERMITIDA");
    }

    @Override
    public Boolean cadastrar(Venda entity) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Anexar (attach) cliente já existente pelo ID (evita INSERT indevido)
            if (entity.getCliente() != null && entity.getCliente().getId() != null) {
                entity.setCliente(em.getReference(Cliente.class, entity.getCliente().getId()));
            }

            // Garantir o lado dono e anexar produto de cada item
            if (entity.getProdutos() != null) {
                for (ProdutoQuantidade pq : entity.getProdutos()) {
                    pq.setVenda(entity); // backref do lado dono
                    if (pq.getProduto() != null && pq.getProduto().getId() != null) {
                        pq.setProduto(em.getReference(Produto.class, pq.getProduto().getId()));
                    }
                }
            }

            // Persiste e força o flush para disparar a UK agora
            em.persist(entity);
            em.flush();

            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();

            // usa o helper herdado do GenericJpaDAO
            if (super.isUniqueViolation(e)) {
                throw new DAOException("Código de venda já existente.", e);
            }
            throw new DAOException("Erro ao cadastrar Venda.", e);

        } finally {
            em.close();
        }
    }

    // Carrega venda + cliente + itens + produto
    @Override
    public Venda consultar(String codigo)
            throws MaisDeUmRegistroException, TableException, DAOException {

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Venda> q = em.createQuery(
                "select distinct v from Venda v " +
                " join fetch v.cliente " +
                " left join fetch v.produtos pq " +
                " left join fetch pq.produto " +
                "where v.codigo = :codigo", Venda.class);
            q.setParameter("codigo", codigo);
            List<Venda> res = q.getResultList();
            if (res.size() > 1) {
                throw new MaisDeUmRegistroException("Mais de uma venda com o código: " + codigo);
            }
            return res.isEmpty() ? null : res.get(0);
        } catch (MaisDeUmRegistroException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Erro ao consultar Venda por código", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Collection<Venda> buscarTodos() throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "select distinct v from Venda v " +
                " join fetch v.cliente " +
                " left join fetch v.produtos pq " +
                " left join fetch pq.produto", Venda.class)
                .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar todas as Vendas", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void finalizarVenda(Venda venda)
            throws TipoChaveNaoEncontradaException, DAOException {

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Venda v = em.find(Venda.class, venda.getId());
            v.setStatus(Venda.Status.CONCLUIDA);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DAOException("Erro ao finalizar venda", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void cancelarVenda(Venda venda)
            throws TipoChaveNaoEncontradaException, DAOException {

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Venda v = em.find(Venda.class, venda.getId());
            v.setStatus(Venda.Status.CANCELADA);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DAOException("Erro ao cancelar venda", e);
        } finally {
            em.close();
        }
    }
}