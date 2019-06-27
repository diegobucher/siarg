package br.gov.caixa.gitecsa.siarg.model.dto;

import java.io.Serializable;

/**
 * Classe de campos obrigatórios padrão DTO.
 * 
 * @author f763644
 */

public class CamposObrigatoriosDTO implements Serializable {

	/** SERIAL. */
	private static final long serialVersionUID = 5230857028565224390L;

	/** Id do Campos Obrigatorios */
	private Long id;

	/** Nome dos campos obrigatórios */
	private String nome;
	
	/** Descricao do campo obrigatório */
	private String descricao;

	/** Tipo dos campos obrigatórios */
	private String tipo;

	/** Tamanho dos campos obrigatórios */
	private String tamanho;

	/** Mascara dos campos obrigatórios */
	private String mascara;

	public CamposObrigatoriosDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTamanho() {
		return tamanho;
	}

	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}

	public String getMascara() {
		return mascara;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
	}

}
