/**
 * FuncaoEmpregadoGerenteService.java
 * Versão: 1.0.0.00
 * Data de Criação : 29-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siico.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.siico.dao.FuncaoEmpregadoGerenteDAO;

/**
 * Classe de serviços Funcao Empregado Gerente Service.
 * @author f520296
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FuncaoEmpregadoGerenteService implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 262773139310633736L;

  /** Injeção de Dependência. */
  @Inject
  private FuncaoEmpregadoGerenteDAO funcaoEmpregadoGerenteDAO;

  /**
   * Validar se o funcionário é gerente.
   * @param numeroFuncao ** integer **
   * @return boolean
   */
  public Boolean obterFuncaoGerentePorCodigo (final Integer numeroFuncao) {

    if (funcaoEmpregadoGerenteDAO.obterFuncaoGerentePorCodigo(numeroFuncao) != null) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
