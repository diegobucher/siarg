package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.DemandaCampoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaCampo;

@Stateless
public class DemandaCampoService extends AbstractService<DemandaCampo> {

	@Inject
	private DemandaCampoDAO demandaCampoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6761858771776787895L;

	@Override
	public List<DemandaCampo> consultar(DemandaCampo entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validaCamposObrigatorios(DemandaCampo entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegras(DemandaCampo entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegrasExcluir(DemandaCampo entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected BaseDAO<DemandaCampo> getDAO() {
		return this.demandaCampoDAO;
	}

	public DemandaCampo obterDemandaCampos(Demanda demanda, CamposObrigatorios camposObrigatorios) {
		DemandaCampo demandaCampo = new DemandaCampo();
		demandaCampo.setDemanda(demanda);
		demandaCampo.setCamposObrigatorios(camposObrigatorios);
		demandaCampo.setDescDemandaCampoFormatada(camposObrigatorios.getDemandaCampo());
		demandaCampo.setDemandaCampo(removerMascaraCampoObrigatorio(camposObrigatorios));
		return demandaCampo;
	}
	
	public String removerMascaraCampoObrigatorio(CamposObrigatorios camposObrigatorios) {
	  if (camposObrigatorios != null && StringUtils.isNotBlank(camposObrigatorios.getDemandaCampo())) {
	    
	    if (TipoCampoEnum.TEXTO.equals(camposObrigatorios.getTipo()) || StringUtils.isBlank(camposObrigatorios.getMascara())) {
        return camposObrigatorios.getDemandaCampo();
      }
	    
	    StringBuilder textoSemFormatacao = new StringBuilder();
	    char[] mascara = camposObrigatorios.getMascara().toCharArray();
	    char[] valorCampo = camposObrigatorios.getDemandaCampo().toCharArray();
	    
	    for (int i = 0; i < mascara.length; i++) {
        char maskItem = mascara[i];
        if (maskItem == 'x' || maskItem == 'X') {
          textoSemFormatacao.append(valorCampo[i]);
        }        
      }
	    
	    return textoSemFormatacao.toString();
    }
	  return null;
	}
	
	public boolean verificarFormatacaoMascaraCampoObrigatorio(CamposObrigatorios camposObrigatorios) {
	  if (camposObrigatorios != null && StringUtils.isNotBlank(camposObrigatorios.getDemandaCampo()) && 
	      StringUtils.isNotBlank(camposObrigatorios.getMascara()) && !TipoCampoEnum.TEXTO.equals(camposObrigatorios.getTipo())) {
	    
	    if (camposObrigatorios.getMascara().length() != camposObrigatorios.getDemandaCampo().length()) {
        return false;
      }
	    
	    char[] mascara = camposObrigatorios.getMascara().toCharArray();
	    char[] valorCampo = camposObrigatorios.getDemandaCampo().toCharArray();
	    
	    for (int i = 0; i < mascara.length; i++) {
	      char maskItem = mascara[i];
	      if (maskItem != 'x' && maskItem != 'X' && valorCampo[i] != maskItem) {
	        return false;
	      }        
	    }
	    
	  }
	  return true;
	}

}
