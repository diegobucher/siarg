package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.OcorrenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public class OcorrenciaDAOImpl  extends BaseDAOImpl<Ocorrencia> implements OcorrenciaDAO {

  @Override
  public List<Ocorrencia> obterListaOcorrenciasPorUnidade(UnidadeDTO unidadeSelecionadaDTO) {
    
    if (unidadeSelecionadaDTO == null) {
      return new ArrayList<Ocorrencia>();
    }
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT ocorrencia ");
    hql.append(" FROM Ocorrencia ocorrencia ");
    hql.append(" LEFT JOIN FETCH ocorrencia.unidade unidade ");
    hql.append(" WHERE ocorrencia.ativo = :ativo ");
    hql.append(" and unidade.id = :idUnidade ");
    hql.append(" ORDER BY ocorrencia.dhPublicacao ");

    TypedQuery<Ocorrencia> query = this.getEntityManager().createQuery(hql.toString(), Ocorrencia.class);
    
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("idUnidade", unidadeSelecionadaDTO.getId());

    return query.getResultList();
    
  }
  
  @Override
  public List<Ocorrencia> obterListaOcorrenciasPor(Abrangencia abrangencia, Date dataPublicacao) {
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT ocorrencia ");
    hql.append(" FROM Ocorrencia ocorrencia ");
    hql.append(" INNER JOIN FETCH ocorrencia.unidade unidade ");
    hql.append(" INNER JOIN unidade.abrangencia abrangencia");
    hql.append(" WHERE ocorrencia.ativo = :ativo ");
    hql.append(" and abrangencia.id = :idAbrangencia ");
    hql.append(" and ocorrencia.dhPublicacao BETWEEN :dataPublicacao AND current_timestamp");
    hql.append(" and ocorrencia.dhExpiracao > current_timestamp ");
    hql.append(" ORDER BY ocorrencia.dhPublicacao DESC, ocorrencia.id DESC");

    TypedQuery<Ocorrencia> query = this.getEntityManager().createQuery(hql.toString(), Ocorrencia.class);
    
    query.setParameter("ativo", Boolean.TRUE);
    query.setParameter("idAbrangencia", abrangencia.getId());
    query.setParameter("dataPublicacao", dataPublicacao, TemporalType.TIMESTAMP);

    return query.getResultList();
    
  }

}
