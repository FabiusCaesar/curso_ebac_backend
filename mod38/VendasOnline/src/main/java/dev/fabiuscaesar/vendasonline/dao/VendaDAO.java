/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import dev.fabiuscaesar.vendasonline.dao.generic.GenericJpaDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.domain.ProdutoQuantidade;
import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

public class VendaDAO extends GenericJpaDAO<Venda, Long> implements IVendaDAO {

    public VendaDAO() {
        super(Venda.class);
    }

    @Override
    public Venda cadastrar(Venda entity) throws DAOException {
        try {
            final EntityManager em = em();

            // anexa cliente já existente (evita INSERT indevido)
            if (entity.getCliente() != null && entity.getCliente().getId() != null) {
                entity.setCliente(em.getReference(Cliente.class, entity.getCliente().getId()));
            }

            // backref + anexa produto de cada item
            if (entity.getProdutos() != null) {
                for (ProdutoQuantidade pq : entity.getProdutos()) {
                    pq.setVenda(entity);
                    Produto p = pq.getProduto();
                    if (p != null && p.getId() != null) {
                        pq.setProduto(em.getReference(Produto.class, p.getId()));
                    }
                }
            }

            em.persist(entity);
            em.flush();
            return entity;

        } catch (RuntimeException e) {
            throw new DAOException("Erro ao cadastrar Venda.", e);
        }
    }

    @Override
    public void excluir(Venda entity) throws DAOException {
        // Política do projeto: não permitir deletar vendas
        throw new UnsupportedOperationException("OPERAÇÃO NÃO PERMITIDA PARA VENDA");
    }

    @Override
    public void finalizarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException {
        try {
            final EntityManager em = em();
            Venda v = (venda.getId() != null) ? em.find(Venda.class, venda.getId()) : null;
            if (v == null) {
                v = em.merge(venda);
            }
            v.setStatus(Venda.Status.CONCLUIDA);
            em.flush();
        } catch (RuntimeException e) {
            throw new DAOException("Erro ao finalizar venda", e);
        }
    }

    @Override
    public void cancelarVenda(Venda venda) throws DAOException {
        try {
            // Garante entidade gerenciada
            Venda managed = em().find(Venda.class, venda.getId());
            if (managed == null) {
                throw new DAOException("Venda não encontrada para cancelamento.", null);
            }

            switch (managed.getStatus()) {
                case INICIADA:
                    // Domínio permite limpar itens enquanto INICIADA
                    managed.removerTodosProdutos();
                    managed.setStatus(Venda.Status.CANCELADA);
                    break;

                case CONCLUIDA:
                    // Permite cancelar pós-conclusão.
                    // Não chamamos removerTodosProdutos() (validação de status impediria).
                    // O estorno de estoque fica a cargo do Service.
                    managed.setStatus(Venda.Status.CANCELADA);
                    break;

                case CANCELADA:
                    // Idempotente: nada a fazer
                    return;
            }

            em().merge(managed);
            em().flush();
        } catch (DAOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new DAOException("Erro ao cancelar venda", e);
        }
    }


    @Override
    public Venda buscarPorCodigo(String codigo) {
        TypedQuery<Venda> q = em().createQuery(
            "select v from Venda v where v.codigo = :codigo", Venda.class);
        q.setParameter("codigo", codigo);
        List<Venda> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Venda buscarPorCodigoComProdutos(String codigo) {
        TypedQuery<Venda> q = em().createQuery(
            "select distinct v from Venda v " +
            " join fetch v.cliente " +
            " left join fetch v.produtos pq " +
            " left join fetch pq.produto " +
            " where v.codigo = :codigo", Venda.class);
        q.setParameter("codigo", codigo);
        List<Venda> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
