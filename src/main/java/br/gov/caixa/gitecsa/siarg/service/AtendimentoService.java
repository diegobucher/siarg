/**
 * AendimentoService.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeIteracoesDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;

/**
 * Classe: .
 * @author f520296
 */
@Stateless
public class AtendimentoService extends AbstractService<Atendimento> {

  /** Serial. */
  private static final long serialVersionUID = -4070663911455635905L;

  @Inject
  private AtendimentoDAO atendimentoDAO;
  
  @Inject
  private FeriadoService feriadoService;

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.BaseService#consultar(java.lang.Object)
   */
  @Override
  public List<Atendimento> consultar(Atendimento entity) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaCamposObrigatorios(java.lang
   * .Object)
   */
  @Override
  protected void validaCamposObrigatorios(Atendimento entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegras(java.lang.Object)
   */
  @Override
  protected void validaRegras(Atendimento entity) {

  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegrasExcluir(java.lang.Object)
   */
  @Override
  protected void validaRegrasExcluir(Atendimento entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#getDAO()
   */
  @Override
  protected BaseDAO<Atendimento> getDAO() {
    return this.atendimentoDAO;
  }

  /**
   * Método: Obter Ultimo Atendimento Por Demanda.
   * @param id idDemanda
   * @return atendimento
   */
  public Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda, Long idCaixaPostalResponsavel) {
    return this.atendimentoDAO.obterUltimoAtendimentoPorDemanda(idDemanda, idCaixaPostalResponsavel);
  }
  
  public Atendimento obterUltimoAtendimentoPorDemanda(Long idDemanda) {
    return this.atendimentoDAO.obterUltimoAtendimentoPorDemanda(idDemanda);
  }

  /**
   * Método: Obter Ultimo Atendimento Por Demanda com responsável externa.
   * @param id idDemanda
   * @return atendimento
   */
  public Atendimento obterUltimoAtendimentoPorDemandaCaixaPostalExterna(Long idDemanda) {
    return this.atendimentoDAO.obterUltimoAtendimentoPorDemandaCaixaPostalExterna(idDemanda);
  }
  
  public List<Atendimento> obterAtendimentosPorDemanda(Long idDemanda){
    return this.atendimentoDAO.obterAtendimentosPorDemanda(idDemanda);
  }

  public List<String> obterUltimasRespostasPorAssuntoCaixa(Long idAssunto, String texto){
    return this.atendimentoDAO.obterUltimasRespostasPorAssunto(idAssunto, texto);
  }

  public List<Atendimento> obterListaAtendimentosEncaminhadosExternas(Abrangencia abrangenciaSelecionada, Date dataInicial,
      Date dataFinal) {
    return  atendimentoDAO.obterListaAtendimentosEncaminhadosExternas(abrangenciaSelecionada, dataInicial, dataFinal);
  }

  public Atendimento obterUltimoAtendimentoPorDemandaSemFluxoDemanda(Long idDemanda, Long idCxpResp) {
    return atendimentoDAO.obterUltimoAtendimentoPorDemandaSemFluxoDemanda(idDemanda, idCxpResp);
  }

  public Atendimento obterAtendimentoComDemandaFluxoDemanda(Long idAtendimento) {
    return atendimentoDAO.obterAtendimentoComDemandaFluxoDemanda(idAtendimento);
  }
  
  public Atendimento findByIdFetch(Long idAtendimento) {
    return atendimentoDAO.findByIdFetch(idAtendimento);
  }
  
  public List<QuantidadeIteracoesDTO> obterListaAtendimentosPorUnidade(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal){
	  
	  List<QuantidadeIteracoesDTO> listaQuantidadeIteracoesDTO = new ArrayList<>();
	  
	  Double tempoMedioQuestionar = 0d;
	  long totalIteracoesQuestionar = 0;
	  
	  Double tempoMedioConsultar = 0d;
	  long totalIteracoesConsultar = 0;
	  
	  Double tempoMedioEncaminhar = 0d;
	  long totalIteracoesEncaminhar = 0;
	  
	  Double tempoMedioResponder = 0d;
	  long totalIteracoesResponder = 0;
	  
	  Double tempoMedioResp = 0d;
	  Double tempoMedioEnc = 0d;
	  Double tempoMedioCons = 0d;
	  Double tempoMedioQuest = 0d;
	  
	  long qtdIteracoesResp = 0;
	  long qtdIteracoesEnc = 0;
	  long qtdIteracoesCons = 0;
	  long qtdIteracoesQuest = 0;
	
	  List<Atendimento> atendimentos = atendimentoDAO.obterListaAtendimentosPorUnidade(idUnidade, abrangenciaSelecionada, dataInicial, dataFinal);
	  
	  Map<String, QuantidadeIteracoesDTO> mapDto = new HashMap<>();
	  
	  if(!atendimentos.isEmpty()) {
	    
	    List<Date> feriadosList = feriadoService.obterListaDeDatasDosFeriados();
		  
		  for(Atendimento atendimento : atendimentos) {
			  if(atendimento.getAcaoEnum() != null &&
					  (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR) || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR) ||
							  atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ENCAMINHAR) || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER))) {
			  
				  if(!mapDto.containsKey(atendimento.getFluxoDemanda().getCaixaPostal().getSigla())){
					  QuantidadeIteracoesDTO dtoNovo = new QuantidadeIteracoesDTO();
					  dtoNovo.setCaixaPostal(atendimento.getFluxoDemanda().getCaixaPostal().getSigla());
					  mapDto.put(atendimento.getFluxoDemanda().getCaixaPostal().getSigla(), dtoNovo);
				  }
				  
				  QuantidadeIteracoesDTO quantidadeIteracoesDTO = mapDto.get(atendimento.getFluxoDemanda().getCaixaPostal().getSigla());
				  
				  if(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)) {
					  tempoMedioQuestionar = 0d;
					  quantidadeIteracoesDTO.setQtdeIteracoesQuestionar(quantidadeIteracoesDTO.getQtdeIteracoesQuestionar() + 1);
					  tempoMedioQuestionar = feriadoService.calcularQtdDiasUteis(atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), feriadosList);
					  quantidadeIteracoesDTO.setTempoMedioParcialQuest(quantidadeIteracoesDTO.getTempoMedioParcialQuest() + tempoMedioQuestionar);
					  totalIteracoesQuestionar += 1;
				  }
				 if(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)){
					 tempoMedioConsultar = 0d;
					 quantidadeIteracoesDTO.setQtdeIteracoesConsultar(quantidadeIteracoesDTO.getQtdeIteracoesConsultar() + 1); 
					 tempoMedioConsultar = feriadoService.calcularQtdDiasUteis( atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), feriadosList);
					 quantidadeIteracoesDTO.setTempoMedioParcialConsu(quantidadeIteracoesDTO.getTempoMedioParcialConsu() + tempoMedioConsultar);
					 totalIteracoesConsultar += 1;
				 }
				 if(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ENCAMINHAR)){
					 tempoMedioEncaminhar = 0d;
					 quantidadeIteracoesDTO.setQtdeIteracoesEncaminhar(quantidadeIteracoesDTO.getQtdeIteracoesEncaminhar() + 1);
					 tempoMedioEncaminhar = feriadoService.calcularQtdDiasUteis(atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), feriadosList);
					 quantidadeIteracoesDTO.setTempoMedioParcialEnc(quantidadeIteracoesDTO.getTempoMedioParcialEnc() + tempoMedioEncaminhar);
					 totalIteracoesEncaminhar += 1;
				  }
				 if(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)){
					 tempoMedioResponder = 0d;
					 quantidadeIteracoesDTO.setQtdeIteracoesResponder(quantidadeIteracoesDTO.getQtdeIteracoesResponder() + 1);
				 	 tempoMedioResponder = feriadoService.calcularQtdDiasUteis(atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), feriadosList);
					 quantidadeIteracoesDTO.setTempoMedioParcialResp(quantidadeIteracoesDTO.getTempoMedioParcialResp() + tempoMedioResponder);
					 totalIteracoesResponder += 1;
				 }
			  }
		  }
	  }
	  
	  for(String key : mapDto.keySet()) {
		  listaQuantidadeIteracoesDTO.add(mapDto.get(key));
	  }
	  
	  for(QuantidadeIteracoesDTO qtdIteracoes : listaQuantidadeIteracoesDTO) {
		  
		  qtdIteracoesResp = qtdIteracoes.getQtdeIteracoesResponder();
		  qtdIteracoesEnc = qtdIteracoes.getQtdeIteracoesEncaminhar();
		  qtdIteracoesCons = qtdIteracoes.getQtdeIteracoesConsultar();
		  qtdIteracoesQuest = qtdIteracoes.getQtdeIteracoesQuestionar();
		  
		  if(qtdIteracoesResp > 0) {
			  tempoMedioResp = qtdIteracoes.getTempoMedioParcialResp() / qtdIteracoesResp;
		  	  qtdIteracoes.setTempoMedioResponder(tempoMedioResp);
		  }
		  if(qtdIteracoesEnc > 0) {
			  tempoMedioEnc = qtdIteracoes.getTempoMedioParcialEnc() / qtdIteracoesEnc;
		  	  qtdIteracoes.setTempoMedioEncaminhar(tempoMedioEnc);
		  }
		  if(qtdIteracoesCons > 0) {
			  tempoMedioCons = qtdIteracoes.getTempoMedioParcialConsu() / qtdIteracoesCons;
		      qtdIteracoes.setTempoMedioConsultar(tempoMedioCons);
		  }
		  if(qtdIteracoesQuest > 0) {
			  tempoMedioQuest = qtdIteracoes.getTempoMedioParcialQuest() / qtdIteracoesQuest;
		  	  qtdIteracoes.setTempoMedioQuestionar(tempoMedioQuest);
		  }
		  
		  qtdIteracoes.setTotalIteracoesQuestionar(totalIteracoesQuestionar);
		  qtdIteracoes.setTotalIteracoesConsultar(totalIteracoesConsultar);
		  qtdIteracoes.setTotalIteracoesEncaminhar(totalIteracoesEncaminhar);
		  qtdIteracoes.setTotalIteracoesResponder(totalIteracoesResponder);
		  
		  
		  
		  
		  
	  }
	 
	  return listaQuantidadeIteracoesDTO;
  }
  
  public Atendimento obterAtendimentoAnteriorPorDemandaCaixaPostal(Long idDemanda, Long idAtendimento, Long idCaixaPostal) {
	  return atendimentoDAO.obterAtendimentoAnteriorPorDemandaCaixaPostal(idAtendimento, idDemanda, idCaixaPostal);	  
  }
  
}
