/**
 * Menu.java
 * Versão: 1.0.0.00
 * Data de Criação : 22-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: argsm001.argtb05_menu.
 * @author f520296
 */
@Entity
@Table(name = "argtb05_menu", schema = "argsm001")
public class Menu implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 6614628516495093946L;

  /** Identificador do menu. Chave primária. */
  private Long id;

  /** Identificador do menu Pai. Chave Estrangeira. */
  private Menu menuPai;

  /** Campo que armazena o nome do menu. */
  private String nome;

  /** Campo que armazena a ordem do menu. */
  private Integer ordem;

  /** Campo que armazena a url do menu. */
  private String link;

  /** Relacionamento Muitos para muitos. */
  private List<Perfil> listaPerfis;

  /** Relacionamento Muitos para muitos. */
  private List<Menu> menuFilhosList;

  /**
   * Construtor padrão.
   */
  public Menu() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_menu", unique = true, nullable = false)
  public Long getId() {
    return this.id;
  }

  /**
   * Gravar o valor do Atributo.
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * Obter o valor do Atributo.
   * @return nome
   */
  @Column(name = "no_menu", length = 70)
  public String getNome() {
    return this.nome;
  }

  /**
   * Gravar o valor do Atributo.
   * @param nome the nome to set
   */
  public void setNome(final String nome) {
    this.nome = nome;
  }

  /**
   * Obter o valor do Atributo.
   * @return ordem
   */
  @Column(name = "nu_ordem")
  public Integer getOrdem() {
    return this.ordem;
  }

  /**
   * Gravar o valor do Atributo.
   * @param ordem the ordem to set
   */
  public void setOrdem(final Integer ordem) {
    this.ordem = ordem;
  }

  /**
   * Obter o valor do Atributo.
   * @return link
   */
  @Column(name = "de_link_interface", length = 255)
  public String getLink() {
    return this.link;
  }

  /**
   * Gravar o valor do Atributo.
   * @param link the link to set
   */
  public void setLink(final String link) {
    this.link = link;
  }

  /**
   * Obter o valor do Atributo.
   * @return listaPerfis
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(schema = "argsm001", name = "argtb06_menu_perfil", joinColumns = @JoinColumn(name = "nu_menu",
      referencedColumnName = "nu_menu"), inverseJoinColumns = { @JoinColumn(name = "nu_perfil",
      referencedColumnName = "nu_perfil") })
  public List<Perfil> getListaPerfis() {
    return this.listaPerfis;
  }

  /**
   * Gravar o valor do Atributo.
   * @param listaPerfis the listaPerfis to set
   */
  public void setListaPerfis(final List<Perfil> listaPerfis) {
    this.listaPerfis = listaPerfis;
  }

  /**
   * Obter o valor do Atributo.
   * @return menuPai
   */
  @ManyToOne
  @JoinColumn(name = "nu_menu_pai")
  public Menu getMenuPai() {
    return this.menuPai;
  }

  /**
   * Gravar o valor do Atributo.
   * @param menuPai the menuPai to set
   */
  public void setMenuPai(final Menu menuPai) {
    this.menuPai = menuPai;
  }

  /**
   * Obter o valor do Atributo.
   * @return menuFilhosList
   */
  @OneToMany(mappedBy = "menuPai")
  public List<Menu> getMenuFilhosList() {
    return this.menuFilhosList;
  }

  /**
   * Gravar o valor do Atributo.
   * @param menuFilhosList the menuFilhosList to set
   */
  public void setMenuFilhosList(final List<Menu> menuFilhosList) {
    this.menuFilhosList = menuFilhosList;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Menu)) {
      return false;
    }
    Menu other = (Menu) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
