/**
 * DemandaService.java
 * Versão: 1.0.0.00
 * 05/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessRollbackException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dto.AnaliticoDemandantesDemandadosDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandantePorSubrodinacaoDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandasAguardandoUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandasEmAbertoDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoDemandaDTO;
import br.gov.caixa.gitecsa.siarg.dto.KeyGroupValuesCollection;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeDemandaPorUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioGeralVisaoSuegPorUnidadesDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioIndicadorReaberturaDTO;
import br.gov.caixa.gitecsa.siarg.email.EmailService;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.MotivoReaberturaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;
import br.gov.caixa.gitecsa.siarg.ws.model.AtendimentoDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.DemandaAbertaDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

/**
 * Classe de serviço de demandas.
 * 
 * @author f520296
 */
@Stateless
public class DemandaService extends AbstractService<Demanda> {

  /** Serial. */
  private static final long serialVersionUID = 1L;

  @Inject
  private DemandaDAO demandaDAO;

  /** Service que contém as regras de negócio da entidade assunto */
  @Inject
  private AssuntoService assuntoService;

  /** Service que contém as regras de negócio da entidade feriado */
  @Inject
  private FeriadoService feriadoService;

  /** Service que contém as regras de negócio da entidade Atendimento */
  @Inject
  private AtendimentoService atendimentoService;

  @Inject
  private CaixaPostalService caixaPostalService;
  
  @Inject
  private FluxoDemandaService fluxoDemandaService;
  
  @Inject
  private EmailService emailService;
  
  @Inject
  private DemandaContratoService demandaContratoService;
  
  @Inject
  private UnidadeService unidadeService;

  @Inject
  private ParametroService parametroService;
  
  @Inject
  private AbrangenciaService abrangenciaService;
  
  public static final String SEPARADOR_FLUXO_DEMANDA = " > ";

  /** Formatação do nome do agrupamento de rede */
  private static final String PREFIXO_AGRUPAMENTO_REDE = "Rede %s";

  /** Número de caracteres para o substring da Rede */
  private static final int NUM_CHARS_SUBSTRING_REDE = 5;
  
  @Inject
  protected Logger logger;
  
  @Override
  public List<Demanda> consultar(Demanda entity) throws Exception {
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Demanda entity) {
    /**
     * Método não utilizado
     */
  }

  @Override
  protected void validaRegras(Demanda entity) {
    /**
     * Método não utilizado
     */
  }

  @Override
  protected void validaRegrasExcluir(Demanda entity) {
    /**
     * Método não utilizado
     */
  }

  @Override
  protected BaseDAO<Demanda> getDAO() {
    return this.demandaDAO;
  }

  /**
   * Método: Obter a lista de prioridades com filtro.
   * 
   * @param idCaixaPostal
   *          idCaixaPostal
   * @param responsavel
   *          responsavel
   * @param situacaoPriori
   *          situacaoPriori
   * @return list
   */
  public List<DemandaDTO> obterListaDemandasPrioridadesDTO(Long idCaixaPostal, String responsavel, SituacaoEnum situacaoPriori,
      List<Date> datasFeriadosList, String corDemandaFiltro, List<Assunto> listaAssuntos) {
    List<DemandaDTO> demandaPrioridadeDTOList = new ArrayList<>();
    List<Demanda> demandasPrioridadelist = new ArrayList<>();

    if (responsavel == null) {
      demandasPrioridadelist = this.demandaDAO.obterListaDemandasPrioridades(idCaixaPostal, situacaoPriori);
      demandasPrioridadelist.addAll(this.obterListaDemandasExternas(idCaixaPostal, situacaoPriori));
    } else if ("@Externa".equals(responsavel)) {
      demandasPrioridadelist = this.obterListaDemandasExternas(idCaixaPostal, situacaoPriori);
    } else {
      demandasPrioridadelist = this.demandaDAO.obterListaDemandasPrioridades(idCaixaPostal, situacaoPriori);
    }

    if (StringUtils.isNotBlank(corDemandaFiltro)) {
      if (!Constantes.COR_PADRAO_DEMANDA.equals(corDemandaFiltro)) {
        List<Demanda> tempList = new ArrayList<>();
        for (Demanda demanda : demandasPrioridadelist) {
          if (demanda.getCor().equals(corDemandaFiltro)) {
            tempList.add(demanda);
          }
        }
        demandasPrioridadelist = new ArrayList<>();
        demandasPrioridadelist.addAll(tempList);
      }
    }

    if (!demandasPrioridadelist.isEmpty()) {
      Collections.sort(demandasPrioridadelist);
      Collections.reverse(demandasPrioridadelist);
      DemandaDTO demandaDTOTemp;
      for (Demanda demanda : demandasPrioridadelist) {
        demandaDTOTemp = new DemandaDTO();
        this.obterDemandaDTOPreenchidaPorDemanda(demanda, demandaDTOTemp, listaAssuntos);
        if (SituacaoEnum.ABERTA.equals(demanda.getSituacao())) {
          processarDemandaAberta(datasFeriadosList, demandaDTOTemp, demanda, idCaixaPostal);
        }
        demandaPrioridadeDTOList.add(demandaDTOTemp);
      }
    }
    return demandaPrioridadeDTOList;
  }

  private void processarDemandaAberta(List<Date> datasFeriadosList, DemandaDTO demandaDTOTemp, Demanda demanda, Long idCaixaPostal) {
    
    Atendimento atendimento;
    if (TipoUnidadeEnum.ARROBA_EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade())
        || TipoUnidadeEnum.EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade())) {
      atendimento = this.atendimentoService.obterUltimoAtendimentoPorDemandaCaixaPostalExterna(demanda.getId());
    } else {
      atendimento =
          this.atendimentoService.obterUltimoAtendimentoPorDemanda(demanda.getId(), demanda.getCaixaPostalResponsavel().getId());
    }
    
    Integer prazoCaixaPostal = 0;
    Atendimento atendimentoAnterior = atendimentoService.obterAtendimentoAnteriorPorDemandaCaixaPostal(demanda.getId(), atendimento.getId(), 
    		atendimento.getFluxoDemanda().getCaixaPostal().getId());
    if (atendimentoAnterior != null && AcaoAtendimentoEnum.QUESTIONAR.equals(atendimentoAnterior.getAcaoEnum())) {
    	Integer qtdDiasTempoResposta = this.feriadoService.calcularQtdDiasUteisEntreDatas(atendimentoAnterior.getDataHoraRecebimento(),
    				atendimentoAnterior.getDataHoraAtendimento());
    	
		Integer prazoQuestionamento = this.feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(),
				new Date());
		
		prazoCaixaPostal = (atendimento.getFluxoDemanda().getPrazo() - prazoQuestionamento) - qtdDiasTempoResposta;
	}else {
		prazoCaixaPostal = this.obterPrazoResponsavelAtual(demanda, datasFeriadosList, atendimento);
	}
    
    //ATUALIZANDO OS PRAZOS
    demandaDTOTemp.setPrazoDemanda(this.obterPrazoDemanda(demanda, datasFeriadosList));
    demandaDTOTemp.setPrazoCaixaPostal(prazoCaixaPostal);
    demandaDTOTemp.setCamposList(demanda.getCamposList());
    
    if(atendimento != null && atendimento.getAcaoEnum() != null &&
        atendimento.getAcaoEnum().getValor().equals(AcaoAtendimentoEnum.RASCUNHO.getValor())) {
      atendimento = atendimentoService.findByIdFetch(atendimento.getId());
      if(atendimento.getFluxoDemanda().getCaixaPostal().getId().equals(idCaixaPostal)) {
        //ATUALIZAR A FLAG SITUACAO RASCUNHO
        demandaDTOTemp.setFlagRascunho(Boolean.TRUE);
      }
    }
  }

  /**
   * Método: obter Lista Demais Demandas DTO com filtro.
   * 
   * @param idCaixaPostal
   *          idCaixaPostal
   * @param envolvimentoFiltro
   *          envolvimentoFiltro
   * @param situacaoDemaisFiltro
   *          situacaoDemaisFiltro
   * @return list
   */
  public List<DemandaDTO> obterListaDemaisDemandasDTO(Long idCaixaPostal, EnvolvimentoEnum envolvimentoFiltro,
      SituacaoEnum situacaoDemaisFiltro, List<Date> datasFeriadosList, String corDemandaFiltroDemais,
      List<Assunto> listaAssuntos) {
    List<DemandaDTO> demaisDemandasDTOList = new ArrayList<>();
    List<Demanda> demaisDemandaslist =
        this.demandaDAO.obterListaDemaisDemandas(idCaixaPostal, envolvimentoFiltro, situacaoDemaisFiltro);

    if (StringUtils.isNotBlank(corDemandaFiltroDemais)) {
      if (!Constantes.COR_PADRAO_DEMANDA.equals(corDemandaFiltroDemais)) {
        List<Demanda> tempList = new ArrayList<>();
        for (Demanda demanda : demaisDemandaslist) {
          if (demanda.getCor().equals(corDemandaFiltroDemais)) {
            tempList.add(demanda);
          }
        }
        demaisDemandaslist = new ArrayList<>();
        demaisDemandaslist.addAll(tempList);
      }
    }

    if (!demaisDemandaslist.isEmpty()) {
      Collections.sort(demaisDemandaslist);
      Collections.reverse(demaisDemandaslist);
      DemandaDTO demandaDTOTemp;
      for (Demanda demanda : demaisDemandaslist) {
        demandaDTOTemp = new DemandaDTO();
        this.obterDemandaDTOPreenchidaPorDemanda(demanda, demandaDTOTemp, listaAssuntos);
        if (SituacaoEnum.ABERTA.equals(demanda.getSituacao())) {
          processarDemandaAberta(datasFeriadosList, demandaDTOTemp, demanda, idCaixaPostal);
        }
        demaisDemandasDTOList.add(demandaDTOTemp);
      }
    }
    return demaisDemandasDTOList;
  }

  /**
   * Método: Preencher o objeto DemandaDTO baseado na demanda.
   * 
   * @param demanda
   *          demanda
   * @return demandaDTO
   */
  private void obterDemandaDTOPreenchidaPorDemanda(Demanda demanda, DemandaDTO demandaDTOTemp, List<Assunto> listaAssuntos) {
    demandaDTOTemp.setNumero(demanda.getId());
    demandaDTOTemp.setSituacao(demanda.getSituacao());
    demandaDTOTemp.setDataAberturaDemanda(demanda.getDataHoraAbertura());
    demandaDTOTemp.setDemandante(demanda.getCaixaPostalDemandante().getSigla());
    demandaDTOTemp.setMatricula(this.obterMatriculaPorSituacao(demanda));
    demandaDTOTemp.setAssunto(this.assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaAssuntos));
    demandaDTOTemp.setTitulo(demanda.getTitulo());
    demandaDTOTemp.setResponsavelAtual(demanda.getCaixaPostalResponsavel().getSigla());
    demandaDTOTemp.setColorStatus(demanda.getCor());
    demandaDTOTemp
        .setExterna(TipoUnidadeEnum.ARROBA_EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade())
            || TipoUnidadeEnum.EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade()));
    if ((demanda.getDemandaPai() != null) && (demanda.getDemandaPai().getId() != null)) {
      demandaDTOTemp.setFlagDemandaFilha(Boolean.TRUE);
      demandaDTOTemp.setIdDemandaPai(demanda.getDemandaPai().getId());
    } else {
      demandaDTOTemp.setFlagDemandaFilha(Boolean.FALSE);
    }
    demandaDTOTemp.setFlagConsulta(TipoDemandaEnum.CONSULTA.equals(demanda.getTipoDemanda()) ? Boolean.TRUE : Boolean.FALSE);

    demandaDTOTemp.setFlagDemandaPai(demanda.getFlagDemandaPai());
    if (SituacaoEnum.RASCUNHO.equals(demanda.getSituacao())) {
      demandaDTOTemp.setFlagRascunho(Boolean.TRUE);
    } else {
      demandaDTOTemp.setFlagRascunho(Boolean.FALSE);
    }
    demandaDTOTemp.setListaContratosString(demanda.getListaContratosString());
  }

  /**
   * Método: Obter a Matrícula pela Situacão.
   * 
   * @param situacao
   *          situacao
   * @return String
   */
  private String obterMatriculaPorSituacao(Demanda demanda) {
    if (SituacaoEnum.MINUTA.equals(demanda.getSituacao())) {
      return demanda.getMatriculaMinuta();
    } else if (SituacaoEnum.RASCUNHO.equals(demanda.getSituacao())) {
      return demanda.getMatriculaRascunho();
    } else if (SituacaoEnum.CANCELADA.equals(demanda.getSituacao())) {
      if (demanda.getMatriculaDemandante() != null) {
        return demanda.getMatriculaDemandante();
      } else if (demanda.getMatriculaMinuta() != null) {
        return demanda.getMatriculaMinuta();
      } else {
        return demanda.getMatriculaRascunho();
      }
    }
    return demanda.getMatriculaDemandante();

  }

  /**
   * Método: Obter o prazo da Demanda.
   * 
   * @param situacao
   *          situacao
   * @return String
   */
  private Integer obterPrazoDemanda(Demanda demanda, List<Date> datasFeriadosList) {
    Integer prazoDemanda = 0;
    Integer prazoTotalFluxoDemanda = 0;
    List<FluxoDemanda> fluxoDemandaList = demanda.getFluxosDemandasAtivoList();
    for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
      if(fluxoDemanda.getOrdem() != 0) {
        prazoTotalFluxoDemanda += fluxoDemanda.getPrazo();
      }
    }
    prazoDemanda =
        this.feriadoService.calcularNumeroDiasUteisPorDataEPrazo(demanda.getDataHoraAbertura(), prazoTotalFluxoDemanda,
            datasFeriadosList);
    return prazoDemanda;
  }
  
  private Integer obterPrazoDemandaBuscandoFluxos(Demanda demanda) {
    Integer prazoTotalFluxoDemanda = 0;
    List<FluxoDemanda> fluxoDemandaList = fluxoDemandaService.findAtivosByIdDemanda(demanda.getId());
    for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
      if(fluxoDemanda.getOrdem() != 0) {
        prazoTotalFluxoDemanda += fluxoDemanda.getPrazo();
      }
    }
    return prazoTotalFluxoDemanda;
  }
  
  private Integer obterPrazoDemanda(Demanda demanda) {
    Integer prazoTotalFluxoDemanda = 0;
    List<FluxoDemanda> fluxoDemandaList = demanda.getFluxosDemandasAtivoSemFluxoDemandanteList();
    for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
      if(fluxoDemanda.getOrdem() != 0) {
        prazoTotalFluxoDemanda += fluxoDemanda.getPrazo();
      }
    }
    return prazoTotalFluxoDemanda;
  }
  
  public boolean isDemandaFechadaNoPrazo(Demanda demanda, List<Date> datasFeriadosList) {
    
    Integer prazoDemanda = obterPrazoDemandaBuscandoFluxos(demanda);
    
    Integer prazoFechamento = feriadoService.calcularQtdDiasUteisEntreDatas(demanda.getDataHoraAbertura(),demanda.getDataHoraEncerramento(), datasFeriadosList);
    
    return prazoFechamento <= prazoDemanda;
  }

  public boolean isDemandaFechadaNoPrazoSemConsulta(Atendimento atendimento, List<Date> datasFeriadosList) {
    
	    Integer prazoFechamento = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(),atendimento.getDataHoraAtendimento(), datasFeriadosList);
	    Integer prazoDemanda = atendimento.getFluxoDemanda().getPrazo();
	    if((prazoDemanda - prazoFechamento) < 0) {
	    	return false;
	    }
	    return true;
  }

  /**
   * Método: Obter o prazo do Responsável atual.
   * 
   * @param situacao
   *          situacao
   * @return String
   */
  private Integer obterPrazoResponsavelAtual(Demanda demanda, List<Date> datasFeriadosList, Atendimento atendimento) {
    Integer prazoRespAtual = 0;

    if (atendimento != null) {
      
      if (atendimento.getUnidadeExterna() != null) {
        prazoRespAtual =
            this.feriadoService.calcularNumeroDiasUteisPorDataEPrazo(atendimento.getDataHoraAtendimento(),
                atendimento.getFluxoDemanda().getPrazo(), datasFeriadosList);
      } else {
        prazoRespAtual =
            this.feriadoService.calcularNumeroDiasUteisPorDataEPrazo(atendimento.getDataHoraRecebimento(),
                atendimento.getFluxoDemanda().getPrazo(), datasFeriadosList);
      }
    } else {
      if (demanda.getCaixaPostalDemandante().getId().equals(demanda.getCaixaPostalResponsavel().getId())
          && SituacaoEnum.ABERTA.equals(demanda.getSituacao()) && Boolean.FALSE.equals(demanda.getFlagDemandaPai())){
        Atendimento atendimentoTemp = this.atendimentoService.obterUltimoAtendimentoPorDemandaSemFluxoDemanda(demanda.getId(), demanda.getCaixaPostalResponsavel().getId());
        prazoRespAtual =
            (this.feriadoService.calcularQtdDiasUteisEntreDatas(atendimentoTemp.getDataHoraRecebimento(),new Date()) * -1);  
        }
    }
    
    return prazoRespAtual;
  }

  /**
   * Método: Alterar a cor da demanda.
   * 
   * @param corDemanda
   *          cor
   */
  public void setColorInDemanda(Long id, String corDemanda) {
    Demanda demandatemp = this.demandaDAO.findById(id);
    if (demandatemp != null) {
      demandatemp.setCor(corDemanda);
      this.demandaDAO.update(demandatemp);
    }
  }

  private List<Demanda> obterListaDemandasExternas(Long idCaixaPostalResponsavel, SituacaoEnum situacao) {
    List<Demanda> listaDemandasExternasFiltrada = new ArrayList<>();
    List<FluxoDemanda> fluxoDemandaListTemp;
    List<Demanda> listaDemandasExternas =
        this.demandaDAO.obterListaDemandasExternasPorAendimentoECaixaPostal(idCaixaPostalResponsavel, situacao);
    for (Demanda demanda : listaDemandasExternas) {
      fluxoDemandaListTemp = demanda.getFluxosDemandasAtivoList();
      if ((fluxoDemandaListTemp != null) && !fluxoDemandaListTemp.isEmpty() && (fluxoDemandaListTemp.size() > 1)) {
        Collections.sort(fluxoDemandaListTemp);
        if (fluxoDemandaListTemp.get(fluxoDemandaListTemp.size() - 2).getCaixaPostal().getId().equals(idCaixaPostalResponsavel)) {
          listaDemandasExternasFiltrada.add(demanda);
        }
      }
    }
    return listaDemandasExternasFiltrada;
  }

  /**
   * Grava a demanda na base de dados com a situação dada.
   * 
   * @param entity
   *          Demanda
   * @param anexo
   *          Anexo da demanda
   * @throws IOException
   * @throws RequiredException
   * @throws BusinessException
   * @throws DataBaseException
   * @throws AppException
   */
  public void salvar(Demanda entity, final UploadedFile anexo)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {
    this.save(entity);
    atualizarListaDemandaContratos(entity);
    
    if(entity.getSituacao().equals(SituacaoEnum.ABERTA)) {
    	
      validarSalvarDemandaAbertaCompleta(entity);
    	
      String assuntoArvore = assuntoService.obterArvoreAssuntos(entity.getAssunto());
      
      int qtdPrazoDias = calcularPrazoDias(entity);
      String dataLimiteFormatada = calcularDataLimiteFormatada(entity, qtdPrazoDias);
      
      List<CaixaPostal> observadoresList = new ArrayList<>();
      observadoresList.addAll(entity.getCaixasPostaisObservadorList());
      observadoresList.addAll(assuntoService.findObservadores(entity.getAssunto()));
      
      emailService.enviarEmailPorAcao(entity,entity.getCaixaPostalDemandante(), entity.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.INCLUIR, entity.getTextoDemanda(), assuntoArvore,
          qtdPrazoDias, dataLimiteFormatada, entity.getMatriculaDemandante());
    }
    
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemanda(entity, anexo);
    }
  }
  
  private void validarSalvarDemandaAbertaCompleta(Demanda demanda) throws BusinessRollbackException {
	  
	for (Atendimento atendimento : demanda.getAtendimentosList()) {
		if(atendimento.getFluxoDemanda() == null){
			logger.error("Ocorreu o erro de salvar Demanda com Atendimento sem FluxoDemanda.");
			throw new BusinessRollbackException("Ocorreu o erro de salvar Demanda com Atendimento sem FluxoDemanda.");
		}
    }
  }
  
  private Integer calcularPrazoDias(Demanda demanda) {
    int qtdPrazoDias = 0; 
    
    for (FluxoDemanda fluxoDemanda : demanda.getFluxosDemandasAtivoSemFluxoDemandanteList()) {
      qtdPrazoDias += fluxoDemanda.getPrazo();
    }
    
    return qtdPrazoDias;
  }
  
  private String calcularDataLimiteFormatada(Demanda demanda, Integer prazoDias) {
    Date dataPrevista = feriadoService.adicionarDiasUteis(demanda.getDataHoraAbertura(), prazoDias);
    return DateUtil.formatDataPadrao(dataPrevista);
  }

  /**
   * Anexa um arquivo à demanda informada.
   * 
   * @param entity
   *          Demanda
   * @param anexo
   *          Anexo da demanda
   * @throws IOException
   * @throws RequiredException
   * @throws BusinessException
   * @throws DataBaseException
   * @throws AppException
   */
  private void anexarArquivoDemanda(final Demanda entity, final UploadedFile anexo)
      throws IOException, AppException {
    /*
     * [RNF001] O sistema deve substituir o nome do arquivo anexo e persistir na base de dados, conforme abaixo:
     * ANEXO_XXX.ZIP. Onde: XXX - É o Id da demanda, cujo o anexo pertence. O sistema deve salvar o arquivo na pasta e
     * caminho padrão definidos nas propriedades do servidor.
     */
    String path =
        obterDiretorioAnexosPorAbrangencia(entity)
            + String.format(Constantes.FORMATO_NOME_ANEXO_DEMANDA, entity.getId());

    File hnd = new File(path);
    try(FileOutputStream out = new FileOutputStream(hnd)){
      out.write(anexo.getContents());
      out.flush();
    }

    /*
     * Todas as demandas (pai e filhas) devem possuir o nome do arquivo anexo definido conforme RNF001.
     */
    List<Demanda> loteDemandaList = new ArrayList<>();

    entity.setAnexoDemanda(hnd.getName());
    loteDemandaList.add(entity);

    if (!ObjectUtils.isNullOrEmpty(entity.getDemandaFilhosList())) {
      for (Demanda filhos : entity.getDemandaFilhosList()) {
        filhos.setAnexoDemanda(hnd.getName());
        loteDemandaList.add(filhos);
      }
    }

    this.update(loteDemandaList);
  }

  public String obterDiretorioAnexosPorAbrangencia(Demanda demanda) {
    
    Abrangencia abrangencia = obterAbrangenciaPorDemanda(demanda);
    
    String path = "";
    
    if(abrangencia.getId().equals(Constantes.ABRANGENCIA_VIGOV_ID)) {
    	path = System.getProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS);
    } else {
    	
    	//Criar a pasta de Anexos se não existir
    	path = System.getProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS_ABRANGENCIAS) ;
    	File pasta = new File(path);
    	if(!pasta.exists()) {
    		pasta.mkdir();
    	}
    	
    	//Criar a pasta da abrangencia se não existir
    	path = System.getProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS_ABRANGENCIAS) + Constantes.PREFIXO_DIRETORIO_UPLOAD_ANEXOS+ abrangencia.getId() + File.separator ;
    	pasta = new File(path);
    	if(!pasta.exists()) {
    		pasta.mkdir();
    	}
    }
    

    return path;
  }

  private Abrangencia obterAbrangenciaPorDemanda(Demanda demanda) {
    return abrangenciaService.obterAbrangenciaPorDemanda(demanda.getId());
  }

  private void anexarArquivoDemandaRascunhoMinuta(final Demanda entity, final UploadedFile anexo)
      throws IOException, AppException {
    /*
     * [RNF001] O sistema deve substituir o nome do arquivo anexo e persistir na base de dados, conforme abaixo:
     * ANEXO_XXX.ZIP. Onde: XXX - É o Id da demanda, cujo o anexo pertence. O sistema deve salvar o arquivo na pasta e
     * caminho padrão definidos nas propriedades do servidor.
     */
    String path =
        obterDiretorioAnexosPorAbrangencia(entity)
            + String.format(Constantes.FORMATO_NOME_ANEXO_DEMANDA, entity.getId());

    File hnd = new File(path);
    try(FileOutputStream out = new FileOutputStream(hnd)){
      out.write(anexo.getContents());
      out.flush();
    }

    entity.setAnexoDemanda(hnd.getName());
    this.update(entity);
  }
  
  private void anexarArquivoDemandaConsulta(final List<Demanda> demandasFilhasList, final Demanda demandaPai, final UploadedFile anexo, final Atendimento atendimentoPai)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {
    
    String path =
        obterDiretorioAnexosPorAbrangencia(demandaPai)
        + String.format(Constantes.FORMATO_NOME_ANEXO_DEMANDA_ATENDIMENTO, demandaPai.getId(), atendimentoPai.getId());
    
    File hnd = new File(path);
    
    try(FileOutputStream out = new FileOutputStream(hnd)){
      out.write(anexo.getContents());
      out.flush();
    }
    
    atendimentoPai.setAnexoAtendimento(hnd.getName());
    
    for (Demanda demandaFilha : demandasFilhasList) {
      demandaFilha.setAnexoDemanda(hnd.getName());
    }
  }

  private void anexarArquivoDemandaAtendimento(final Demanda entity, final UploadedFile anexo, final Atendimento atendimento)
      throws IOException {

    String path =
        obterDiretorioAnexosPorAbrangencia(entity)
            + String.format(Constantes.FORMATO_NOME_ANEXO_DEMANDA_ATENDIMENTO, entity.getId(), atendimento.getId());

    File hnd = new File(path);
    try(FileOutputStream out = new FileOutputStream(hnd)){
      out.write(anexo.getContents());
      out.flush();
    }

    atendimento.setAnexoAtendimento(hnd.getName());
  }

  public Demanda findByIdFetch(Long id) {
    return this.demandaDAO.findByIdFetch(id);
  }
  
  public Demanda findByIdAndAbrangenciaFetch(Long idDemanda, Long idAbrangencia) {
  	return this.demandaDAO.findByIdAndAbrangenciaFetch(idDemanda, idAbrangencia);
  }

  public List<Demanda> findFilhosByIdPaiFetch(Long id) {
    return this.demandaDAO.findFilhosByIdPaiFetch(id);
  }

  /**
   * Método: .
   * 
   * @param caixasPostaisList
   */
  public void obterCaixaPostalComValoresDemandas(List<CaixaPostal> caixasPostaisList) {
    Long totaltemp;
    for (CaixaPostal caixaPostal : caixasPostaisList) {
      totaltemp = 0L;
      totaltemp += this.demandaDAO.obterQuantidadeDemandasAbertasDaCaixaPostal(caixaPostal.getId());
      totaltemp += this.obterQuantidadeDemandasExternas(caixaPostal.getId()).size();
      caixaPostal.setTotalDemandasDaCaixaPostal(totaltemp);
    }
  }

  /**
   * Método: .
   * 
   * @param id
   * @param object
   * @return
   */
  private List<Demanda> obterQuantidadeDemandasExternas(Long idCaixaPostalResponsavel) {
    List<Demanda> listaDemandasExternasFiltrada = new ArrayList<>();
    List<FluxoDemanda> fluxoDemandaListTemp;
    List<Demanda> listaDemandasExternas =
        this.demandaDAO.obterListaDemandasExternasPorAendimentoECaixaPostal(idCaixaPostalResponsavel, null);
    for (Demanda demanda : listaDemandasExternas) {
      fluxoDemandaListTemp = demanda.getFluxosDemandasAtivoList();
      if ((fluxoDemandaListTemp != null) && !fluxoDemandaListTemp.isEmpty() && (fluxoDemandaListTemp.size() > 1)) {
        Collections.sort(fluxoDemandaListTemp);
        if (fluxoDemandaListTemp.get(fluxoDemandaListTemp.size() - 2).getCaixaPostal().getId().equals(idCaixaPostalResponsavel)) {
          listaDemandasExternasFiltrada.add(demanda);
        }
      }
    }
    return listaDemandasExternasFiltrada;
  }

  private Atendimento getAtendimentoAtual(Demanda managedDemanda) {
    List<Atendimento> atendimentoList = managedDemanda.getAtendimentosList();
    Collections.sort(atendimentoList);
    Collections.reverse(atendimentoList);

    Atendimento atendimentoAtual = atendimentoList.iterator().next();
    return atendimentoAtual;
  }

  public void salvarRascunho(Demanda demanda, final UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaUsuarioLogado, CaixaPostal caixaPostalSelecionada, String textoAtendimento, String nomeUsuarioLogado)
      throws IOException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    if (demanda.getSituacao().equals(SituacaoEnum.RASCUNHO) || demanda.getSituacao().equals(SituacaoEnum.MINUTA)) {

      managedDemanda.setTextoDemanda(textoAtendimento);

      if (!ObjectUtils.isNullOrEmpty(anexo)) {
        this.anexarArquivoDemandaRascunhoMinuta(managedDemanda, anexo);
      }

      for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
        CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
        managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
        managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
      }

      this.update(managedDemanda);
    } else {

      Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);
      atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.RASCUNHO);
      atendimentoAtual.setDataHoraAtendimento(new Date());
      atendimentoAtual.setMatricula(matriculaUsuarioLogado);
      atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
      atendimentoAtual.setDescricao(textoAtendimento);
      atendimentoService.update(atendimentoAtual);

      // Anexar no atendimento rascunho
      if (!ObjectUtils.isNullOrEmpty(anexo)) {
        this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
      }
      atendimentoService.update(atendimentoAtual);
      
      for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
        CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
        managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
        managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
      }
      
      
      this.update(managedDemanda);

    }

  }

  /**
   * Atualiza uma Demanda em situação Rascunho para Minuta.
   * @param nomeUsuarioLogado 
   */
  public void atualizarRascunhoParaMinuta(Demanda demanda, final UploadedFile anexo,
      List<CaixaPostal> caixaPostalObservadorSelecionadosList, String matriculaLogado, String nomeUsuarioLogado)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {

    demanda.setSituacao(SituacaoEnum.MINUTA);
    demanda.setMatriculaMinuta(matriculaLogado);
    demanda.setNomeUsuarioMinuta(nomeUsuarioLogado);
    demanda.setDataHoraAbertura(new Date());
    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaRascunhoMinuta(managedDemanda, anexo);
    }

    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }

    this.update(managedDemanda);
  }

  public void encaminharDemanda(Demanda demanda, final UploadedFile anexo,
      List<CaixaPostal> caixaPostalObservadorSelecionadosList, String matriculaLogado, CaixaPostal caixaPostalSelecionada,
      String textoAtendimento, String nomeUsuarioLogado) throws IOException, RequiredException, BusinessException, DataBaseException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);
    List<FluxoDemanda> fluxosDemanda = managedDemanda.getFluxosDemandasList();
    Collections.sort(fluxosDemanda);
    List<FluxoDemanda> fluxosDemandaAtivos = retornaFluxosDemandaAtivos(fluxosDemanda); 
    
    //Atributos do email
    CaixaPostal caixaDestinoEmail = null;
    AcaoAtendimentoEnum acaoEmail = null;

    if (managedDemanda.getSituacao().equals(SituacaoEnum.MINUTA) || managedDemanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {

      Iterator<FluxoDemanda> proximosFluxosAtivos = fluxosDemandaAtivos.iterator();
      FluxoDemanda primeiroFluxo = proximosFluxosAtivos.next();
      FluxoDemanda segundoFluxo = proximosFluxosAtivos.next();

      managedDemanda.setSituacao(SituacaoEnum.ABERTA);
      managedDemanda.setCaixaPostalResponsavel(segundoFluxo.getCaixaPostal());
      managedDemanda.setDataHoraAbertura(new Date());
      managedDemanda.setMatriculaDemandante(matriculaLogado);
      managedDemanda.setNomeUsuarioDemandante(nomeUsuarioLogado);

      Atendimento atendimentoDemandante = new Atendimento();
      //Criando atendimento para o próprio demandante (Abertura da demanda)
      atendimentoDemandante.setDemanda(managedDemanda);
      atendimentoDemandante.setDescricao("Demanda incluida com sucesso.");
      atendimentoDemandante.setDataHoraRecebimento(new Date());
      atendimentoDemandante.setDataHoraAtendimento(new Date());
      atendimentoDemandante.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
      atendimentoDemandante.setMatricula(matriculaLogado);
      atendimentoDemandante.setNomeUsuarioAtendimento(nomeUsuarioLogado);
      atendimentoDemandante.setFluxoDemanda(primeiroFluxo);
      atendimentoService.save(atendimentoDemandante);
      
      managedDemanda.getAtendimentosList().add(atendimentoDemandante);

      Atendimento atendimento = new Atendimento();
      atendimento.setDemanda(managedDemanda);
      atendimento.setFluxoDemanda(segundoFluxo);
      atendimento.setDataHoraRecebimento(new Date());
      atendimentoService.save(atendimento);
      managedDemanda.getAtendimentosList().add(atendimento);

      // Anexar o Arquivo na Demanda
      if (!ObjectUtils.isNullOrEmpty(anexo)) {
        this.anexarArquivoDemandaRascunhoMinuta(managedDemanda, anexo);
      }
      
      caixaDestinoEmail = primeiroFluxo.getCaixaPostal();
      acaoEmail = AcaoAtendimentoEnum.INCLUIR;
    } else {

      List<Atendimento> atendimentoList = managedDemanda.getAtendimentosList();
      Collections.sort(atendimentoList);

      Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

      FluxoDemanda proximoFluxo = null;
      for (int i = 0; i < fluxosDemandaAtivos.size(); i++) {
        FluxoDemanda fluxo = fluxosDemandaAtivos.get(i);
        if (fluxo.getCaixaPostal().equals(caixaPostalSelecionada)) {
          proximoFluxo = fluxosDemandaAtivos.get(i + 1);          break;        }
      }
      
      // Se não tem o proximo fluxo (demandante) pega o primeiro
      if (proximoFluxo == null) {
        proximoFluxo = fluxosDemandaAtivos.iterator().next();
      }

      // Atualizar o atendimento atual
      atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.ENCAMINHAR);
      atendimentoAtual.setDataHoraAtendimento(new Date());
      atendimentoAtual.setMatricula(matriculaLogado);
      atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
      atendimentoAtual.setDescricao(textoAtendimento);

      // Atualiza o responsavel
      managedDemanda.setCaixaPostalResponsavel(proximoFluxo.getCaixaPostal());

      // Encaminha para proxima Caixa
      Atendimento proxAtendimento = new Atendimento();
      proxAtendimento.setDemanda(managedDemanda);
      proxAtendimento.setFluxoDemanda(proximoFluxo);
      proxAtendimento.setDataHoraRecebimento(new Date());
      managedDemanda.getAtendimentosList().add(proxAtendimento);

      atendimentoService.save(proxAtendimento);

      // Anexar no atendimento atual
      if (!ObjectUtils.isNullOrEmpty(anexo)) {
        this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
      }
      atendimentoService.update(atendimentoAtual);
      
      caixaDestinoEmail = proximoFluxo.getCaixaPostal();
      acaoEmail = AcaoAtendimentoEnum.ENCAMINHAR;

    }

    // Atualizar Caixas Observadoras
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }
    
    managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);

    this.update(managedDemanda);

    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda, caixaPostalSelecionada, caixaDestinoEmail, observadoresList, acaoEmail, textoAtendimento, assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaLogado);
  }
  
  public void encaminharDemandaExterna(Demanda demanda, final UploadedFile anexo,
      List<CaixaPostal> caixaPostalObservadorSelecionadosList, String matriculaLogado, CaixaPostal caixaPostalSelecionada,
      String textoAtendimento, Unidade unidadeExterna, String nomeUsuarioLogado) throws IOException, RequiredException, BusinessException, DataBaseException, AppException {
    
    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);
    List<FluxoDemanda> fluxosDemanda = managedDemanda.getFluxosDemandasList();
    Collections.sort(fluxosDemanda);
    List<FluxoDemanda> fluxosDemandaAtivos = retornaFluxosDemandaAtivos(fluxosDemanda);
    
    List<Atendimento> atendimentoList = managedDemanda.getAtendimentosList();
    Collections.sort(atendimentoList);
    
    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);
    
    FluxoDemanda proximoFluxo = null;
    for (int i = 0; i < fluxosDemandaAtivos.size(); i++) {
      FluxoDemanda fluxo = fluxosDemandaAtivos.get(i);
      if (fluxo.getCaixaPostal().equals(caixaPostalSelecionada)) {
        proximoFluxo = fluxosDemandaAtivos.get(i + 1);
        break;
      }
    }
    
    // Atualizar o atendimento atual
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.ENCAMINHAR);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setMatricula(matriculaLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoAtual.setDescricao(textoAtendimento);
    atendimentoAtual.setUnidadeExterna(unidadeExterna);
    
    // Atualiza o responsavel
    managedDemanda.setCaixaPostalResponsavel(unidadeExterna.getCaixasPostaisList().iterator().next());
    
    // Encaminha para proxima Caixa
    Atendimento proxAtendimento = new Atendimento();
    proxAtendimento.setDemanda(managedDemanda);
    proxAtendimento.setFluxoDemanda(proximoFluxo);
    proxAtendimento.setDataHoraRecebimento(new Date());
    managedDemanda.getAtendimentosList().add(proxAtendimento);
    
    atendimentoService.save(proxAtendimento);
    
    // Anexar no atendimento atual
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
    }
    atendimentoService.update(atendimentoAtual);
    
    // Atualizar Caixas Observadoras
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }
    
    this.update(managedDemanda);
    
    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
   
    CaixaPostal caixaDestino = null;
    
    for (FluxoDemanda fluxoDemanda : managedDemanda.getFluxosDemandasList()) {
        if (fluxoDemanda.getOrdem() ==  atendimentoAtual.getFluxoDemanda().getOrdem()) {
          caixaDestino = fluxoDemanda.getCaixaPostal();
        }
    }
    
    if(caixaDestino != null) {
      caixaDestino = caixaPostalService.findById(caixaDestino.getId());
    }
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda,caixaPostalSelecionada, caixaDestino, observadoresList, AcaoAtendimentoEnum.ENCAMINHAR, textoAtendimento, assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaLogado);
  }

  public void questionarDemanda(Demanda demanda, final UploadedFile anexo,
      List<CaixaPostal> caixaPostalObservadorSelecionadosList, String matriculaLogado, String textoAtendimento,
      CaixaPostal caixaPostalSelecionada, String nomeUsuarioLogado)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);
    List<FluxoDemanda> fluxosDemanda = managedDemanda.getFluxosDemandasList();
    Collections.sort(fluxosDemanda);
    List<FluxoDemanda> fluxosDemandaAtivos = retornaFluxosDemandaAtivos(fluxosDemanda);

    List<Atendimento> atendimentoList = managedDemanda.getAtendimentosList();
    Collections.sort(atendimentoList);

    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

    FluxoDemanda proximoFluxo = null;
    for (int i = 0; i < fluxosDemandaAtivos.size(); i++) {
      FluxoDemanda fluxo = fluxosDemandaAtivos.get(i);
      // Encontra a posição da caixa selecionada no fluxo da demanda e pega o anterior.
      if (fluxo.getCaixaPostal().equals(caixaPostalSelecionada) && i >= 1) {
          proximoFluxo = fluxosDemandaAtivos.get(i - 1);
          break;
      }
    }

    // Atualizar o ultimo atendimento
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.QUESTIONAR);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setMatricula(matriculaLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoAtual.setDescricao(textoAtendimento);

    // Atualizar o responsavel, se não tiver fluxo então vai pro demandante
    if (proximoFluxo != null) {
      managedDemanda.setCaixaPostalResponsavel(proximoFluxo.getCaixaPostal());
    } else {
      managedDemanda.setCaixaPostalResponsavel(demanda.getCaixaPostalDemandante());
    }

    // Encaminha para proxima Caixa
    Atendimento proxAtendimento = new Atendimento();
    proxAtendimento.setDemanda(managedDemanda);
    proxAtendimento.setFluxoDemanda(proximoFluxo);
    proxAtendimento.setDataHoraRecebimento(new Date());
    managedDemanda.getAtendimentosList().add(proxAtendimento);

    atendimentoService.save(proxAtendimento);

    // Anexar no atendimento atual
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
    }
    atendimentoService.update(atendimentoAtual);

    // Atualizar Caixas Observadoras
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }
    
    managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);
    this.update(managedDemanda);
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    emailService.enviarEmailPorAcao(managedDemanda, caixaPostalSelecionada, managedDemanda.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.QUESTIONAR, textoAtendimento, assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaLogado);

  }

  public void responderDemanda(Demanda demanda, final UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaLogado, String textoAtendimento, CaixaPostal caixaPostalSelecionada, String nomeUsuarioLogado)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);
    List<FluxoDemanda> fluxosDemanda = managedDemanda.getFluxosDemandasList();
    Collections.sort(fluxosDemanda);
    List<FluxoDemanda> fluxosDemandaAtivos = retornaFluxosDemandaAtivos(fluxosDemanda);

    List<Atendimento> atendimentoList = managedDemanda.getAtendimentosList();
    Collections.sort(atendimentoList);

    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

    FluxoDemanda proximoFluxo = null;
    for (int i = 0; i < fluxosDemandaAtivos.size(); i++) {
      FluxoDemanda fluxo = fluxosDemandaAtivos.get(i);
      // Encontra a posição da caixa selecionada no fluxo da demanda e pega o anterior.
      if (fluxo.getCaixaPostal().equals(caixaPostalSelecionada) && i >= 1) {
        proximoFluxo = fluxosDemandaAtivos.get(i - 1);
        break;
      }
    }

    // Atualizar o ultimo atendimento
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.RESPONDER);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setMatricula(matriculaLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoAtual.setDescricao(textoAtendimento);

    // Atualizar o responsavel, se não tiver fluxo então vai pro demandante
    if (proximoFluxo != null && proximoFluxo.getOrdem() >= 1) {
      managedDemanda.setCaixaPostalResponsavel(proximoFluxo.getCaixaPostal());

      // Encaminha para proxima Caixa
      Atendimento proxAtendimento = new Atendimento();
      proxAtendimento.setDemanda(managedDemanda);
      proxAtendimento.setFluxoDemanda(proximoFluxo);
      proxAtendimento.setDataHoraRecebimento(new Date());
      managedDemanda.getAtendimentosList().add(proxAtendimento);

      atendimentoService.save(proxAtendimento);

    } else {
      managedDemanda.setCaixaPostalResponsavel(demanda.getCaixaPostalDemandante());
      managedDemanda.setSituacao(SituacaoEnum.FECHADA);
      managedDemanda.setDataHoraEncerramento(new Date());
      
      cancelarFilhasDaDemandaRecursivamente(managedDemanda,caixaPostalSelecionada, textoAtendimento, matriculaLogado, nomeUsuarioLogado);
    }

    // Anexar no atendimento atual
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
    }
    atendimentoService.update(atendimentoAtual);

    // Atualizar Caixas Observadoras
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }

    managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);
    this.update(managedDemanda);
    
    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda,caixaPostalSelecionada, managedDemanda.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.RESPONDER, textoAtendimento, assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaLogado);

  }

  public void atualizarDemanda(Demanda demanda, final UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaLogado, String textoAtendimento, CaixaPostal caixaAtendimento, String nomeUsuarioLogado)
      throws IOException, RequiredException, BusinessException, DataBaseException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

    // "DUPLICAR" OS DADOS DO ATENDIMENTO ATUAL PARA UM NOVO ATENDIMENTO
    Atendimento novoAtendimento = new Atendimento();
    novoAtendimento.setDemanda(managedDemanda);
    novoAtendimento.setFluxoDemanda(atendimentoAtual.getFluxoDemanda());
    novoAtendimento.setDataHoraRecebimento(atendimentoAtual.getDataHoraRecebimento());
    managedDemanda.getAtendimentosList().add(novoAtendimento);
    atendimentoService.save(novoAtendimento);

    // ATUALIZAR O ATENDIMENTO ATUAL PARA ACAO ATUALIZAR
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.ATUALIZAR);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setDescricao(textoAtendimento);
    atendimentoAtual.setMatricula(matriculaLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoService.update(atendimentoAtual);

    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
      atendimentoService.update(atendimentoAtual);
    }

    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }
    

    this.update(managedDemanda);

    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    
    CaixaPostal caixaDestino = null;
    
    if(managedDemanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)){
      if(atendimentoAtual.getFluxoDemanda().getOrdem() <= 2) {
        caixaDestino = managedDemanda.getCaixaPostalDemandante();
      } else {
        for (FluxoDemanda fluxoDemanda : managedDemanda.getFluxosDemandasAtivoList()) {
          if (fluxoDemanda.getOrdem() ==  atendimentoAtual.getFluxoDemanda().getOrdem()-2) {
            caixaDestino = fluxoDemanda.getCaixaPostal();
          }
        }
      }
    } else if(atendimentoAtual.getFluxoDemanda().getOrdem() == 1) {
        caixaDestino = managedDemanda.getCaixaPostalDemandante();
    } else {
      
      for (FluxoDemanda fluxoDemanda : managedDemanda.getFluxosDemandasAtivoList()) {
        if (fluxoDemanda.getOrdem() ==  atendimentoAtual.getFluxoDemanda().getOrdem()-1) {
          caixaDestino = fluxoDemanda.getCaixaPostal();
        }
      }
      
    }
    
    if(caixaDestino != null) {
      caixaDestino = caixaPostalService.findById(caixaDestino.getId());
    }
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda, caixaAtendimento, caixaDestino, observadoresList, AcaoAtendimentoEnum.ATUALIZAR, textoAtendimento, assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaLogado);

  }

  public void cancelar(Demanda demanda, UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaUsuarioLogado, CaixaPostal caixaPostalSelecionada, String textoAtendimento, String nomeUsuarioLogado) throws Exception {
    
    atualizarListaDemandaContratos(demanda);
    if (demanda.getSituacao().equals(SituacaoEnum.MINUTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {
      demanda.setDataHoraEncerramento(new Date());
      demanda.setSituacao(SituacaoEnum.CANCELADA);
      this.update(demanda);
    } else {

      Demanda managedDemanda = this.update(demanda);

      Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

      // ATUALIZAR O ATENDIMENTO ATUAL PARA ACAO CANCELAR
      atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.CANCELAR);
      atendimentoAtual.setDataHoraAtendimento(new Date());
      atendimentoAtual.setDescricao(textoAtendimento);
      atendimentoAtual.setMatricula(matriculaUsuarioLogado);
      atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
      atendimentoService.update(atendimentoAtual);

      managedDemanda.setDataHoraEncerramento(new Date());
      managedDemanda.setSituacao(SituacaoEnum.CANCELADA);
      managedDemanda.setCaixaPostalResponsavel(managedDemanda.getCaixaPostalDemandante());
      this.update(managedDemanda);
      
      cancelarFilhasDaDemandaRecursivamente(managedDemanda,caixaPostalSelecionada, textoAtendimento, matriculaUsuarioLogado,nomeUsuarioLogado);

      if (!ObjectUtils.isNullOrEmpty(anexo)) {
        this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
        atendimentoService.update(atendimentoAtual);
      }

      for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
        CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
        managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
        managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
      }

      managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);
      this.update(managedDemanda);
      
      String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
      int qtdPrazoDias = calcularPrazoDias(managedDemanda);
      String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
      
      List<CaixaPostal> observadoresList = new ArrayList<>();
      observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
      observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
      
      emailService.enviarEmailPorAcao(managedDemanda, caixaPostalSelecionada, managedDemanda.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.CANCELAR, textoAtendimento, 
          assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);

    }
  }

  public void reabrirDemanda(Demanda demanda, UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaUsuarioLogado, String textoAtendimento, CaixaPostal caixaPostalSelecionada,
      MotivoReaberturaEnum motivoReabertura, String nomeUsuarioLogado)
      throws IOException, AppException {

    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    List<FluxoDemanda> fluxosDemanda = managedDemanda.getFluxosDemandasList();
    Collections.sort(fluxosDemanda);
    List<FluxoDemanda> fluxosDemandaAtivos = retornaFluxosDemandaAtivos(fluxosDemanda);
    //Iterator<FluxoDemanda> proximosFluxosAtivos = fluxosDemandaAtivos.iterator();
    //Pegando o segundo fluxo pois o primeiro é o próprio demandante (ordem 0)
    
   // FluxoDemanda segundoFluxo = proximosFluxosAtivos.next();
    FluxoDemanda segundoFluxo = new FluxoDemanda();
    
    for (FluxoDemanda fluxoDemanda : fluxosDemandaAtivos) {
    	if(fluxoDemanda.getOrdem() == 1){
    		segundoFluxo = fluxoDemanda;
    	}
	}

    Atendimento reabertura = new Atendimento();

    // Cria o atendimento reabertura
    reabertura.setDemanda(managedDemanda);
    reabertura.setAcaoEnum(AcaoAtendimentoEnum.REABRIR);
    reabertura.setDataHoraRecebimento(new Date());
    reabertura.setDataHoraAtendimento(new Date());
    reabertura.setMatricula(matriculaUsuarioLogado);
    reabertura.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    reabertura.setDescricao(textoAtendimento);
    reabertura.setMotivoReabertura(motivoReabertura);
    managedDemanda.getAtendimentosList().add(reabertura);
    atendimentoService.save(reabertura);

    // Atualiza o responsavel
    managedDemanda.setSituacao(SituacaoEnum.ABERTA);
    managedDemanda.setCaixaPostalResponsavel(segundoFluxo.getCaixaPostal());
    managedDemanda.setDataHoraEncerramento(null);

    // Encaminha para proxima Caixa
    Atendimento proxAtendimento = new Atendimento();
    proxAtendimento.setDemanda(managedDemanda);
    proxAtendimento.setFluxoDemanda(segundoFluxo);
    proxAtendimento.setDataHoraRecebimento(new Date());
    managedDemanda.getAtendimentosList().add(proxAtendimento);
    atendimentoService.save(proxAtendimento);

    // Anexar no atendimento Reabertura
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, reabertura);
    }
    atendimentoService.update(reabertura);

    // Atualizar Caixas Observadoras
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }

    managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);
    this.update(managedDemanda);
    
    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda,caixaPostalSelecionada, managedDemanda.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.REABRIR, textoAtendimento, 
        assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);
  }
  
  private List<FluxoDemanda> retornaFluxosDemandaAtivos(List<FluxoDemanda> fluxosDemanda) {
    List<FluxoDemanda> listaFluxosDemandaAtivos = new ArrayList<>();
    
    for(FluxoDemanda fluxoDemanda : fluxosDemanda) {
      if(fluxoDemanda.isAtivo()) {
        listaFluxosDemandaAtivos.add(fluxoDemanda);
      }
    }
    
    return listaFluxosDemandaAtivos;
  }

  private void cancelarFilhasDaDemandaRecursivamente(Demanda demanda, CaixaPostal caixaPostalSelecionada, String textoAtendimento, String matriculaUsuarioLogado, String nomeUsuarioLogado) throws RequiredException, BusinessException, DataBaseException {
   
    atualizarListaDemandaContratos(demanda);
    List<Demanda> demandasVinculadasList = findFilhosByIdPaiFetch(demanda.getId());
    
    if(demandasVinculadasList != null && !demandasVinculadasList.isEmpty()) {
      for (Demanda filha : demandasVinculadasList) {
        if(filha.getSituacao().equals(SituacaoEnum.ABERTA)) {
          Atendimento atendimentoFilha = getAtendimentoAtual(filha);
          atendimentoFilha.setAcaoEnum(AcaoAtendimentoEnum.CANCELAR);
          atendimentoFilha.setDataHoraAtendimento(new Date());
          atendimentoFilha.setDescricao(textoAtendimento);
          atendimentoFilha.setMatricula(matriculaUsuarioLogado);
          atendimentoFilha.setNomeUsuarioAtendimento(nomeUsuarioLogado);
          atendimentoService.update(atendimentoFilha);
          
          //Atualizar a Demanda Filha
          filha.setDataHoraEncerramento(new Date());
          filha.setSituacao(SituacaoEnum.CANCELADA);
          Demanda managedFilha = this.update(filha);
          
          String assuntoArvore = assuntoService.obterArvoreAssuntos(managedFilha.getAssunto());
          int qtdPrazoDias = calcularPrazoDias(managedFilha);
          String dataLimiteFormatada = calcularDataLimiteFormatada(managedFilha, qtdPrazoDias);
          
          List<CaixaPostal> observadoresList = new ArrayList<>();
          observadoresList.addAll(managedFilha.getCaixasPostaisObservadorList());
          observadoresList.addAll(assuntoService.findObservadores(managedFilha.getAssunto()));
          
          emailService.enviarEmailPorAcao(managedFilha, caixaPostalSelecionada, managedFilha.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.CANCELAR, textoAtendimento, 
              assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);
          
          //Cancela as filhas das filhas
          cancelarFilhasDaDemandaRecursivamente(filha,caixaPostalSelecionada, textoAtendimento,matriculaUsuarioLogado,nomeUsuarioLogado);
        }
      }
    }
  }

  public void fecharDemanda(Demanda demanda, UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaUsuarioLogado, String textoAtendimento, CaixaPostal caixaPostalSelecionada, String nomeUsuarioLogado) throws IOException, RequiredException, BusinessException, DataBaseException, AppException {
    
    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);

    // ATUALIZAR O ATENDIMENTO ATUAL PARA ACAO FECHAR
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.FECHAR);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setDescricao(textoAtendimento);
    atendimentoAtual.setMatricula(matriculaUsuarioLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoService.update(atendimentoAtual);

    //Atualizar a Demanda
    managedDemanda.setDataHoraEncerramento(new Date());
    managedDemanda.setSituacao(SituacaoEnum.FECHADA);
    managedDemanda.setCaixaPostalResponsavel(managedDemanda.getCaixaPostalDemandante());
    this.update(managedDemanda);
    
    List<Demanda> demandasVinculadasList = findFilhosByIdPaiFetch(managedDemanda.getId());

    //Fecha todas as filhas e Cancela as netas
    for (Demanda filha : demandasVinculadasList) {
      if(filha.getSituacao().equals(SituacaoEnum.ABERTA)) {
        Atendimento atendimentoFilha = getAtendimentoAtual(filha);
        atendimentoFilha.setAcaoEnum(AcaoAtendimentoEnum.FECHAR);
        atendimentoFilha.setDataHoraAtendimento(new Date());
        atendimentoFilha.setDescricao(textoAtendimento);
        atendimentoFilha.setMatricula(matriculaUsuarioLogado);
        atendimentoFilha.setNomeUsuarioAtendimento(nomeUsuarioLogado);

        atendimentoService.update(atendimentoFilha);
        
        //Atualizar a Demanda Filha
        filha.setDataHoraEncerramento(new Date());
        filha.setSituacao(SituacaoEnum.FECHADA);
        Demanda managedFilha = this.update(filha);
        
        String assuntoArvore = assuntoService.obterArvoreAssuntos(managedFilha.getAssunto());
        int qtdPrazoDias = calcularPrazoDias(managedFilha);
        String dataLimiteFormatada = calcularDataLimiteFormatada(managedFilha, qtdPrazoDias);
        emailService.enviarEmailPorAcao(managedFilha,caixaPostalSelecionada, managedFilha.getCaixaPostalResponsavel(), managedFilha.getCaixasPostaisObservadorList(), AcaoAtendimentoEnum.FECHAR, textoAtendimento, 
            assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);
        
        cancelarFilhasDaDemandaRecursivamente(filha, caixaPostalSelecionada, textoAtendimento, matriculaUsuarioLogado, nomeUsuarioLogado);
      }
    }

    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaAtendimento(managedDemanda, anexo, atendimentoAtual);
      atendimentoService.update(atendimentoAtual);
    }

    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }

    managedDemanda.setCor(Constantes.COR_PADRAO_DEMANDA);
    this.update(managedDemanda);
    
    String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemanda.getAssunto());
    int qtdPrazoDias = calcularPrazoDias(managedDemanda);
    String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemanda, qtdPrazoDias);
    
    List<CaixaPostal> observadoresList = new ArrayList<>();
    observadoresList.addAll(managedDemanda.getCaixasPostaisObservadorList());
    observadoresList.addAll(assuntoService.findObservadores(managedDemanda.getAssunto()));
    
    emailService.enviarEmailPorAcao(managedDemanda, caixaPostalSelecionada, managedDemanda.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.FECHAR, textoAtendimento, 
        assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);
  }

  public void consultarDemandas(Demanda demanda, UploadedFile anexo, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      String matriculaUsuarioLogado, String textoAtendimento, CaixaPostal caixaPostalSelecionada, Integer prazoConsulta,
      List<Long> caixasPostaisMarcadasConsulta, String nomeUsuarioLogado) throws IOException, RequiredException, BusinessException, DataBaseException, AppException {
    
    atualizarListaDemandaContratos(demanda);
    Demanda managedDemanda = this.update(demanda);

    Atendimento atendimentoAtual = getAtendimentoAtual(managedDemanda);
    
    List<CaixaPostal> caixasParaConsulta = caixaPostalService.findByRangeId(caixasPostaisMarcadasConsulta);
    
    // "DUPLICAR" OS DADOS DO ATENDIMENTO ATUAL PARA UM NOVO ATENDIMENTO
    Atendimento novoAtendimento = new Atendimento();
    novoAtendimento.setDemanda(managedDemanda);
    novoAtendimento.setFluxoDemanda(atendimentoAtual.getFluxoDemanda());
    novoAtendimento.setDataHoraRecebimento(atendimentoAtual.getDataHoraRecebimento());
    managedDemanda.getAtendimentosList().add(novoAtendimento);
    atendimentoService.save(novoAtendimento);

    // ATUALIZAR O ATENDIMENTO ATUAL PARA ACAO CONSULTAR
    atendimentoAtual.setAcaoEnum(AcaoAtendimentoEnum.CONSULTAR);
    atendimentoAtual.setDataHoraAtendimento(new Date());
    atendimentoAtual.setMatricula(matriculaUsuarioLogado);
    atendimentoAtual.setNomeUsuarioAtendimento(nomeUsuarioLogado);
    atendimentoService.update(atendimentoAtual);

    List<Demanda> demandasConsultaList = new ArrayList<>();
    
    for (CaixaPostal caixaPostalConsulta : caixasParaConsulta) {
      Demanda demandaConsulta = new Demanda();
      demandaConsulta.setTextoDemanda(textoAtendimento);
      demandaConsulta.setDataHoraAbertura(new Date());
      demandaConsulta.setCaixaPostalDemandante(caixaPostalSelecionada);
      demandaConsulta.setCaixaPostalResponsavel(caixaPostalConsulta);
      demandaConsulta.setAssunto(managedDemanda.getAssunto());
      demandaConsulta.setMatriculaDemandante(matriculaUsuarioLogado);
      demandaConsulta.setNomeUsuarioDemandante(nomeUsuarioLogado);
      demandaConsulta.setTitulo(managedDemanda.getTitulo());
      demandaConsulta.setTipoDemanda(TipoDemandaEnum.CONSULTA);
      demandaConsulta.setSituacao(SituacaoEnum.ABERTA);
      demandaConsulta.setCor(Constantes.COR_PADRAO_DEMANDA);
      demandaConsulta.setFlagDemandaPai(false);
      demandaConsulta.setDemandaPai(managedDemanda);
      demandaConsulta.setFluxosDemandasList(new ArrayList<FluxoDemanda>());
      demandaConsulta.setAtendimentosList(new ArrayList<Atendimento>());
      managedDemanda.getDemandaFilhosList().add(demandaConsulta);
      save(demandaConsulta);
      demandasConsultaList.add(demandaConsulta);
      
      FluxoDemanda fluxoDemandaDemandante = new FluxoDemanda();
      fluxoDemandaDemandante.setDemanda(demandaConsulta);
      demandaConsulta.getFluxosDemandasList().add(fluxoDemandaDemandante);
      fluxoDemandaDemandante.setCaixaPostal(demandaConsulta.getCaixaPostalDemandante());
      fluxoDemandaDemandante.setTipoFluxoDemanda(TipoFluxoEnum.DEMANDANTE_RESPONSAVEL);
      fluxoDemandaDemandante.setPrazo(1);
      fluxoDemandaDemandante.setOrdem(0);
      fluxoDemandaDemandante.setAtivo(true);
      fluxoDemandaService.save(fluxoDemandaDemandante);
      
      FluxoDemanda fluxoDemandaConsulta = new FluxoDemanda();
      fluxoDemandaConsulta.setDemanda(demandaConsulta);
      demandaConsulta.getFluxosDemandasList().add(fluxoDemandaConsulta);
      fluxoDemandaConsulta.setCaixaPostal(caixaPostalConsulta);
      fluxoDemandaConsulta.setPrazo(prazoConsulta);
      fluxoDemandaConsulta.setTipoFluxoDemanda(TipoFluxoEnum.DEMANDANTE_RESPONSAVEL);
      fluxoDemandaConsulta.setOrdem(1);
      fluxoDemandaService.save(fluxoDemandaConsulta);
      
      Atendimento atendimentoDemandante = new Atendimento();
      //Criando atendimento para o próprio demandante (Abertura da demanda)
      atendimentoDemandante.setDemanda(demandaConsulta);
      atendimentoDemandante.setDescricao("Demanda incluida com sucesso.");
      atendimentoDemandante.setDataHoraRecebimento(new Date());
      atendimentoDemandante.setDataHoraAtendimento(new Date());
      atendimentoDemandante.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
      atendimentoDemandante.setMatricula(matriculaUsuarioLogado);
      atendimentoDemandante.setNomeUsuarioAtendimento(nomeUsuarioLogado);
      atendimentoDemandante.setFluxoDemanda(fluxoDemandaDemandante);
      atendimentoService.save(atendimentoDemandante);
      demandaConsulta.getAtendimentosList().add(atendimentoDemandante);
      
      Atendimento atendimentoConsulta = new Atendimento();
      atendimentoConsulta.setFluxoDemanda(fluxoDemandaConsulta);
      atendimentoConsulta.setDemanda(demandaConsulta);
      demandaConsulta.getAtendimentosList().add(atendimentoConsulta);
      atendimentoConsulta.setDataHoraRecebimento(new Date());
      atendimentoService.save(atendimentoConsulta);
      
      Demanda managedDemandaConsulta = this.update(demandaConsulta);
      
      String assuntoArvore = assuntoService.obterArvoreAssuntos(managedDemandaConsulta.getAssunto());
      int qtdPrazoDias = calcularPrazoDias(managedDemandaConsulta);
      String dataLimiteFormatada = calcularDataLimiteFormatada(managedDemandaConsulta, qtdPrazoDias);
      
      List<CaixaPostal> observadoresList = new ArrayList<>();
      if (managedDemandaConsulta.getCaixasPostaisObservadorList() != null) {
        observadoresList.addAll(managedDemandaConsulta.getCaixasPostaisObservadorList());        
      }
      observadoresList.addAll(assuntoService.findObservadores(managedDemandaConsulta.getAssunto()));
      
      emailService.enviarEmailPorAcao(managedDemandaConsulta, caixaPostalSelecionada, managedDemandaConsulta.getCaixaPostalResponsavel(), observadoresList, AcaoAtendimentoEnum.CONSULTAR, textoAtendimento, 
          assuntoArvore, qtdPrazoDias, dataLimiteFormatada, matriculaUsuarioLogado);
    }
    
    if (!ObjectUtils.isNullOrEmpty(anexo)) {
      this.anexarArquivoDemandaConsulta(demandasConsultaList, managedDemanda, anexo, atendimentoAtual);
      //Atualizar o campo String anexo
      for (Demanda filha : demandasConsultaList) {
        update(filha);
      }
      atendimentoService.update(atendimentoAtual);
    }
    
    for (CaixaPostal caixaPostal : caixaPostalObservadorSelecionadosList) {
      CaixaPostal managedCaixaPostal = caixaPostalService.update(caixaPostal);
      managedCaixaPostal.getDemandasObservadasList().add(managedDemanda);
      managedDemanda.getCaixasPostaisObservadorList().add(managedCaixaPostal);
    }
    
    StringBuilder sbDescricaoConsulta = new StringBuilder();
    sbDescricaoConsulta.append("Consulta incluida ");
    for (Demanda filha : demandasConsultaList) {
        sbDescricaoConsulta.append(" - "+filha.getCaixaPostalResponsavel().getSigla()+" ("+filha.getId()+")");
    }
    
    atendimentoAtual.setDescricao(sbDescricaoConsulta.toString());
    atendimentoService.update(atendimentoAtual);

    this.update(managedDemanda);
  }
  
  public List<Demanda> findFilhosConsultaByIdPaiECaixaPostalFetch(Long id, CaixaPostal caixaDemandante){
    return demandaDAO.findFilhosConsultaByIdPaiECaixaPostalFetch(id, caixaDemandante);
  }

  public List<Demanda> findFilhosIniciaisByIdPaiPostalFetch(Long id){
    return demandaDAO.findFilhosIniciaisByIdPaiPostalFetch(id);
  }
  
  public Long obterQuantidadeDemandasPorAssunto(Assunto assunto) {
    return demandaDAO.obterQuantidadeDemandasPorAssunto(assunto);
  }
  
  public List<Demanda> obterDemandasEncaminhadosExternasNoPeriodo(Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal){
    return demandaDAO.obterDemandasEncaminhadosExternasNoPeriodo(abrangenciaSelecionada, dataInicial, dataFinal);
  }

  public List<Demanda> obterListaDemandasAbertasParaExtrato() {
    return demandaDAO.obterListaDemandasAbertasParaExtrato();
  }

  public List<Demanda> obterListaDemandasQuestionadasDemandante() {
    return demandaDAO.obterListaDemandasQuestionadasDemandante();
  }

  public List<AnaliticoDemandantesDemandadosDTO> relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(Date dataInicio, Date datatFim, Unidade unidade, List<Assunto> listaTodosAssuntos) {
    List<AnaliticoDemandantesDemandadosDTO> lista = new ArrayList<>();
    datatFim = DateUtil.getDataFinal(datatFim);
    Map<String, AnaliticoDemandantesDemandadosDTO> mapDTO = new HashMap<>();
    
    List<Demanda> listaDemandas = demandaDAO.relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(dataInicio, datatFim, unidade);
    String chaveTeste = "";
    AnaliticoDemandantesDemandadosDTO dto;
    for (Demanda demanda : listaDemandas) {
      chaveTeste = (demanda.getCaixaPostalDemandante().getUnidade().getId() + "#" + demanda.getAssunto().getId());
      dto = mapDTO.get(chaveTeste);
      
      //Não tem no Service - Inicializa os campos
      if (dto == null) {
        dto = new AnaliticoDemandantesDemandadosDTO();
        dto.setAssunto(this.assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaTodosAssuntos));
        dto.setUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade());
        dto.setUnidadeDemandada(demanda.getAssunto().getCaixaPostal().getUnidade());
        dto.setQtdAberta(0L);
        dto.setQtdFechada(0L);
      } 
      dto.setQtdAberta(dto.getQtdAberta() + 1);
      if (demanda.getDataHoraEncerramento() != null) {        
        dto.setQtdFechada(dto.getQtdFechada() + 1);
      }
      mapDTO.put(chaveTeste, dto);
    }
    //Transforma o map na lista esperada
    for (String key : mapDTO.keySet()) {
      lista.add(mapDTO.get(key));
    }
    return lista;
  }

  public List<DemandantePorSubrodinacaoDTO> relatorioUnidadesDemandantesPorSubordinacao(Date dataInicio, Date datatFim,
      Unidade unidade, List<Assunto> listaTodosAssuntos, List<Unidade> listaUnidades, Abrangencia abrangenciaSelecionada) throws Exception {
    List<DemandantePorSubrodinacaoDTO> lista = new ArrayList<>();
    datatFim = DateUtil.getDataFinal(datatFim);
    Map<String, DemandantePorSubrodinacaoDTO> mapDTO = new HashMap<>();
    DemandantePorSubrodinacaoDTO dto;
    String chaveTeste = "";
    
    List<Demanda> listaDemandas = demandaDAO.relatorioUnidadesDemandantesPorSubordinacao(dataInicio, datatFim, unidade, listaUnidades, abrangenciaSelecionada);
    Map<String,Integer> unidadesDemandadas = new HashMap<String,Integer>();
    for (Demanda demanda : listaDemandas) {
      unidadesDemandadas.put(demanda.getAssunto().getCaixaPostal().getUnidade().getId() + "#"+demanda.getAssunto().getCaixaPostal().getUnidade().getSigla(),0);
    }

    String chaveTemp;
    Integer valorTemp;
    for (Demanda demanda : listaDemandas) {
      chaveTeste = (demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao().getId() + "#" + demanda.getCaixaPostalDemandante().getUnidade().getId());
      dto = mapDTO.get(chaveTeste);
        
      if (dto == null) {
        dto = new DemandantePorSubrodinacaoDTO();
        dto.setQtdDemandas(0);
        Map<String,Integer> unidadesTemp = new HashMap<String,Integer>();
        unidadesTemp.putAll(unidadesDemandadas);
        dto.setListaDemandadas(unidadesTemp);
        dto.setSubordinacao(demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao());
        dto.setLetraSueg(StringUtils.substring(dto.getSubordinacao().getSigla(), dto.getSubordinacao().getSigla().length()-1));
        dto.setUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade());
        dto.setIdUnidadeDemandante(dto.getUnidadeDemandante().getId());
      }
      
      dto.setQtdDemandas(dto.getQtdDemandas() + 1);
      chaveTemp = demanda.getAssunto().getCaixaPostal().getUnidade().getId() + "#" + demanda.getAssunto().getCaixaPostal().getUnidade().getSigla();
      valorTemp = dto.getListaDemandadas().get(chaveTemp) + 1;    
      dto.getListaDemandadas().put(chaveTemp,valorTemp);
      
      mapDTO.put(chaveTeste, dto);
    }
    
    //Transforma o map na lista esperada
    for (String key : mapDTO.keySet()) {
      lista.add(mapDTO.get(key));
    }
    
    return lista;
  }

  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> relatorioGeralSuegConsolidadoPorUnidade(Abrangencia abrangencia,
      Unidade unidade, Date dataInicio, Date datatFim, List<Unidade> listaUnidades) {
    
    datatFim = DateUtil.getDataFinal(datatFim);
    List<RelatorioGeralVisaoSuegPorUnidadesDTO> lista = new ArrayList<>();
    List<Long> listaIdsUnidades = new ArrayList<>();
    for (Unidade temp : listaUnidades) {
      listaIdsUnidades.add(temp.getId());
    }
    
    List<Demanda> demandasRealizadasList = demandaDAO.obterListaDemandasAbertasPorSuegPeriodoRealizadas(abrangencia, unidade, dataInicio, datatFim, listaIdsUnidades);
    List<Demanda> demandasATratarList = demandaDAO.obterListaDemandasAbertasPorSuegPeriodoATratar(abrangencia, unidade, dataInicio, datatFim, listaIdsUnidades);
    
    RelatorioGeralVisaoSuegPorUnidadesDTO dto;
    String chave = "";
    Map<String, RelatorioGeralVisaoSuegPorUnidadesDTO> mapDTO = new HashMap<>();
    
    for (Demanda demanda : demandasATratarList) {
      chave = "";
      dto = new RelatorioGeralVisaoSuegPorUnidadesDTO();
      for (FluxoDemanda fd : demanda.getFluxosDemandasList()) {
        if (fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao() != null) {
          if (unidade!=null) {
            if (fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId().equals(unidade.getId())) {
              chave = (fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId() + "#" + fd.getCaixaPostal().getUnidade().getId()); 
              
              dto = mapDTO.get(chave);
              
              if(dto == null) {
                dto = new RelatorioGeralVisaoSuegPorUnidadesDTO();
                
                dto.setIdUnidadeSubordinacao(fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId());
                dto.setIdUnidadeDemandante(fd.getCaixaPostal().getUnidade().getId());
                dto.setUnidadeDemandante(fd.getCaixaPostal().getUnidade());
                
                dto.setDemandasAbertasTratar(0L);
                dto.setDemandasFechadasTratar(0L);
                dto.setTotalDemandasTratar(0L);
                
                dto.setDemandasAbertasRealizadas(0L);
                dto.setDemandasFechadasRealizadas(0L);
                dto.setTotalDemandasRealizadas(0L);
              }
              break;
            }
          } else if (listaIdsUnidades.contains(fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId())){
            chave = (fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId() + "#" + fd.getCaixaPostal().getUnidade().getId());
            dto = mapDTO.get(chave);
            if(dto == null) {
              dto = new RelatorioGeralVisaoSuegPorUnidadesDTO();
              
              dto.setIdUnidadeSubordinacao(fd.getCaixaPostal().getUnidade().getUnidadeSubordinacao().getId());
              dto.setIdUnidadeDemandante(fd.getCaixaPostal().getUnidade().getId());
              dto.setUnidadeDemandante(fd.getCaixaPostal().getUnidade());
              
              dto.setDemandasAbertasTratar(0L);
              dto.setDemandasFechadasTratar(0L);
              dto.setTotalDemandasTratar(0L);
              
              dto.setDemandasAbertasRealizadas(0L);
              dto.setDemandasFechadasRealizadas(0L);
              dto.setTotalDemandasRealizadas(0L);
            }
            break;
          }      
        }
      } 
      
      if(demanda.getDataHoraEncerramento() != null) {
        dto.setDemandasFechadasTratar(dto.getDemandasFechadasTratar() + 1);
      } else {
        dto.setDemandasAbertasTratar(dto.getDemandasAbertasTratar() + 1);          
      }
      dto.setTotalDemandasTratar(dto.getTotalDemandasTratar() + 1);
            
      mapDTO.put(chave, dto);
      
    }
    
    for (Demanda demanda : demandasRealizadasList) {
      chave = "";
      if (unidade!=null) {
        chave = (demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao().getId() + "#" + demanda.getCaixaPostalDemandante().getUnidade().getId()); 
      } else if (listaIdsUnidades.contains(demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao().getId())){
        chave = (demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao().getId() + "#" + demanda.getCaixaPostalDemandante().getUnidade().getId());
      }
      
      dto = mapDTO.get(chave);
      
      if(dto == null) {
        dto = new RelatorioGeralVisaoSuegPorUnidadesDTO();
        
        dto.setIdUnidadeSubordinacao(demanda.getCaixaPostalDemandante().getUnidade().getUnidadeSubordinacao().getId());
        dto.setIdUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade().getId());
        dto.setUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade());
        
        dto.setDemandasAbertasTratar(0L);
        dto.setDemandasFechadasTratar(0L);
        dto.setTotalDemandasTratar(0L);
        
        dto.setDemandasAbertasRealizadas(0L);
        dto.setDemandasFechadasRealizadas(0L);
        dto.setTotalDemandasRealizadas(0L);
      }
      
      if (dto.getIdUnidadeDemandante().equals(demanda.getCaixaPostalDemandante().getUnidade().getId())) {
        if(demanda.getDataHoraEncerramento() != null) {
          dto.setDemandasFechadasRealizadas(dto.getDemandasFechadasRealizadas() + 1);
        } else {
          dto.setDemandasAbertasRealizadas(dto.getDemandasAbertasRealizadas() + 1);          
        }
        dto.setTotalDemandasRealizadas(dto.getTotalDemandasRealizadas() + 1);
      } 
      
      mapDTO.put(chave, dto);
    }

    // Transforma o map na lista esperada
    for (String key : mapDTO.keySet()) {
      lista.add(mapDTO.get(key));
    }
    return lista;
  }
  
  /**
   * OS 041 Persistencia da lista de Contratos 
   * BUG do Hibernate - remover todos e persistir os novos.
   * @throws DataBaseException 
   * @throws BusinessException 
   */
  public void atualizarListaDemandaContratos(Demanda demanda) throws BusinessException, DataBaseException {
    List<DemandaContrato> temp = demandaContratoService.obterContratosPorIdDemanda(demanda.getId());
    for (DemandaContrato dc : temp) {
      if (!demanda.getContratosList().contains(dc)){
        demandaContratoService.removerContrato(dc);
      }
    }
  }
  
  public List<ExportacaoDemandaDTO> obterListaDemandasPorUnidadesResponsavelPeloAssunto(Abrangencia abrangencia, List<UnidadeDTO> unidadesDtoList, List<SituacaoEnum> situacaoEnumList){
    
    List<Long> idUnidadeList = new ArrayList<>();
    for (UnidadeDTO unidadeDTO : unidadesDtoList) {
      idUnidadeList.add(unidadeDTO.getId());
    }
    
    List<Demanda> demandaList = demandaDAO.obterListaDemandasPorUnidadesResponsavelPeloAssunto(idUnidadeList, situacaoEnumList);
    HashMap<Long, String> assuntoArvoreMap = new HashMap<>();
    
    List<ExportacaoDemandaDTO> demandasList = new ArrayList<>();
    
    for (Demanda demanda : demandaList) {
      
      ExportacaoDemandaDTO dtoMock = new ExportacaoDemandaDTO();
      
      if(!assuntoArvoreMap.containsKey(demanda.getAssunto().getId())) {
        assuntoArvoreMap.put(demanda.getAssunto().getId(), assuntoService.obterArvoreAssuntos(demanda.getAssunto()));
      }
      
      String arvore = assuntoArvoreMap.get(demanda.getAssunto().getId());
      
      dtoMock.setArvoreAssuntoAtual(arvore);
      

      dtoMock.setNumeroDemanda(demanda.getId());
      dtoMock.setCaixaPostalDemandante(demanda.getCaixaPostalDemandante().getSigla());

      //Necessário Ordenar os Fluxos Demanda
      List<FluxoDemanda> fluxosDemanda = demanda.getFluxosDemandasAtivoList();
      Collections.sort(fluxosDemanda);
      
      String fluxoDemandaStr = "";
      String prazoFluxoDemandaStr = "";
      String observadoresStr = "";
      
      Integer ultimaOrdemAtiva = 0;
      
      for (Iterator<FluxoDemanda> iterator = fluxosDemanda.iterator(); iterator.hasNext();) {
        FluxoDemanda fluxoDemanda = (FluxoDemanda) iterator.next();
        
        if(fluxoDemanda.isAtivo() && !fluxoDemanda.getOrdem().equals(0)) {
          ultimaOrdemAtiva = fluxoDemanda.getOrdem();
          
          fluxoDemandaStr += fluxoDemanda.getCaixaPostal().getSigla();
          prazoFluxoDemandaStr += fluxoDemanda.getPrazo();
          
          if(iterator.hasNext()) {
            fluxoDemandaStr += AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO;
            prazoFluxoDemandaStr += AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO;
          }
        }
      } 

      dtoMock.setFluxoDemanda(fluxoDemandaStr);
      dtoMock.setPrazoFluxoDemanda(prazoFluxoDemandaStr);
      
      List<CaixaPostal> observadorList = demanda.getCaixasPostaisObservadorList();
      for (Iterator<CaixaPostal> iterator = observadorList.iterator(); iterator.hasNext();) {
        CaixaPostal caixaPostal = (CaixaPostal) iterator.next();
        observadoresStr += caixaPostal.getSigla();
        
        if(iterator.hasNext()) {
          observadoresStr += AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO;
        }
      }
      
      dtoMock.setObservadoresDemanda(observadoresStr);
      if(!demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA) && 
          !demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA) ) {
        dtoMock.setResponsavelAtual(demanda.getCaixaPostalResponsavel().getSigla());
      } else {
        
        for (FluxoDemanda fluxoDem : fluxosDemanda) {
          if(fluxoDem.getOrdem().equals(ultimaOrdemAtiva-1)) {
            dtoMock.setResponsavelAtual(fluxoDem.getCaixaPostal().getSigla());
            break;
          }
        }
        
      }
      demandasList.add(dtoMock);
      
    }
    
    return demandasList;
  }

 public Boolean existeDemandasPorUnidadesResponsavelPeloAssunto(List<UnidadeDTO> unidadesDtoList, List<SituacaoEnum> situacaoEnumList){
    
    List<Long> idUnidadeList = new ArrayList<>();
    for (UnidadeDTO unidadeDTO : unidadesDtoList) {
      idUnidadeList.add(unidadeDTO.getId());
    }
    
    return demandaDAO.existeDemandasPorUnidadesResponsavelPeloAssunto(idUnidadeList, situacaoEnumList);
 }
 
 public void adicionarObservadorDemanda(Long idDemanda, CaixaPostal caixaObservadora) {
   Demanda demanda = findById(idDemanda);
   demanda.getCaixasPostaisObservadorList().add(caixaObservadora);
   demandaDAO.update(demanda);
 }
 
 /**
  * Obtém o agrupamento das unidades/caixas-postais ativas.
  * 
  * @return Unidades/caixas-postais ativas
  */
 public KeyGroupValuesCollection<CaixaPostal> getAgrupamentoCaixaPostal(Abrangencia abrangencia, List<CaixaPostal> caixasPostaisEnvolvidas ) {

     KeyGroupValuesCollection<CaixaPostal> agrupamentoCaixaPostal = new KeyGroupValuesCollection<>();

     //Lista de Unidades a ser percorrida
     List<Unidade> unidades =
         unidadeService.obterUnidadesECaixasPostaisPorTipo(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);

     String agrupamentoParam = parametroService.obterParametroByNome(ParametroConstantes.AGRUPAMENTO_CAIXA_POSTAL).getValor();
     List<String> agrupamentoGrupo = Arrays.asList(agrupamentoParam.split(";"));
     
     for (Unidade unidade : unidades) {
       
       String chave = null;
       
       for (String agrupamentoComCoringa : agrupamentoGrupo) {
         
         int posCoringa = agrupamentoComCoringa.indexOf("%");
         String agrupamento = agrupamentoComCoringa.replace("%", "");
         
         //Não tem coringa
         if(posCoringa == -1) {
           if(agrupamentoComCoringa.equals(unidade.getSigla())) {
             chave = String.format(PREFIXO_AGRUPAMENTO_REDE, agrupamento);
           }
           
         // Coringa no inicio da Sigla
         } else if (posCoringa == 0) {
           if(unidade.getSigla().endsWith(agrupamento)) {
             chave = String.format(PREFIXO_AGRUPAMENTO_REDE, agrupamento);
           }
           
         // Coringa no Fim  
         } else {
           if(unidade.getSigla().startsWith(agrupamento)) {
             chave = String.format(PREFIXO_AGRUPAMENTO_REDE, agrupamento);
           }
         }
         
       }
       
       //Caso não agrupou pelo agrupamento da tabela Parametro vai pelo caminho padrão
       if(chave == null) {
          chave =
               (TipoUnidadeEnum.MATRIZ.equals(unidade.getTipoUnidade())) ? TipoUnidadeEnum.MATRIZ.getDescricao()
                   : String.format(PREFIXO_AGRUPAMENTO_REDE, unidade.getSigla().length() > 5 ? unidade.getSigla().substring(0, NUM_CHARS_SUBSTRING_REDE) : unidade.getSigla());
       }
       

       // Remover as caixas postais informadas por parametro
       if(caixasPostaisEnvolvidas != null && !caixasPostaisEnvolvidas.isEmpty()) {
         unidade.getCaixasPostaisList().removeAll(caixasPostaisEnvolvidas);
       }

       if (!unidade.getCaixasPostaisList().isEmpty()) {
         agrupamentoCaixaPostal.put(chave, unidade.getCaixasPostaisList());
       }
   }

   return agrupamentoCaixaPostal;
 }
 
 public List<DemandaDTO> obterListaDemandasRelatorioAssuntoPeriodo(Long idAssunto, Date periodoDtInicial, Date periodoDtFinal, SituacaoEnum... situacao ) {

   List<DemandaDTO> demandaDTOList = new ArrayList<>();
   List<Demanda> demandasList = demandaDAO.obterListaDemandasRelatorioAssuntoPeriodo(idAssunto, periodoDtInicial, periodoDtFinal, situacao);
   
   List<Date> datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();

   
   List<Assunto> assuntoList = assuntoService.obterAssuntosFetchPai();
   if(!demandasList.isEmpty()) {
     
     for (Demanda demanda : demandasList) {
       
       DemandaDTO demandaDTOTemp = new DemandaDTO();
       this.obterDemandaDTOPreenchidaPorDemanda(demanda, demandaDTOTemp, assuntoList);
       if (SituacaoEnum.ABERTA.equals(demanda.getSituacao())) {
         processarDemandaAberta(datasFeriadosList, demandaDTOTemp, demanda, null);
       }
       demandaDTOList.add(demandaDTOTemp);
       
    }
   }
   
   return demandaDTOList;
 }
 
 public List<DemandaDTO> obterListaDemandasRelatorioAssuntoPeriodo(List<Demanda> demandaList) {
   
   List<DemandaDTO> demandaDTOList = new ArrayList<>();
   
   List<Demanda> demandasListAux = demandaDAO.findByIdFetchList(demandaList);
   
   List<Date> datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();
   
   List<Assunto> assuntoList = assuntoService.obterAssuntosFetchPai();
   if(!demandaList.isEmpty()) {
     
     for (Demanda demanda : demandasListAux) {
       
       DemandaDTO demandaDTOTemp = new DemandaDTO();
       this.obterDemandaDTOPreenchidaPorDemanda(demanda, demandaDTOTemp, assuntoList);
       if (SituacaoEnum.ABERTA.equals(demanda.getSituacao())) {
         processarDemandaAberta(datasFeriadosList, demandaDTOTemp, demanda, null);
       }
       demandaDTOList.add(demandaDTOTemp);
       
    }
   }
   
   return demandaDTOList;
 }
  

 public List<DemandasEmAbertoDTO> obterDemandasEmAbertoPorAssunto(UnidadeDTO unidade, Assunto assunto, Date dataAberturaInicio,
     Date dataEncerramento, Abrangencia abrangenciaSelecionada, List<Assunto> listaTodosAssuntos) {
   List<Demanda> demandas = demandaDAO.obterDemandasEmAbertoPorAssunto(unidade, assunto, dataAberturaInicio, dataEncerramento, abrangenciaSelecionada);
   
   List<Date> datasFeriadosList = feriadoService.obterListaDeDatasDosFeriados();

   List<DemandasEmAbertoDTO> listaDemandasEmAbertoDTO = new ArrayList<>();
   for (Demanda demanda : demandas) {
     DemandasEmAbertoDTO demandaEmAbertoDTO = new DemandasEmAbertoDTO();
      demandaEmAbertoDTO.setAreaDemandada(demanda.getCaixaPostalResponsavel().getSigla());
      demandaEmAbertoDTO.setAssunto(this.assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaTodosAssuntos));
      demandaEmAbertoDTO.setNumeroDemanda(demanda.getId());
      
      if ((demanda.getDemandaPai() != null) && (demanda.getDemandaPai().getId() != null)) {
    	  demandaEmAbertoDTO.setFlagDemandaFilha(Boolean.TRUE);
    	  demandaEmAbertoDTO.setIdDemandaPai(demanda.getDemandaPai().getId());
    	  demandaEmAbertoDTO.setAreaDemandada(demanda.getCaixaPostalResponsavel().getSigla());
      } else {
    	  demandaEmAbertoDTO.setFlagDemandaFilha(Boolean.FALSE);
      }
      
      Object[] objeto = getPrazoAtendimento(demanda, datasFeriadosList);
      demandaEmAbertoDTO.setDtPrazoVencimento(objeto[0].toString());
      Integer numDiasPrazo = (Integer) objeto[1];
      demandaEmAbertoDTO.setPrazo(numDiasPrazo);
      listaDemandasEmAbertoDTO.add(demandaEmAbertoDTO);
      
      demandaEmAbertoDTO.setSituacaoVencimento((Integer) objeto[2]);
      demandaEmAbertoDTO.setDtPrazoVencimentoOrdenacao((String) objeto[3]);
  }
   
   return listaDemandasEmAbertoDTO;
 }
 
 private List<Demanda> obterDemandasNaExternaAguardandoUnidade(Date dataInicio, Date dataFim, Long idUnidade) {

   List<Demanda> listaDemandasExternasFiltrada = new ArrayList<>();
   
   List<Demanda> listaDemandasExternas = demandaDAO.obterDemandasNaExternaAguardandoUnidade(dataInicio, dataFim, idUnidade);
   
   for (Demanda demanda : listaDemandasExternas) {
     
     List<FluxoDemanda> fluxoDemandaListTemp = demanda.getFluxosDemandasAtivoList();
     
     if ((fluxoDemandaListTemp != null) && !fluxoDemandaListTemp.isEmpty() && (fluxoDemandaListTemp.size() > 1)) {
       Collections.sort(fluxoDemandaListTemp);
       
       if (fluxoDemandaListTemp.get(fluxoDemandaListTemp.size() - 2).getCaixaPostal().getUnidade().getId().equals(idUnidade)) {
         listaDemandasExternasFiltrada.add(demanda);
       }
     }
   }
   
   return listaDemandasExternas;
   
 }
 
 public List<DemandasAguardandoUnidadeDTO> obterDemandasAguardandoUnidade(Date dataInicio, Date dataFim, Abrangencia abrangenciaSelecionada, Long idUnidade, List<Assunto> listaTodosAssuntos){
	 List<Demanda> demandas = demandaDAO.obterDemandasAguardandoUnidade(dataInicio, dataFim, abrangenciaSelecionada, idUnidade);	 
	 if(idUnidade != null) {
	   demandas.addAll(obterDemandasNaExternaAguardandoUnidade(dataInicio, dataFim, idUnidade));   
	 }

	 List<DemandasAguardandoUnidadeDTO> listaDemandasAguardandoUnidadeDTO = new ArrayList<>();
	 
	 for(Demanda demanda : demandas) {
		 
		 Atendimento atendimento = this.atendimentoService.obterUltimoAtendimentoPorDemanda(demanda.getId());
		 
     int diasComResponsavel = DateUtil.calculaDiferencaEntreDatasEmDias(new Date(), atendimento.getDataHoraRecebimento());
     int diasUltimoEncaminhamento = DateUtil.calculaDiferencaEntreDatasEmDias(atendimento.getDataHoraRecebimento(), demanda.getDataHoraAbertura());

		 DemandasAguardandoUnidadeDTO demandaAguardandoDTO = new DemandasAguardandoUnidadeDTO();
		 demandaAguardandoDTO.setAssunto(this.assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaTodosAssuntos));
		 demandaAguardandoDTO.setDataAbertura(demanda.getDataHoraAbertura());
		 demandaAguardandoDTO.setDiasComResponsavel(diasComResponsavel);
		 demandaAguardandoDTO.setDiasUltimoEncaminhamento(diasUltimoEncaminhamento);
		 demandaAguardandoDTO.setMatriculaDemandante(demanda.getMatriculaDemandante());
		 demandaAguardandoDTO.setNomeDemandante(demanda.getNomeUsuarioDemandante());
		 demandaAguardandoDTO.setNumeroSiarg(demanda.getId());
		 if(atendimento.getFluxoDemanda() != null) {
			 demandaAguardandoDTO.setPrazoAtual(atendimento.getFluxoDemanda().getPrazo());
		 }
		 demandaAguardandoDTO.setResponsavelAtual(demanda.getCaixaPostalResponsavel().getSigla());
		 demandaAguardandoDTO.setTitulo(demanda.getTitulo());
		 demandaAguardandoDTO.setUltimoEncaminhamento(atendimento.getDataHoraRecebimento());
		 demandaAguardandoDTO.setUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade().getSigla());
		 listaDemandasAguardandoUnidadeDTO.add(demandaAguardandoDTO);
	 }
	 
	 return listaDemandasAguardandoUnidadeDTO;
 }
     
 public Object[] getPrazoAtendimento(final Demanda demanda, final List<Date> datasFeriadosList) {
   List<FluxoDemanda> fluxoDemandaList = demanda.getFluxosDemandasAtivoList();
   Object[] objects = new Object[4];
   String dataFormatada = "";
   String dataCompleta = "";
   int qtdPrazoDias = 0;

   for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
       if(!fluxoDemanda.getOrdem().equals(0)) {
         qtdPrazoDias += fluxoDemanda.getPrazo();
     }
   }
   objects[1] = qtdPrazoDias;
   if (qtdPrazoDias == 0) {
     Date dataPrevista = new Date();
     dataFormatada = DateUtil.formatDataPadrao(dataPrevista);
     dataCompleta = dataFormatada;
     objects[0] = dataCompleta;
     objects[2] = 0;
     objects[3] = DateUtil.format(dataPrevista, DateUtil.FORMATO_DATA_AMERICANO_SEM_SEPARADOR);
     return objects;
   } else {
     Date dataPrevista = feriadoService.adicionarDiasUteis(demanda.getDataHoraAbertura(), qtdPrazoDias, datasFeriadosList);
     dataFormatada = DateUtil.formatDataPadrao(dataPrevista);
     dataCompleta = dataFormatada;
     objects[0] = dataCompleta;
     objects[3] = DateUtil.format(dataPrevista, DateUtil.FORMATO_DATA_AMERICANO_SEM_SEPARADOR);
     
     Calendar hoje = Calendar.getInstance();
     
     hoje.set(Calendar.HOUR_OF_DAY, 23);
     hoje.set(Calendar.MINUTE, 59);
     hoje.set(Calendar.SECOND, 59);
     
     Calendar vencimento = Calendar.getInstance();
     
     vencimento.setTime(dataPrevista);
     
     objects[2] = hoje.compareTo(vencimento);
          
     return objects;
   }
 }
 
public Integer getPrazoSituacaoVencimento(final Demanda demanda, final List<Date> datasFeriadosList) {
   
   List<FluxoDemanda> fluxoDemandaList = demanda.getFluxosDemandasAtivoList();
   int qtdPrazoDias = 0;

   for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
       if(!fluxoDemanda.getOrdem().equals(0)) {
         qtdPrazoDias += fluxoDemanda.getPrazo();
     }
   }
   
   if (qtdPrazoDias == 0) {
     return 0;
   } else {
     Date dataPrevista = feriadoService.adicionarDiasUteis(demanda.getDataHoraAbertura(), qtdPrazoDias, datasFeriadosList);

     Calendar hoje = Calendar.getInstance();
     
     hoje.set(Calendar.HOUR_OF_DAY, 23);
     hoje.set(Calendar.MINUTE, 59);
     hoje.set(Calendar.SECOND, 59);
     
     Calendar vencimento = Calendar.getInstance();
     
     vencimento.setTime(dataPrevista);
     
     return  hoje.compareTo(vencimento);
   }
 }
  
 public List<Demanda> obterDemandasPorAcaoAtendimento(Date dataInicial, Date dataFinal, String caixaPostal, String situacao){
	 return demandaDAO.obterDemandasPorAcaoAtendimento(dataInicial, dataFinal, caixaPostal, situacao);
 }
 
 public List<DemandaAbertaDTO> obterDemandasWSConsulta(FiltrosConsultaDemandas filtro) {

   long ini = System.currentTimeMillis();
   List<Demanda> demandaList = demandaDAO.obterDemandasWSConsulta(filtro);
   long fim = System.currentTimeMillis();
   logger.debug("Quantidade de demandas: "+demandaList.size());
   logger.debug("Tempo para consulta de demandas: " + (fim-ini)/1000d + "segundos.");
   
   demandaList = filtrarDemandasWSConsulta(demandaList, filtro);
   
   ini = System.currentTimeMillis();
   List<DemandaAbertaDTO> demandaDTOList = processarDemandasWSConsulta(demandaList, filtro);
   fim = System.currentTimeMillis();
   
   logger.debug("Tempo para processar dados encontrados: " + (fim-ini)/1000d + "segundos.");
   
   return demandaDTOList;
 }

  private List<Demanda> filtrarDemandasWSConsulta(List<Demanda> demandaList, FiltrosConsultaDemandas filtro) {
    List<Demanda> demandasFiltradas = new ArrayList<>();

    for (Demanda demanda : demandaList) {
       
       List<FluxoDemanda> fluxoDemandasList = demanda.getFluxosDemandasAtivoSemFluxoDemandanteList();
       Collections.sort(fluxoDemandasList);
       FluxoDemanda primeiroFluxo = fluxoDemandasList.get(0);
      
       boolean adicionarDemanda = true;
       
       if(filtro.getCoUnidadeDemandada() != null && 
         !primeiroFluxo.getCaixaPostal().getUnidade().getCgcUnidade().equals(filtro.getCoUnidadeDemandada())) {
           adicionarDemanda = false;
       }
       
       if(StringUtils.isNotBlank(filtro.getCpDemandada()) 
           && !primeiroFluxo.getCaixaPostal().getSigla().equals(filtro.getCpDemandada())) {
           adicionarDemanda = false;
       }
       
       if(filtro.getPrazoDemanda() != null) {
         Integer prazoTotalDemanda = 0;
         
         for (FluxoDemanda fluxoDemanda : fluxoDemandasList) {
           prazoTotalDemanda += fluxoDemanda.getPrazo();
         }
         
         if(!prazoTotalDemanda.equals(filtro.getPrazoDemanda())) {
           adicionarDemanda = false;
         }

         
       }
       if(filtro.getPrazoResponsavel() != null) {
         
         for (FluxoDemanda fluxoDemanda : fluxoDemandasList) {
          if(fluxoDemanda.getCaixaPostal().getId().equals(demanda.getCaixaPostalResponsavel().getId())
              && !fluxoDemanda.getPrazo().equals(filtro.getPrazoResponsavel())
              ) {
            adicionarDemanda = false;
            break;
          }
        }
       }
       
       if(adicionarDemanda) {
         demandasFiltradas.add(demanda);
       }
       
       
    }
    
    
    return demandasFiltradas;
  }

  private List<DemandaAbertaDTO> processarDemandasWSConsulta(List<Demanda> demandaList, FiltrosConsultaDemandas filtro) {
    
    List<Assunto> listaAssuntos = this.assuntoService.obterAssuntosFetchPai();
    List<Date> datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();

    
    List<DemandaAbertaDTO> demandaDTOList = new ArrayList<>();
    for (Demanda demanda : demandaList) {
      demandaDTOList.add(converterDemandaParaExportacaoDemandaDTO(demanda, listaAssuntos, datasFeriadosList, filtro));
    }
    return demandaDTOList;
  }

  private DemandaAbertaDTO converterDemandaParaExportacaoDemandaDTO(Demanda demanda, List<Assunto> listaAssuntos, List<Date> datasFeriadosList, FiltrosConsultaDemandas filtro) {
    
    List<FluxoDemanda> fluxosDemanda = demanda.getFluxosDemandasAtivoSemFluxoDemandanteList();

    DemandaAbertaDTO dto = new DemandaAbertaDTO();
    dto.setNumeroDemanda(demanda.getId());
    dto.setCodigoAssunto(demanda.getAssunto().getId());
    dto.setNomeAssunto(demanda.getAssunto().getNome());
    dto.setArvoreAssunto(assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaAssuntos));

    dto.setCoSituacao(demanda.getSituacao().getValor());
    dto.setNomeSituacao(demanda.getSituacao().getDescricao());
    
    dto.setSiglaCaixaDemandante(demanda.getCaixaPostalDemandante().getSigla());
    dto.setCgcUnidadeDemandante(demanda.getCaixaPostalDemandante().getUnidade().getCgcUnidade());
    
    dto.setSiglaCaixaResponsavel(demanda.getCaixaPostalResponsavel().getSigla());
    dto.setCgcUnidadeResponsavel(demanda.getAssunto().getCaixaPostal().getUnidade().getCgcUnidade());
    
    dto.setCodigoUnidadePrimeiroFluxo(fluxosDemanda.get(0).getCaixaPostal().getUnidade().getCgcUnidade());

    dto.setDataAbertura(demanda.getDataHoraAbertura());
    dto.setTitulo(demanda.getTitulo());
    if(filtro.getConteudo().equals(1)) {
      dto.setConteudo(demanda.getTextoDemanda());
    }else {
      dto.setConteudo("");
    }
    
    Integer demandaNaExterna = 0;
    Integer demandaTipoConsulta = 0;
    Integer demandaReaberta = 0;
    
    if(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA) 
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)) {
      demandaNaExterna = 1;
    }

    if(demanda.getTipoDemanda().equals(TipoDemandaEnum.CONSULTA)) {
      demandaTipoConsulta = 1;
    }
    
    List<Atendimento> atendimentoList = demanda.getAtendimentosList();

    for (Atendimento atendimento : atendimentoList) {
      if(atendimento.getAcaoEnum() != null 
          && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.REABRIR) ) {
        demandaReaberta = 1;
      }
    }
    
    dto.setDemandaTipoConsulta(demandaTipoConsulta);
    dto.setDemandaNaExterna(demandaNaExterna);
    dto.setDemandaReaberta(demandaReaberta);
    
    int prazoUnidadeAtual = 0;
    int prazoDemanda = 0;
    for (FluxoDemanda fluxoDemanda : fluxosDemanda) {
      prazoDemanda += fluxoDemanda.getPrazo();
      if(fluxoDemanda.getCaixaPostal().equals(demanda.getCaixaPostalResponsavel())) {
        prazoUnidadeAtual = fluxoDemanda.getPrazo();
      }
    }

    
    dto.setPrazoDemanda(prazoDemanda);
    dto.setPrazoUnidadeAtual(prazoUnidadeAtual);
    if(demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
      dto.setPrazoRestante(this.obterPrazoDemanda(demanda, datasFeriadosList));
    } else {
      dto.setPrazoRestante(0);
    }
    
    dto.setAtendimentoList(converterAtendimentosParaExportacaoAtendimentoDTO(demanda));
    
    List<String> numeroContratoList = new ArrayList<>();
    
    if(demanda.getAssunto().getContrato()) {
      for (DemandaContrato demandaContrato :  demanda.getContratosList()) {
        numeroContratoList.add(demandaContrato.getNumeroContrato());
      }
    }
    dto.setNumeroContratoList(numeroContratoList);
    
    return dto;
  }

  private List<AtendimentoDTO> converterAtendimentosParaExportacaoAtendimentoDTO(Demanda demanda) {
   
    List<AtendimentoDTO> atendimentoDTOList = new ArrayList<>();
    
    List<Atendimento> atendimentoList = demanda.getAtendimentosList();
    
    AcaoAtendimentoEnum acaoAtendimentoAnterior = null;
    
    Collections.sort(demanda.getAtendimentosList());
    
    //CRIAR AO ATENDIMENTO Padrão 01
    Atendimento atendimentoAbertura = demanda.getAtendimentosList().get(0);
    Atendimento atendimentoDois = demanda.getAtendimentosList().get(1);
    AtendimentoDTO atendimentoAberturaDTO = new AtendimentoDTO();
    atendimentoAberturaDTO.setNumeroAtendimento(atendimentoAbertura.getId());
    atendimentoAberturaDTO.setDataHistorico(demanda.getDataHoraAbertura());
    atendimentoAberturaDTO.setMatriculaUsuario(demanda.getMatriculaDemandante());
    atendimentoAberturaDTO.setNomeUsuario(demanda.getNomeUsuarioDemandante());
    atendimentoAberturaDTO.setNumeroAcao(AcaoAtendimentoEnum.INCLUIR.getValor());
    atendimentoAberturaDTO.setDescricaoAcao(AcaoAtendimentoEnum.INCLUIR.getDescricao());
    atendimentoAberturaDTO.setSiglaCaixaOrigem(atendimentoAbertura.getFluxoDemanda().getCaixaPostal().getSigla());
    atendimentoAberturaDTO.setSiglaCaixaDestino(atendimentoDois.getFluxoDemanda().getCaixaPostal().getSigla());
    atendimentoDTOList.add(atendimentoAberturaDTO);
    
    
    String caixaOrigem = "";
    String caixaDestino = "";
    
    for (int i = 1; i < atendimentoList.size(); i++) {
      

      Atendimento atendimento = atendimentoList.get(i);

      if(atendimento.getDataHoraAtendimento() == null ||
          AcaoAtendimentoEnum.INCLUIR.equals(atendimento.getAcaoEnum()) ||
          AcaoAtendimentoEnum.RASCUNHO.equals(atendimento.getAcaoEnum()) ) {
        continue;
      }
      
      AtendimentoDTO atendimentoDTO = new AtendimentoDTO();
      atendimentoDTO.setNumeroAtendimento(atendimento.getId());
      atendimentoDTO.setDataHistorico(atendimento.getDataHoraAtendimento());
      atendimentoDTO.setMatriculaUsuario(atendimento.getMatricula());
      atendimentoDTO.setNomeUsuario(atendimento.getNomeUsuarioAtendimento());
      
      if(atendimento.getAcaoEnum() != null) {
        atendimentoDTO.setNumeroAcao(atendimento.getAcaoEnum().getValor());
        atendimentoDTO.setDescricaoAcao(atendimento.getAcaoEnum().getDescricao());
      } 
      
      if(atendimento.getFluxoDemanda() == null
        && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.REABRIR) || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CANCELAR) )) {
          atendimentoDTO.setSiglaCaixaOrigem(demanda.getCaixaPostalDemandante().getSigla());
          atendimentoDTO.setSiglaCaixaDestino(demanda.getCaixaPostalDemandante().getSigla());
          
          atendimentoDTOList.add(atendimentoDTO);
          continue;
      }
      
      //Definição de Caixa Origem
      if (atendimento.getAcaoEnum() != null
          && atendimento.getFluxoDemanda().getCaixaPostal().getSigla().equals(TipoUnidadeEnum.ARROBA_EXTERNA.getDescricao())) {
        

        // PEGAR O ULTIMO ATENDIMENTO ANTES DESSA MIGRACAO, IGNORANDO ATUALIZACAO

        if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.MIGRAR)
            || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)
            || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)) {
          int j = 1;
          while (i > j) {
            Atendimento atendimentoAnteriorLoop = atendimentoList.get(i - j);
            if (atendimentoAnteriorLoop.getAcaoEnum() != null
                && !atendimentoAnteriorLoop.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)) {
              caixaOrigem = atendimentoAnteriorLoop.getFluxoDemanda().getCaixaPostal().getSigla();
              break;
            }
            j++;
          }
        } else {
          caixaOrigem = atendimento.getFluxoDemanda().getCaixaPostal().getSigla();
        }
        
      } else {
        caixaOrigem = atendimento.getFluxoDemanda().getCaixaPostal().getSigla();
      }
      //Definição de Caixa Origem
      
      
      //Definição de Resposta ao demandante
      boolean respondeuAoDemandante = false;
      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)
      // Se o primeiro fluxo demanda
          && (atendimento.getFluxoDemanda().getOrdem().equals(1) || (
          // Um externa fluxo 2 respondeu
          (atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
              || atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))
              && atendimento.getFluxoDemanda().getOrdem() <= 2))) {
        respondeuAoDemandante = true;
      }
      
      
      //Definição da Caixa Destino e Caixa Origem
      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.FECHAR)) {
        // Inverte de Demandande para destino
        caixaDestino = caixaOrigem;
        caixaOrigem = demanda.getCaixaPostalDemandante().getSigla();
        
      } else if (atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CANCELAR))) {
        caixaDestino = demanda.getCaixaPostalDemandante().getSigla();
        caixaOrigem = demanda.getCaixaPostalDemandante().getSigla();
      
      } else if (atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)
          || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR))) {
        caixaDestino = atendimento.getFluxoDemanda().getCaixaPostal().getSigla();

      } else {

        // Se o i atual não é o ultimo atendimento
        if (i < (atendimentoList.size() - 1)) {

          // Pega o destino da caixa postal do proximo registro de atendimento
          if (atendimentoList.get(i + 1).getFluxoDemanda() != null) {
            caixaDestino = atendimentoList.get(i + 1).getFluxoDemanda().getCaixaPostal().getSigla();
          } else {
            caixaDestino = demanda.getCaixaPostalDemandante().getSigla();
          }
          // Se for o ultimo registro que respondeu
        } else if (i == (atendimentoList.size() - 1) && respondeuAoDemandante) {
          caixaDestino = demanda.getCaixaPostalDemandante().getSigla();
          // Se for o Ultimo atendimento, uma migração e o destino Caixa Demandante
        } else if (i == (atendimentoList.size() - 1) && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.MIGRAR)) {

          caixaDestino = atendimento.getFluxoDemanda().getCaixaPostal().getSigla(); // ADICIONANDO
        }
      }
      
      atendimentoDTO.setSiglaCaixaOrigem(caixaOrigem);
      atendimentoDTO.setSiglaCaixaDestino(caixaDestino);
      
      atendimentoDTOList.add(atendimentoDTO);
    }
    
    return atendimentoDTOList;
  }
  
  public List<Demanda> obterDemandasReabertasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal) {
    return demandaDAO.obterDemandasReabertasPorUnidadePeriodo(idUnidade, abrangenciaSelecionada, dataInicial, dataFinal);
  }
  
  public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasFechadas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterDemandasFechadas(idUnidade,  abrangenciaSelecionada, dataInicial,  dataFinal);
  }

  public List<Demanda> obterListaDemandasFechadas(String sg,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterListaDemandasFechadas(sg, dataInicial,  dataFinal);
  }

  public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasAbertas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterDemandasAbertas(idUnidade,  abrangenciaSelecionada, dataInicial,  dataFinal);
  }
  
  public List<Demanda> obterListaDemandasAbertas(String sg,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterListaDemandasAbertas(sg, dataInicial,  dataFinal);
  }
  
  public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasCanceladas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterDemandasCanceladas(idUnidade,  abrangenciaSelecionada, dataInicial,  dataFinal);
  }
  
  public List<Demanda> obterListaDemandasCanceladas(String sg,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterListaDemandasCanceladas(sg, dataInicial,  dataFinal);
  }

  public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasReabertas(Long idUnidade, Abrangencia abrangenciaSelecionada,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterDemandasReabertas(idUnidade,  abrangenciaSelecionada, dataInicial,  dataFinal);
  }
  
  public List<Demanda> obterDemandasFechadasTotal(Long idUnidade, Long idAbrangencia,Date dataInicial, Date dataFinal){
	  return demandaDAO.obterDemandasFechadasTotal(idUnidade, idAbrangencia,  dataInicial,  dataFinal);
  }
  
   public Atendimento consultaAtendimento(Long nuDemanda) {
	   return demandaDAO.consultaAtendimento(nuDemanda);
   }
  
   public List<Demanda> obterListaDemandasReabertasTotal(Long idUnidade,Date dataInicial, Date dataFinal){
		  return demandaDAO.obterListaDemandasReabertasTotal(idUnidade,  dataInicial,  dataFinal);
	  }
  
   public List<RelatorioIndicadorReaberturaDTO> obterIndicadorReaberturaPorUnidadePeriodo(Long idUnidade, Long idAbrangencia, Date dataInicial, Date dataFinal) {
		  return demandaDAO.obterIndicadorReaberturaPorUnidadePeriodo(idUnidade, idAbrangencia, dataInicial, dataFinal);
	  }
  
}

