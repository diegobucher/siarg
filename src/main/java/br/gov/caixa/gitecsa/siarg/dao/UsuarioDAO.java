/**
 * UsuarioDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * Interface do DAO de Usuario.
 * @author f520296.
 */
public interface UsuarioDAO extends BaseDAO<Usuario> {

  Usuario obterUsuarioExcessaoPorMatricula(String nuMatricula);

  Usuario buscarUsuarioPorMatriculaFetch(String matricula);

  List<UsuarioUnidade> buscarUsuarioUnidadePorMatricula(String matricula, Long idAbrangencia);

}
