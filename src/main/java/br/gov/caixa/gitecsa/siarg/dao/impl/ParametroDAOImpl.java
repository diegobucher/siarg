package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.model.Parametro;

public class ParametroDAOImpl extends BaseDAOImpl<Parametro> implements ParametroDAO {

  @Override
  public Parametro obterParametroByNome(String parametro) {
    
    StringBuilder hql = new StringBuilder();

    hql.append(" Select p From Parametro p ");
    hql.append(" Where p.nome = :nomeParam ");

    TypedQuery<Parametro> query = this.getEntityManager().createQuery(hql.toString(), Parametro.class);
    query.setParameter("nomeParam", parametro);

    List<Parametro> list = query.getResultList();
    if (!list.isEmpty()) {      
      return list.get(0);
    } 
    return null;
    
  }

}
