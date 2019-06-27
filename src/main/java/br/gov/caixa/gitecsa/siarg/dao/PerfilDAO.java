/**
 * PerfilDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Perfil;

/**
 * Interface DAO para Perfil.
 * @author f520296
 */
public interface PerfilDAO extends BaseDAO<Perfil> {

  Perfil obterPerfilPorId(Long id);

}
