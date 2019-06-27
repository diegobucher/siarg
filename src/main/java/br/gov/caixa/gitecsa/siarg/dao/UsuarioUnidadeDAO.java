/**
 * UsuarioUnidadeDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 24-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * Interface DAO para Usuario Unidade.
 * @author f520296
 */
public interface UsuarioUnidadeDAO  extends BaseDAO<UsuarioUnidade>{

  UsuarioUnidade findByIdFetch(Long id);

  boolean existeVinculacaoConflitante(UsuarioUnidade usuarioUnidade);

}
