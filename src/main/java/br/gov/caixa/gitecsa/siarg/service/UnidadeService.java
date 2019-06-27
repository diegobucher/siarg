/**
 * UnidadeService.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dto.UnidadeCadastroDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Classe de serviços de Unidade.
 * @author f520296
 */
@Stateless
public class UnidadeService extends AbstractService<Unidade> {

  private static final long serialVersionUID = 1L;
  
  @Inject
  private UnidadeDAO unidadeDAO;
  
  @Inject
  private CaixaPostalDAO caixaPostalDAO;

  /** Construtor Padrão. */
  public UnidadeService() {
    super();
  }

  public Unidade obterUnidadePorChaveFetch(Long idUnidade) {
    return this.unidadeDAO.obterUnidadePorChaveFetch(idUnidade);
  }
  
  public List<Unidade> obterListaUnidadesPorAbrangencia(Long idAbrangencia) {
    return this.unidadeDAO.obterListaUnidadesPorAbrangencia(idAbrangencia);
  }
  
  public List<Unidade> obterListaUnidadesSUEG(Long idAbrangencia) {
    return this.unidadeDAO.obterListaUnidadesSUEG(idAbrangencia);
  }

  /**
   * Obtém as unidades ativas dos tipos especificados e suas respectivas caixas-postais (igualmente
   * ativas).
   * @param tiposUnidade Um ou mais tipos de unidade
   * @return Lista contendo as unidades que atendem aos critérios especificados.
   */
  public List<Unidade> obterUnidadesECaixasPostaisPorTipo(TipoUnidadeEnum... tiposUnidade) {
    return this.unidadeDAO.obterUnidadesECaixasPostais(tiposUnidade);
  }
  
  public List<Unidade> obterUnidadesECaixasPostaisPorTipo(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade) {
    return this.unidadeDAO.obterUnidadesECaixasPostais(abrangencia, tiposUnidade);
  }
  
  public List<UnidadeCadastroDTO> obterUnidadesDTOPor(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade) {
    List<Unidade> unidadeList = this.obterUnidadesECaixasPostaisPorTipo(abrangencia, tiposUnidade);
    List<UnidadeCadastroDTO> unidadeDTOList = new ArrayList<UnidadeCadastroDTO>();
    for (Unidade unidade : unidadeList) {
      UnidadeCadastroDTO unidadeDTO = new UnidadeCadastroDTO();
      unidadeDTO.setId(unidade.getId());
      unidadeDTO.setSigla(unidade.getSigla());
      unidadeDTO.setNome(unidade.getNome());
      unidadeDTO.setCgcUnidade(unidade.getCgcUnidade());
      unidadeDTO.setTipoUnidade(unidade.getTipoUnidade());
      unidadeDTOList.add(unidadeDTO);
    }
    return unidadeDTOList;
  }
  
  public Unidade update(Unidade unidade, List<CaixaPostal> caixaPostais){
    Unidade unidadeDB = this.unidadeDAO.update(unidade);
    
    
    
    
    
    return this.unidadeDAO.update(unidade);
  }
  
  public List<Unidade> obterUnidadesDemandateByAssunto(Assunto assunto) {
    return this.unidadeDAO.obterUnidadesDemandateByAssunto(assunto);
  }
  
  public Unidade obterUnidadeUsuarioLogado(Integer coUnidade) {
    return unidadeDAO.obterUnidadeUsuarioLogado(coUnidade);
  }

  public Unidade findBySigla(String sigla) {
    return unidadeDAO.findBySigla(sigla);
  }
  
  public List<Unidade> obterListaUnidadeUsuarioLogado(Integer cgcUnidade){
    return unidadeDAO.obterListaUnidadeUsuarioLogado(cgcUnidade);
  }


  public List<Unidade> obterListaUnidadesRelatorioAnaliticoPorAssunto(Long idAbrangencia) {
    return this.unidadeDAO.obterListaUnidadesRelatorioAnaliticoPorAssunto(idAbrangencia);
  }
  
  public Unidade obterUnidadePorAbrangenciaCGC(Abrangencia abrangencia, Integer cgcUnidade) {
    return this.unidadeDAO.obterUnidadePorAbrangenciaCGC(abrangencia, cgcUnidade);
  }
  
  public Unidade obterUnidadePorAbrangenciaSigla(Abrangencia abrangencia, String sigla) {
    return this.unidadeDAO.obterUnidadePorAbrangenciaSigla(abrangencia, sigla);
  }
  
  public void excluirLogicamente(Long idUnidade) throws RequiredException, BusinessException, DataBaseException {
    
    Unidade unidadeDB = findById(idUnidade);
    
    unidadeDB.setAtivo(false);
    
    for (CaixaPostal caixaPostal : unidadeDB.getCaixasPostaisList()) {
      caixaPostal.setAtivo(false);
    }
    
    update(unidadeDB);
    
    caixaPostalDAO.updateList(unidadeDB.getCaixasPostaisList());
  }

  @Override
  public List<Unidade> consultar(Unidade entity) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Unidade entity) {
    // TODO Auto-generated method stub
  }

  @Override
  protected void validaRegras(Unidade entity) {
    // TODO Auto-generated method stub
  }

  @Override
  public void validaRegrasExcluir(Unidade entity) {
    // TODO Auto-generated method stub
  }

  @Override
  protected BaseDAO<Unidade> getDAO() {
    return this.unidadeDAO;
  }


}
