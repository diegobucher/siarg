/**
 * UnidadeDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 24-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Implementação da Interface UnidadeDAO.
 * @author f520296
 */
public class UnidadeDAOImpl extends BaseDAOImpl<Unidade> implements UnidadeDAO {

  /**
   * Método para obter a unidade do usuário cadastrado como excessão.
   */
  @Override
  public Unidade obterUnidadeUsuarioLogado(Integer cgcUnidade) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN FETCH unidade.caixasPostaisList caixasPostaisList ");
    hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
    hql.append(" WHERE unidade.cgcUnidade = :cgcUnidade ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" AND (unidade.tipoUnidade = :filial ");
    hql.append(" OR unidade.tipoUnidade = :matriz ) ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("cgcUnidade", cgcUnidade);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("filial", TipoUnidadeEnum.FILIAL);
    query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);

    List<Unidade> unidadeList = query.getResultList();
    if (!unidadeList.isEmpty()) {
      return unidadeList.get(0);
    }

    return null;
  }
  
  @Override
  public List<Unidade> obterListaUnidadeUsuarioLogado(Integer cgcUnidade) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN FETCH unidade.caixasPostaisList caixasPostaisList ");
    hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
    hql.append(" WHERE unidade.cgcUnidade = :cgcUnidade ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" AND (unidade.tipoUnidade = :filial ");
    hql.append(" OR unidade.tipoUnidade = :matriz ) ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("cgcUnidade", cgcUnidade);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("filial", TipoUnidadeEnum.FILIAL);
    query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);

    return query.getResultList();

  }

  /**
   * Obter a lista de unidades ativas de um usuário excessão.
   */
  @Override
  public List<Unidade> obterListaUnidadesUsuarioExcessao(Long idUsuario) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" INNER JOIN unidade.usuarioUnidadeList usuarioUnidadeList ");
    hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
    hql.append(" INNER JOIN FETCH unidade.caixasPostaisList caixasPostaisList ");
    hql.append(" WHERE usuarioUnidadeList.usuario.id = :idUsuario ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" AND caixasPostaisList.ativo = :ativo ");
    hql.append(" AND (unidade.tipoUnidade = :filial ");
    hql.append(" OR unidade.tipoUnidade = :matriz ) ");
    
    hql.append(" AND (usuarioUnidadeList.dataFim IS NULL OR Date(usuarioUnidadeList.dataFim) >= current_date()) ");
    
    hql.append(" ORDER BY unidade.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("idUsuario", idUsuario);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("filial", TipoUnidadeEnum.FILIAL);
    query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);

    return query.getResultList();

  }

  @Override
  public List<Unidade> obterListaUnidadesPorAbrangencia(Long idAbrangencia) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
    hql.append(" INNER JOIN unidade.caixasPostaisList caixasPostaisList ");
    hql.append(" WHERE abrangencia.id = :idAbrangencia ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" AND caixasPostaisList.ativo = :ativo ");
    hql.append(" AND (unidade.tipoUnidade = :filial ");
    hql.append(" OR unidade.tipoUnidade = :matriz ) ");
    hql.append(" ORDER BY unidade.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("idAbrangencia", idAbrangencia);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("filial", TipoUnidadeEnum.FILIAL);
    query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);

    return query.getResultList();

  }
  
  @Override
  public List<Unidade> obterListaUnidadesSUEG(Long idAbrangencia) {

    List<String> listaSuegSigla = new ArrayList<String>();
    listaSuegSigla.add("SUEGA");
    listaSuegSigla.add("SUEGB");
    listaSuegSigla.add("SUEGC");
    listaSuegSigla.add("SUEGD");
    listaSuegSigla.add("SUEGE");
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN unidade.abrangencia abrangencia ");
    hql.append(" WHERE abrangencia.id = :idAbrangencia ");
    hql.append(" AND unidade.sigla IN (:listaSuegSigla) ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" ORDER BY unidade.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("idAbrangencia", idAbrangencia);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("listaSuegSigla", listaSuegSigla);

    return query.getResultList();

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO#obterUnidadePorChaveFetch(java.lang.Long)
   */
  @Override
  public Unidade obterUnidadePorChaveFetch(Long idUnidade) {

    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT DISTINCT unidade ");
      hql.append(" FROM Unidade unidade ");
      hql.append(" INNER JOIN FETCH unidade.caixasPostaisList caixasPostaisList ");
      hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
      hql.append(" WHERE unidade.id = :idUnidade ");
      hql.append(" AND unidade.ativo = :ativo ");
      hql.append(" AND caixasPostaisList.ativo = :ativo ");

      TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

      query.setParameter("idUnidade", idUnidade);
      query.setParameter("ativo", Boolean.TRUE);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public List<Unidade> obterUnidadesECaixasPostais(TipoUnidadeEnum... tiposUnidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select Distinct u ");
    hql.append(" From Unidade u ");
    hql.append(" Inner Join Fetch u.caixasPostaisList c ");
    hql.append(" Where u.ativo = true And c.ativo = true ");
    hql.append(" And u.tipoUnidade In (:tipos) ");
    hql.append(" Order By u.tipoUnidade, u.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);
    query.setParameter("tipos", Arrays.asList(tiposUnidade));

    return query.getResultList();
  }
  
  @Override
  public Unidade obterUnidadePorAbrangenciaCGC(Abrangencia abrangencia, Integer cgcUnidade) {

      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT unidade ");
      hql.append(" FROM Unidade unidade ");
      hql.append(" LEFT JOIN  unidade.abrangencia abrangencia ");
      hql.append(" WHERE unidade.cgcUnidade = :cgcUnidade ");
      hql.append(" AND abrangencia.id = :abrangenciaId ");
      hql.append(" AND unidade.ativo = :ativo ");

      TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

      query.setParameter("abrangenciaId", abrangencia.getId());
      query.setParameter("cgcUnidade", cgcUnidade);
      query.setParameter("ativo", Boolean.TRUE);

      List<Unidade> unidadeList = query.getResultList();

      if (!unidadeList.isEmpty()) {
        return unidadeList.get(0);
      } else {
        return null;
      }
  }

  @Override
  public Unidade obterUnidadePorAbrangenciaSigla(Abrangencia abrangencia, String sigla) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN unidade.abrangencia abrangencia ");
    hql.append(" WHERE UPPER(TRIM(unidade.sigla)) LIKE UPPER(TRIM(:sigla)) ");
    hql.append(" AND abrangencia.id = :abrangenciaId ");
    hql.append(" AND unidade.ativo = :ativo ");
    
    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);
    
    query.setParameter("abrangenciaId", abrangencia.getId());
    query.setParameter("sigla", sigla);
    query.setParameter("ativo", Boolean.TRUE);
    
    List<Unidade> unidadeList = query.getResultList();
    
    if (!unidadeList.isEmpty()) {
      return unidadeList.get(0);
    } else {
      return null;
    }
  }
  
  @Override
  public List<Unidade> obterUnidadesECaixasPostais(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select Distinct u ");
    hql.append(" From Unidade u ");
    hql.append(" Inner Join u.abrangencia a ");
    hql.append(" Inner Join Fetch u.caixasPostaisList c ");
    hql.append(" Where u.ativo = true And c.ativo = true ");
    hql.append(" And a.id = :abrangencia ");
    hql.append(" And u.tipoUnidade In (:tipos) ");
    hql.append(" Order By u.tipoUnidade, u.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);
    query.setParameter("tipos", Arrays.asList(tiposUnidade));
    query.setParameter("abrangencia", abrangencia.getId());

    return query.getResultList();
  }
  
  @Override
  public List<Unidade> obterUnidadesAtivas(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select Distinct u ");
    hql.append(" From Unidade u ");
    hql.append(" Inner Join u.abrangencia a ");
    hql.append(" Inner Join Fetch u.caixasPostaisList c ");
    hql.append(" Where u.ativo = true ");
    hql.append(" And a.id = :abrangencia ");
    hql.append(" And u.tipoUnidade In (:tipos) ");
    hql.append(" Order By u.tipoUnidade, u.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);
    query.setParameter("tipos", Arrays.asList(tiposUnidade));
    query.setParameter("abrangencia", abrangencia.getId());

    return query.getResultList();
  }
  
  @Override
  public List<Unidade> obterUnidadesDemandateByAssunto(Assunto assunto) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select Distinct u ");
    hql.append(" From Unidade u ");
    hql.append(" Inner Join u.assuntoUnidadeDemandanteList assuntosList ");
    hql.append(" Where assuntosList.id = :idAssunto ");
    hql.append(" Order By u.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);
    query.setParameter("idAssunto", assunto.getId());

    return query.getResultList();
  }

  @Override
  public Unidade findBySigla(String sigla) {
    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT DISTINCT unidade ");
      hql.append(" FROM Unidade unidade ");
      hql.append(" WHERE unidade.sigla = :sigla ");

      TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

      query.setParameter("sigla", sigla);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public List<Unidade> obterListaUnidadesRelatorioAnaliticoPorAssunto(Long idAbrangencia) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT unidade ");
    hql.append(" FROM Unidade unidade ");
    hql.append(" LEFT JOIN FETCH unidade.abrangencia abrangencia ");
    hql.append(" INNER JOIN unidade.caixasPostaisList caixasPostaisList ");
    hql.append(" WHERE abrangencia.id = :idAbrangencia ");
    hql.append(" AND unidade.ativo = :ativo ");
    hql.append(" AND caixasPostaisList.ativo = :ativo ");
    hql.append(" AND (unidade.tipoUnidade = :filial ");
    hql.append(" OR unidade.tipoUnidade = :matriz ) ");
    hql.append(" AND unidade.isRelConsolidadoAssunto = :ativo ");
    hql.append(" ORDER BY unidade.sigla ");

    TypedQuery<Unidade> query = this.getEntityManager().createQuery(hql.toString(), Unidade.class);

    query.setParameter("idAbrangencia", idAbrangencia);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("filial", TipoUnidadeEnum.FILIAL);
    query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);

    return query.getResultList();
  }

}
