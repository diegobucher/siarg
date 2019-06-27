/**
 * UnidadeService.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.model.Perfil;

/**
 * Classe de serviços de Usuario.
 */
@Stateless
public class PerfilService {

  @Inject
  private PerfilDAO perfilDAO;

  /** Construtor Padrão. */
  public PerfilService() {
    super();
  }
  
  public List<Perfil> findAll(){
    return perfilDAO.findAll();
  }

}
