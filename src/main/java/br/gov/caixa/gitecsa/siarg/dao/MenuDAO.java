/**
 * MenuDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Menu;

/**
 * Interface DAO para menu.
 * @author f520296
 */
public interface MenuDAO extends BaseDAO<Menu> {

  /**
   * Obter a lista de Menus Pais.
   * @param perfil **Perfil
   * @param idAbrangencia **
   * @return list
   */
  List<Menu> obterListaMenusPaisPorPerfil(Long perfil, Long idAbrangencia);

  Menu findByIdEager(Long id);

  Menu findByIdEager(Long idMenu, Long idAbrangencia);

}
