package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.siarg.model.Feriado;

public class FeriadoDAOImpl extends BaseDAOImpl<Feriado> implements FeriadoDAO {

  @Override
  public Boolean isFeriado(Date data) throws DataBaseException {
    StringBuilder hql = new StringBuilder();

    hql.append("Select count(f.id) From Feriado f ");
    hql.append("Where f.data = :data ");

    Query query = this.getEntityManager().createQuery(hql.toString());
    query.setParameter("data", data);

    try {
      return (((Long) query.getSingleResult()).longValue() > 0) ? true : false;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO#obterListaDeDatasDosFeriados()
   */
  @Override
  public List<Date> obterListaDeDatasDosFeriados() {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT feriado.data ");
    hql.append(" FROM Feriado feriado ");

    TypedQuery<Date> query = this.getEntityManager().createQuery(hql.toString(), Date.class);

    return query.getResultList();
  }

}
