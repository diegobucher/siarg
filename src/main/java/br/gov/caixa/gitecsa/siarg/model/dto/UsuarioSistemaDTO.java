/**
 * UsuarioSistema.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.model.Perfil;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;

/**
 * Modelo padrão de usuário com os atributos necessários para o sistema.
 * @author f520296
 */
public class UsuarioSistemaDTO implements Serializable{

  /** Serial. */
  private static final long serialVersionUID = -2916111293868496425L;

  /** Variável Global */
  private Unidade unidade;

  /** Variável Global */
  private UsuarioLdap usuarioLdap;

  /** Variável Global */
  private Usuario usuario;

  /** Variável Global - Verifica se o perfil do Usuário é Master. */
  private Boolean perfilGerencial;

  /** Variável Global - Verifica se o perfil do Usuário é Master. */
  private Perfil perfil;

  /** Variável Global */
  private List<UnidadeDTO> listaUnidadesExcessao;

  /**
   * Construtor Padrão sem argumentos.
   */
  public UsuarioSistemaDTO() {
    this.unidade = new Unidade();
    this.usuarioLdap = new UsuarioLdap();
    this.usuario = new Usuario();
    this.perfil = new Perfil();
    this.perfilGerencial = Boolean.FALSE;
    this.listaUnidadesExcessao = new ArrayList<>();
  }

  /**
   * Obter o valor do Atributo.
   * @return unidade
   */
  public Unidade getUnidade() {
    return unidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidade the unidade to set
   */
  public void setUnidade(final Unidade unidade) {
    this.unidade = unidade;
  }

  /**
   * Obter o valor do Atributo.
   * @return usuarioLdap
   */
  public UsuarioLdap getUsuarioLdap() {
    return usuarioLdap;
  }

  /**
   * Gravar o valor do Atributo.
   * @param usuarioLdap the usuarioLdap to set
   */
  public void setUsuarioLdap(final UsuarioLdap usuarioLdap) {
    this.usuarioLdap = usuarioLdap;
  }

  /**
   * Obter o valor do Atributo.
   * @return usuario
   */
  public Usuario getUsuario() {
    return usuario;
  }

  /**
   * Gravar o valor do Atributo.
   * @param usuario the usuario to set
   */
  public void setUsuario(final Usuario usuario) {
    this.usuario = usuario;
  }

  /**
   * Obter o valor do Atributo.
   * @return perfilGerencial
   */
  public Boolean getPerfilGerencial() {
    return perfilGerencial;
  }

  /**
   * Gravar o valor do Atributo.
   * @param perfilGerencial the perfilGerencial to set
   */
  public void setPerfilGerencial(final Boolean perfilGerencial) {
    this.perfilGerencial = perfilGerencial;
  }

  /**
   * Obter o valor do Atributo.
   * @return listaUnidadesExcessao
   */
  public List<UnidadeDTO> getListaUnidadesExcessao() {
    return listaUnidadesExcessao;
  }

  /**
   * Gravar o valor do Atributo.
   * @param listaUnidadesExcessao the listaUnidadesExcessao to set
   */
  public void setListaUnidadesExcessao(List<UnidadeDTO> listaUnidadesExcessao) {
    this.listaUnidadesExcessao = listaUnidadesExcessao;
  }

  /**
   * Obter o valor do Atributo.
   * @return perfil
   */
  public Perfil getPerfil() {
    return perfil;
  }

  /**
   * Gravar o valor do Atributo.
   * @param perfil the perfil to set
   */
  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }

}
