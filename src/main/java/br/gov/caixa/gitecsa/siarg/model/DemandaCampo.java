package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Entity implementation class for Entity: argsm001.argtb25_demanda_campo.
 * Tabela utilizada para armazenar os registros obrigatórios vinculados a
 * demanda.
 * 
 * @author f763697
 */
@Entity
@Table(name = "argtb25_demanda_campo", schema = "argsm001")
public class DemandaCampo implements Comparable<DemandaCampo>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2294333185381764287L;

	private Long id;

	private Demanda demanda;

	private CamposObrigatorios camposObrigatorios;

	private String demandaCampo;
	
	private String descDemandaCampoFormatada;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_demanda_campo", unique = true, nullable = false, columnDefinition = "serial")
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the demanda
	 */
	@ManyToOne(targetEntity = Demanda.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "nu_demanda")
	public Demanda getDemanda() {
		return demanda;
	}

	/**
	 * @param demanda
	 *            the demanda to set
	 */
	public void setDemanda(Demanda demanda) {
		this.demanda = demanda;
	}

	@OneToOne
	@JoinColumn(name = "nu_campo")
	public CamposObrigatorios getCamposObrigatorios() {
		return camposObrigatorios;
	}

	public void setCamposObrigatorios(CamposObrigatorios camposObrigatorios) {
		this.camposObrigatorios = camposObrigatorios;
	}

	@Column(name = "de_demanda_campo")
	public String getDemandaCampo() {
		return demandaCampo;
	}

	/**
	 * Obter Descricao do campo obrigatório.
	 * 
	 * @param de_demanda_campo
	 */
	public void setDemandaCampo(String demandaCampo) {
		this.demandaCampo = demandaCampo;
	}

	@Transient
	public String getDescDemandaCampoFormatada() {
    return descDemandaCampoFormatada;
  }

  public void setDescDemandaCampoFormatada(String descDemandaCampoFormatada) {
    this.descDemandaCampoFormatada = descDemandaCampoFormatada;
  }

  @Override
	public int compareTo(DemandaCampo o) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(this.id, o.getId());
		
		return compare.toComparison();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashcodeBuilder = new HashCodeBuilder();
		hashcodeBuilder.append(this.id);
		hashcodeBuilder.append(this.demanda);
		hashcodeBuilder.append(this.demandaCampo);
		hashcodeBuilder.append(this.camposObrigatorios);
		
		return hashcodeBuilder.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		DemandaCampo objeto = (DemandaCampo) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.id, objeto.getId());
		equalsBuilder.append(this.demanda, objeto.getDemanda());
		equalsBuilder.append(this.demandaCampo, objeto.getDemandaCampo());
		equalsBuilder.append(this.camposObrigatorios, objeto.getCamposObrigatorios());
		
		return equalsBuilder.build();
	}

}
