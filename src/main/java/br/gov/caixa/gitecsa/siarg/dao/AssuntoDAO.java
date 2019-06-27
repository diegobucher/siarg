/**
 * AssuntoDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public interface AssuntoDAO extends BaseDAO<Assunto> {
  /**
   * Obtém todos os assuntos.
   * @param abrangencia 
   * @return Lista de assuntos.
   */
  List<Assunto> findAtivos(Abrangencia abrangencia);

  /**
   * Verifica se a unidade dada é um dos demandantes do assunto dado.
   * @param assunto Assunto
   * @param unidade Unidade
   * @return True se a unidade é demandante e false caso contrário.
   */
  Boolean isDemandanteAssunto(Assunto assunto, Unidade unidade) throws DataBaseException;

  /**
   * Obtém o assunto a partir do id dado.
   * @param id Identificador chave
   * @return Assunto ou null caso nenhum registro seja encontrado.
   * @throws DataBaseException
   */
  Assunto findByIdEager(Long id) throws DataBaseException;

  /**
   * Consulta os fluxos de assuntos de um determinado tipo para o assunto informado.
   * @param assunto Assunto
   * @param tipo Tipo do fluxo
   * @return Fluxos de assuntos
   */
  List<FluxoAssunto> findFluxoAssuntoByAssuntoTipo(Assunto assunto, TipoFluxoEnum tipo);

  /**
   * Consulta as caixas-postais cadastradas como observadores do assunto.
   * @param assunto Assunto
   * @return Lista de caixas-postais pré-definidas como observadores.
   */
  List<CaixaPostal> findObservadoresByAssunto(Assunto assunto);

  /**
   * Método: .
   * @return
   */
  List<Assunto> obterAssuntosFetchPai();

  /**
   * Método: Pesquisar relacao entre assuntos.
   * @param abrangencia abrangencia
   * @param listaUnidades listaUnidades
   * @return list
   */
  List<Assunto> pesquisarRelacaoAssuntos(Abrangencia abrangencia, UnidadeDTO unidade, List<UnidadeDTO> unidadeList);

  List<Assunto> findCategoriasNaoExcluidos(Abrangencia abrangencia);

  List<Assunto> findAssuntosByPaiNaoExcluidos(Assunto assuntoPai);

  List<Assunto> findAssuntosByPai(Assunto assuntoPai);

  List<Long> listNumeroAssunto();

  List<Assunto> findAllByAbrangencia(Abrangencia abrangencia);

  List<Assunto> findAllBy(Abrangencia abrangencia);
  
  List<Assunto> pesquisarAssuntosPorAbrangenciaEUnidade(Abrangencia abrangencia, UnidadeDTO unidade);

  List<RelatorioPeriodoPorAssuntoDTO> findRelatorioPeriodoPorAssunto(Long idAbrangencia, Long idUnidade, Date dtInicioPerInicial, Date dtFimPerInicial, Date dtInicioPerFinal, Date dtFimPerFinal);

  List<Assunto> pesquisarAssuntosPorAbrangencia(Abrangencia abrangencia);

}
