package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.dto.ConfiguracaoEmailDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExtratoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.dto.PrazoQuestionamentoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.email.EmailService;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;

@Stateless
public class RotinaEmailService {
  
  @Inject
  private DemandaService demandaService;

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private FluxoDemandaService fluxoDemandaService;

  @Inject
  private EmailService emailService;

  @Inject
  private ParametroDAO parametroDAO;

  @Inject
  private FeriadoService feriadoService;
  
  @Inject
  protected Logger logger;

  
  /**
   * Item 02 da OS024 do Siarg de envio de emails.
   * Envia E-mail de Extrato de Demandas para Atuar POR ABRANGENCIA.
   */
  public void enviarEmailExtratoDemandas() {
    String motivo = "Extrato";
    String assunto = parametroDAO.obterParametroByNome(ParametroConstantes.ASSUNTO_JOB_EXTRATO).getValor();
    String conteudo = parametroDAO.obterParametroByNome(ParametroConstantes.TEMPLATE_JOB_EXTRATO).getValor();
    String urlSistema = parametroDAO.obterParametroByNome(ParametroConstantes.URL_SISTEMA).getValor();
    String emailComCopia = parametroDAO.obterParametroByNome(ParametroConstantes.CONFIG_FROM).getValor();

    
    ConfiguracaoEmailDTO configuracaoEmailDTO = emailService.getConfiguracaoEmail();
    Map<CaixaPostal, List<ExtratoDemandasDTO>> map = obterListaCaixasPostaisParaEnvioEmailExtrato();
    
    for (Map.Entry<CaixaPostal, List<ExtratoDemandasDTO>> el : map.entrySet()) {
      CaixaPostal cp = el.getKey();
      List<ExtratoDemandasDTO> lista = el.getValue();
      emailService.enviarEmailExtratoPorCaixa(cp, configuracaoEmailDTO, lista, assunto, conteudo, urlSistema, motivo, emailComCopia);
    }
  }
  
  private Map<CaixaPostal, List<ExtratoDemandasDTO>> obterListaCaixasPostaisParaEnvioEmailExtrato() {
    HashMap<CaixaPostal, List<ExtratoDemandasDTO>> hashMap = new HashMap<>();
    List<ExtratoDemandasDTO> listaDemandas =  listarTodasDemandasAbertas();
    for (ExtratoDemandasDTO extratoDemandasDTO : listaDemandas) {
      if (!hashMap.containsKey(extratoDemandasDTO.getCaixaPostalResponsavel())) {
        hashMap.put(extratoDemandasDTO.getCaixaPostalResponsavel(), new ArrayList<ExtratoDemandasDTO>());
      }      
      List<ExtratoDemandasDTO> listaTemp = hashMap.get(extratoDemandasDTO.getCaixaPostalResponsavel());
      listaTemp.add(extratoDemandasDTO);
    }
    return hashMap;
  }
  
  private List<ExtratoDemandasDTO> listarTodasDemandasAbertas(){
    List<ExtratoDemandasDTO> lista = new ArrayList<>();
    List<Assunto> listaAssuntos = this.assuntoService.obterAssuntosFetchPai();
    List<Date> listaFeriados = feriadoService.obterListaDeDatasDosFeriados();
    List<Demanda> demandasAbertasList = demandaService.obterListaDemandasAbertasParaExtrato();
    
    ExtratoDemandasDTO extratoTemp;
    for (Demanda demanda : demandasAbertasList) {
      Atendimento ultimoAtendimento = Collections.max(demanda.getAtendimentosList());
      extratoTemp = new ExtratoDemandasDTO();
      extratoTemp.setIdDemanda(demanda.getId());
      extratoTemp.setDemanda(demanda);
      extratoTemp.setAssuntoCompleto(assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaAssuntos));
      extratoTemp.setPrazo(calcularPrazoDemanda(demanda));
      if(extratoTemp.getPrazo() == null) {
        logger.info("Demanda id: "+demanda.getId() +" com problema de prazo null");
        continue;
      }
      extratoTemp.setUltimaMovimentacao(ultimoAtendimento.getDataHoraRecebimento());
      extratoTemp.setDiasSemInteracao(feriadoService.calcularQtdDiasUteisEntreDatas(ultimoAtendimento.getDataHoraRecebimento(), new Date(), listaFeriados));
      if (TipoUnidadeEnum.EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade()) || 
          TipoUnidadeEnum.ARROBA_EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade())) {
        extratoTemp.setCaixaPostalResponsavel(fluxoDemandaService.obterCaixaPostalFluxoAnteriorExterna(demanda));
        extratoTemp.setNomeCaixaPostalResponsavel(extratoTemp.getCaixaPostalResponsavel().getSigla() + " >> "+ demanda.getCaixaPostalResponsavel().getSigla() + " (@EXTERNA)");
      } else {
        extratoTemp.setCaixaPostalResponsavel(demanda.getCaixaPostalResponsavel());
        extratoTemp.setNomeCaixaPostalResponsavel(demanda.getCaixaPostalResponsavel().getSigla());
      }
      extratoTemp.setCor(obterCorDemandaPorPrazo(ultimoAtendimento, extratoTemp.getPrazo(), listaFeriados));
      lista.add(extratoTemp);
    }
    return lista;
  }

  private String obterCorDemandaPorPrazo(final Atendimento ultimoAtendimento, final Integer prazo, final List<Date> listaFeriados) {
    Calendar dataMaxAtendimento = Calendar.getInstance();
    dataMaxAtendimento.setTime(ultimoAtendimento.getDataHoraRecebimento());
    
    int i = 0;
    
    while (i < prazo) {
      dataMaxAtendimento.add(Calendar.DAY_OF_MONTH, 1);
      int diaSemana = dataMaxAtendimento.get(Calendar.DAY_OF_WEEK);
      if ((diaSemana != Calendar.SATURDAY) && (diaSemana != Calendar.SUNDAY)
          && !listaFeriados.contains((Date) DateUtils.truncate(dataMaxAtendimento.getTime(), Calendar.DAY_OF_MONTH))) {
        i++;
      }
    }
    
    Calendar dataMaxAtendimentoMaisUm = Calendar.getInstance();
    dataMaxAtendimentoMaisUm.setTime(dataMaxAtendimento.getTime());
    dataMaxAtendimentoMaisUm.add(Calendar.DAY_OF_MONTH, 1);
    
    
    String cor = "";
    if (DateUtils.truncate(new Date(), Calendar.DATE).before(DateUtils.truncate(dataMaxAtendimento.getTime(), Calendar.DATE))) {
      cor = "green";
    } else if (DateUtils.truncate(new Date(), Calendar.DATE).after(DateUtils.truncate(dataMaxAtendimentoMaisUm.getTime(), Calendar.DATE))) {
      cor = "red";
    } else {
      cor = "#f90";
    }
    return cor;
  }

  private Integer calcularPrazoDemanda(Demanda demanda) {
    if(demanda.getCaixaPostalResponsavel().getId().equals(demanda.getCaixaPostalDemandante().getId())) {
      return 5;
    }
    if (TipoUnidadeEnum.ARROBA_EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade()) || 
        TipoUnidadeEnum.EXTERNA.equals(demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade())) {
      return fluxoDemandaService.obterPrazoDaCaixaPostalAtualEncaminhadaExterna(demanda.getId());  
    } else {
      return fluxoDemandaService.obterPrazoDaCaixaPostalAtual(demanda.getId(), demanda.getCaixaPostalResponsavel().getId()); 
    }
  }
  
  /**
   * Item 03 da OS024 do Siarg de envio de emails.
   * Monitora Prazo Questionamento ao Demandante.
   */
  public void enviarEmailMonitorarPrazoQuestionamentoDemandante() {
    String motivo = "Alerta";
    String assunto = parametroDAO.obterParametroByNome(ParametroConstantes.ASSUNTO_JOB_QUESTIONAMENTO).getValor();
    String conteudo = parametroDAO.obterParametroByNome(ParametroConstantes.TEMPLATE_JOB_QUESTIONAMENTO).getValor();
    String urlSistema = parametroDAO.obterParametroByNome(ParametroConstantes.URL_SISTEMA).getValor();

    ConfiguracaoEmailDTO configuracaoEmailDTO = emailService.getConfiguracaoEmail();
    Map<CaixaPostal, List<PrazoQuestionamentoDemandasDTO>> map = obterListaDemandasParaMonitorarPrazoQuestionamentoDemandante();
    
    String prazoLimiteAviso = parametroDAO.obterParametroByNome(ParametroConstantes.PRAZO_LIMITE_AVISO).getValor();
    String prazoLimiteCancelamento= parametroDAO.obterParametroByNome(ParametroConstantes.PRAZO_LIMITE_CANCELAMENTO).getValor();

    for (Map.Entry<CaixaPostal, List<PrazoQuestionamentoDemandasDTO>> el : map.entrySet()) {
      CaixaPostal cp = el.getKey();
      List<PrazoQuestionamentoDemandasDTO> lista = el.getValue();
      emailService.enviarEmailQuestionamentoDemandante(cp, configuracaoEmailDTO, lista, assunto, conteudo, motivo, prazoLimiteAviso, prazoLimiteCancelamento, urlSistema);
    }
  }
  
  private Map<CaixaPostal, List<PrazoQuestionamentoDemandasDTO>> obterListaDemandasParaMonitorarPrazoQuestionamentoDemandante() {
    HashMap<CaixaPostal, List<PrazoQuestionamentoDemandasDTO>> hashMap = new HashMap<>();
    List<PrazoQuestionamentoDemandasDTO> listaDemandasQuestionadas =  listarTodasDemandasQuestionadasDemandante();
    for (PrazoQuestionamentoDemandasDTO prazoQuestionamentoDemandasDTO : listaDemandasQuestionadas) {
      if (!hashMap.containsKey(prazoQuestionamentoDemandasDTO.getDemanda().getCaixaPostalResponsavel())) {
        hashMap.put(prazoQuestionamentoDemandasDTO.getDemanda().getCaixaPostalResponsavel(), new ArrayList<PrazoQuestionamentoDemandasDTO>());
      }      
      List<PrazoQuestionamentoDemandasDTO> listaTemp = hashMap.get(prazoQuestionamentoDemandasDTO.getDemanda().getCaixaPostalResponsavel());
      listaTemp.add(prazoQuestionamentoDemandasDTO);
    }
    
    return hashMap;
  }

  /**
   * Todas as demandas com o Demandante com prazo de 02 e 05 dias. (Parametros na base)
   * @return Lista de PrazoQuestionamentoDemandasDTO
   */
  private List<PrazoQuestionamentoDemandasDTO> listarTodasDemandasQuestionadasDemandante() {
    List<PrazoQuestionamentoDemandasDTO> lista = new ArrayList<>();
    //Retorna demandas que estão com (Responsavel = Demandante) e que tiveram atendimento questionamento
    List<Demanda> demandasList = demandaService.obterListaDemandasQuestionadasDemandante();
    
    Integer prazoLimiteCancelamento = Integer.parseInt(parametroDAO.obterParametroByNome(ParametroConstantes.PRAZO_LIMITE_CANCELAMENTO).getValor());
    Integer prazoLimiteAviso = Integer.parseInt(parametroDAO.obterParametroByNome(ParametroConstantes.PRAZO_LIMITE_AVISO).getValor());
    
    for (Demanda demanda : demandasList) {
      
      if(demanda.getAtendimentosList() != null && !demanda.getAtendimentosList().isEmpty()) {
        
        List<Atendimento> listaAtendimentosReverso = new ArrayList<>(demanda.getAtendimentosList());
        Collections.reverse(listaAtendimentosReverso);
        
        Iterator<Atendimento> it = listaAtendimentosReverso.iterator();
        Atendimento ultimoAtendimento = it.next();
        
        Atendimento penultimoAtendimento = null;
        if(it.hasNext()) {
          penultimoAtendimento = it.next();
        }
        
        //Caso a ação do penúltimo atendimento da demanda seja igual à "QUESTIONAR", 
        if(penultimoAtendimento != null && penultimoAtendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)) {
          executarAcoesDemandaPenultimoAtendimentoQuestionar(lista, prazoLimiteCancelamento, prazoLimiteAviso, demanda,
              ultimoAtendimento);
        }
      }
      
    }
    return lista;
  }

  private void executarAcoesDemandaPenultimoAtendimentoQuestionar(List<PrazoQuestionamentoDemandasDTO> lista,
      Integer prazoLimiteCancelamento, Integer prazoLimiteAviso, Demanda demanda, Atendimento ultimoAtendimento) {
    if(validarDemandaEntreDoisECincoDias(ultimoAtendimento, prazoLimiteAviso, prazoLimiteCancelamento)) {
      lista.add(montarPrazoQuestionamentoDemandasDTO(demanda, ultimoAtendimento));
    } else if (validarDemandaComMaisDeCincoDiasParaCancelar(ultimoAtendimento, prazoLimiteCancelamento)) {
      cancelarDemandasQuestionadasComMaisDeCincoDias(demanda);
    }
  }

  private void cancelarDemandasQuestionadasComMaisDeCincoDias(Demanda demanda) {
    try {
      demandaService.cancelar(demanda, null, new ArrayList<CaixaPostal>(), "SIARG", demanda.getCaixaPostalResponsavel(), 
          "Demanda cancelada. Prazo de resposta do demandante expirado",
          "SIARG");
    } catch (Exception e) {
      logger.info(e);
    }
  }

  /**
   * Montagem do DTO.
   * @param demanda demanda
   * @param ultimoAtendimento ultimoAtendimento
   * @return PrazoQuestionamentoDemandasDTO
   */
  private PrazoQuestionamentoDemandasDTO montarPrazoQuestionamentoDemandasDTO(Demanda demanda, Atendimento ultimoAtendimento) {
    PrazoQuestionamentoDemandasDTO item = new PrazoQuestionamentoDemandasDTO();
    List<Assunto> listaAssuntos = this.assuntoService.obterAssuntosFetchPai();
    item.setAssuntoCompleto(assuntoService.obterArvoreAssuntos(demanda.getAssunto(), listaAssuntos));
    item.setDataDemanda(demanda.getDataHoraAbertura());
    item.setDataUltimaAlteracao(ultimoAtendimento.getDataHoraRecebimento());
    item.setDemanda(demanda);
    item.setDiasSemInteracao(DateUtil.calculaDiferencaEntreDatasEmDias(new Date(), ultimoAtendimento.getDataHoraRecebimento()));
    item.setIdDemanda(demanda.getId());
    return item ;
  }

  private boolean validarDemandaComMaisDeCincoDiasParaCancelar(Atendimento ultimoAtendimento, Integer prazoLimiteCancelamento) {
    try {
      Date cinco = feriadoService.calcularDataVencimentoPrazo(ultimoAtendimento.getDataHoraRecebimento(), prazoLimiteCancelamento);
      if (DateUtils.truncate(new Date(), Calendar.DATE).after(DateUtils.truncate(cinco, Calendar.DATE))) {
        return Boolean.TRUE;
      }
    } catch (DataBaseException e) {
      logger.info(e);
    }
    return Boolean.FALSE;
  }

  private boolean validarDemandaEntreDoisECincoDias(Atendimento ultimoAtendimento, Integer prazoLimiteAviso, Integer prazoLimiteCancelamento) {
    try {
      Date dois = feriadoService.calcularDataVencimentoPrazo(ultimoAtendimento.getDataHoraRecebimento(), prazoLimiteAviso);
      Date cinco = feriadoService.calcularDataVencimentoPrazo(ultimoAtendimento.getDataHoraRecebimento(), prazoLimiteCancelamento);
      if (DateUtils.truncate(new Date(), Calendar.DATE).after(DateUtils.truncate(dois, Calendar.DATE))  && 
          DateUtils.truncate(new Date(), Calendar.DATE).before(DateUtils.truncate(cinco, Calendar.DATE))) {
        return Boolean.TRUE;
      }
    } catch (DataBaseException e) {
      logger.info(e);
    }
    return Boolean.FALSE;
  }
}
