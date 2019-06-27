package br.gov.caixa.gitecsa.siarg.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.model.UploadedFile;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessRollbackException;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoImportacaoEnum;
import br.gov.caixa.gitecsa.siarg.service.ImportacaoService;

@Named
@ViewScoped
public class ImportacaoController extends BaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  private TipoImportacaoEnum tipoImportacaoEnum;

  private UploadedFile planilhaImportacaoFile;

  private final String EXT_XLS = ".xls";
  
  private Integer quantidadeErros;
  
  @Inject
  private ImportacaoService importacaoService;
  
  private String mensagemErros;

  @PostConstruct
  public void init() {
  }
  
  public void handlerChangeTipoPlanilha() {
    quantidadeErros = 0;
    mensagemErros = null;
  }

  public void importarPlanilha() {
    
    quantidadeErros = 0;
    
    if (validarAnexo()) {
      
      UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
      String matriculaUsuarioLogado = usuario.getNuMatricula();
      String nomeUsuarioLogado = usuario.getNomeUsuario();
      
      Integer qtdImportados = 0;
      
      try {
        if (TipoImportacaoEnum.ASSUNTOS.equals(tipoImportacaoEnum)) {
          qtdImportados = importacaoService.importarAssuntos(planilhaImportacaoFile.getInputstream());
        } else {
          qtdImportados = importacaoService.importarDemandas(planilhaImportacaoFile.getInputstream(), matriculaUsuarioLogado, nomeUsuarioLogado);
        }
        
        this.facesMessager.addMessageInfo("Foi(ram) importado(s) "+qtdImportados+" registro(s) com sucesso.");
        
      } catch (BusinessRollbackException e) {
        
        if (StringUtils.isNotBlank(e.getMensagemFormatada())) {
          mensagemErros = e.getMensagemFormatada();
          quantidadeErros = e.getErroList().size();
        } else {
          this.facesMessager.addMessageError(e.getMessage());
        }
        
      } catch (IOException e) {
        this.facesMessager.addMessageError(e.getMessage());
      }
    }
  }
  
  public void downloadLogErros() throws IOException {
      
    InputStream stream = new ByteArrayInputStream(mensagemErros.toString().getBytes(StandardCharsets.UTF_8));

    Faces.sendFile(stream, "siarg.log", true);
  }

  public boolean validarAnexo() {

    boolean valido = true;

    if (planilhaImportacaoFile == null || StringUtils.isBlank(planilhaImportacaoFile.getFileName())) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
      valido = false;
    } else {
      if (!planilhaImportacaoFile.getFileName().toLowerCase().endsWith(EXT_XLS)) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA039"));
        valido = false;
      }
    }

    return valido;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public TipoImportacaoEnum getTipoImportacaoEnum() {
    return tipoImportacaoEnum;
  }

  public void setTipoImportacaoEnum(TipoImportacaoEnum tipoImportacaoEnum) {
    this.tipoImportacaoEnum = tipoImportacaoEnum;
  }

  public List<TipoImportacaoEnum> getTipoImportacaoEnumList() {
    return Arrays.asList(TipoImportacaoEnum.values());
  }

  public UploadedFile getPlanilhaImportacaoFile() {
    return planilhaImportacaoFile;
  }

  public void setPlanilhaImportacaoFile(UploadedFile planilhaImportacaoFile) {
    if (planilhaImportacaoFile != null && StringUtils.isNotBlank(planilhaImportacaoFile.getFileName())) {
      this.planilhaImportacaoFile = planilhaImportacaoFile;
    } else {
      this.planilhaImportacaoFile = null;
    }
  }

  public Integer getQuantidadeErros() {
    return quantidadeErros;
  }

  public void setQuantidadeErros(Integer quantidadeErros) {
    this.quantidadeErros = quantidadeErros;
  }
}
