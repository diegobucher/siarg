/**
 * InclusaoDemandaController.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FluxoDemandaService;

/**
 * Classe Controller para tela de cadastro de usuario
 */
@Named
@ViewScoped
public class PesquisaRapidaDemandaController extends BaseController implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @EJB
  private DemandaService demandaService;
  
  @EJB
  private CaixaPostalService caixaPostalService;
  
  @EJB
  private FluxoDemandaService fluxoDemandaService;
  
  private Long numeroDemanda;
  
  private String mensagemValidacao;
  
  private boolean flagErroValidacao;
  
  private Abrangencia abrangencia;

  @PostConstruct
  public void init() {
	  
    abrangencia = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");

  }
  
  public void pesquisar() {
    
    if(numeroDemanda == null) {
      mensagemValidacao = "É necessário informar o número da demanda que deseja consultar";
      flagErroValidacao = true;
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
      numeroDemanda = null;
      return;
    }
    
    Demanda demanda = demandaService.findByIdAndAbrangenciaFetch(numeroDemanda, abrangencia.getId());
    
    if(demanda == null) {
      mensagemValidacao = "Não foi encontrada a demanda de n. "+numeroDemanda +". ";
      flagErroValidacao = true;
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
      numeroDemanda = null;
      return;
    } else {
      
      List<CaixaPostal> caixasAutorizadasList = new ArrayList<CaixaPostal>();
      caixasAutorizadasList.addAll(caixaPostalService.findObservadoresByAssunto(demanda.getAssunto().getId()));
      caixasAutorizadasList.addAll(caixaPostalService.findObservadoresByDemanda(demanda.getId()));
      caixasAutorizadasList.add(demanda.getCaixaPostalDemandante());
      
      List<FluxoDemanda> fluxosList = fluxoDemandaService.findByIdDemanda(demanda.getId());
      for (FluxoDemanda fluxoDemanda : fluxosList) {
        if(!fluxoDemanda.getCaixaPostal().getSigla().equals("@EXTERNA")) {
          caixasAutorizadasList.add(fluxoDemanda.getCaixaPostal());
        }
      }
      
      caixasAutorizadasList = new ArrayList<CaixaPostal>(new HashSet(caixasAutorizadasList));
      Collections.sort(caixasAutorizadasList);
      
      CaixaPostal caixaPostalSelecionada = (CaixaPostal) JSFUtil.getSessionMapValue("caixaPostal");

      if(!caixasAutorizadasList.contains(caixaPostalSelecionada)) {
        StringBuilder sb = new StringBuilder();
        int cont = 1;
        for (CaixaPostal caixa : caixasAutorizadasList) {
          sb.append(caixa.getSigla());
          if(cont < caixasAutorizadasList.size()) {
            sb.append(",");
          }
          if(cont % 10 == 0) {
            sb.append("<br>");
          }
          cont++;
        }
        mensagemValidacao ="<b>ATENÇÃO!</b> Você está tentando acessar uma demanda com a CAIXA POSTAL <b>indevida</b>. Favor consultar outra demanda ou alterar a CAIXA POSTAL. Esta demanda pode ser acessada pelas seguintes CP: "+sb.toString()+".";
        flagErroValidacao = true;
        RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
        numeroDemanda = null;
      } 
    }
    
  }
  
  public String redirectTratarDemanda() {
    JSFUtil.setSessionMapValue(TratarDemandaController.PARAM_ID_DEMANDA, numeroDemanda);
    return "/paginas/demanda/tratar/tratar.xhtml?faces-redirect=true";
  }

  @Override
  public Logger getLogger() {
    return null;
  }

  public Long getNumeroDemanda() {
    return numeroDemanda;
  }

  public void setNumeroDemanda(Long numeroDemanda) {
    this.numeroDemanda = numeroDemanda;
  }

  public String getMensagemValidacao() {
    return mensagemValidacao;
  }

  public void setMensagemValidacao(String mensagemValidacao) {
    this.mensagemValidacao = mensagemValidacao;
  }

  public boolean isFlagErroValidacao() {
    return flagErroValidacao;
  }

  public void setFlagErroValidacao(boolean flagErroValidacao) {
    this.flagErroValidacao = flagErroValidacao;
  }

}
