package br.gov.caixa.gitecsa.siico.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.caixa.gitecsa.arquitetura.entity.BaseEntity;

@Entity
@Table(name = "icovw002_siarg_unidade", schema = "icosm001")
public class UnidadeSiicoVO extends BaseEntity {

  private static final long serialVersionUID = 1l;

  @Id
  @Column(name = "nu_unidade", columnDefinition = "int2")
  private Integer id;

  @Column(name = "no_unidade", columnDefinition = "bpchar", length = 35)
  private String nome;

  @Column(name = "sg_unidade", columnDefinition = "bpchar", length = 5)
  private String sigla;
  
  @Column(name = "sigla_tipo", columnDefinition = "bpchar", length = 5)
  private String siglaTipo;

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
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    UnidadeSiicoVO other = (UnidadeSiicoVO) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getSigla() {
    return sigla;
  }

  public void setSigla(String sigla) {
    this.sigla = sigla;
  }

  public String getSiglaTipo() {
    return siglaTipo;
  }

  public void setSiglaTipo(String siglaTipo) {
    this.siglaTipo = siglaTipo;
  }

  public void setId(Integer id) {
    this.id = id;
  }


}
