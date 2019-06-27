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

import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.dao.SiicoDAOImpl;
import br.gov.caixa.gitecsa.siico.dao.UnidadeSiicoDAO;
import br.gov.caixa.gitecsa.siico.vo.UnidadeSiicoVO;

public class UnidadeSiicoDAOImpl extends SiicoDAOImpl<UnidadeSiicoVO> implements UnidadeSiicoDAO {

  public static final Logger LOGGER = Logger.getLogger(UnidadeSiicoDAOImpl.class);

  @Override
  public UnidadeSiicoVO obterUnidadePorCGC(Integer numeroCGC) {

    if (numeroCGC == null) {
      return null;
    }

    try {
      StringBuilder hql = new StringBuilder();
      hql.append(" SELECT u ");
      hql.append(" FROM UnidadeSiicoVO u");
      hql.append(" WHERE u.id = :numeroCGC ");

      Query query = getEntityManager().createQuery(hql.toString(), UnidadeSiicoVO.class);

      query.setParameter("numeroCGC", numeroCGC);

      return (UnidadeSiicoVO) query.getSingleResult();

    } catch (NoResultException e) {
      LOGGER.debug(e);
      return null;
    }
  }

}
