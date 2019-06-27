package br.gov.caixa.gitecsa.siarg.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exception.CaixaPostalException;
import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.LogUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.ldap.AutenticadorLdap;
import br.gov.caixa.gitecsa.ldap.usuario.Credentials;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Menu;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UsuarioSistemaDTO;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.ControleAcessoService;
import br.gov.caixa.gitecsa.siarg.service.MenuService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.service.UsuarioService;

/**
 * Classe de controle de acesso para LDAP e Gerencia de usuário.
 * @author f520296
 */
@Named
@SessionScoped
public class ControleAcesso implements Serializable {

  private static final String DOIS_PONTOS = ":";

  /** Serial. */
  private static final long serialVersionUID = 7444956049460916877L;

  private static final String REDIRECT_SELECIONAR_CAIXA_POSTAL =
      "/selecionarCaixaPostal.xhtml?faces-redirect=true";

  /** Constante. */
  private static final String CONTROLE_ACESSO = "ControleAcesso";

  /** Constante. */
  private static final String REDIRECT_LOGIN = "login";

  /** Injeção de Dependência. */
  @Inject
  @ConfigurationRepository("configuracoes.properties")
  private Properties configuracoes;

  /** Injeção de Dependência. */
  @Inject
  private static Logger logger = Logger.getLogger(ControleAcesso.class);

  /** Injeção de Dependência. */
  @Inject
  private ControleAcessoService controleAcessoService;

  @Inject
  private AbrangenciaService abrangenciaService;
  
  @Inject
  private UsuarioService usuarioService;

  /** Injeção de Dependência. */
  @Inject
  private MenuService menuService;
  
  @Inject
  private UnidadeService unidadeService;

  /** Variável Global */
  private Unidade unidade;

  /** Variável Global */
  private AutenticadorLdap autenticador;

  /** Variável Global */
  private UsuarioLdap usuarioLdap;

  /** Variável Global */
  private Credentials credentials;

  /** Variável Global */
  private Boolean gerente;

  /** Variável Global */
  private String perfilUsuario;
  
  private Long idPerfilUsuario;

  /** Variável Global */
  private List<Menu> menuList;

  /** Variável Global */
  private List<UnidadeDTO> unidadesList;

  /** Variável Global */
  private UnidadeDTO unidadeSelecionadaDTO;
  
  private List<CaixaPostal> caixaPostalList;

  private List<Abrangencia> abrangenciaList;
  
  private Abrangencia abrangencia;

  /**
   * Construtor padrão.
   */
  public ControleAcesso() {
    this.credentials = new Credentials();
    this.getDiaDaSemana();
    this.autenticador = new AutenticadorLdap();
  }

  /**
   * Autenticação de usuários baseado no pacote ldap.
   * @return String
   */
  public String autenticarLdap() {

    if (this.validarCampos()) {
      try {

//        char matPermitida = this.credentials.getLogin().charAt(0);
//        if ((matPermitida != 'C') && (matPermitida != 'c') && (matPermitida != 'P') && (matPermitida != 'p')
//            && (matPermitida != 'F') && (matPermitida != 'f')) {
//          this.limparCampos();
//          JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.dados.naoautorizado"));
//          return REDIRECT_LOGIN;
//        }
//
//        this.usuarioLdap =
//            this.autenticador.autenticaUsuarioLdapCaixa(this.credentials, this.configuracoes.getProperty(Constantes.URL_LDAP),
//                this.configuracoes.getProperty(Constantes.NOME_SISTEMA),
//                this.configuracoes.getProperty(Constantes.SECURITY_DOMAIN));
    	  
    	  usuarioLdap = new UsuarioLdap();
    	  usuarioLdap.setCoUnidade(22334455);
    	  usuarioLdap.setNomeUsuario("diegobucher");
    	  usuarioLdap.setNuMatricula("4455");
    	  usuarioLdap.setNuFuncao(1);
    	  
    	  System.setProperty(Constantes.SET_TIME_REFRESH, "60");

        if (!Util.isNullOuVazio(this.usuarioLdap) && !Util.isNullOuVazio(this.usuarioLdap.getNuMatricula())) {
          this.validarPerfilDeAcessoDoUsuario();
          return redirecionarAposLogar();
        } else {
          throw new AppException(LogUtil.getMensagemPadraoLog("NÃO FOI POSSÍVEL OBTER O USUÁRIO DO LDAP", CONTROLE_ACESSO,
              REDIRECT_LOGIN, this.credentials.getLogin()));
        }

//      } catch (CommunicationException e) {
//        this.erroComunicationException();
//        return REDIRECT_LOGIN;
//
//      } catch (FailedLoginException f) {
//        this.erroFailedLoginException();
//        return REDIRECT_LOGIN;
//
//      } catch (AuthenticationException e) {
//        this.erroAuthenticationException();
//        return REDIRECT_LOGIN;

      } catch (AppException e) {
        this.erroAppException(e);
        return REDIRECT_LOGIN;

      } catch (CaixaPostalException e) {
        this.erroCaixaPostalException(e);
        return REDIRECT_LOGIN;

      } catch (Exception e) {
        this.erroException();
        return REDIRECT_LOGIN;
      }
    }

    return REDIRECT_LOGIN;
  }

  private String redirecionarAposLogar() {
    
    //Se tem mais de uma unidade ou a unidade dele tenha mais de 1 caixa postal
//    if(abrangenciaList.size() > 1 || unidadesList.size() > 1 || this.unidade.getCaixasPostaisList().size() > 1) {
//      return REDIRECT_SELECIONAR_CAIXA_POSTAL;
//    } else {
//      return REDIRECT_ACOMPANHAMENTO_DEMANADAS;
//    }
    
    return REDIRECT_SELECIONAR_CAIXA_POSTAL;
  }

  /**
   * Excessão: erroCaixaPostalException.
   * @param e **Exception**
   */
  private void erroCaixaPostalException(CaixaPostalException e) {
    logger.error(LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO", CONTROLE_ACESSO, REDIRECT_LOGIN,
        this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage(e.getMessage());
  }

  /**
   * Excessão: erroException.
   */
  private void erroException() {
    logger.error(LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO", CONTROLE_ACESSO, REDIRECT_LOGIN,
        this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
  }

  /**
   * Excessão: erroAppException.
   */
  private void erroAppException(AppException e) {
    logger.error(LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO", CONTROLE_ACESSO, REDIRECT_LOGIN,
        this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.ldap.naoautorizado"));
  }

  /**
   * Excessão: erroAuthenticationException.
   */
  private void erroAuthenticationException() {
    logger.error(LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO - USUARIO OU SENHA INVALIDOS", CONTROLE_ACESSO,
        REDIRECT_LOGIN, this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage("A senha ou usuário informados são inválidos. Por favor, verifique se houve erros de digitação.");
  }

  /**
   * Excessão: erroFailedLoginException.
   */
  private void erroFailedLoginException() {
    logger.error(LogUtil.getMensagemPadraoLog("FALHA DE AUTENTICAÇÃO - USUARIO OU SENHA INVALIDOS", CONTROLE_ACESSO,
        REDIRECT_LOGIN, this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.ldap.naoautorizado"));
  }

  /**
   * Excessão: erroComunicationException.
   */
  private void erroComunicationException() {
    String nomeLdap = this.configuracoes.getProperty(Constantes.URL_LDAP);
    String ipLdap = nomeLdap.substring(nomeLdap.indexOf("//") + 2);
    String portaLdap = ipLdap.substring(ipLdap.indexOf(DOIS_PONTOS) + 1);
    ipLdap = ipLdap.substring(0, ipLdap.indexOf(DOIS_PONTOS));
    String mensagem =
        "ERRO - NAO FOI POSSIVEL CONECTAR O SERVIDOR LDAP - DADOS:" + "IP_SERVIDOR LDAP: " + ipLdap + " PORTA DO SERVIDOR LDAP: "
            + portaLdap;
    logger.error(LogUtil.getMensagemPadraoLog(mensagem, CONTROLE_ACESSO, REDIRECT_LOGIN, this.credentials.getLogin()));
    this.limparCampos();
    JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
  }

  /**
   * Valida o perfil do usuário logado de acordo com o perfil.
   * @return boolean
   */
  private void validarPerfilDeAcessoDoUsuario() throws AppException, CaixaPostalException {

    UsuarioSistemaDTO usuarioSistema = this.controleAcessoService.obterPerfilCompletoDoUsuarioLogado(this.usuarioLdap);
    this.gerente = usuarioSistema.getPerfilGerencial();
    this.unidadesList = usuarioSistema.getListaUnidadesExcessao();
    this.unidadeSelecionadaDTO = usuarioSistema.getListaUnidadesExcessao().get(0);
//    this.menuList = this.menuService.obterListaMenusPais(usuarioSistema.getPerfil().getId());
    this.unidade = usuarioSistema.getUnidade();
    
    //Removendo inativas
    Iterator<CaixaPostal> it = usuarioSistema.getUnidade().getCaixasPostaisList().iterator();
    while(it.hasNext()) {
      CaixaPostal caixa = it.next();
      if(!caixa.isAtivo()) {
        it.remove();
      }
    }
    
    carregarAbrangencias();
    JSFUtil.setSessionMapValue("controleAcessoBean", this);
    JSFUtil.setSessionMapValue("unidadeSelecionadaDTO", this.unidadeSelecionadaDTO);
    JSFUtil.setSessionMapValue("abrangencia", abrangencia);
    JSFUtil.setSessionMapValue("abrangenciaList", abrangenciaList);
    JSFUtil.setSessionMapValue("unidadesDTOList", usuarioSistema.getListaUnidadesExcessao());
    JSFUtil.setSessionMapValue("loggedMatricula", this.getUsuarioLdap().getNuMatricula());
    JSFUtil.setSessionMapValue("loggedUser", this.credentials.getLogin());
    JSFUtil.setSessionMapValue("loggedUserNomeCompleto", this.getUsuarioLdap().getNomeUsuario());
    JSFUtil.setSessionMapValue("usuario", this.getUsuarioLdap());
    JSFUtil.setSessionMapValue("flagGerente", this.gerente);

    Collections.sort(usuarioSistema.getUnidade().getCaixasPostaisList());
    JSFUtil.setSessionMapValue("caixaPostal", usuarioSistema.getUnidade().getCaixasPostaisList().get(0));

    this.idPerfilUsuario = usuarioSistema.getPerfil().getId();
    this.perfilUsuario = usuarioSistema.getPerfil().getNome();

  }
  
  private void carregarAbrangencias() {
    Usuario usuarioExcessao = this.usuarioService.obterUsuarioExcessaoPorMatricula(usuarioLdap.getNuMatricula());
    abrangenciaList = new ArrayList<>();
    if(usuarioExcessao != null) {
      abrangenciaList = this.abrangenciaService.obterListaAbrangeciaDasUnidadesUsuarioExcessao(usuarioExcessao.getId());
    } 

    List<Unidade> unidadeUsuarioList = unidadeService.obterListaUnidadeUsuarioLogado(usuarioLdap.getCoUnidade());
    if (unidadeUsuarioList != null){
      for (Unidade unidadeLotacao : unidadeUsuarioList) {
        
        if (unidadeLotacao.getCaixasPostaisList() != null
            && !unidadeLotacao.getCaixasPostaisAtivasList().isEmpty()
            &&!abrangenciaList.contains(unidadeLotacao.getAbrangencia())) {
          abrangenciaList.add(unidadeLotacao.getAbrangencia());
        }
      }
    }
    
    if(abrangenciaList.size() == 1) {
      this.abrangencia = abrangenciaList.get(0);
    }
    
  }

  public void setUnidadeSelecionadaDTOSessao() {
    JSFUtil.setSessionMapValue("unidadeSelecionadaDTO", this.unidadeSelecionadaDTO);
  }

  /**
   * Efetuando o logoff.
   * @return String
   */
  public String logout() {

    this.limparSessao();
    this.limparCampos();

    return "/login.xhtml?faces-redirect=true";
  }

  /**
   * Validação de campos de formulário de login.
   * @return boolean
   */
  public boolean validarCampos() {

    if (Util.isNullOuVazio(this.credentials) || Util.isNullOuVazio(this.credentials.getLogin())
        || Util.isNullOuVazio(this.credentials.getSenha())) {

      if (Util.isNullOuVazio(this.credentials.getLogin())) {
        JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios",
            MensagemUtil.obterMensagem("login.label.usuario")));
      } else if (Util.isNullOuVazio(this.credentials.getSenha())) {
        JSFUtil.addErrorMessage(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios",
            MensagemUtil.obterMensagem("login.label.senha")));
      }

      if (!Util.isNullOuVazio(this.credentials.getSenha())) {
        this.credentials.setSenha("");
      }
      return false;

    } else {
      return true;
    }
  }

  /**
   * Limpar tela.
   */
  public void limparCampos() {
    if ((FacesContext.getCurrentInstance().getMessageList() == null)
        || (FacesContext.getCurrentInstance().getMessageList().isEmpty())) {
      this.usuarioLdap = new UsuarioLdap();
      this.credentials = new Credentials();
      this.gerente = Boolean.FALSE;
      this.unidadesList = new ArrayList<>();
      this.unidadeSelecionadaDTO = new UnidadeDTO();
      JSFUtil.setSessionMapValue("usuarioSistema", null);
    }
  }

  /**
   * Limpar sessão.
   */
  public void limparSessao() {
    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    response.resetBuffer();

    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
  }

  /**
   * Obter dia da Semana.
   */
  private void getDiaDaSemana() {
    JSFUtil.setSessionMapValue("dataDiaExtenso", DateUtil.formatData(Calendar.getInstance().getTime(), "EEEE, dd/MM/yyyy HH:mm"));
  }
  
  public String getPrimeiroNomeUsuario() {
	  if(usuarioLdap != null && usuarioLdap.getNomeUsuario() != null) {
		  String [] nomeTokens = usuarioLdap.getNomeUsuario().split(" ");
		  return nomeTokens[0];
	  }
	  return "";
  }

  /**
   * Obter o valor do Atributo.
   * @return unidade
   */
  public Unidade getUnidade() {
    return this.unidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidade the unidade to set
   */
  public void setUnidade(Unidade unidade) {
    this.unidade = unidade;
  }

  /**
   * Obter o valor do Atributo.
   * @return usuarioLdap
   */
  public UsuarioLdap getUsuarioLdap() {
    return this.usuarioLdap;
  }

  /**
   * Gravar o valor do Atributo.
   * @param usuarioLdap the usuarioLdap to set
   */
  public void setUsuarioLdap(UsuarioLdap usuarioLdap) {
    this.usuarioLdap = usuarioLdap;
  }

  /**
   * Obter o valor do Atributo.
   * @return credentials
   */
  public Credentials getCredentials() {
    return this.credentials;
  }

  /**
   * Gravar o valor do Atributo.
   * @param credentials the credentials to set
   */
  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  /**
   * Obter o valor do Atributo.
   * @return gerente
   */
  public Boolean getGerente() {
    return this.gerente;
  }

  /**
   * Gravar o valor do Atributo.
   * @param gerente the gerente to set
   */
  public void setGerente(Boolean gerente) {
    this.gerente = gerente;
  }

  /**
   * Obter o valor do Atributo.
   * @return perfilUsuario
   */
  public String getPerfilUsuario() {
    return this.perfilUsuario;
  }

  /**
   * Gravar o valor do Atributo.
   * @param perfilUsuario the perfilUsuario to set
   */
  public void setPerfilUsuario(String perfilUsuario) {
    this.perfilUsuario = perfilUsuario;
  }

  /**
   * Obter o valor do Atributo.
   * @return menuList
   */
  public List<Menu> getMenuList() {
    
    if(menuList == null) {
      
      this.menuList = this.menuService.obterListaMenusPais(idPerfilUsuario, abrangencia.getId());
    }

    return this.menuList;
  }

  /**
   * Gravar o valor do Atributo.
   * @param menuList the menuList to set
   */
  public void setMenuList(List<Menu> menuList) {
    this.menuList = menuList;
  }

  /**
   * Obter o valor do Atributo.
   * @return unidadesList
   */
  public List<UnidadeDTO> getUnidadesList() {
    return this.unidadesList;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidadesList the unidadesList to set
   */
  public void setUnidadesList(List<UnidadeDTO> unidadesList) {
    this.unidadesList = unidadesList;
  }

  /**
   * Obter o valor do Atributo.
   * @return unidadeDTO
   */
  public UnidadeDTO getUnidadeSelecionadaDTO() {
    return this.unidadeSelecionadaDTO;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidadeDTO the unidadeDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  public List<CaixaPostal> getCaixaPostalList() {
    return caixaPostalList;
  }

  public void setCaixaPostalList(List<CaixaPostal> caixaPostalList) {
    this.caixaPostalList = caixaPostalList;
  }

  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  public List<UnidadeDTO> getUnidadesPorAbrangenciaList() {
    
    List<UnidadeDTO> unidadesPorAbrangenciaList = new ArrayList<>();
    
    for (UnidadeDTO unidadeDTO : unidadesList) {
      if(unidadeDTO.getAbrangencia().equals(abrangencia.getId())) {
        unidadesPorAbrangenciaList.add(unidadeDTO);
      }
    }
    
    return unidadesPorAbrangenciaList;
  }

}
