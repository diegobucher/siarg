/**
 * EnumInterface.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.enumerator;

import java.io.Serializable;

/**
 * Interface de enuns - base para enuns.
 * 
 * Necessário criar o método. Caso T seja String já existe na estrutura de ENUM
 * do java. "public static Enum<?> valueOf(T valor);"
 * 
 * @author f520296
 * @param <T>
 *            **Valor**
 */
public interface EnumInterface<T extends Serializable> {

	/** Constante. */
	public final String METHOD_VALUEOF_DESCRICAO = "valueOfDescricao";

	/**
	 * Retorna o código da instância do domínio.
	 * 
	 * @return Valor
	 */
	public abstract T getValor();

	/**
	 * Retorna a descrição longa da instância do domínio caso exista, se não
	 * existir, retorna a descrição.
	 * 
	 * @return Descricao
	 */
	public abstract String getDescricao();

	/**
	 * Representação em String do valor e descrição da instância do domínio.
	 * 
	 * @return ToString
	 */
	@Override
	public abstract String toString();

}
