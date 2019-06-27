package br.gov.caixa.gitecsa.siarg.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.OcorrenciaLidaDAO;
import br.gov.caixa.gitecsa.siarg.model.OcorrenciaLida;

public class OcorrenciaLidaDAOImpl  extends BaseDAOImpl<OcorrenciaLida> implements OcorrenciaLidaDAO {

  @Override
  public OcorrenciaLida obterOcorrenciaLida(String matricula, Long idAbrangencia) {
    
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT ocorrencia ");
    hql.append(" FROM OcorrenciaLida ocorrencia ");
    hql.append(" INNER JOIN ocorrencia.abrangencia abrangencia ");
    hql.append(" WHERE ocorrencia.matricula = :matricula ");
    hql.append(" AND abrangencia.id = :idAbrangencia ");

    TypedQuery<OcorrenciaLida> query = this.getEntityManager().createQuery(hql.toString(), OcorrenciaLida.class);
    
    query.setParameter("matricula", matricula);
    query.setParameter("idAbrangencia", idAbrangencia);

    try {
      return query.getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
    
  }

}
