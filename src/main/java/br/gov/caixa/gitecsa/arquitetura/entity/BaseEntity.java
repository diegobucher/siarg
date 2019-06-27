/**
 * BaseEntity.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * Classe abstrata da arquitetura - Base Entity.
 * @author f520296
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = -2426297005110989046L;

  /**
   * Obter Id do objeto.
   * @return object.
   */
  public abstract Object getId();

  /**
   * Gravar Id do Objeto.
   * @param id **di**
   */
  public abstract void setId(final Object id);

  /**
   * Coluna Order By.
   * @return string.
   */
  public abstract String getColumnOrderBy();

  /**
   * Hashcode Padrão.
   * @return result
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    return result;
  }

  /**
   * Equals Padrão.
   * @param obj **Object**
   * @return boolean.
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BaseEntity other = (BaseEntity) obj;
    if (getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!getId().equals(other.getId())) {
      return false;
    }
    return true;
  }
}
