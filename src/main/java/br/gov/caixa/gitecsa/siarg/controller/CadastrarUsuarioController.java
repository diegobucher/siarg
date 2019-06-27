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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.ldap.AutenticadorLdap;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Perfil;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.AuditoriaService;
import br.gov.caixa.gitecsa.siarg.service.PerfilService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.service.UsuarioService;

/**
 * Classe Controller para tela de cadastro de usuario
 */
@Named
@ViewScoped
public class CadastrarUsuarioController extends BaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Injections necessários
   */
  @Inject
  @ConfigurationRepository("configuracoes.properties")
  private Properties configuracoes;

  @EJB
  private UsuarioService usuarioService;

  @EJB
  private AbrangenciaService abrangenciaService;

  @EJB
  private UnidadeService unidadeService;

  @EJB
  private PerfilService perfilService;

  @EJB
  private AuditoriaService auditoria;

  /*
   * Campos da tela
   */
  /** Autenticador do LDAP */
  private AutenticadorLdap autenticador;

  /** Matricula a ser pesquisa no inicio da tela */
  private String matricula;

  /** UsuarioLdap retornado */
  private UsuarioLdap usuarioLdap;

  /** Usuario do BD */
  private Usuario usuario;

  /** Grid das unidades vinculadas ao usuario */
  private List<UsuarioUnidade> usuarioUnidadeList;

  private List<UsuarioUnidade> usuarioUnidadeAdicionadaAtualizadaList;

  private List<UsuarioUnidade> usuarioUnidadeRemovidasList;

  private List<Perfil> perfilList;

  /*
   * Campos do Modal
   */
  /** Lista do combo de unidades */
  private List<Unidade> unidadeList;

  /** Abrangência selecionada */
  private Abrangencia abrangencia;

  /** Objeto a ser salvo */
  private UsuarioUnidade usuarioUnidade;

  private Unidade unidadeLogada;

  private Boolean flagMaster;

  private String matriculaLogado;

  @PostConstruct
  public void init() {
    autenticador = new AutenticadorLdap();
    perfilList = perfilService.findAll();

    UnidadeDTO unidadeDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.unidadeLogada = this.unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());

    UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
    matriculaLogado = usuario.getNuMatricula();
    limpar();

    abrangencia = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");

  }

  public List<Unidade> carregarUnidades() {
	  
	abrangencia = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
	
    if (abrangencia != null) {
      this.unidadeList =
          unidadeService.obterUnidadesECaixasPostaisPorTipo(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);
      Collections.sort(unidadeList);
    } else {
      this.unidadeList = new ArrayList<Unidade>();
    }
    
    return unidadeList;
  }

  public void salvarUsuario() {

    try {
      usuarioService.salvarUsuario(usuario, usuarioUnidadeAdicionadaAtualizadaList, usuarioUnidadeRemovidasList, matriculaLogado,
          unidadeLogada);
      this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("usuario.mensagem.salvar"));
    } catch (Exception e) {
    }

  }

  /**
   * Busca o Usuario no LDAP e registros associados se existir;
   */
  public void pesquisarUsuario() {

    if (validarCampos()) {
      limpar();

      try {

        this.usuario = usuarioService.buscarUsuarioPorMatriculaFetch(matricula);

        this.usuarioLdap =
            this.autenticador.pesquisarForaDoGrupoDoLdap(matricula, this.configuracoes.getProperty(Constantes.URL_LDAP));

        if (usuarioLdap == null) {
          this.facesMessager.addMessageError(MensagemUtil.obterMensagem("usuario.mensagem.matriculaInexistenteLdap"));
          usuario = null;
        } else {
          if (usuario == null) {
            usuario = new Usuario();
            usuario.setMatricula(this.usuarioLdap.getNuMatricula());
            usuario.setNome(this.usuarioLdap.getNomeUsuario());
            for (Perfil perfil : perfilList) {
              if (perfil.getId().equals(2l)) {
                usuario.setPerfil(perfil);
                break;
              }
            }
            this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("usuario.mensagem.naoCadastrado"));
          }

          this.usuarioUnidadeList.addAll(usuarioService.buscarUsuarioUnidadePorMatricula(matricula, abrangencia.getId()));
        }

      } catch (Exception e) {
        this.usuarioLdap = new UsuarioLdap();
        getLogger().error(e);
      }
    }

  }

  public void handlerAbrirModalNovaUnidade() {
    this.abrangencia = null;
    this.unidadeList = new ArrayList<Unidade>();
    this.usuarioUnidade = new UsuarioUnidade();
    this.usuarioUnidade.setUsuario(this.usuario);
  }

  public void handlerAbrirModalEditarUnidade(UsuarioUnidade usuarioUnidade) {
    this.usuarioUnidade = SerializationUtils.clone(usuarioUnidade);
    this.usuarioUnidade.setUsuarioUnidadeTransient(usuarioUnidade);
  }

  public void handlerAbrirModalCancelarUnidade(UsuarioUnidade usuarioUnidade) {
    this.usuarioUnidade = SerializationUtils.clone(usuarioUnidade);
    this.usuarioUnidade.setUsuarioUnidadeTransient(usuarioUnidade);
  }

  public void adicionarNovaUnidade() {
    if (validarNovaUnidade()) {
      usuarioUnidade.setUnidade(unidadeService.obterUnidadePorChaveFetch(usuarioUnidade.getUnidade().getId()));
      this.usuarioUnidadeList.add(usuarioUnidade);
      this.usuarioUnidadeAdicionadaAtualizadaList.add(usuarioUnidade);
    }
  }

  public void editarUnidade() {
    if (validarEditarUnidade()) {

      // Percorre a lista de unidades para achar qual vai ser atualizada.
      for (UsuarioUnidade usuarioUnidade : usuarioUnidadeList) {

        // Existe na base compara os ID
        //Senão existe na base compara a Unidade
        if ((this.usuarioUnidade.getId() != null && this.usuarioUnidade.getId().equals(usuarioUnidade.getId()))
            || (this.usuarioUnidade.getId() == null && usuarioUnidade.getId() == null 
                && usuarioUnidade.getUnidade().equals(this.usuarioUnidade.getUnidade())) 
            ) {

          // Atualiza a instancia que está na grid
          usuarioUnidade.setDataInicio(this.usuarioUnidade.getDataInicio());
          usuarioUnidade.setDataFim(this.usuarioUnidade.getDataFim());
          usuarioUnidade.setObservacao(this.usuarioUnidade.getObservacao());

          // Verifica se tá na lista de atualizados
          boolean estaAdicionada = false;
          for (UsuarioUnidade usuarioUnidadeAdicionar : usuarioUnidadeAdicionadaAtualizadaList) {
//            if (usuarioUnidadeAdicionar.getUnidade().equals(usuarioUnidade.getUnidade())) {
            if ((this.usuarioUnidade.getId() != null && this.usuarioUnidade.getId().equals(usuarioUnidadeAdicionar.getId()))
                || (this.usuarioUnidade.getId() == null && usuarioUnidade.getId() == null 
                && usuarioUnidadeAdicionar.getUnidade().equals(this.usuarioUnidade.getUnidade()) ) 
                ) {
              estaAdicionada = true;
              break;
            }
          }

          if (!estaAdicionada) {
            usuarioUnidadeAdicionadaAtualizadaList.add(usuarioUnidade);
          }
        }
      }

    }
  }

  private boolean existeVinculacaoConflitanteMemoria() {

    Calendar calDataNoFuturo = Calendar.getInstance();
    calDataNoFuturo.set(Calendar.YEAR, 2199);

    // Percorre a lista de Unidade na memoria
    for (UsuarioUnidade usuaUnidade : this.usuarioUnidadeList) {

      // Se o registro for da mesma unidade
      if (this.usuarioUnidade.getUnidade().equals(usuaUnidade.getUnidade())) {

        // Em adição/edição
        Date dataFimCadastroEdicao = usuarioUnidade.getDataFim();
        if (dataFimCadastroEdicao == null) {
          dataFimCadastroEdicao = calDataNoFuturo.getTime();
        }

        // Item da lista de adicionado
        Date dataFimLoop = usuaUnidade.getDataFim();
        if (dataFimLoop == null) {
          dataFimLoop = calDataNoFuturo.getTime();
        }

        // Se for a mesma usuario unidade, estando em Edição então pula e não faz a comparação
        if (this.usuarioUnidade.getId() != null && usuaUnidade.getId() != null
            && this.usuarioUnidade.getId().equals(usuaUnidade.getId())) {
          continue;
        } else if(this.usuarioUnidade.getId() == null && usuaUnidade.getId() == null 
            && this.usuarioUnidade.getUsuarioUnidadeTransient() == usuaUnidade){
          continue;
        }

        //
        if ((this.usuarioUnidade.getDataInicio().before(dataFimLoop) || this.usuarioUnidade.getDataInicio().equals(dataFimLoop))
            && (dataFimCadastroEdicao.after(usuaUnidade.getDataInicio())
                || dataFimCadastroEdicao.equals(usuaUnidade.getDataInicio()))
        // (dataFimLoop.after(usuarioUnidade.getDataInicio()) || dataFimLoop.equals(usuarioUnidade.getDataInicio()))
        ) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean validarEditarUnidade() {
    boolean valido = true;

    if (this.usuarioUnidade.getDataInicio() == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
      valido = false;
    }

    if (valido && this.usuarioUnidade.getDataFim() != null) {
      if (this.usuarioUnidade.getDataFim().before(this.usuarioUnidade.getDataInicio())) {
        this.facesMessager.addMessageError("Data Início deve ser menor ou igual à Data Fim.");
        valido = false;
      }
    }

    if (valido) {
      if (existeVinculacaoConflitanteMemoria()) {
        this.facesMessager.addMessageError("Já existe vinculação para o período informado.");
        valido = false;
      }
    }

    if (!valido) {
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
    }

    return valido;
  }

  public boolean validarNovaUnidade() {
    boolean valido = true;

    if (this.abrangencia == null || this.usuarioUnidade.getUnidade() == null || this.usuarioUnidade.getDataInicio() == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
      valido = false;
    }

    if (valido && this.usuarioUnidade.getDataFim() != null) {
      if (this.usuarioUnidade.getDataFim().before(this.usuarioUnidade.getDataInicio())) {
        this.facesMessager.addMessageError("Data Início deve ser menor ou igual à Data Fim.");
        valido = false;
      }
    }

    if (valido) {
      if (existeVinculacaoConflitanteMemoria()) {
        this.facesMessager.addMessageError("Já existe vinculação para o período informado.");
        valido = false;
      }
    }

    if (!valido) {
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
    }

    return valido;
  }

  public UsuarioUnidade getUsuarioUnidade() {
    return usuarioUnidade;
  }

  public void setUsuarioUnidade(UsuarioUnidade usuarioUnidade) {
    this.usuarioUnidade = usuarioUnidade;
  }

  public void limparTela() {
    limpar();
    this.matricula = "";
  }

  public void limpar() {
    this.usuarioLdap = new UsuarioLdap();
    this.usuarioUnidadeList = new ArrayList<UsuarioUnidade>();
    this.usuarioUnidadeAdicionadaAtualizadaList = new ArrayList<UsuarioUnidade>();
    this.usuarioUnidadeRemovidasList = new ArrayList<UsuarioUnidade>();

    this.usuario = null;
    this.usuarioUnidade = new UsuarioUnidade();
    this.unidadeList = new ArrayList<Unidade>();
  }

  private boolean validarCampos() {
    boolean valido = true;

    if (StringUtils.isBlank(matricula)) {
      valido = false;
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"));
    }
    return valido;
  }

  @Override
  public Logger getLogger() {
    return this.LOGGER;
  }

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public UsuarioLdap getUsuarioLdap() {
    return usuarioLdap;
  }

  public void setUsuarioLdap(UsuarioLdap usuarioLdap) {
    this.usuarioLdap = usuarioLdap;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public List<UsuarioUnidade> getUsuarioUnidadeList() {
    return usuarioUnidadeList;
  }

  public void setUsuarioUnidadeList(List<UsuarioUnidade> usuarioUnidadeList) {
    this.usuarioUnidadeList = usuarioUnidadeList;
  }

  public List<Unidade> getUnidadeList() {
    return unidadeList;
  }

  public void setUnidadeList(List<Unidade> unidadeList) {
    this.unidadeList = unidadeList;
  }

  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  public Boolean getFlagMaster() {
    return flagMaster;
  }

  public void setFlagMaster(Boolean flagMaster) {
    this.flagMaster = flagMaster;
  }

  public List<UsuarioUnidade> getUsuarioUnidadeRemovidasList() {
    return usuarioUnidadeRemovidasList;
  }

  public void setUsuarioUnidadeRemovidasList(List<UsuarioUnidade> usuarioUnidadeRemovidasList) {
    this.usuarioUnidadeRemovidasList = usuarioUnidadeRemovidasList;
  }

  public List<Perfil> getPerfilList() {
    return perfilList;
  }

  public void setPerfilList(List<Perfil> perfilList) {
    this.perfilList = perfilList;
  }

}
