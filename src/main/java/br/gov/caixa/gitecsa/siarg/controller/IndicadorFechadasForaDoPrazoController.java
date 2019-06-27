package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.siarg.dto.IndicadorFechadasForaDoPrazoDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;

@Named
@ViewScoped
public class IndicadorFechadasForaDoPrazoController extends BaseController implements Serializable {

	private static final long serialVersionUID = 2643963801632335988L;
	
	@Inject
	private DemandaService demandaService;
	@Inject
	private FeriadoService feriadoService;

	private String dtInicial;
	private String dtFinal;
	private Date dataIniPerInicial;
	private Date dataFimPerFinal;
	private List<String> listaUnidadesStringCombo;
	private Abrangencia abrangenciaSelecionada;
	private Boolean flagPesquisado;
	private List<UnidadeDTO> listaUnidadesDTOCombo;
	private List<IndicadorFechadasForaDoPrazoDTO> indicadorFechadasForaDoPrazoList;
	private String unidadeSelecionadaString;
	private UnidadeDTO unidadeSelecionadaDTO;
	
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		this.listaUnidadesStringCombo = new ArrayList<>();
		flagPesquisado = false;
		this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
		List<UnidadeDTO> listaUnidadesSessaoDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");

		this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia(listaUnidadesSessaoDTO);

		if (this.listaUnidadesDTOCombo.size() > 1) {
			listaUnidadesStringCombo.add("TODAS");
		}

		for (UnidadeDTO unidade : listaUnidadesDTOCombo) {
			this.listaUnidadesStringCombo.add(unidade.getSigla());
		}

		inicializarDatas();
	}
	
	public void pesquisar() {
		if (validarCampos()) {
			try {

				flagPesquisado = true;

				dataIniPerInicial = DateUtils.parseDateStrictly(this.dtInicial, DateUtil.FORMATO_DATA);
				dataFimPerFinal = DateUtils.parseDateStrictly(this.dtFinal, DateUtil.FORMATO_DATA);

				Long idUnidade = null;
				for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
					if (this.unidadeSelecionadaString.equals(unidadeDTO.getSigla())) {
						this.unidadeSelecionadaDTO = unidadeDTO;
						idUnidade = unidadeSelecionadaDTO.getId();
						break;
					} else {
						this.unidadeSelecionadaDTO = null;
						idUnidade = null;
					}
				}
				
				List<Date> datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();
				List<Demanda> demandasFechadas = this.demandaService.obterDemandasFechadasTotal(idUnidade,abrangenciaSelecionada.getId(), dataIniPerInicial,dataFimPerFinal);
				HashMap<String, IndicadorFechadasForaDoPrazoDTO> map = new HashMap<>();
				indicadorFechadasForaDoPrazoList = new ArrayList<>();
				
				for (Demanda demanda : demandasFechadas) {
					String caixaPostal = demanda.getFluxosDemandasList().get(0).getCaixaPostal().getSigla();
					Atendimento atendimento = new Atendimento();
					atendimento = demandaService.consultaAtendimento(demanda.getId());

					if (!map.containsKey(caixaPostal)) {
						IndicadorFechadasForaDoPrazoDTO dto = new IndicadorFechadasForaDoPrazoDTO();
						dto.setCaixaPostal(caixaPostal);
						map.put(caixaPostal, dto);
					}
					IndicadorFechadasForaDoPrazoDTO dto = map.get(caixaPostal);
					dto.setQtFechadaPrazo(dto.getQtFechadaPrazo() + 1);
					
					if (!demandaService.isDemandaFechadaNoPrazoSemConsulta(atendimento, datasFeriadosList)) {
						dto.setQtFechadaForaPrazo(dto.getQtFechadaForaPrazo() + 1);
					}
				}
				
				for (Map.Entry<String, IndicadorFechadasForaDoPrazoDTO> hashDemandasPorUnidade : map.entrySet()) {
					IndicadorFechadasForaDoPrazoDTO dto = hashDemandasPorUnidade.getValue();
					String str = Util.converterDecimalFormatado(dto.getQtFechadaForaPrazo().doubleValue(),dto.getQtFechadaPrazo().doubleValue());
					dto.setIndicador(str);
					indicadorFechadasForaDoPrazoList.add(dto);
				}
	
			} catch (ParseException e) {
				this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
				getLogger().info(e);
			} 
		}

	}


	private List<UnidadeDTO> obterListaUnidadePorAbrangencia(List<UnidadeDTO> listaUnidadesSessaoDTO) {
		List<UnidadeDTO> temp = new ArrayList<>();
		for (UnidadeDTO dto : listaUnidadesSessaoDTO) {
			if (dto.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
				temp.add(dto);
			}
		}
		return temp;
	}

	private void inicializarDatas() {

		Date hoje = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMATO_DATA);
		this.dtFinal = sdf.format(hoje);
		this.dtInicial = sdf.format(DateUtils.addDays(hoje, -15));
	}
	
	private boolean validarCampos() {
	    
	    if(StringUtils.isBlank(this.dtInicial)
	        || StringUtils.isBlank(this.dtFinal)
	        ) {
	      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
	      return Boolean.FALSE;
	    }
	    
	    
	    try {
	      Date dtIniPerInicial = DateUtils.parseDateStrictly(this.dtInicial,DateUtil.FORMATO_DATA);
	      Date dtFimPerFinal = DateUtils.parseDateStrictly(this.dtFinal,DateUtil.FORMATO_DATA);

	      /*
	      if(this.unidadeSelecionadaString.equals("TODAS")) {
	        
	        if(DateUtil.calculaDiferencaEntreDatasEmDias(dtFimPerFinal, dtIniPerInicial) > 30) {
	          this.facesMessager.addMessageError(MensagemUtil.obterMensagem("relatorio.demanda.por.situacao"));
	          return Boolean.FALSE;
	        }
	        
	      }*/
	      
	      
	      if (dtFimPerFinal.before(dtIniPerInicial)) {
	        this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
	        return Boolean.FALSE;
	      } 
	    } catch (ParseException e) {
	      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
	      return Boolean.FALSE;
	    }
	    
	    return Boolean.TRUE;
	  }

	public String getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(String dtInicial) {
		this.dtInicial = dtInicial;
	}

	public String getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(String dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Date getDataIniPerInicial() {
		return dataIniPerInicial;
	}

	public void setDataIniPerInicial(Date dataIniPerInicial) {
		this.dataIniPerInicial = dataIniPerInicial;
	}

	public Date getDataFimPerFinal() {
		return dataFimPerFinal;
	}

	public void setDataFimPerFinal(Date dataFimPerFinal) {
		this.dataFimPerFinal = dataFimPerFinal;
	}

	public List<String> getListaUnidadesStringCombo() {
		return listaUnidadesStringCombo;
	}

	public void setListaUnidadesStringCombo(List<String> listaUnidadesStringCombo) {
		this.listaUnidadesStringCombo = listaUnidadesStringCombo;
	}

	public Abrangencia getAbrangenciaSelecionada() {
		return abrangenciaSelecionada;
	}

	public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
		this.abrangenciaSelecionada = abrangenciaSelecionada;
	}

	public Boolean getFlagPesquisado() {
		return flagPesquisado;
	}

	public void setFlagPesquisado(Boolean flagPesquisado) {
		this.flagPesquisado = flagPesquisado;
	}

	public List<UnidadeDTO> getListaUnidadesDTOCombo() {
		return listaUnidadesDTOCombo;
	}

	public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
		this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
	}

	public List<IndicadorFechadasForaDoPrazoDTO> getIndicadorFechadasForaDoPrazoList() {
		return indicadorFechadasForaDoPrazoList;
	}

	public void setIndicadorFechadasForaDoPrazoList(
			List<IndicadorFechadasForaDoPrazoDTO> indicadorFechadasForaDoPrazoList) {
		this.indicadorFechadasForaDoPrazoList = indicadorFechadasForaDoPrazoList;
	}

	public String getUnidadeSelecionadaString() {
		return unidadeSelecionadaString;
	}

	public void setUnidadeSelecionadaString(String unidadeSelecionadaString) {
		this.unidadeSelecionadaString = unidadeSelecionadaString;
	}

	public UnidadeDTO getUnidadeSelecionadaDTO() {
		return unidadeSelecionadaDTO;
	}

	public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
		this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
	}

}
