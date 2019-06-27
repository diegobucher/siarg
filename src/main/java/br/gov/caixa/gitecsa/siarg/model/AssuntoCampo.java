package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * Entity implementation class for Entity: argsm001.argtb24_assunto_campo.
 * Tabela utilizada para armazenar a vinculação entre assunto X campos obrigatórios.
 * @author f763644
 */

@Entity
@Table(name = "argtb24_assunto_campo", schema = "argsm001")
public class AssuntoCampo implements Serializable {

	/** Serial. */
	private static final long serialVersionUID = -996277596358105284L;

	/** Identificador. Chave primária. */
	private Long id;
	
	private Assunto assunto;
	
	private CamposObrigatorios camposObrigatorios;
	
	private int quantidade;

	public AssuntoCampo() {
		super();
	}
	
	/**
	 * Campo utilizado para armazenar o Id da tabela assunto campos.
	 * @return id 
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="nu_assunto_campo", unique = true, nullable = false, columnDefinition = "serial")
	public Long getId() {
		return id;
	}
	
	/**
	 * Gravar o valor do Atributo.
	 * @param id
	 * the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nu_assunto")
	public Assunto getAssunto() { 
		return assunto;
	}

	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nu_campo")
	public CamposObrigatorios getCamposObrigatorios() {
		return camposObrigatorios;
	}

	public void setCamposObrigatorios(CamposObrigatorios camposObrigatorios) {
		this.camposObrigatorios = camposObrigatorios;
	}

	@Column(name="nu_quantidade", nullable = false)
	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

}
