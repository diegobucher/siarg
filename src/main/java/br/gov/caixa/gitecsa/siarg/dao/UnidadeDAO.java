/**
 * UnidadeDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 24-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Interface DAO para Unidade.
 * @author f520296
 */
public interface UnidadeDAO extends BaseDAO<Unidade> {

  Unidade obterUnidadeUsuarioLogado(Integer coUnidade);

  List<Unidade> obterListaUnidadesUsuarioExcessao(Long idUsuario);

  List<Unidade> obterListaUnidadesPorAbrangencia(Long idAbrangencia);

  /**
   * Método: .
   * @param idUnidade
   * @return
   */
  Unidade obterUnidadePorChaveFetch(Long idUnidade);

  /**
   * Obtém as unidades ativas dos tipos especificados e suas respectivas caixas-postais (igualmente
   * ativas).
   * @param tiposUnidade Um ou mais tipos de unidade
   * @return Lista contendo as unidades que atendem aos critérios especificados.
   */
  List<Unidade> obterUnidadesECaixasPostais(TipoUnidadeEnum... tiposUnidade);

  List<Unidade> obterUnidadesECaixasPostais(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade);

  List<Unidade> obterUnidadesDemandateByAssunto(Assunto assunto);

  Unidade findBySigla(String sigla);

  List<Unidade> obterListaUnidadesRelatorioAnaliticoPorAssunto(Long idAbrangencia);

  List<Unidade> obterUnidadesAtivas(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade);

  Unidade obterUnidadePorAbrangenciaCGC(Abrangencia abrangencia, Integer cgcUnidade);

  List<Unidade> obterListaUnidadesSUEG(Long idAbrangencia);

  Unidade obterUnidadePorAbrangenciaSigla(Abrangencia abrangencia, String sigla);

  List<Unidade> obterListaUnidadeUsuarioLogado(Integer cgcUnidade);

}
