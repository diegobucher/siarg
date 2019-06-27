/**
 * AssuntoService.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Hibernate;
import org.jsoup.Jsoup;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoCampoDAO;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.AuditoriaDAO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelacaoAssuntosDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAuditoriaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.AssuntoCampo;
import br.gov.caixa.gitecsa.siarg.model.Auditoria;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.comparator.AssuntoNomeComparator;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

/**
 * Classe de serviços de assuntos.
 * @author f757783
 */
@Stateless
public class AssuntoService extends AbstractService<Assunto> {

  private static final String FLUXO_ASSUNTO_AUDITORIA = "FluxoAssunto";

  private static final String DEMANDANTE_AUDITORIA = "Demandante";

  private static final String UNIDADE_AUDITORIA = " Unidade: ";

  private static final String TIPO_FLUXO_AUDITORIA = " TipoFluxo: ";

  private static final String PRAZO_AUDITORIA = " Prazo: ";

  private static final String ORDEM_AUDITORIA = " Ordem: ";

  private static final String NU_UNIDADE_AUDITORIA = " Nu_unidade: ";

  private static final String NU_CAIXA_POSTAL_AUDITORIA = " Nu_caixa_postal: ";

  private static final String NU_ASSUNTO_AUDITORIA = " Nu_assunto: ";

  private static final String IC_EXCLUSAO_AUDITORIA = " IcExclusao: ";

  private static final String CAIXA_POSTAL_AUDITORIA = " CaixaPostal: ";

  private static final String ASSUNTO_AUDITORIA = " Assunto: ";

  private static final String ASSUNTO = "Assunto";

  private static final String OBSERVADOR_ASSUNTO = "ObservadorAssunto";
  
  /** Tornar o atributo retornável */
  public static final String CAMPOS_OBRIGATORIOS = "camposObrigatorios";

  /** Serial */
  private static final long serialVersionUID = 1L;

  @Inject
  private AssuntoDAO dao;

  @Inject
  private AuditoriaDAO auditoriaDAO;
  
  
  @Inject
  private AssuntoCampoDAO assuntoCampoDAO;
  

  public static final String SEPARADOR_CATEGORIA_ASSUNTO = " > ";

  /**
   * Método não implementado.
   * @param entity Assunto
   */
  @Override
  public List<Assunto> consultar(Assunto entity) throws Exception {
    return new ArrayList<>();
  }

  /**
   * Método não implementado.
   * @param entity Assunto
   */
  @Override
  protected void validaCamposObrigatorios(Assunto entity) {
    // Método herdado não utilizado
  }

  /**
   * Método não implementado.
   * @param entity Assunto
   */
  @Override
  protected void validaRegras(Assunto entity) {
    // Método herdado não utilizado
  }

  /**
   * Método não implementado.
   * @param entity Assunto
   */
  @Override
  protected void validaRegrasExcluir(Assunto entity) {
    // Método herdado não utilizado
  }

  /**
   * Obtém a instância do DAO de Assuntos.
   * @return A instância de AssuntoDAOImpl.
   */
  @Override
  protected BaseDAO<Assunto> getDAO() {
    return this.dao;
  }

  /**
   * Retorna todos os assuntos organizados hierarquicamente.
   * @param abrangencia
   * @return Lista de assuntos.
   */
  public List<Assunto> getRelacionamentoAssuntos(Abrangencia abrangencia) {

    List<Assunto> arvore = new ArrayList<>();
    List<Assunto> assuntos = this.dao.findAtivos(abrangencia);

    AssuntoNomeComparator assuntoNomeComparator = new AssuntoNomeComparator();

    // Limpar a referencia do entity manager
    for (Assunto assunto : assuntos) {
      assunto.setAssuntosList(new ArrayList<Assunto>());
    }

    for (Assunto assunto : assuntos) {
      if (assunto.getAssuntoPai() == null) {
        arvore.add(assunto);
      } else if (assuntos.contains(assunto.getAssuntoPai())) {
        Assunto parent = assuntos.get(assuntos.indexOf(assunto.getAssuntoPai()));
        parent.getAssuntosList().add(assunto);
        // Reordenando após cada inserção de assunto
        Collections.sort(parent.getAssuntosList(), assuntoNomeComparator);

      }
    }

    // Ordenando as categorias mais altas
    Collections.sort(arvore, assuntoNomeComparator);

    return arvore;
  }

  public List<Assunto> findAllByAbrangencia(Abrangencia abrangencia) {
    return this.dao.findAllBy(abrangencia);
  }

  /**
   * Retorna todos os assuntos organizados hierarquicamente.
   * @param abrangencia
   * @return Lista de assuntos.
   */
  public List<Assunto> getRelacionamentoAssuntosIncluindoInativos(Abrangencia abrangencia) {

    List<Assunto> arvore = new ArrayList<>();
    List<Assunto> assuntos = this.dao.findAllBy(abrangencia);

    for (Assunto assunto : assuntos) {
      assunto.setAssuntosList(new ArrayList<Assunto>());
    }

    for (Assunto assunto : assuntos) {
      if (assunto.getAssuntoPai() == null) {
        arvore.add(assunto);
      } else if (assuntos.contains(assunto.getAssuntoPai())) {
        Assunto parent = assuntos.get(assuntos.indexOf(assunto.getAssuntoPai()));
        parent.getAssuntosList().add(assunto);
      }
    }

    return arvore;
  }

  /**
   * Método: Obter Arvore de Assuntos.
   * @param assunto assunto
   * @return string
   */
  public String obterArvoreAssuntos(final Assunto assunto, final List<Assunto> listaAssuntos) {
    String nomeCompleto = assunto.getNome();
    Assunto assuntoTemp = assunto.getAssuntoPai();
    while ((assuntoTemp != null) && (assuntoTemp.getId() != null)) {
      nomeCompleto = assuntoTemp.getNome() + SEPARADOR_CATEGORIA_ASSUNTO + nomeCompleto;
      for (Assunto assuntoDaLista : listaAssuntos) {
        if (assuntoTemp.getId().equals(assuntoDaLista.getId())) {
          assuntoTemp = assuntoDaLista.getAssuntoPai();
          break;
        }
      }
    }
    return nomeCompleto;

  }

  /**
   * Método: Obter Arvore de Assuntos.
   * @param assunto assunto
   * @return string
   */
  public String obterArvoreAssuntos(final Assunto assunto) {
    String nomeCompleto = assunto.getNome();
    Assunto assuntoTemp = assunto;
    while (assuntoTemp.getAssuntoPai() != null) {
      assuntoTemp = this.dao.findById(assuntoTemp.getAssuntoPai().getId());
      nomeCompleto = assuntoTemp.getNome() + SEPARADOR_CATEGORIA_ASSUNTO + nomeCompleto;
    }
    return nomeCompleto;
  }

  /**
   * Método: Obter Arvore de Assuntos.
   * @param assunto assunto
   * @return string
   */
  public String obterArvoreAssuntos(final Assunto assunto, String separador) {
    String nomeCompleto = assunto.getNome();
    Assunto assuntoTemp = assunto;
    while (assuntoTemp.getAssuntoPai() != null) {
      assuntoTemp = this.dao.findById(assuntoTemp.getAssuntoPai().getId());
      nomeCompleto = assuntoTemp.getNome() + " " + separador + " " + nomeCompleto;
    }
    return nomeCompleto;
  }

  /**
   * Determina qual o tipo de fluxo do assunto com base no demandante e no responsável.
   * @param assunto Assunto
   * @param caixaPostal Caixa-postal
   * @return TipoFluxoEnum Tipo de fluxo definido ao assunto e caixa-postal informados.
   * @throws DataBaseException
   */
  public TipoFluxoEnum determinarTipoFluxoAssunto(final Assunto assunto, final CaixaPostal caixaPostal) throws DataBaseException {
    if (!ObjectUtils.isNullOrEmpty(assunto) && !ObjectUtils.isNullOrEmpty(caixaPostal)
        && assunto.getCaixaPostal().equals(caixaPostal)) {
      return TipoFluxoEnum.DEMANDANTE_RESPONSAVEL;
    }

    if (this.dao.isDemandanteAssunto(assunto, caixaPostal.getUnidade())) {
      return TipoFluxoEnum.DEMANDANTE_DEFINIDO;
    }

    return TipoFluxoEnum.OUTROS_DEMANDANTES;
  }

  /**
   * Obtém o assunto a partir do id dado.
   * @param id Identificador chave
   * @return Assunto ou null caso nenhum registro seja encontrado.
   * @throws DataBaseException
   */
  public Assunto findByIdEager(final Long id) throws DataBaseException {
    return this.dao.findByIdEager(id);
  }

  /**
   * Obtém os fluxos de determinado tipo para o assunto dado.
   * @param assunto Assunto
   * @param tipo Tipo do fluxo
   * @return Fluxos de assuntos
   */
  public List<FluxoAssunto> findFluxoAssunto(final Assunto assunto, final TipoFluxoEnum tipo) {
    return this.dao.findFluxoAssuntoByAssuntoTipo(assunto, tipo);
  }

  /**
   * Consulta as caixas-postais cadastradas como observadores do assunto.
   * @param assunto Assunto
   * @return Lista de caixas-postais pré-definidas como observadores.
   */
  public List<CaixaPostal> findObservadores(final Assunto assunto) {
    return this.dao.findObservadoresByAssunto(assunto);
  }

  /**
   * Método: Obter Assuntos trazendo os pais.
   * @return
   */
  public List<Assunto> obterAssuntosFetchPai() {
    return this.dao.obterAssuntosFetchPai();
  }

  /**
   * Método: Obter a pesquisar de relacao entre assuntos.
   * @param abrangencia abrangencia
   * @param listaUnidades listaUnidades
   * @return List
   */
  public List<Assunto> pesquisarRelacaoAssuntos(Abrangencia abrangencia, UnidadeDTO unidade, List<UnidadeDTO> unidadeList) {
    return this.dao.pesquisarRelacaoAssuntos(abrangencia, unidade, unidadeList);
  }

  public List<Assunto> pesquisarAssuntosPorAbrangenciaEUnidade(Abrangencia abrangencia, UnidadeDTO unidade) {
    return this.dao.pesquisarAssuntosPorAbrangenciaEUnidade(abrangencia, unidade);
  }
  
  public List<Assunto> pesquisarAssuntosPorAbrangencia(Abrangencia abrangencia){
    return this.dao.pesquisarAssuntosPorAbrangencia(abrangencia);
  }
  
  public List<Assunto> preencherArvoreAssunto(List<Assunto> assuntoAPreencherList, List<Assunto> assuntoCompletoPreenchido){
    
    for (Assunto assunto : assuntoAPreencherList) {
      assunto.setArvoreCompleta(this.obterArvoreAssuntos(assunto, assuntoCompletoPreenchido));
    }
    
    return assuntoAPreencherList;
  }


  /**
   * Método para obter os nomes dos observadores autorizados .
   * @param assunto assunto
   * @return string
   */
  public String obterNomesObservadoresAutorizados(Assunto assunto) {
    String retorno = "";
    int count = 0;
    assunto = this.dao.findById(assunto.getId());
    Hibernate.initialize(assunto.getCaixasPostaisObservadorList());
    List<CaixaPostal> tempList = assunto.getCaixasPostaisObservadorList();
    for (CaixaPostal temp : tempList) {
      count++;
      retorno += temp.getSigla();
      if (count < tempList.size()) {
        retorno += "; ";
      }
    }
    return retorno;
  }

  /**
   * Método para obter os nomes dos observadores autorizados .
   * @param assunto assunto
   * @return string
   */
  private String obterObservadoresAssunto(Assunto assunto) {
    String retorno = "";
    int count = 0;
    assunto = this.dao.findById(assunto.getId());
    Hibernate.initialize(assunto.getCaixasPostaisObservadorList());
    List<CaixaPostal> tempList = assunto.getCaixasPostaisObservadorList();
    for (CaixaPostal temp : tempList) {
      count++;
      retorno += temp.getSigla();
      if (count < tempList.size()) {
        retorno += SEPARADOR_CATEGORIA_ASSUNTO;
      }
    }
    return retorno;
  }

  public List<Assunto> findCategoriasNaoExcluidos(Abrangencia abrangencia) {
    return dao.findCategoriasNaoExcluidos(abrangencia);
  }

  public List<Assunto> findAssuntosByPaiNaoExcluidos(Assunto assuntoPai) {
    return dao.findAssuntosByPaiNaoExcluidos(assuntoPai);
  }

  public void salvarCategoria(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada) {

    assunto.setContrato(false);
    assunto.setPermissaoAbertura(false);
    dao.save(assunto);
    Auditoria auditoria = gerarAuditoriaCategoria(assunto, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.I);
    auditoriaDAO.save(auditoria);

  }

  public void salvarAssunto(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada,
      List<Unidade> unidadeDemandanteSelecionadaList, List<CaixaPostal> caixaPostalObservadorSelecionadosList,
      List<FluxoAssunto> fluxoAssuntoPreDefinidoList, List<CamposObrigatorios> camposObrigatoriosList) {

    if (assunto.getId() == null) {
      dao.save(assunto);

      assunto.setAssuntoUnidadeDemandanteList(unidadeDemandanteSelecionadaList);
      assunto.setCaixasPostaisObservadorList(caixaPostalObservadorSelecionadosList);
      assunto.setFluxosAssuntosList(fluxoAssuntoPreDefinidoList);
      FluxoAssunto fluxoAssuntoDemais = new FluxoAssunto();
      fluxoAssuntoDemais.setAssunto(assunto);
      fluxoAssuntoDemais.setCaixaPostal(assunto.getCaixaPostal());
      fluxoAssuntoDemais.setOrdem(1);
      fluxoAssuntoDemais.setPrazo(assunto.getPrazo());
      fluxoAssuntoDemais.setTipoFluxo(TipoFluxoEnum.OUTROS_DEMANDANTES);
      assunto.getFluxosAssuntosList().add(fluxoAssuntoDemais);

      dao.update(assunto);

      List<Auditoria> auditoriaList = gerarAuditoriaNovoAsssunto(assunto, matriculaLogado, unidadeSelecionada);
      auditoriaDAO.saveList(auditoriaList);

    } else {

      assunto.setAssuntoUnidadeDemandanteList(unidadeDemandanteSelecionadaList);

      assunto.setCaixasPostaisObservadorList(caixaPostalObservadorSelecionadosList);

      FluxoAssunto fluxoAssuntoDemais = null;

      List<FluxoAssunto> listaFluxoAssuntoCadastrados = findFluxoAssunto(assunto, TipoFluxoEnum.OUTROS_DEMANDANTES);

      // Guarda o fluxo tipo 3 existente
      for (FluxoAssunto fluxoAssunto : listaFluxoAssuntoCadastrados) {
        if (fluxoAssunto.getTipoFluxo().equals(TipoFluxoEnum.OUTROS_DEMANDANTES)) {
          fluxoAssuntoDemais = fluxoAssunto;
          fluxoAssuntoDemais.setCaixaPostal(assunto.getCaixaPostal());
          fluxoAssuntoDemais.setPrazo(assunto.getPrazo());
          break;
        }
      }

      assunto.setFluxosAssuntosList(new ArrayList<FluxoAssunto>());
      assunto.getFluxosAssuntosList().add(fluxoAssuntoDemais);
      assunto.getFluxosAssuntosList().addAll(fluxoAssuntoPreDefinidoList);

      Assunto assuntoNaBase = null;

      try {
        assuntoNaBase = dao.findByIdEager(assunto.getId());
      } catch (DataBaseException e) {
      }

      List<Auditoria> auditoriaList = gerarAuditoriaEditarAsssunto(assunto, assuntoNaBase, matriculaLogado, unidadeSelecionada);
      auditoriaDAO.saveList(auditoriaList);
    }
    
    ArrayList<AssuntoCampo> relacaoAssuntoCampo = new ArrayList<AssuntoCampo>();
    
    if(camposObrigatoriosList != null ) {
    	
    	for (CamposObrigatorios entidade  : camposObrigatoriosList) {
    		
    		AssuntoCampo assuntoCampo = new AssuntoCampo();
    		assuntoCampo.setAssunto(assunto);
    		assuntoCampo.setCamposObrigatorios(entidade);
    		assuntoCampo.setQuantidade(entidade.getQuantidade());
    		relacaoAssuntoCampo.add(assuntoCampo);
    	}
    	assuntoCampoDAO.saveList(relacaoAssuntoCampo);
    }
    
    
     
    

    try {
      inativarAtivarCategoriasPaiRecursivamente(assunto, matriculaLogado, unidadeSelecionada);
    } catch (DataBaseException e) {
    }
    
  }

  public void excluirCategoria(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada) throws DataBaseException {
    excluirCategoriaRecursivamenteFilhos(assunto, matriculaLogado, unidadeSelecionada);
    inativarAtivarCategoriasPaiRecursivamente(assunto, matriculaLogado, unidadeSelecionada);
  }

  public void excluirAssunto(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada) throws DataBaseException {

    assunto.setExcluido(true);
    assunto.setAtivo(false);
    Auditoria auditoria = gerarAuditoriaCategoria(assunto, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.E);
    dao.update(assunto);
    if (auditoria != null) {
      auditoriaDAO.save(auditoria);
    }

    inativarAtivarCategoriasPaiRecursivamente(assunto, matriculaLogado, unidadeSelecionada);
  }

  private void inativarAtivarCategoriasPaiRecursivamente(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada)
      throws DataBaseException {

    // Pega o assunto atualizado com Fetch
    Assunto assuntoFetch = findByIdEager(assunto.getId());

    // Se tem pai faz as checagens
    Assunto assuntoPai = assuntoFetch.getAssuntoPai();

    if (assuntoPai != null) {

      // Busca os filhos do pai
      List<Assunto> assuntosFilhoList = findAssuntosByPai(assuntoPai);

      boolean todosInativos = true;
      for (Assunto filhoDoPai : assuntosFilhoList) {
        if (filhoDoPai.getAtivo()) {
          todosInativos = false;
          break;
        }
      }

      assuntoPai = SerializationUtils.clone(assuntoPai);

      // SE TODOS OS FILHOS DELE ESTIVEREM INATIVOS
      // ENTAO INATIVA O PAI
      if (todosInativos) {
        assuntoPai.setAtivo(false);
      } else {
        assuntoPai.setAtivo(true);
      }

      Auditoria auditoria = gerarAuditoriaCategoria(assuntoPai, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.A);
      dao.update(assuntoPai);
      if (auditoria != null) {
        auditoriaDAO.save(auditoria);
      }

      inativarAtivarCategoriasPaiRecursivamente(assuntoPai, matriculaLogado, unidadeSelecionada);
    }

  }

  /**
   * Exclui a categoria informada e seus filhos que não estejam excluidos.
   */
  private void excluirCategoriaRecursivamenteFilhos(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada) {

    assunto.setExcluido(true);
    assunto.setAtivo(false);
    Auditoria auditoria = gerarAuditoriaCategoria(assunto, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.E);
    dao.update(assunto);
    if (auditoria != null) {
      auditoriaDAO.save(auditoria);
    }

    List<Assunto> assuntosFilhoList = findAssuntosByPai(assunto);

    for (Assunto filho : assuntosFilhoList) {
      if (!filho.getExcluido()) {
        excluirCategoriaRecursivamenteFilhos(filho, matriculaLogado, unidadeSelecionada);
      }
    }

  }

  private Auditoria gerarAuditoriaCategoria(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada,
      AcaoAuditoriaEnum acao) {

    Auditoria auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(acao);
    auditoria.setNomeTabelaReferenciada(ASSUNTO);
    auditoria.setNumeroChaveTabela(assunto.getId());

    StringBuilder sb = new StringBuilder();
    if (acao.equals(AcaoAuditoriaEnum.I)) {
      sb.append(" Nome: " + assunto.getNome() + ";");
      sb.append(" Abrangencia: " + assunto.getAbrangencia().getNome() + ";");
      sb.append(" Ativo: " + assunto.getAtivo() + ";");
      sb.append(" IcAssunto: " + assunto.getFlagAssunto() + ";");
      sb.append(IC_EXCLUSAO_AUDITORIA + assunto.getExcluido() + ";");

    } else if (acao.equals(AcaoAuditoriaEnum.A) || acao.equals(AcaoAuditoriaEnum.E)) {

      boolean houveMudanca = false;

      Assunto assuntoNaBase = null;
      try {
        assuntoNaBase = findByIdEager(assunto.getId());
      } catch (DataBaseException e) {
        e.getMessage();
      }

      if (assuntoNaBase != null && !assuntoNaBase.getAtivo().equals(assunto.getAtivo())) {
        sb.append(" Ativo: " + assuntoNaBase.getAtivo() + Auditoria.SEPARADOR + assunto.getAtivo() + ";");
        houveMudanca = true;
      }

      if (assuntoNaBase != null && !assuntoNaBase.getExcluido().equals(assunto.getExcluido())) {
        sb.append(IC_EXCLUSAO_AUDITORIA + assuntoNaBase.getExcluido() + Auditoria.SEPARADOR + assunto.getExcluido() + ";");
        houveMudanca = true;
      }

      if (!houveMudanca) {
        return null;
      }
    }

    auditoria.setDescricao(sb.toString());

    return auditoria;
  }

  private List<Auditoria> gerarAuditoriaNovoAsssunto(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada) {

    List<Auditoria> auditoriaList = new ArrayList<>();

    // Tabela Auditoria
    Auditoria auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
    auditoria.setNomeTabelaReferenciada(ASSUNTO);
    auditoria.setNumeroChaveTabela(assunto.getId());

    StringBuilder sb = new StringBuilder();
    sb.append(" Abrangencia: " + assunto.getAbrangencia().getNome() + ";");
    sb.append(" Nome: " + assunto.getNome() + ";");

    sb.append(" Responsavel: " + assunto.getCaixaPostal().getSigla() + ";");
    sb.append(PRAZO_AUDITORIA + assunto.getPrazo() + ";");
    sb.append(" Status: " + assunto.getAtivo() + ";");
    sb.append(" Permissao: " + assunto.getPermissaoAbertura() + ";");

    if (!ObjectUtils.isNullOrEmpty(Jsoup.parse(assunto.getTextoPreDefinido()).text())) {
      sb.append(" Descricao: " + assunto.getTextoPreDefinido() + ";");
    }

    sb.append(" IcAssunto: " + assunto.getFlagAssunto() + ";");
    sb.append(IC_EXCLUSAO_AUDITORIA + assunto.getExcluido() + ";");
    auditoria.setDescricao(sb.toString());

    auditoriaList.add(auditoria);

    auditoria = new Auditoria();

    // Tabela Demandante
    if (assunto.getAssuntoUnidadeDemandanteList() != null && !assunto.getAssuntoUnidadeDemandanteList().isEmpty()) {

      for (Unidade unidade : assunto.getAssuntoUnidadeDemandanteList()) {
        trilharNovasUnidades(assunto, matriculaLogado, unidadeSelecionada, auditoriaList, unidade);
      }

    }

    // Tabela Observador
    if (assunto.getCaixasPostaisObservadorList() != null && !assunto.getCaixasPostaisObservadorList().isEmpty()) {

      for (CaixaPostal caixa : assunto.getCaixasPostaisObservadorList()) {
        auditoria = new Auditoria();
        auditoria.setDataAuditoria(new Date());
        auditoria.setMatricula(matriculaLogado);
        auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
        auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
        auditoria.setNomeTabelaReferenciada(OBSERVADOR_ASSUNTO);
        auditoria.setNumeroChaveTabela(caixa.getId());

        sb = new StringBuilder();
        sb.append(NU_ASSUNTO_AUDITORIA + assunto.getId() + ";");
        sb.append(NU_CAIXA_POSTAL_AUDITORIA + caixa.getId() + ";");
        sb.append(ASSUNTO_AUDITORIA + assunto.getNome() + ";");
        sb.append(CAIXA_POSTAL_AUDITORIA + caixa.getSigla() + ";");
        auditoria.setDescricao(sb.toString());
        auditoriaList.add(auditoria);
      }
    }

    // Tabela Fluxo
    if (assunto.getFluxosAssuntosList() != null && !assunto.getFluxosAssuntosList().isEmpty()) {

      for (FluxoAssunto fluxo : assunto.getFluxosAssuntosList()) {
        auditoria = new Auditoria();
        auditoria.setDataAuditoria(new Date());
        auditoria.setMatricula(matriculaLogado);
        auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
        auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
        auditoria.setNomeTabelaReferenciada(FLUXO_ASSUNTO_AUDITORIA);
        auditoria.setNumeroChaveTabela(fluxo.getId());

        sb = new StringBuilder();
        sb.append(ASSUNTO_AUDITORIA + fluxo.getAssunto().getNome() + ";");
        sb.append(CAIXA_POSTAL_AUDITORIA + fluxo.getCaixaPostal().getSigla() + ";");
        sb.append(PRAZO_AUDITORIA + fluxo.getPrazo() + ";");
        sb.append(TIPO_FLUXO_AUDITORIA + fluxo.getTipoFluxo().getValor() + ";");
        sb.append(ORDEM_AUDITORIA + fluxo.getOrdem() + ";");
        auditoria.setDescricao(sb.toString());
        auditoriaList.add(auditoria);

      }

    }

    return auditoriaList;
  }

  private List<Auditoria> gerarAuditoriaEditarAsssunto(Assunto assunto, Assunto assuntoNaBase, String matriculaLogado,
      Unidade unidadeSelecionada) {

    List<Auditoria> auditoriaList = new ArrayList<>();

    // Tabela Assunto
    trilharTabelaAssunto(assunto, assuntoNaBase, matriculaLogado, unidadeSelecionada, auditoriaList);

    // Tabela Demandante
    if (assunto.getAssuntoUnidadeDemandanteList() != null) {

      trilharTabelaDemandante(assunto, assuntoNaBase, matriculaLogado, unidadeSelecionada, auditoriaList);
    }

    // Tabela Observador
    if (assunto.getCaixasPostaisObservadorList() != null) {

      trilharTabelaObservador(assunto, assuntoNaBase, matriculaLogado, unidadeSelecionada, auditoriaList);
    }

    List<FluxoAssuntoAuditoria> fluxoAssuntoAuditoriaNovaList = new ArrayList<FluxoAssuntoAuditoria>();

    // Tabela Fluxo
    trilharTabelaFluxo(assunto, assuntoNaBase, matriculaLogado, unidadeSelecionada, auditoriaList, fluxoAssuntoAuditoriaNovaList);

    Assunto assuntoAtualizado = dao.update(assunto);
    List<FluxoAssunto> listaFluxos = assuntoAtualizado.getFluxosAssuntosList();

    for (FluxoAssuntoAuditoria fluxoAssuntoAuditoria : fluxoAssuntoAuditoriaNovaList) {

      for (FluxoAssunto fluxoAssunto : listaFluxos) {
        if (fluxoAssunto.getCaixaPostal().getId().equals(fluxoAssuntoAuditoria.fluxoAssunto.getCaixaPostal().getId())
            && fluxoAssunto.getTipoFluxo().equals(fluxoAssuntoAuditoria.fluxoAssunto.getTipoFluxo())) {
          fluxoAssuntoAuditoria.auditoria.setNumeroChaveTabela(fluxoAssunto.getId());
        }
      }

    }

    return auditoriaList;
  }

  private void trilharTabelaAssunto(Assunto assunto, Assunto assuntoNaBase, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList) {
    Auditoria auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.A);
    auditoria.setNomeTabelaReferenciada(ASSUNTO);
    auditoria.setNumeroChaveTabela(assunto.getId());

    boolean houveMudanca = false;

    StringBuilder sb = new StringBuilder();

    if (!assuntoNaBase.getAtivo().equals(assunto.getAtivo())) {
      sb.append(" Status: " + assuntoNaBase.getAtivo() + Auditoria.SEPARADOR + assunto.getAtivo() + ";");
      houveMudanca = true;
    }
    if (!assuntoNaBase.getCaixaPostal().equals(assunto.getCaixaPostal())) {
      sb.append(" Responsavel: " + assuntoNaBase.getCaixaPostal().getSigla() + Auditoria.SEPARADOR
          + assunto.getCaixaPostal().getSigla() + ";");
      houveMudanca = true;
    }
    if (!assuntoNaBase.getPrazo().equals(assunto.getPrazo())) {
      sb.append(PRAZO_AUDITORIA + assuntoNaBase.getPrazo() + Auditoria.SEPARADOR + assunto.getPrazo() + ";");
      houveMudanca = true;
    }
    if (!assuntoNaBase.getPermissaoAbertura().equals(assunto.getPermissaoAbertura())) {
      sb.append(
          " Permissao: " + assuntoNaBase.getPermissaoAbertura() + Auditoria.SEPARADOR + assunto.getPermissaoAbertura() + ";");
      houveMudanca = true;
    }

    if (!(ObjectUtils.isNullOrEmpty(Jsoup.parse(assuntoNaBase.getTextoPreDefinido()).text())
        && ObjectUtils.isNullOrEmpty(Jsoup.parse(assunto.getTextoPreDefinido()).text()))) {

      if (!assuntoNaBase.getTextoPreDefinido().equals(assunto.getTextoPreDefinido())) {
        sb.append(
            " Descricao: " + assuntoNaBase.getTextoPreDefinido() + Auditoria.SEPARADOR + assunto.getTextoPreDefinido() + ";");
        houveMudanca = true;
      }
    }

    auditoria.setDescricao(sb.toString());

    if (houveMudanca) {
      auditoriaList.add(auditoria);
    }
  }

  private void trilharTabelaDemandante(Assunto assunto, Assunto assuntoNaBase, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList) {
    List<Unidade> unidadesNaBase = assuntoNaBase.getAssuntoUnidadeDemandanteList();

    // PROCURA OS NOVOS
    for (Unidade unidade : assunto.getAssuntoUnidadeDemandanteList()) {
      if (!unidadesNaBase.contains(unidade)) {
        trilharNovasUnidades(assunto, matriculaLogado, unidadeSelecionada, auditoriaList, unidade);
      }
    }
    // VERIFICA OS QUE FORAM EXCLUIDOS
    for (Unidade unidade : unidadesNaBase) {
      if (!assunto.getAssuntoUnidadeDemandanteList().contains(unidade)) {
        trilharUnidadesExcluidas(assunto, matriculaLogado, unidadeSelecionada, auditoriaList, unidade);
      }
    }
  }

  private void trilharTabelaFluxo(Assunto assunto, Assunto assuntoNaBase, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList, List<FluxoAssuntoAuditoria> fluxoAssuntoAuditoriaNovaList) {
    Auditoria auditoria;
    StringBuilder sb;
    if (assunto.getFluxosAssuntosList() != null && !assunto.getFluxosAssuntosList().isEmpty()) {

      List<FluxoAssunto> fluxosNaBaseList = assuntoNaBase.getFluxosAssuntosList();

      for (FluxoAssunto fluxo : assunto.getFluxosAssuntosList()) {

        if (fluxo.getId() == null) {
          trilharNovoFluxoAssunto(matriculaLogado, unidadeSelecionada, auditoriaList, fluxoAssuntoAuditoriaNovaList, fluxo);
        } else {
          trilharMudancaFluxoAssunto(matriculaLogado, unidadeSelecionada, auditoriaList, fluxosNaBaseList, fluxo);
        }
      }
      for (FluxoAssunto fluxoBase : fluxosNaBaseList) {
        boolean deletou = true;
        verificarFluxoAssuntoDeletado(assunto, matriculaLogado, unidadeSelecionada, auditoriaList, fluxoBase, deletou);
      }

    }
  }

  private void verificarFluxoAssuntoDeletado(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList, FluxoAssunto fluxoBase, boolean deletou) {
    for (FluxoAssunto fluxo : assunto.getFluxosAssuntosList()) {
      if (fluxoBase.getId().equals(fluxo.getId())) {
        deletou = false;
        break;
      }
    }
    if (deletou) {
      trilharDeleteFluxoAssunto(matriculaLogado, unidadeSelecionada, auditoriaList, fluxoBase);
    }
  }

  private void trilharMudancaFluxoAssunto(String matriculaLogado, Unidade unidadeSelecionada, List<Auditoria> auditoriaList,
      List<FluxoAssunto> fluxosNaBaseList, FluxoAssunto fluxo) {
    Auditoria auditoria;
    StringBuilder sb;
    boolean mudou = false;

    FluxoAssunto fluxoNaBase = null;

    for (FluxoAssunto fluxoAssunto : fluxosNaBaseList) {
      if (fluxoAssunto.getId().equals(fluxo.getId())) {
        fluxoNaBase = fluxoAssunto;
        break;
      }
    }

    auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.A);
    auditoria.setNomeTabelaReferenciada(FLUXO_ASSUNTO_AUDITORIA);
    auditoria.setNumeroChaveTabela(fluxo.getId());
    sb = new StringBuilder();
    
    if(fluxoNaBase != null) {
	    if (!ObjectUtils.isNullOrEmpty(fluxoNaBase.getCaixaPostal()) && !fluxoNaBase.getCaixaPostal().equals(fluxo.getCaixaPostal())) {
	      sb.append(CAIXA_POSTAL_AUDITORIA + fluxoNaBase.getCaixaPostal().getSigla() + Auditoria.SEPARADOR
	          + fluxo.getCaixaPostal().getSigla() + ";");
	      mudou = true;
	    }
	    
	    if (!fluxoNaBase.getPrazo().equals(fluxo.getPrazo())) {
	        sb.append(PRAZO_AUDITORIA + fluxoNaBase.getPrazo() + Auditoria.SEPARADOR + fluxo.getPrazo() + ";");
	        mudou = true;
	    }

	    if (!fluxoNaBase.getOrdem().equals(fluxo.getOrdem())) {
	        sb.append(ORDEM_AUDITORIA + fluxoNaBase.getOrdem() + Auditoria.SEPARADOR + fluxo.getOrdem() + ";");
	        mudou = true;
	    }
    }
    
    auditoria.setDescricao(sb.toString());

    if (mudou) {
      auditoriaList.add(auditoria);
    }
  }

  private void trilharNovoFluxoAssunto(String matriculaLogado, Unidade unidadeSelecionada, List<Auditoria> auditoriaList,
      List<FluxoAssuntoAuditoria> fluxoAssuntoAuditoriaNovaList, FluxoAssunto fluxo) {
    Auditoria auditoria;
    StringBuilder sb;
    auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
    auditoria.setNomeTabelaReferenciada(FLUXO_ASSUNTO_AUDITORIA);

    sb = new StringBuilder();
    sb.append(ASSUNTO_AUDITORIA + fluxo.getAssunto().getNome() + ";");
    sb.append(CAIXA_POSTAL_AUDITORIA + fluxo.getCaixaPostal().getSigla() + ";");
    sb.append(PRAZO_AUDITORIA + fluxo.getPrazo() + ";");
    sb.append(TIPO_FLUXO_AUDITORIA + fluxo.getTipoFluxo().getValor() + ";");
    sb.append(ORDEM_AUDITORIA + fluxo.getOrdem() + ";");
    auditoria.setDescricao(sb.toString());

    auditoriaList.add(auditoria);
    fluxoAssuntoAuditoriaNovaList.add(new FluxoAssuntoAuditoria(fluxo, auditoria));
  }

  private void trilharDeleteFluxoAssunto(String matriculaLogado, Unidade unidadeSelecionada, List<Auditoria> auditoriaList,
      FluxoAssunto fluxoBase) {
    Auditoria auditoria;
    StringBuilder sb;
    auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.E);
    auditoria.setNomeTabelaReferenciada(FLUXO_ASSUNTO_AUDITORIA);
    auditoria.setNumeroChaveTabela(fluxoBase.getId());

    sb = new StringBuilder();
    sb.append(ASSUNTO_AUDITORIA + fluxoBase.getAssunto().getNome() + ";");
    sb.append(CAIXA_POSTAL_AUDITORIA + fluxoBase.getCaixaPostal().getSigla() + ";");
    sb.append(PRAZO_AUDITORIA + fluxoBase.getPrazo() + ";");
    sb.append(TIPO_FLUXO_AUDITORIA + fluxoBase.getTipoFluxo().getValor() + ";");
    sb.append(ORDEM_AUDITORIA + fluxoBase.getOrdem() + ";");
    auditoria.setDescricao(sb.toString());
    auditoriaList.add(auditoria);
  }

  private void trilharTabelaObservador(Assunto assunto, Assunto assuntoNaBase, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList) {
    Auditoria auditoria;
    StringBuilder sb;
    List<CaixaPostal> observadoresNaBase = assuntoNaBase.getCaixasPostaisObservadorList();

    for (CaixaPostal caixa : assunto.getCaixasPostaisObservadorList()) {
      if (!observadoresNaBase.contains(caixa)) {
        auditoria = new Auditoria();
        auditoria.setDataAuditoria(new Date());
        auditoria.setMatricula(matriculaLogado);
        auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
        auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
        auditoria.setNomeTabelaReferenciada(OBSERVADOR_ASSUNTO);
        auditoria.setNumeroChaveTabela(caixa.getId());

        sb = new StringBuilder();
        sb.append(NU_ASSUNTO_AUDITORIA + assunto.getId() + ";");
        sb.append(NU_CAIXA_POSTAL_AUDITORIA + caixa.getId() + ";");
        sb.append(ASSUNTO_AUDITORIA + assunto.getNome() + ";");
        sb.append(CAIXA_POSTAL_AUDITORIA + caixa.getSigla() + ";");
        auditoria.setDescricao(sb.toString());
        auditoriaList.add(auditoria);
      }
    }

    for (CaixaPostal caixa : observadoresNaBase) {
      if (!assunto.getCaixasPostaisObservadorList().contains(caixa)) {
        auditoria = new Auditoria();
        auditoria.setDataAuditoria(new Date());
        auditoria.setMatricula(matriculaLogado);
        auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
        auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.E);
        auditoria.setNomeTabelaReferenciada(OBSERVADOR_ASSUNTO);
        auditoria.setNumeroChaveTabela(caixa.getId());

        sb = new StringBuilder();
        sb.append(NU_ASSUNTO_AUDITORIA + assunto.getId() + ";");
        sb.append(NU_CAIXA_POSTAL_AUDITORIA + caixa.getId() + ";");
        sb.append(ASSUNTO_AUDITORIA + assunto.getNome() + ";");
        sb.append(CAIXA_POSTAL_AUDITORIA + caixa.getSigla() + ";");
        auditoria.setDescricao(sb.toString());
        auditoriaList.add(auditoria);
      }
    }
  }

  private void trilharNovasUnidades(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList, Unidade unidade) {
    Auditoria auditoria;
    StringBuilder sb;
    auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.I);
    auditoria.setNomeTabelaReferenciada(DEMANDANTE_AUDITORIA);
    auditoria.setNumeroChaveTabela(unidade.getId());

    sb = new StringBuilder();
    sb.append(NU_ASSUNTO_AUDITORIA + assunto.getId() + ";");
    sb.append(NU_UNIDADE_AUDITORIA + unidade.getId() + ";");

    sb.append(ASSUNTO_AUDITORIA + assunto.getNome() + ";");
    sb.append(UNIDADE_AUDITORIA + unidade.getSigla() + ";");
    auditoria.setDescricao(sb.toString());
    auditoriaList.add(auditoria);
  }

  private void trilharUnidadesExcluidas(Assunto assunto, String matriculaLogado, Unidade unidadeSelecionada,
      List<Auditoria> auditoriaList, Unidade unidade) {
    Auditoria auditoria;
    StringBuilder sb;
    auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(AcaoAuditoriaEnum.E);
    auditoria.setNomeTabelaReferenciada(DEMANDANTE_AUDITORIA);
    auditoria.setNumeroChaveTabela(unidade.getId());

    sb = new StringBuilder();
    sb.append(NU_ASSUNTO_AUDITORIA + assunto.getId() + ";");
    sb.append(NU_UNIDADE_AUDITORIA + unidade.getId() + ";");
    sb.append(ASSUNTO_AUDITORIA + assunto.getNome() + ";");
    sb.append(UNIDADE_AUDITORIA + unidade.getSigla() + ";");
    auditoria.setDescricao(sb.toString());
    auditoriaList.add(auditoria);
  }

  public List<Assunto> findAssuntosByPai(Assunto assuntoPai) {
    return dao.findAssuntosByPai(assuntoPai);
  }

  public List<ExportacaoAssuntoDTO> buscarAssuntosExportacao(List<Assunto> assuntoList,
      List<RelacaoAssuntosDTO> relacaoAssuntosDTOList) {

    Collections.sort(assuntoList);

    List<ExportacaoAssuntoDTO> assuntoExportacaoList = new ArrayList<>();

    for (Assunto assunto : assuntoList) {

      ExportacaoAssuntoDTO expDto = new ExportacaoAssuntoDTO();
      expDto.setNumeroAssunto(assunto.getId());
      expDto.setResponsavel(assunto.getCaixaPostal().getSigla());
      expDto.setPrazoResponsavel(assunto.getPrazo());

      String fluxoAssuntoStr = "";
      String prazoFluxoAssuntoStr = "";

      List<FluxoAssunto> fluxos = findFluxoAssunto(assunto, TipoFluxoEnum.DEMANDANTE_DEFINIDO);

      for (Iterator iterator = fluxos.iterator(); iterator.hasNext();) {
        FluxoAssunto fluxoAssunto = (FluxoAssunto) iterator.next();
        fluxoAssuntoStr += fluxoAssunto.getCaixaPostal().getSigla();

        prazoFluxoAssuntoStr += fluxoAssunto.getPrazo();

        if (iterator.hasNext()) {
          fluxoAssuntoStr += SEPARADOR_CATEGORIA_ASSUNTO;
          prazoFluxoAssuntoStr += SEPARADOR_CATEGORIA_ASSUNTO;
        }

      }

      expDto.setFluxoAssunto(fluxoAssuntoStr);
      expDto.setPrazoFluxoAssunto(prazoFluxoAssuntoStr);

      expDto.setObservadoresAssunto(obterObservadoresAssunto(assunto));
      expDto.setAtivo(assunto.getAtivo() ? "SIM" : "NÃO");

      String demandantes = "";
      int count = 0;
      for (Unidade unidade : assunto.getAssuntoUnidadeDemandanteList()) {
        demandantes += unidade.getSigla();
        if (count < assunto.getAssuntoUnidadeDemandanteList().size()) {
          demandantes += SEPARADOR_CATEGORIA_ASSUNTO;
        }
      }
      expDto.setDemandantesPreDefinidos(demandantes);

      setarCategoriasDosAssuntos(relacaoAssuntosDTOList, assunto, expDto);

      assuntoExportacaoList.add(expDto);
    }

    return assuntoExportacaoList;
  }

  private void setarCategoriasDosAssuntos(List<RelacaoAssuntosDTO> relacaoAssuntosDTOList, Assunto assunto,
      ExportacaoAssuntoDTO expDto) {
    for (RelacaoAssuntosDTO relacao : relacaoAssuntosDTOList) {
      if (relacao.getNumeroAssunto().equals(assunto.getId())) {
        expDto.setArvoreAssuntoAtual(relacao.getNomeAssunto());

        String[] tokensAssuntos = relacao.getNomeAssunto().split(SEPARADOR_CATEGORIA_ASSUNTO);

        expDto.setNomeAssunto(tokensAssuntos[tokensAssuntos.length - 1]);

        for (int i = 0; i < tokensAssuntos.length - 1; i++) {
          String categoria = tokensAssuntos[i];

          if (i == 0) {
            expDto.setCategoria1(categoria);
          } else if (i == 1) {
            expDto.setCategoria2(categoria);
          } else if (i == 2) {
            expDto.setCategoria3(categoria);
          }

        }
      }
    }
  }

  public List<RelatorioPeriodoPorAssuntoDTO> findRelatorioPeriodoPorAssunto(Long idAbrangencia, Long idUnidade,
      Date dtInicioPerInicial, Date dtFimPerInicial, Date dtInicioPerFinal, Date dtFimPerFinal) {

    List<RelatorioPeriodoPorAssuntoDTO> lista =
        dao.findRelatorioPeriodoPorAssunto(idAbrangencia, idUnidade, dtInicioPerInicial, dtFimPerInicial, dtInicioPerFinal,
            dtFimPerFinal);

    List<Assunto> listaTodosAssuntos = obterAssuntosFetchPai();

    for (RelatorioPeriodoPorAssuntoDTO relatorioPeriodoPorAssuntoDTO : lista) {
      relatorioPeriodoPorAssuntoDTO
          .setNomeAssunto(obterArvoreAssuntos(relatorioPeriodoPorAssuntoDTO.getAssunto(), listaTodosAssuntos));
    }

    return lista;
  }
  
  public List<AssuntoCampo> obterAssuntoCampoPorAssunto(Long idAssunto) {
	    return assuntoCampoDAO.obterAssuntoCampoPorAssunto(idAssunto);
  }

  public void removerAssuntoCampo(AssuntoCampo assuntoCampo) {
	  assuntoCampoDAO.delete(assuntoCampo);
  }
  
  public List<AssuntoCampo> obterAssuntoCampoPorAssuntoCampoObrigatorio(Long idAssunto, Long idCampoObrigatorio) {
	  return assuntoCampoDAO.obterAssuntoCampoPorAssuntoCampoObrigatorio(idAssunto, idCampoObrigatorio);
  }
  
  

}

class FluxoAssuntoAuditoria {
  FluxoAssunto fluxoAssunto;
  Auditoria auditoria;

  public FluxoAssuntoAuditoria(FluxoAssunto fluxo, Auditoria auditoria) {
    this.fluxoAssunto = fluxo;
    this.auditoria = auditoria;
  }

}
