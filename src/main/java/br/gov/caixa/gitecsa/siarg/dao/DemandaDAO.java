/**
 * DemandaDAO.java
 * Versão: 1.0.0.00
 * 05/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeDemandaPorUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioIndicadorReaberturaDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.model.relatorio.DemandaRelatorio;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

/**
 * Inerface: Interface de acesso aos DAOs de demanda.
 * @author f520296
 */
public interface DemandaDAO extends BaseDAO<Demanda> {

  /**
   * Método: Obter Lista Demandas Prioridades.
   * @param idCaixaPostal idCaixaPostal
   * @param envolvimentoFiltro envolvimentoFiltro
   * @param situacaoDemaisFiltro situacaoDemaisFiltro
   * @return list
   */
  List<Demanda> obterListaDemandasPrioridades(Long idCaixaPostal, SituacaoEnum situacaoPriori);

  /**
   * Método: Obter Lista Demais Demandas.
   * @param idCaixaPostal idCaixaPostal
   * @param envolvimentoFiltro envolvimentoFiltro
   * @param situacaoDemaisFiltro situacaoDemaisFiltro
   * @return list
   */
  List<Demanda> obterListaDemaisDemandas(Long idCaixaPostal, EnvolvimentoEnum envolvimentoFiltro,
      SituacaoEnum situacaoDemaisFiltro);

  /**
   * Método: .
   * @param caixaPostalResponsavel
   */
  List<Demanda> obterListaDemandasExternasPorAendimentoECaixaPostal(Long idCaixaPostalResponsavel, SituacaoEnum situacao);

  /**
   * Obtém a demanda pelo Id fazendo fetch nos campos necessarios do tratar demanda.
   */
  public Demanda findByIdFetch(Long id);

  List<Demanda> findFilhosByIdPaiFetch(Long id);

  Long obterQuantidadeDemandasAbertasDaCaixaPostal(Long idCaixaPostal);

  List<Demanda> findFilhosConsultaByIdPaiECaixaPostalFetch(Long id, CaixaPostal caixaDemandante);

  List<Demanda> findFilhosIniciaisByIdPaiPostalFetch(Long id);

  Long obterQuantidadeDemandasPorAssunto(Assunto assunto);

  List<Demanda> obterDemandasEncaminhadosExternasNoPeriodo(Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);

  List<Demanda> obterListaDemandasAbertasParaExtrato();

  List<Demanda> obterListaDemandasQuestionadasDemandante();

  List<Demanda> relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(Date dataInicio, Date datatFim, Unidade unidade);

  List<Demanda> relatorioUnidadesDemandantesPorSubordinacao(Date dataInicio, Date datatFim, Unidade unidade, List<Unidade> listaUnidades, Abrangencia abrangenciaSelecionada);

  List<Demanda> obterListaDemandasAbertasPorSuegPeriodoRealizadas(Abrangencia abrangencia, Unidade unidade, Date dataInicio, Date datatFim, List<Long> listaIdsUnidades);

  List<Demanda> obterListaDemandasAbertasPorSuegPeriodoATratar(Abrangencia abrangencia, Unidade unidade, Date dataInicio, Date datatFim, List<Long> listaIdsUnidades);

  List<Demanda> obterListaDemandasPorUnidadesResponsavelPeloAssunto(List<Long> idUnidadeList,
      List<SituacaoEnum> situacaoEnumList);

  List<Demanda> findByNumeroDemanda(List<Long> listaDemandaArquivo);

  Boolean existeDemandasPorUnidadesResponsavelPeloAssunto(List<Long> idUnidadeList, List<SituacaoEnum> situacaoEnumList);
  
  List<Demanda> obterListaDemandasRelatorioAssuntoPeriodo(Long idAssunto, Date periodoDtInicial, Date periodoDtFinal, SituacaoEnum... situacao );

  List<Demanda> obterDemandasEmAbertoPorAssunto(UnidadeDTO unidade, Assunto assunto, Date dataAberturaInicio,
      Date dataEncerramento, Abrangencia abrangenciaSelecionada);
  
  List<Demanda> obterDemandasAguardandoUnidade (Date dataInicio, Date dataFim, Abrangencia abrangenciaSelecionada, Long idUnidade);

  List<Demanda> obterDemandasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);

  List<Demanda> findByIdFetchList(List<Demanda> demandasList);

  List<Demanda> obterDemandasNaExternaAguardandoUnidade(Date dataInici, Date dataFim, Long idUnidade);
  
  List<Demanda> obterDemandasPorAcaoAtendimento(Date dataInicial, Date dataFinal, String caixaPostal, String situacao);

  Demanda findByIdAndAbrangenciaFetch(Long idDemanda, Long idAbrangencia);
  
  List<Demanda> obterDemandasWSConsulta(FiltrosConsultaDemandas filtro);

  List<Demanda> obterDemandasReabertasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);

  List<QuantidadeDemandaPorUnidadeDTO> obterDemandasFechadas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal);

  List<Demanda> obterListaDemandasFechadas(String siglaUnidade, Date dataInicial, Date dataFinal);

  List<QuantidadeDemandaPorUnidadeDTO> obterDemandasAbertas(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);

  List<Demanda> obterListaDemandasAbertas(String siglaUnidade, Date dataInicial, Date dataFinal);

  List<QuantidadeDemandaPorUnidadeDTO> obterDemandasCanceladas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal);

  List<Demanda> obterListaDemandasCanceladas(String siglaUnidade, Date dataInicial, Date dataFinal);

  List<QuantidadeDemandaPorUnidadeDTO> obterDemandasReabertas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal);

 // List<QuantidadeDemandaPorUnidadeDTO> obterDemandasFechadasForaDoPrazo(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);

  List<Demanda> obterDemandasFechadasTotal(Long idUnidade, Long idAbrangencia, Date dataInicial, Date dataFinal);

  Atendimento consultaAtendimento(Long nuDemanda);

List<Demanda> obterListaDemandasReabertasTotal(Long idUnidade, Date dataInicial, Date dataFinal);

 List<RelatorioIndicadorReaberturaDTO> obterIndicadorReaberturaPorUnidadePeriodo(Long idUnidade, Long idAbrangencia, Date dataInicial, Date dataFinal);
}
