package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.DemandaContratoDAO;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;

public class DemandaContratoDAOImpl  extends BaseDAOImpl<DemandaContrato> implements DemandaContratoDAO{

  @Override
  public List<DemandaContrato> obterContratosPorIdDemanda(Long idDemanda) {
    
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT dc ");
    hql.append(" FROM DemandaContrato dc ");
    hql.append(" INNER JOIN dc.demanda demanda ");
    hql.append(" WHERE demanda.id = :idDemanda ");

    TypedQuery<DemandaContrato> query = this.getEntityManager().createQuery(hql.toString(), DemandaContrato.class);
    
    query.setParameter("idDemanda", idDemanda);

    return query.getResultList();
  }

}
