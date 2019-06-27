/**
 * AtendimentoDAOImpl.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;

/**
 * Classe: Implementação da interface AtendimentoDAO.
 * 
 * @author f520296
 */
public class AtendimentoDAOImpl extends BaseDAOImpl<Atendimento> implements AtendimentoDAO {

  /*
   * (non-Javadoc)
   * 
   * @see br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO#obterUltimoAtendimentoPorDemanda(java.lang.Long)
   */
  @Override
  public Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda, Long idCaixaPostalResponsavel) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN atendimento.demanda demanda ");
    hql.append(" INNER JOIN FETCH atendimento.fluxoDemanda fluxoDemanda ");
    hql.append(" INNER JOIN FETCH fluxoDemanda.caixaPostal caixaPostal ");
    hql.append(" WHERE demanda.id = :idDemanda ");
    hql.append(" AND fluxoDemanda.caixaPostal.id = :idCaixaPostalResponsavel ");
    hql.append(" ORDER BY atendimento.dataHoraRecebimento DESC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("idDemanda", idDemanda);
    query.setParameter("idCaixaPostalResponsavel", idCaixaPostalResponsavel);

    List<Atendimento> temp = query.getResultList();
    if (!temp.isEmpty()) {
      return temp.get(0);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO#obterUltimoAtendimentoPorDemanda(java.lang.Long)
   */
  @Override
  public Atendimento obterUltimoAtendimentoPorDemandaCaixaPostalExterna(Long idDemanda) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN FETCH atendimento.demanda demanda ");
    hql.append(" LEFT JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
    hql.append(" WHERE demanda.id = :idDemanda ");
    hql.append(" AND atendimento.unidadeExterna is not null ");
    hql.append(" ORDER BY atendimento.dataHoraRecebimento DESC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("idDemanda", idDemanda);

    List<Atendimento> temp = query.getResultList();
    if (!temp.isEmpty()) {
      return temp.get(0);
    }
    return null;
  }

  @Override
  public List<Atendimento> obterAtendimentosPorDemanda(Long idDemanda) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN atendimento.demanda demanda ");
    hql.append(" LEFT JOIN FETCH atendimento.fluxoDemanda fluxo ");
    hql.append(" LEFT JOIN FETCH fluxo.caixaPostal caixa ");
    hql.append(" LEFT JOIN FETCH caixa.unidade unidade ");
    hql.append(" WHERE demanda.id = :idDemanda ");
    hql.append(" ORDER BY atendimento.id ASC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("idDemanda", idDemanda);

    return query.getResultList();
  }

  @Override
  public List<String> obterUltimasRespostasPorAssunto(Long idAssunto, String texto) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT atendimento.descricao ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN atendimento.demanda demanda ");
    hql.append(" INNER JOIN demanda.assunto assunto ");
    hql.append(" WHERE assunto.id = :idAssunto ");
    hql.append(" AND atendimento.descricao IS NOT NULL ");

    hql.append(" AND UPPER(atendimento.descricao) like :texto ");
    hql.append(" ORDER BY atendimento.dataHoraAtendimento DESC ");

    TypedQuery<String> query = this.getEntityManager().createQuery(hql.toString(), String.class);

    query.setParameter("idAssunto", idAssunto);
    query.setParameter("texto", "%" + texto.toUpperCase() + "%");
    query.setMaxResults(50);

    return query.getResultList();
  }

  @Override
  public List<Atendimento> obterListaAtendimentosEncaminhadosExternas(Abrangencia abrangenciaSelecionada, Date dataInicial,
      Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN FETCH atendimento.demanda demanda ");
    hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
    hql.append(" INNER JOIN FETCH caixaPostalResponsavel.unidade unidadeCaixaPostalResponsavel  ");
    hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
    hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostalAssunto ");
    hql.append(" INNER JOIN FETCH atendimento.unidadeExterna unidadeExterna ");
    hql.append(" INNER JOIN unidadeExterna.abrangencia abrangencia ");
    hql.append(" WHERE 1 = 1 ");
    hql.append(" AND abrangencia = :abrangenciaSelecionada ");
    hql.append(" AND atendimento.dataHoraRecebimento BETWEEN :dataInicial AND :dataFinal ");
    hql.append(
        " AND ( atendimento.acaoEnum is null or atendimento.acaoEnum != :atualizar or atendimento.acaoEnum != :rascunho) ");
    hql.append(" ORDER BY atendimento.unidadeExterna ASC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
    query.setParameter("dataInicial", dataInicial);
    query.setParameter("dataFinal", dataFinal);
    query.setParameter("atualizar", AcaoAtendimentoEnum.ATUALIZAR);
    query.setParameter("rascunho", AcaoAtendimentoEnum.RASCUNHO);

    return query.getResultList();
  }

  @Override
  public Atendimento obterUltimoAtendimentoPorDemandaSemFluxoDemanda(Long idDemanda, Long idCxpResp) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN atendimento.demanda demanda ");
    hql.append(" WHERE demanda.id = :idDemanda ");
    hql.append(
        " AND ( atendimento.acaoEnum is null OR Atendimento.acaoEnum != :atualizar OR atendimento.acaoEnum != :rascunho) ");
    hql.append(" AND ( atendimento.fluxoDemanda is null ) ");
    hql.append(" AND demanda.caixaPostalResponsavel.id = :idCaixaPostalResponsavel ");
    hql.append(" ORDER BY atendimento.dataHoraRecebimento DESC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("idDemanda", idDemanda);
    query.setParameter("idCaixaPostalResponsavel", idCxpResp);
    query.setParameter("atualizar", AcaoAtendimentoEnum.ATUALIZAR);
    query.setParameter("rascunho", AcaoAtendimentoEnum.RASCUNHO);

    List<Atendimento> temp = query.getResultList();
    if (!temp.isEmpty()) {
      return temp.get(0);
    }
    return null;
  }

  @Override
  public Atendimento obterAtendimentoComDemandaFluxoDemanda(Long idAtendimento) {

    try {

      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT DISTINCT atendimento ");
      hql.append(" FROM Atendimento atendimento ");
      hql.append(" LEFT JOIN FETCH atendimento.demanda demanda ");
      hql.append(" LEFT JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
      hql.append(" WHERE atendimento.id = :idAtendimento ");

      TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

      query.setParameter("idAtendimento", idAtendimento);

      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public Atendimento findByIdFetch(Long idAtendimento) {

    try {

      StringBuilder hql = new StringBuilder();
      hql.append(" SELECT atendimento ");
      hql.append(" FROM Atendimento atendimento ");
      hql.append(" INNER JOIN FETCH atendimento.fluxoDemanda fluxoDemanda ");
      hql.append(" INNER JOIN FETCH fluxoDemanda.caixaPostal caixaPostal ");
      hql.append(" WHERE atendimento.id = :idAtendimento ");

      TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

      query.setParameter("idAtendimento", idAtendimento);

      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
  
  @Override
  public Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" WHERE demanda.id = :idDemanda ");
    hql.append(" ORDER BY 1 DESC ");

    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);

    query.setParameter("idDemanda", idDemanda);

    List<Atendimento> temp = query.getResultList();
    if (!temp.isEmpty()) {
      return temp.get(0);
    }
    return null;
  }
  
  @Override
  public List<Atendimento> obterListaAtendimentosPorUnidade(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial,
      Date dataFinal) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT atendimento ");
    hql.append(" FROM Atendimento atendimento ");
    hql.append(" INNER JOIN FETCH atendimento.demanda demanda ");
    hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
    hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante ");
    hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");
    hql.append(" INNER JOIN FETCH atendimento.fluxoDemanda fluxoDemanda ");
    hql.append(" INNER JOIN FETCH fluxoDemanda.caixaPostal caixaPostal ");
    hql.append(" INNER JOIN caixaPostal.unidade unidade ");
    hql.append(" WHERE DATE(atendimento.dataHoraAtendimento) BETWEEN :dataInicial AND :dataFinal ");
    
    if(idUnidade != null) {
    	hql.append(" AND unidade.id = :idUnidade ");
    }else {
    	hql.append(" AND (unidade.tipoUnidade = :tipoUnidadeMatriz OR unidade.tipoUnidade = :tipoUnidadeFilial) ");
    }
    
    if(idUnidade == null) {
      hql.append(" AND abrangencia = :abrangenciaSelecionada ");
    }
    
    TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);
    
    if(idUnidade != null) {
    	query.setParameter("idUnidade", idUnidade);
    }else {
    	query.setParameter("tipoUnidadeMatriz", TipoUnidadeEnum.MATRIZ);
        query.setParameter("tipoUnidadeFilial", TipoUnidadeEnum.FILIAL);
    }
    
    if(idUnidade == null) {
      query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
    }
    
    query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

    return query.getResultList();
  }
  
  public Atendimento obterAtendimentoAnteriorPorDemandaCaixaPostal(Long idAtendimento, Long idDemanda, Long idCaixaPostal) {
	  StringBuilder hql = new StringBuilder();
	  hql.append(" SELECT DISTINCT atendimento ");
	  hql.append(" FROM Atendimento atendimento ");
	  hql.append(" INNER JOIN FETCH atendimento.fluxoDemanda fluxoDemanda ");
	  hql.append(" INNER JOIN FETCH atendimento.demanda demanda ");
	  hql.append(" WHERE demanda.id = :idDemanda ");
	  hql.append(" AND atendimento.id < :idAtendimento "); 
	  hql.append(" AND fluxoDemanda.caixaPostal.id = :idCaixaPostal "); 
	  
	  TypedQuery<Atendimento> query = this.getEntityManager().createQuery(hql.toString(), Atendimento.class);
	  query.setMaxResults(1);
	  
	  query.setParameter("idDemanda", idDemanda);
	  query.setParameter("idAtendimento", idAtendimento);
	  query.setParameter("idCaixaPostal", idCaixaPostal);
	  
	  List<Atendimento> atendimentos = query.getResultList();
	  if (atendimentos != null && !atendimentos.isEmpty()) {
		  return atendimentos.get(0);
	  }
	  
	  return null;
  }
}
