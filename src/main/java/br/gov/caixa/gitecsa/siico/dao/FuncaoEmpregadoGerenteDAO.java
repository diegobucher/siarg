/**
 * FuncaoEmpregadoGerenteDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 27-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siico.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siico.vo.FuncaoEmpregadoGerenteVO;

public interface FuncaoEmpregadoGerenteDAO extends BaseDAO<FuncaoEmpregadoGerenteVO> {

  FuncaoEmpregadoGerenteVO obterFuncaoGerentePorCodigo(Integer numerofuncao);

}
