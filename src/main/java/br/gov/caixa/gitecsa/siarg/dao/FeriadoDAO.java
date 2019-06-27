/**
 * FeriadoDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 13-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.model.Feriado;

public interface FeriadoDAO extends BaseDAO<Feriado> {

  /**
   * Verifica se a data informada é um feriado nacional.
   * @param data data
   * @return True se a data é um feriado nacional e false caso contrário
   * @throws DataBaseException
   */
  Boolean isFeriado(final Date data) throws DataBaseException;

  /**
   * Método: Obter Lista De Datas Dos Feriados.
   * @return list
   */
  List<Date> obterListaDeDatasDosFeriados();
}
