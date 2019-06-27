/**
 * FuncaoEmpregadoGerenteDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 27-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siico.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.gov.caixa.gitecsa.arquitetura.dao.SisrhDAOImpl;
import br.gov.caixa.gitecsa.siico.dao.FuncaoEmpregadoGerenteDAO;
import br.gov.caixa.gitecsa.siico.vo.FuncaoEmpregadoGerenteVO;

public class FuncaoEmpregadoGerenteDAOImpl extends SisrhDAOImpl<FuncaoEmpregadoGerenteVO> implements FuncaoEmpregadoGerenteDAO {

  @Override
  public FuncaoEmpregadoGerenteVO obterFuncaoGerentePorCodigo(Integer numerofuncao) {

    if (numerofuncao == null) {
      return null;
    }

    try {
      StringBuilder hql = new StringBuilder();
      hql.append(" SELECT funcao ");
      hql.append(" FROM FuncaoEmpregadoGerenteVO funcao ");
      hql.append(" WHERE funcao.id = :numerofuncao ");

      Query query = getEntityManager().createQuery(hql.toString(), FuncaoEmpregadoGerenteVO.class);

      query.setParameter("numerofuncao", numerofuncao);

      return (FuncaoEmpregadoGerenteVO) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }
}
