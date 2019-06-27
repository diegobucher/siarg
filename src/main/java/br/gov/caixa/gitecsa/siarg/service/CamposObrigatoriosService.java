package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.CamposObrigatoriosDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Classe de serviÃ§os de Campos ObrigatÃ³rios.
 * 
 * @author f763644
 */
@Stateless
public class CamposObrigatoriosService extends AbstractService<CamposObrigatorios> {

	private static final long serialVersionUID = 1L;

	@Inject
	private CamposObrigatoriosDAO camposObrigatoriosDAO;

	/** Construtor PadrÃ£o. */
	public CamposObrigatoriosService() {
		super();
	}
	
	public CamposObrigatorios obterCamposObrigatoriosPorId(Long id){
		return this.camposObrigatoriosDAO.findById(id);
	}
	
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorNome(String nome) {
		return this.camposObrigatoriosDAO.obterCamposObrigatoriosPorNome(nome);
	}
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorDescricao(String descricao){
		return this.camposObrigatoriosDAO.obterObrigatoriosPorDescricao(descricao);
	}
	
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorTipo(TipoCampoEnum tipo) {
		return this.camposObrigatoriosDAO.obterCamposObrigatoriosPorTipo(tipo);
	}
	
	
	public List<CamposObrigatorios> obterCampoObrigatorioPorTamanho(Integer tamanho) {
		return this.camposObrigatoriosDAO.obterCamposObrigatoriosPorTamanho(tamanho);
	}
	
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorMascara(String mascara){
		return this.camposObrigatoriosDAO.obterCamposObrigatoriosPorMascara(mascara);
	}
	
	public List<CamposObrigatorios> retornarCheckboxControleFluxo(Boolean ativo){
	   return this.camposObrigatoriosDAO.carregarTodosCamposCheckbox(ativo);	
	}
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorAssunto(Long IdAssunto){
		return this.camposObrigatoriosDAO.obterCamposObrigatoriosPorAssunto(IdAssunto);	
	}
	
	public void excluirLogicamente(Long id) throws RequiredException, BusinessException, DataBaseException {
		    
		CamposObrigatorios campos = findById(id);
		campos.setAtivo(false);
		update(campos);
		
	  }
	
    public CamposObrigatorios updateCampos(CamposObrigatorios camposObrigatorios){
    	  CamposObrigatorios campo = this.camposObrigatoriosDAO.update(camposObrigatorios);
    	  
    	return this.camposObrigatoriosDAO.update(camposObrigatorios);
	 }
	
    @Override
    protected BaseDAO<CamposObrigatorios> getDAO() {
    	return this.camposObrigatoriosDAO;
    }
	
	@Override
	public List<CamposObrigatorios> consultar(CamposObrigatorios entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validaCamposObrigatorios(CamposObrigatorios entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegras(CamposObrigatorios entity) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaRegrasExcluir(CamposObrigatorios entity) {
		// TODO Auto-generated method stub

	}



}
