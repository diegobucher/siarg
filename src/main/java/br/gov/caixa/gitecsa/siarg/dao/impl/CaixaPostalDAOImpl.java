/**
 * CaixaPostalDAOImpl.java
 * Versão: 1.0.0.00
 * 11/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Classe: Implementação da interface CaixaPostalDAO.
 * @author f520296
 */
public class CaixaPostalDAOImpl extends BaseDAOImpl<CaixaPostal> implements CaixaPostalDAO {

  @Override
  public List<CaixaPostal> findByUnidade(final Unidade unidade) {
    StringBuilder hql = new StringBuilder();

    hql.append("Select c From CaixaPostal c ");
    hql.append("Inner Join c.unidade u ");
    hql.append("Where u.id = :unidade And c.ativo = true ");
    hql.append("Order by c.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("unidade", unidade.getId());

    return query.getResultList();
  }

  @Override
  public List<CaixaPostal> findByTipoUnidade(final TipoUnidadeEnum... tiposUnidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select c From CaixaPostal c ");
    hql.append(" Inner Join c.unidade u ");
    hql.append(" Where c.ativo = :ativo And u.ativo = :ativo ");
    hql.append(" And u.tipoUnidade In (:tipos) ");
    hql.append(" Order By c.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("tipos", Arrays.asList(tiposUnidade));

    return query.getResultList();
  }
  
  @Override
  public List<CaixaPostal> findByAbrangenciaTipoUnidade(Abrangencia abrangencia, final TipoUnidadeEnum... tiposUnidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select c From CaixaPostal c ");
    hql.append(" Inner Join c.unidade u ");
    hql.append(" Inner Join u.abrangencia a ");
    hql.append(" Where c.ativo = :ativo And u.ativo = :ativo ");
    hql.append(" And u.tipoUnidade In (:tipos) ");
    hql.append(" And a.id = :abrangencia ");
    hql.append(" Order By c.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("abrangencia", abrangencia.getId());
    query.setParameter("tipos", Arrays.asList(tiposUnidade));

    return query.getResultList();
  }

  @Override
  public List<CaixaPostal> findByRangeId(final List<Long> ids) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select c From CaixaPostal c ");
    hql.append(" Inner Join c.unidade u ");
    hql.append(" Where c.ativo = :ativo And u.ativo = :ativo ");
    hql.append(" And c.id In (:ids) ");
    hql.append(" Order By c.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("ids", ids);

    return query.getResultList();
  }

  @Override
  public List<CaixaPostal> findObservadoresByDemanda(final Long idDemanda) {
    StringBuilder hql = new StringBuilder();

    hql.append("SELECT obs FROM Demanda d ");
    hql.append("INNER JOIN d.caixasPostaisObservadorList obs ");
    hql.append("WHERE d.id = :id ");
    hql.append("ORDER BY obs.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("id", idDemanda);

    return query.getResultList();
  }

  @Override
  public List<CaixaPostal> findObservadoresByAssunto(final Long idAssunto) {
    StringBuilder hql = new StringBuilder();

    hql.append("SELECT obs FROM Assunto a ");
    hql.append("INNER JOIN a.caixasPostaisObservadorList obs ");
    hql.append("WHERE a.id = :id ");
    hql.append("ORDER BY obs.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("id", idAssunto);

    return query.getResultList();
  }
  
  @Override
  public CaixaPostal findByIdFetch(final Long idCaixaPostal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select c From CaixaPostal c ");
    hql.append(" Inner Join Fetch c.unidade u ");
    hql.append(" Where c.id = (:idCaixaPostal) ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("idCaixaPostal", idCaixaPostal);

    return query.getSingleResult();
  }

}
