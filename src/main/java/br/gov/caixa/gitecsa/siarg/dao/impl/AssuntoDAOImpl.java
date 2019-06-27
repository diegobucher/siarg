package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public class AssuntoDAOImpl extends BaseDAOImpl<Assunto> implements AssuntoDAO {

  @Override
  public List<Assunto> findAtivos(Abrangencia abrangencia) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select a From Assunto a ");
    hql.append(" Left Join Fetch a.assuntoPai ");
    hql.append(" Left Join Fetch a.caixaPostal ");
    hql.append(" Inner Join Fetch a.abrangencia abran ");
    hql.append(" Where a.ativo = :ativo ");
    hql.append(" And abran.id = :abrangencia ");
    hql.append(" Order By a.id ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("abrangencia", abrangencia.getId());

    return query.getResultList();
  }
  
  @Override
  public List<Assunto> findAllBy(Abrangencia abrangencia) {
    StringBuilder hql = new StringBuilder();
    
    hql.append(" Select a From Assunto a ");
    hql.append(" Left Join Fetch a.assuntoPai ");
    hql.append(" Left Join Fetch a.caixaPostal ");
    hql.append(" Inner Join Fetch a.abrangencia abran ");
    hql.append(" Where abran.id = :abrangencia ");
    hql.append(" Order By a.id ");
    
    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("abrangencia", abrangencia.getId());
    
    return query.getResultList();
  }
  
  @Override
  public Boolean isDemandanteAssunto(final Assunto assunto, final Unidade unidade) throws DataBaseException {
    StringBuilder hql = new StringBuilder();

    hql.append("Select Count(d.id) From Assunto a ");
    hql.append("Inner Join a.assuntoUnidadeDemandanteList d ");
    hql.append("Where a.id = :idAssunto And d.id = :idUnidade ");

    Query query = this.getEntityManager().createQuery(hql.toString());
    query.setParameter("idAssunto", assunto.getId());
    query.setParameter("idUnidade", unidade.getId());

    try {
      return (((Long) query.getSingleResult()).longValue() > 0) ? true : false;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  @Override
  public Assunto findByIdEager(final Long id) throws DataBaseException {
    StringBuilder hql = new StringBuilder();

    hql.append("Select a From Assunto a ");
    hql.append("Left Join Fetch a.assuntoPai ");
    hql.append("Left Join Fetch a.caixaPostal ");
    hql.append("Where a.id = :id ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("id", id);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  @Override
  public List<FluxoAssunto> findFluxoAssuntoByAssuntoTipo(final Assunto assunto, final TipoFluxoEnum tipo) {
    StringBuilder hql = new StringBuilder();

    hql.append("Select f From FluxoAssunto f ");
    hql.append("Inner Join f.assunto a ");
    hql.append("Left Join fetch f.caixaPostal c ");
    hql.append("Left Join fetch c.unidade ");
    hql.append("Where a.id = :id And f.tipoFluxo = :tipo ");
    hql.append("Order by f.ordem ");

    TypedQuery<FluxoAssunto> query = this.getEntityManager().createQuery(hql.toString(), FluxoAssunto.class);
    query.setParameter("id", assunto.getId());
    query.setParameter("tipo", tipo);

    return query.getResultList();
  }

  @Override
  public List<CaixaPostal> findObservadoresByAssunto(final Assunto assunto) {
    StringBuilder hql = new StringBuilder();

    hql.append("Select o From Assunto a ");
    hql.append("Inner Join a.caixasPostaisObservadorList o ");
    hql.append("Where a.id = :id And o.ativo = :ativo ");
    hql.append("Order by o.sigla ");

    TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class);
    query.setParameter("id", assunto.getId());
    query.setParameter("ativo", Boolean.TRUE);

    return query.getResultList();
  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO#obterAssuntosAtivos()
   */
  @Override
  public List<Assunto> obterAssuntosFetchPai() {

    StringBuilder hql = new StringBuilder();

    hql.append("Select a From Assunto a ");
    hql.append("Left Join Fetch a.assuntoPai ");
    hql.append("Order By a.id ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);

    return query.getResultList();
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO#pesquisarRelacaoAssuntos(br.gov.caixa.gitecsa.siarg
   * .model.Abrangencia, java.util.List)
   */
  @Override
  public List<Assunto> pesquisarRelacaoAssuntos(Abrangencia abrangencia, UnidadeDTO unidade, List<UnidadeDTO> unidadeList) {
    List<Long> idUnidadesList = new ArrayList<Long>();
    if (unidade == null) {
      for (UnidadeDTO unidadeDto : unidadeList) {
        idUnidadesList.add(unidadeDto.getId());
      }
    }
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT assunto ");
    hql.append(" FROM Assunto assunto ");
    hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostal ");
    hql.append(" LEFT JOIN FETCH assunto.assuntoUnidadeDemandanteList assuntoUnidadeDemandanteList ");
    hql.append(" WHERE assunto.ativo = :ativo ");
   
    if (unidade != null) {
      hql.append(" AND caixaPostal.id IN ( ");
      hql.append(" SELECT cxp.id FROM CaixaPostal cxp ");
      hql.append(" WHERE cxp.unidade.id = :idUnidade ");
      hql.append(" ) ");

    } else {
      hql.append(" AND caixaPostal.unidade.id IN (:listaUnidadesExcessao ) ");
    }

    hql.append(" ORDER BY caixaPostal.sigla, assunto.id ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);

    query.setParameter("ativo", Boolean.TRUE);
   
    if (unidade != null) {
      query.setParameter("idUnidade", unidade.getId());
    } else {
      query.setParameter("listaUnidadesExcessao", idUnidadesList);
    }

    return query.getResultList();
  }
  
  @Override
  public List<Assunto> pesquisarAssuntosPorAbrangenciaEUnidade(Abrangencia abrangencia, UnidadeDTO unidade) {

       StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT assunto ");
    hql.append(" FROM Assunto assunto ");
    hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostal ");
    hql.append(" LEFT JOIN FETCH assunto.assuntoUnidadeDemandanteList assuntoUnidadeDemandanteList ");
    hql.append(" WHERE assunto.ativo = :ativo ");
   
    if (unidade != null) {
      hql.append(" AND caixaPostal.id IN ( ");
      hql.append(" SELECT cxp.id FROM CaixaPostal cxp ");
      hql.append(" WHERE cxp.unidade.id = :idUnidade ");
      hql.append(" ) ");
    } 

    hql.append(" ORDER BY caixaPostal.sigla, assunto.id ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);

    query.setParameter("ativo", Boolean.TRUE);
   
    if (unidade != null) {
      query.setParameter("idUnidade", unidade.getId());
    } 

    return query.getResultList();
  }
  
  @Override
  public List<Assunto> pesquisarAssuntosPorAbrangencia(Abrangencia abrangencia) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT assunto ");
    hql.append(" FROM Assunto assunto ");
    hql.append(" INNER JOIN FETCH assunto.caixaPostal cxPostal ");
    hql.append(" WHERE assunto.ativo = :ativo ");
    hql.append(" ORDER BY assunto.nome ");
    
    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    
    query.setParameter("ativo", Boolean.TRUE);
    
    return query.getResultList();
  }
  
  @Override
  public List<Assunto> findCategoriasNaoExcluidos(Abrangencia abrangencia) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT a FROM Assunto a ");
    hql.append(" INNER JOIN a.abrangencia abran ");
    hql.append(" WHERE a.excluido = :excluido ");
    hql.append(" AND abran.id = :abrangencia ");
    hql.append(" AND a.assuntoPai IS NULL");
    hql.append(" ORDER BY a.nome ASC ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("excluido", Boolean.FALSE);
    query.setParameter("abrangencia", abrangencia.getId());

    return query.getResultList();
  }
  
  
  @Override
  public List<Assunto> findAssuntosByPaiNaoExcluidos(Assunto assuntoPai) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT a FROM Assunto a ");
    hql.append(" WHERE a.excluido = :excluido ");
    hql.append(" AND a.assuntoPai.id = :idPai");
    hql.append(" ORDER BY a.nome ASC ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("excluido", Boolean.FALSE);
    query.setParameter("idPai", assuntoPai.getId());

    return query.getResultList();
  }
  
  
  @Override
  public List<Assunto> findAssuntosByPai(Assunto assuntoPai) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT a FROM Assunto a ");
    hql.append(" WHERE a.assuntoPai.id = :idPai");
    hql.append(" ORDER BY a.nome ASC ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("idPai", assuntoPai.getId());

    return query.getResultList();
  }
  
  @Override
  public List<Assunto> findAllByAbrangencia(Abrangencia abrangencia) {
    StringBuilder hql = new StringBuilder();

    hql.append(" Select a From Assunto a ");
    hql.append(" Inner Join Fetch a.abrangencia abran ");
    hql.append(" Left Join Fetch a.assuntoPai a1 ");
    hql.append(" Left Join Fetch a1.caixaPostal ");
    hql.append(" Left Join Fetch a1.assuntoPai a2 ");
    hql.append(" Left Join Fetch a2.caixaPostal ");
    hql.append(" Left Join Fetch a2.assuntoPai a3 ");
    hql.append(" Left Join Fetch a3.caixaPostal ");
    hql.append(" Where a.flagAssunto = :flgAssunto ");
    hql.append(" And abran.id = :abrangencia ");

    TypedQuery<Assunto> query = this.getEntityManager().createQuery(hql.toString(), Assunto.class);
    query.setParameter("flgAssunto", Boolean.TRUE);
    query.setParameter("abrangencia", abrangencia.getId());

    return query.getResultList();
  }
  
  @Override
  public List<Long> listNumeroAssunto() {
    
    StringBuilder hql = new StringBuilder(" Select a.id From Assunto a ");
    TypedQuery<Long> query = this.getEntityManager().createQuery(hql.toString(), Long.class);

    return query.getResultList();
  }
  
  @Override
  public List<RelatorioPeriodoPorAssuntoDTO> findRelatorioPeriodoPorAssunto(Long idAbrangencia, Long idUnidade, Date dtInicioPerInicial, Date dtFimPerInicial, Date dtInicioPerFinal, Date dtFimPerFinal){

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT caixa.id AS caixaId, caixa.sigla AS caixaSigla, ass AS assunto, ass.nome AS assuntoNome, ");
    hql.append("  SUM(CASE WHEN (dem.situacao = :situacaoAberta  AND dem.dataHoraAbertura BETWEEN :dtIniPerIni AND :dtFimPerIni) THEN 1 ELSE 0 END) as qtdAbertaPini, ");
    hql.append("  SUM(CASE WHEN (dem.situacao = :situacaoFechada AND dem.dataHoraEncerramento BETWEEN :dtIniPerIni AND :dtFimPerIni) THEN 1 ELSE 0 END) as qtdFechadaPini, ");
    
    hql.append("  SUM(CASE WHEN (dem.situacao = :situacaoAberta AND dem.dataHoraAbertura BETWEEN :dtIniPerFim AND :dtFimPerFim) THEN 1 ELSE 0 END) as qtdAbertaPfim,  ");
    hql.append("  SUM(CASE WHEN (dem.situacao = :situacaoFechada AND dem.dataHoraEncerramento BETWEEN :dtIniPerFim AND :dtFimPerFim) THEN 1 ELSE 0 END) as qtdFechadaPfim, ");
    
    hql.append("  SUM(CASE WHEN (1=1) THEN 1 END) as qtdTotal   ");
    hql.append(" FROM Demanda dem  ");
    hql.append("  INNER JOIN dem.assunto ass  ");
    hql.append("  INNER JOIN ass.caixaPostal caixa  ");
    hql.append("  INNER JOIN caixa.unidade uni   ");
    hql.append("  INNER JOIN uni.abrangencia abra   ");
    hql.append(" WHERE abra.id = :idAbrangencia ");
    hql.append("  AND ass.ativo = TRUE ");
    hql.append("  AND uni.ativo = TRUE ");
    hql.append("  AND caixa.ativo = TRUE ");
    hql.append("  AND dem.situacao IN (:situacaoAberta,:situacaoFechada)  ");
    if(idUnidade != null) {
      hql.append(" AND uni.id = :idUnidade   ");
    }
    
    hql.append(" GROUP BY caixa.id, caixa.sigla, ass.id, ass.nome ");

    Query query = this.getEntityManager().createQuery(hql.toString());
    
    query.setParameter("idAbrangencia", idAbrangencia);
    query.setParameter("situacaoAberta", SituacaoEnum.ABERTA);
    query.setParameter("situacaoFechada", SituacaoEnum.FECHADA);
    
    query.setParameter("dtIniPerIni", dtInicioPerInicial, TemporalType.DATE);
    query.setParameter("dtFimPerIni", dtFimPerInicial, TemporalType.DATE);
    query.setParameter("dtIniPerFim", dtInicioPerFinal, TemporalType.DATE);
    query.setParameter("dtFimPerFim", dtFimPerFinal, TemporalType.DATE);
    
    if(idUnidade != null) {
      query.setParameter("idUnidade", idUnidade);
    }
    
    List<Object[]> resultList = (List<Object[]> )query.getResultList();
    
    List<RelatorioPeriodoPorAssuntoDTO> relatorioDtoList = new ArrayList<RelatorioPeriodoPorAssuntoDTO>();
    
    for (Object[] result : resultList) {
     
      Long caixaId = (Long) result[0];
      String caixaSigla = (String) result[1];
      Assunto assunto = (Assunto)result[2];
      String assuntoNome = (String) result[3];
      Long qtdAbertaPeriodoInicial = (Long) result[4];
      Long qtdFechadaPeriodoInicial = (Long) result[5];
      Long qtdAbertaPeriodoFinal = (Long) result[6];
      Long qtdFechadaPeriodoFinal = (Long) result[7];

      if(qtdAbertaPeriodoInicial.equals(0l)
          && qtdFechadaPeriodoInicial.equals(0l)
          && qtdAbertaPeriodoFinal.equals(0l)
          && qtdFechadaPeriodoFinal.equals(0l)
          ) {
        //Não adiciona na lista
        continue;
      }
      
      RelatorioPeriodoPorAssuntoDTO dto = new RelatorioPeriodoPorAssuntoDTO();
      dto.setAreaDemandada(caixaSigla);
      dto.setNumeroAssunto(assunto.getId());
      dto.setAssunto(assunto);
      dto.setNomeAssunto(assuntoNome);
      dto.setQtdAbertaPeriodoInicial(qtdAbertaPeriodoInicial);
      dto.setQtdFechadaPeriodoInicial(qtdFechadaPeriodoInicial);
      dto.setQtdAbertaPeriodoFinal(qtdAbertaPeriodoFinal);
      dto.setQtdFechadaPeriodoFinal(qtdFechadaPeriodoFinal);
      dto.setAreaDemandada(caixaSigla);
      
      relatorioDtoList.add(dto);
    }
    
    return relatorioDtoList;
  }
  
}
