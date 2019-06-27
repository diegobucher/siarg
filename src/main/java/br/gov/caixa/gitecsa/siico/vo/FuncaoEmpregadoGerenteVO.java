/**
 * ViewFuncaoEmpregadoGerenteVO.java
 * Versão: 1.0.0.00
 * Data de Criação : 27-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siico.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.caixa.gitecsa.arquitetura.entity.BaseEntity;

/**
 * View Funcao Empregado Gerente VO.
 * @author f520296
 */
@Entity
@Table(name = "icovw001_siarg_funcao_gerencial", schema = "icosm001")
public class FuncaoEmpregadoGerenteVO extends BaseEntity {


  /** Serial. */
  private static final long serialVersionUID = -5609750317764313662L;

  /** Número da função retornada do usuário do LDAP. */
  @Id
  @Column(name = "nu_funcao", columnDefinition = "int2")
  private Integer id;

  /** Nome da Função baseada no código. */
  @Column(name = "no_funcao", columnDefinition = "bpchar", length = 25)
  private String nomeFuncao;

  @Override
  public Object getId() {
    return id;
  }

  @Override
  public void setId(Object id) {
    this.id = (Integer) id;

  }

  @Override
  public String getColumnOrderBy() {
    return null;
  }

  /**
   * Obter o valor do Atributo.
   * @return nomeFuncao
   */
  public String getNomeFuncao() {
    return nomeFuncao;
  }

  /**
   * Gravar o valor do Atributo.
   * @param nomeFuncao the nomeFuncao to set
   */
  public void setNomeFuncao(String nomeFuncao) {
    this.nomeFuncao = nomeFuncao;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof FuncaoEmpregadoGerenteVO)) {
      return false;
    }
    FuncaoEmpregadoGerenteVO other = (FuncaoEmpregadoGerenteVO) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


}
