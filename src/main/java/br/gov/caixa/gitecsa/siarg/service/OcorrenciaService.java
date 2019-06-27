package br.gov.caixa.gitecsa.siarg.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.OcorrenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

@Stateless
public class OcorrenciaService extends AbstractService<Ocorrencia> {

  /** Serial. */
  private static final long serialVersionUID = 3462009978376728223L;
  
  @Inject
  private OcorrenciaDAO ocorrenciaDAO;
  
  @Override
  public List<Ocorrencia> consultar(Ocorrencia entity) throws Exception {
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Ocorrencia entity) {
    
  }

  @Override
  protected void validaRegras(Ocorrencia entity) {
    
  }

  @Override
  protected void validaRegrasExcluir(Ocorrencia entity) {
    
  }

  @Override
  protected BaseDAO<Ocorrencia> getDAO() {
    return ocorrenciaDAO;
  }

  public List<Ocorrencia> obterListaOcorrenciasPorUnidade(UnidadeDTO unidadeSelecionadaDTO) {
    return ocorrenciaDAO.obterListaOcorrenciasPorUnidade(unidadeSelecionadaDTO);
  }

  public Ocorrencia salvar(final Ocorrencia ocorrencia) throws RequiredException, BusinessException, DataBaseException {
    if (ocorrencia.getId() == null) {
      return this.save(ocorrencia);
    } else {
      return this.update(ocorrencia);
    }
  }

  public Ocorrencia adicionar(Ocorrencia ocorrenciaSelecionada) throws RequiredException, BusinessException, DataBaseException {
    return salvar(ocorrenciaSelecionada);
  }

  public Ocorrencia alterar(Ocorrencia ocorrenciaSelecionada) throws RequiredException, BusinessException, DataBaseException {
    return salvar(ocorrenciaSelecionada);
  }
  
  public Ocorrencia excluir(Ocorrencia ocorrenciaSelecionada) throws RequiredException, BusinessException, DataBaseException {
    ocorrenciaSelecionada.setAtivo(Boolean.FALSE);
    return salvar(ocorrenciaSelecionada);
  }
  
  public List<Ocorrencia> obterListaOcorrenciasPor(Abrangencia abrangencia, Date dataPublicacao){
    return ocorrenciaDAO.obterListaOcorrenciasPor(abrangencia, dataPublicacao);
  }

}
