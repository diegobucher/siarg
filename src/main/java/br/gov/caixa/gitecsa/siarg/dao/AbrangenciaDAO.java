/**
 * AbrangenciaDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 28-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;

public interface AbrangenciaDAO extends BaseDAO<Abrangencia> {

  List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(Long idUsuario);
  
  List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(String matricula);

  List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(String matricula);

  Abrangencia obterAbrangenciaPorDemanda(Long idDemanda);

}
