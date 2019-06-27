/**
 * AtendimentoDAO.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;

/**
 * Interface DAO de Atendimento.
 * @author f520296
 */
public interface AtendimentoDAO extends BaseDAO<Atendimento> {

  /**
   * Método: .
   * @param idDemanda
   * @return
   */
  Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda, Long idCaixaPostalResponsavel);
  
  Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda);

  Atendimento obterUltimoAtendimentoPorDemandaCaixaPostalExterna(Long idDemanda);

  public List<Atendimento> obterAtendimentosPorDemanda(Long idDemanda);

  List<String> obterUltimasRespostasPorAssunto(Long idAssunto, String texto);

  List<Atendimento> obterListaAtendimentosEncaminhadosExternas(Abrangencia abrangenciaSelecionada, Date dataInicial,
      Date dataFinal);

  Atendimento obterUltimoAtendimentoPorDemandaSemFluxoDemanda(Long idDemanda, Long idCxpResp);

  Atendimento obterAtendimentoComDemandaFluxoDemanda(Long idAtendimento);

  Atendimento findByIdFetch(Long idAtendimento);

  List<Atendimento> obterListaAtendimentosPorUnidade(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal);
  Atendimento obterAtendimentoAnteriorPorDemandaCaixaPostal(Long idAtendimento, Long idDemanda, Long idCaixaPostal);

}
