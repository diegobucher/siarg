/**
 * UnidadeService.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.siarg.dao.AuditoriaDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioUnidadeDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAuditoriaEnum;
import br.gov.caixa.gitecsa.siarg.model.Auditoria;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * Classe de serviços de Usuario.
 */
@Stateless
public class UsuarioService {

  @Inject
  private UsuarioDAO usuarioDAO;

  @Inject
  private UsuarioUnidadeDAO usuarioUnidadeDAO;

  @Inject
  private UnidadeDAO unidadeDAO;
  
  @Inject
  private AuditoriaDAO auditoriaDAO;

  /** Construtor Padrão. */
  public UsuarioService() {
    super();
  }

  public Usuario buscarUsuarioPorMatriculaFetch(String matricula) {
    return usuarioDAO.buscarUsuarioPorMatriculaFetch(matricula);
  }

  public List<UsuarioUnidade> buscarUsuarioUnidadePorMatricula(final String matricula, final Long idAbrangencia) {
    return usuarioDAO.buscarUsuarioUnidadePorMatricula(matricula, idAbrangencia);
  }
  
  public Usuario obterUsuarioExcessaoPorMatricula(final String matricula) {
    return usuarioDAO.obterUsuarioExcessaoPorMatricula(matricula);
  }

  public void salvarUsuario(Usuario usuario, List<UsuarioUnidade> usuarioUnidadeAdicionadaAtualizadaList,
      List<UsuarioUnidade> usuarioUnidadeRemovidasList, String matriculaLogado, Unidade unidadeSelecionada) {

    List<Auditoria> listaAuditoria = new ArrayList<>();

    // Atualizando a tabela de usuarios
    Auditoria auditoriaUsuario = null;
    if (usuario.getId() == null) {
      usuarioDAO.save(usuario);
      auditoriaUsuario = gerarAuditoriaUsuario(usuario, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.I);
    } else {
      auditoriaUsuario = gerarAuditoriaUsuario(usuario, matriculaLogado, unidadeSelecionada, AcaoAuditoriaEnum.A);
      usuarioDAO.update(usuario);
    }

    if (auditoriaUsuario != null) {
      listaAuditoria.add(auditoriaUsuario);
    }

    // Atualiza / Adiciona

    Auditoria auditoriaUsuarioUnidade = null;

    for (UsuarioUnidade usuarioUnidade : usuarioUnidadeAdicionadaAtualizadaList) {
      if (usuarioUnidade.getId() != null) {
        auditoriaUsuarioUnidade =
            gerarAuditoriaUsuarioUnidade(matriculaLogado, unidadeSelecionada, usuarioUnidade, AcaoAuditoriaEnum.A);
        usuarioUnidadeDAO.update(usuarioUnidade);
      } else {

        usuarioUnidade.setUnidade(unidadeDAO.obterUnidadePorChaveFetch(usuarioUnidade.getUnidade().getId()));
        usuarioUnidade.setUsuario(usuarioDAO.findById(usuarioUnidade.getUsuario().getId()));
        usuarioUnidadeDAO.save(usuarioUnidade);
        auditoriaUsuarioUnidade =
            gerarAuditoriaUsuarioUnidade(matriculaLogado, unidadeSelecionada, usuarioUnidade, AcaoAuditoriaEnum.I);
      }

      if (auditoriaUsuarioUnidade != null) {
        listaAuditoria.add(auditoriaUsuarioUnidade);
      }

    }
    
    auditoriaDAO.saveList(listaAuditoria);

  }

  private Auditoria gerarAuditoriaUsuarioUnidade(String matriculaLogado, Unidade unidadeSelecionada,
      UsuarioUnidade usuarioUnidade, AcaoAuditoriaEnum acao) {

    Auditoria auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(acao);
    auditoria.setNomeTabelaReferenciada("UsuarioUnidade");
    auditoria.setNumeroChaveTabela(usuarioUnidade.getId());

    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMATO_DATA);

    StringBuilder sb = new StringBuilder();
    if (acao.equals(AcaoAuditoriaEnum.I)) {
      incluirAuditoria(usuarioUnidade, sdf, sb);

    } else if (acao.equals(AcaoAuditoriaEnum.A)) {
      UsuarioUnidade usuarioUnidadeNaBase = usuarioUnidadeDAO.findByIdFetch(usuarioUnidade.getId());

      boolean mudouDataInicio = false;
      if (!DateUtils.truncatedEquals(usuarioUnidadeNaBase.getDataInicio(),usuarioUnidade.getDataInicio(),Calendar.DATE)) {
        sb.append(" Data Inicio: " + sdf.format(usuarioUnidadeNaBase.getDataInicio()) + "#&"
            + sdf.format(usuarioUnidade.getDataInicio()) + ";");
        mudouDataInicio = true;
      }
      
      boolean mudouObservacao = logarMudancaObservacao(usuarioUnidade, sb, usuarioUnidadeNaBase);

      boolean mudouDataFim = false;

      mudouDataFim = verificarMudouDataFim(usuarioUnidade, usuarioUnidadeNaBase, mudouDataFim);

      if (mudouDataFim) {
        logarMudancaDataFim(usuarioUnidade, sdf, sb, usuarioUnidadeNaBase);
      }

      if(!mudouDataInicio && !mudouDataFim && !mudouObservacao) {
        return null;
      }
    }
    

    auditoria.setDescricao(sb.toString());

    return auditoria;
  }

  private boolean verificarMudouDataFim(UsuarioUnidade usuarioUnidade, UsuarioUnidade usuarioUnidadeNaBase,
      boolean mudouDataFim) {
    if ((usuarioUnidade.getDataFim() == null && usuarioUnidadeNaBase.getDataFim() != null)
        || (usuarioUnidade.getDataFim() != null && usuarioUnidadeNaBase.getDataFim() == null)) {
      mudouDataFim = true;
    }

    if (usuarioUnidade.getDataFim() != null && usuarioUnidadeNaBase.getDataFim() != null
        && !usuarioUnidade.getDataFim().equals(usuarioUnidadeNaBase.getDataFim())) {
      mudouDataFim = true;
    }
    return mudouDataFim;
  }

  private boolean logarMudancaObservacao(UsuarioUnidade usuarioUnidade, StringBuilder sb, UsuarioUnidade usuarioUnidadeNaBase) {
    boolean mudouObservacao = false;
    
    if(usuarioUnidadeNaBase.getObservacao() != null && usuarioUnidade.getObservacao() != null &&
        !usuarioUnidadeNaBase.getObservacao().equals(usuarioUnidade.getObservacao())) {
     mudouObservacao = true; 
    }
    
    if((usuarioUnidadeNaBase.getObservacao() == null && usuarioUnidade.getObservacao() != null)
        && (usuarioUnidadeNaBase.getObservacao() != null && usuarioUnidade.getObservacao() == null)) {
      mudouObservacao = true;
    }

    if(mudouObservacao) {
      sb.append(" Observação: " + usuarioUnidadeNaBase.getObservacao() + "#&" + usuarioUnidade.getObservacao() + ";");
    }
    return mudouObservacao;
  }

  private void logarMudancaDataFim(UsuarioUnidade usuarioUnidade, SimpleDateFormat sdf, StringBuilder sb,
      UsuarioUnidade usuarioUnidadeNaBase) {
    String dataFimStr = "";
    if (usuarioUnidade.getDataFim() != null) {
      dataFimStr = sdf.format(usuarioUnidade.getDataFim());
    }

    String dataFimNaBaseStr = "";
    if (usuarioUnidadeNaBase.getDataFim() != null) {
      dataFimNaBaseStr = sdf.format(usuarioUnidadeNaBase.getDataFim());
    }

    sb.append(" Data Fim: " + dataFimNaBaseStr + "#&" + dataFimStr + ";");
  }

  private void incluirAuditoria(UsuarioUnidade usuarioUnidade, SimpleDateFormat sdf, StringBuilder sb) {
    sb.append(" Usuario: " + usuarioUnidade.getUsuario().getMatricula() + ";");
    sb.append(" Unidade: " + usuarioUnidade.getUnidade().getCgcUnidade() + ";");
    sb.append(" Data Inicio: " + sdf.format(usuarioUnidade.getDataInicio()) + ";");
    String dataFimStr = "";
    if (usuarioUnidade.getDataFim() != null) {
      dataFimStr = sdf.format(usuarioUnidade.getDataFim());
      sb.append(" Data Fim: " + dataFimStr + ";");
    }
    if(StringUtils.isNotBlank(usuarioUnidade.getObservacao())) {
      sb.append(" Observação: " + usuarioUnidade.getObservacao() + ";");
    }
  }

  private Auditoria gerarAuditoriaUsuario(Usuario usuario, String matriculaLogado, Unidade unidadeSelecionada,
      AcaoAuditoriaEnum acao) {

    Auditoria auditoria = new Auditoria();
    auditoria.setDataAuditoria(new Date());
    auditoria.setMatricula(matriculaLogado);
    auditoria.setCgcUnidade(unidadeSelecionada.getCgcUnidade());
    auditoria.setAcaoAuditoriaEnum(acao);
    auditoria.setNomeTabelaReferenciada("Usuario");
    auditoria.setNumeroChaveTabela(usuario.getId());

    StringBuilder sb = new StringBuilder();
    if (acao.equals(AcaoAuditoriaEnum.I)) {
      sb.append(" Nome: " + usuario.getNome() + ";");
      sb.append(" Matrícula: " + usuario.getMatricula() + ";");
      sb.append(" Perfil: " + usuario.getPerfil().getNome() + ";");

    } else if (acao.equals(AcaoAuditoriaEnum.A)) {
      Usuario usuarioNaBase = buscarUsuarioPorMatriculaFetch(usuario.getMatricula());
      if (!usuario.getPerfil().getId().equals(usuarioNaBase.getPerfil().getId())) {
        sb.append(" Perfil: " + usuarioNaBase.getPerfil().getNome() + "#&" + usuario.getPerfil().getNome() + ";");
      } else {
        return null;
      }
    }

    auditoria.setDescricao(sb.toString());

    return auditoria;
  }
  
  public boolean existeVinculacaoConflitante(UsuarioUnidade usuarioUnidade) {
    return usuarioUnidadeDAO.existeVinculacaoConflitante(usuarioUnidade);
  }


}
