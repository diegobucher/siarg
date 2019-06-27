package br.gov.caixa.gitecsa.siarg.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.dto.ConfiguracaoEmailDTO;
import br.gov.caixa.gitecsa.siarg.dto.EmailAcaoUsuarioDTO;
import br.gov.caixa.gitecsa.siarg.dto.EmailDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExtratoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.dto.PrazoQuestionamentoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;
import br.gov.caixa.gitecsa.siarg.util.TagEmailConstantes;

@Stateless
public class EmailService {

  @Inject
  private transient Logger logger;

  @Inject
  private ParametroDAO parametroDAO;

  public void enviar(EmailDTO emailDTO, ConfiguracaoEmailDTO configuracaoEmailDTO, String motivo) {
    Boolean enviado = Boolean.TRUE;
    try {
      HtmlEmail lEmail = new HtmlEmail();

      lEmail.setHostName(configuracaoEmailDTO.getHost());
      lEmail.setSmtpPort(Integer.parseInt(configuracaoEmailDTO.getPorta()));
      lEmail.setFrom(configuracaoEmailDTO.getRemetente());

      lEmail.addTo(emailDTO.getDestinatario());
      if (emailDTO.getComCopiaList() != null && !emailDTO.getComCopiaList().isEmpty()) {

        String[] comCopiaArray = new String[emailDTO.getComCopiaList().size()];
        comCopiaArray = emailDTO.getComCopiaList().toArray(comCopiaArray);
        lEmail.addCc(comCopiaArray);
      }

      lEmail.setSubject(emailDTO.getAssunto().toLowerCase().trim());
      lEmail.setSSLOnConnect(false);
      lEmail.setStartTLSEnabled(false);
      lEmail.setHtmlMsg(emailDTO.getConteudo());

      lEmail.setCharset("UTF-8");
      lEmail.send();
    } catch (EmailException e) {
      enviado = Boolean.FALSE;
    } catch (Exception e) {
      enviado = Boolean.FALSE;
    } finally {
      String msgErro = "";
      msgErro += "Caixa Postal Destino: " + emailDTO.getDestinatario();
      msgErro += "Enviado: " + (enviado ? "Sim" : "Não" + "\r\n");
      this.logger.info(msgErro);
    }
  }

  public void enviarEmail(ConfiguracaoEmailDTO configuracaoEmailDTO, Map<String, String> parametros, String assunto,
      String conteudo, List<String> comCopiaList, String motivo) {
    EmailDTO emailDTO = new EmailDTO();

    for (String key : parametros.keySet()) {
      conteudo = conteudo.replace(key, parametros.get(key));
    }
    emailDTO.setConteudo(conteudo);
    emailDTO.setAssunto(assunto);
    emailDTO.setDestinatario(parametros.get(TagEmailConstantes.TAG_EMAIL_DESTINO));
    emailDTO.setComCopiaList(comCopiaList);

    enviar(emailDTO, configuracaoEmailDTO, motivo);
  }

  public void enviarEmailPorAcao(Demanda demanda, CaixaPostal caixaOrigem, CaixaPostal caixaDestino, List<CaixaPostal> caixasObservadorasList,
      AcaoAtendimentoEnum acaoAtendimentoEnumParam, String acompanhamento, String arvoreAssunto, Integer qtdPrazoDias,
      String dataLimite, String matriculaLogado) {
    
    AcaoAtendimentoEnum acaoAtendimentoEnum = acaoAtendimentoEnumParam;

    if (caixaDestino.getRecebeEmail()) {
      try {

        List<String> comCopiaList = new ArrayList<String>();
        
        // Não se deve adicionar o com copia para o usuario logado
        // Quando é uma ação de CONSULTAR.
        if(!AcaoAtendimentoEnum.CONSULTAR.equals(acaoAtendimentoEnum) 
            && StringUtils.isNotBlank(matriculaLogado)) {
          comCopiaList.add(matriculaLogado + "@mail.caixa");
        }
        
        // Após esse tratamento Altera a Acao de CONSULTAR para INCLUIR e seguir o fluxo antigo
        if(AcaoAtendimentoEnum.CONSULTAR.equals(acaoAtendimentoEnum)) {
          acaoAtendimentoEnum = AcaoAtendimentoEnum.INCLUIR;
        }
        
        EmailAcaoUsuarioDTO emailAcaoDTO = new EmailAcaoUsuarioDTO();
        emailAcaoDTO.setArvoreAssunto(arvoreAssunto);
        emailAcaoDTO.setCaixaOrigem(caixaOrigem.getSigla());
        emailAcaoDTO.setCaixaDestino(caixaDestino.getSigla());
        emailAcaoDTO.setNumeroDemanda(demanda.getId().toString());
        emailAcaoDTO.setDataLimite(dataLimite);

        emailAcaoDTO.setDescricaoAcao(acaoAtendimentoEnum.getDescricaoEmail());
        emailAcaoDTO.setPrazo(qtdPrazoDias.toString());
        emailAcaoDTO.setEmailDestino(caixaDestino.getEmail());

        Integer qtdCaracteres =
            Integer.parseInt(parametroDAO.obterParametroByNome(ParametroConstantes.QTD_CARACTERES_RESPOSTA).getValor());
        if (acompanhamento.length() > qtdCaracteres) {
          emailAcaoDTO.setResumo(acompanhamento.substring(0, qtdCaracteres));
        } else {
          emailAcaoDTO.setResumo(acompanhamento);
        }
        emailAcaoDTO.setTituloDemanda(demanda.getTitulo());
        emailAcaoDTO.setUrlSistema(parametroDAO.obterParametroByNome(ParametroConstantes.URL_SISTEMA).getValor());
        emailAcaoDTO.setUsuarioDemandante(demanda.getNomeUsuarioDemandante());
        emailAcaoDTO.setMacriculaUsuarioDemandante(demanda.getMatriculaDemandante());

        ConfiguracaoEmailDTO configuracaoEmailDTO = getConfiguracaoEmail();
        String assunto = demanda.getAssunto().getNome();
        String conteudo = parametroDAO.obterParametroByNome(ParametroConstantes.TEMPLATE_JOB_MOVIMENTACAO).getValor();
        
        enviarEmailPorAcao(emailAcaoDTO, configuracaoEmailDTO, assunto, conteudo, comCopiaList, null);
        
      } catch (Exception e) {
        logger.error(e);
      }
    }

  }

  public ConfiguracaoEmailDTO getConfiguracaoEmail() {

    ConfiguracaoEmailDTO configuracaoEmailDTO = new ConfiguracaoEmailDTO();

    configuracaoEmailDTO.setHost(parametroDAO.obterParametroByNome(ParametroConstantes.CONFIG_HOST).getValor());
    configuracaoEmailDTO.setPorta(parametroDAO.obterParametroByNome(ParametroConstantes.CONFIG_PORT).getValor());
    configuracaoEmailDTO.setRemetente(parametroDAO.obterParametroByNome(ParametroConstantes.CONFIG_FROM).getValor());

    return configuracaoEmailDTO;
  }

  private void enviarEmailPorAcao(EmailAcaoUsuarioDTO emailAcaoDTO, ConfiguracaoEmailDTO configuracaoEmailDTO, String assunto,
      String conteudo, List<String> comCopiaList, String motivo) {
    Map<String, String> parametros = new HashMap<String, String>();
    parametros.put(TagEmailConstantes.TAG_DESCRICAO_ACAO, emailAcaoDTO.getDescricaoAcao());
    parametros.put(TagEmailConstantes.TAG_URL_SISTEMA, emailAcaoDTO.getUrlSistema());
    parametros.put(TagEmailConstantes.TAG_EMAIL_DESTINO, emailAcaoDTO.getEmailDestino());
    parametros.put(TagEmailConstantes.TAG_CAIXA_ORIGEM,emailAcaoDTO.getCaixaOrigem());
    parametros.put(TagEmailConstantes.TAG_CAIXA_DESTINO, emailAcaoDTO.getCaixaDestino());
    parametros.put(TagEmailConstantes.TAG_MATRICULA_USUARIO_DEMANDANTE, emailAcaoDTO.getMacriculaUsuarioDemandante());
    parametros.put(TagEmailConstantes.TAG_USUARIO_DEMANDANTE, emailAcaoDTO.getUsuarioDemandante());
    parametros.put(TagEmailConstantes.TAG_NUMERO_DEMANDA, emailAcaoDTO.getNumeroDemanda());
    parametros.put(TagEmailConstantes.TAG_ASSUNTO_ARVORE, emailAcaoDTO.getArvoreAssunto());
    parametros.put(TagEmailConstantes.TAG_TITULO_DEMANDA, emailAcaoDTO.getTituloDemanda());
    parametros.put(TagEmailConstantes.TAG_DATA_LIMITE, emailAcaoDTO.getDataLimite());
    parametros.put(TagEmailConstantes.TAG_PRAZO, emailAcaoDTO.getPrazo());
    parametros.put(TagEmailConstantes.TAG_RESUMO, emailAcaoDTO.getResumo());

    enviarEmail(configuracaoEmailDTO, parametros, assunto, conteudo, comCopiaList, motivo);
  }

  public void enviarEmailExtratoPorCaixa(CaixaPostal caixaPostal, ConfiguracaoEmailDTO configuracaoEmailDTO,
      List<ExtratoDemandasDTO> extratoDemandasList, String assunto, String conteudo, String urlSistema, String motivo, String emailComCopia) {

    List<String> comCopiaList = new ArrayList<>();
    if(caixaPostal.getUnidade().getAbrangencia().getId().equals(1L)) {
      comCopiaList.add(emailComCopia);
    }
    
    Map<String, String> parametros = new HashMap<>();
    parametros.put(TagEmailConstantes.TAG_EMAIL_DESTINO, caixaPostal.getEmail());
    parametros.put(TagEmailConstantes.TAG_CAIXA_DESTINO, caixaPostal.getSigla());
    parametros.put(TagEmailConstantes.TAG_TABELA_DEMANDAS, criarTabelaExtratoPorCaixaExterna(extratoDemandasList, urlSistema));

    enviarEmail(configuracaoEmailDTO, parametros, assunto, conteudo, comCopiaList, motivo);
  }

  public void enviarEmailQuestionamentoDemandante(CaixaPostal caixaPostal, ConfiguracaoEmailDTO configuracaoEmailDTO,
      List<PrazoQuestionamentoDemandasDTO> prazoQuestionamentoDemandasList, String assunto, String conteudo, String motivo, String prazoLimiteAviso, String prazoLimiteCancelamento, String urlSistema) {
    Map<String, String> parametros = new HashMap<String, String>();

    parametros.put(TagEmailConstantes.TAG_EMAIL_DESTINO, caixaPostal.getEmail());
    parametros.put(TagEmailConstantes.TAG_CAIXA_DESTINO, caixaPostal.getSigla());
    parametros.put(TagEmailConstantes.TAG_TABELA_DEMANDAS, criarTabelaQuestionamentoDemandante(prazoQuestionamentoDemandasList, urlSistema));
    
    parametros.put(TagEmailConstantes.TAG_PRAZO_LIMITE_QUESTIONAMENTO_AVISO, prazoLimiteAviso);
    parametros.put(TagEmailConstantes.TAG_PRAZO_LIMITE_QUESTIONAMENTO_CANCELAMENTO, prazoLimiteCancelamento);

    enviarEmail(configuracaoEmailDTO, parametros, assunto, conteudo, null, motivo);
  }

  private String criarTabelaExtratoPorCaixaExterna(List<ExtratoDemandasDTO> extratoDemandasList, String urlSistema) {
    StringBuilder sb = new StringBuilder();

    sb.append(
        "<table class=MsoNormalTable border=0 cellpadding=0 style='mso-cellspacing:1.5pt;                                  ");
    sb.append(
        " mso-yfti-tbllook:160;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>                                                       ");
    sb.append(
        " <thead>                                                                                                          ");
    sb.append(
        "  <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes'>                                                              ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal><o:p>&nbsp;</o:p></p>                                                                       ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Nº Demanda<o:p></o:p></span></b></p>                                                 ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Título<o:p></o:p></span></b></p>                                                     ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Assunto<o:p></o:p></span></b></p>                                                    ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><span                                                ");
    sb.append(
        "   class=SpellE><b><span style='font-family:Arial'>Resp</span></b></span><b><span                                 ");
    sb.append(
        "   style='font-family:Arial'> Atual<o:p></o:p></span></b></p>                                                     ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Prazo CP Atual<o:p></o:p></span></b></p>                                             ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Última Movimentação <o:p></o:p></span></b></p>                                       ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "   <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                      ");
    sb.append(
        "   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                                           ");
    sb.append(
        "   <p class=MsoNormal align=center style='text-align:center'><b><span                                             ");
    sb.append(
        "   style='font-family:Arial'>Dias sem iteração<o:p></o:p></span></b></p>                                          ");
    sb.append(
        "   </td>                                                                                                          ");
    sb.append(
        "  </tr>                                                                                                           ");
    sb.append(
        " </thead>                                                                                                         ");

    for (ExtratoDemandasDTO demanda : extratoDemandasList) {
      sb.append(
          " <tr style='mso-yfti-irow:1;mso-yfti-lastrow:yes'>                                                                ");

      sb.append(
          "  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append("  background:" + demanda.getCor()
          + ";padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append(
          "  <p class=MsoNormal align=center style='text-align:center'><span                                                 ");
      sb.append(
          "  style='font-size:10.0pt;line-height:107%;font-family:Arial'>&nbsp;&nbsp;&nbsp;<o:p></o:p></span></p>            ");
      sb.append(
          "  </td>                                                                                                           ");

      sb.append(
          "  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append(
          "  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append(
          "  <p class=MsoNormal align=center style='text-align:center'><a                                                    ");
      sb.append("  href='" + urlSistema + "' target='_blank'><span                                             ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>" + demanda.getDemanda().getId()
          + "</span></a><span    ");
      sb.append(
          "  style='font-size:10.0pt;line-height:107%;font-family:Arial'><o:p></o:p></span></p>                              ");
      sb.append(
          "  </td>                                                                                                           ");

      sb.append(
          "  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append(
          "  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append(
          "  <p class=MsoNormal style='text-align:justify'><span style='font-size:10.0pt;                                    ");
      sb.append("  line-height:107%;font-family:Arial'>" + demanda.getDemanda().getTitulo()
          + "<o:p></o:p></span></p>                  ");
      sb.append(
          "  </td>                                                                                                           ");

      sb.append(
          "  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append(
          "  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append(
          "  <p class=MsoNormal style='text-align:justify'><span style='font-size:10.0pt;                                    ");
      sb.append("  line-height:107%;font-family:Arial'>" + demanda.getAssuntoCompleto()
          + "<o:p></o:p></span></p>                      ");
      sb.append(
          "  </td>                                                                                                           ");

      sb.append(
          "  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append(
          "  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span      ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"+demanda.getNomeCaixaPostalResponsavel()+"<o:p></o:p></span></p>");
      sb.append("  </td>                                                                                                           ");
      
      sb.append("  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span class=GramE><span                               ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"+demanda.getPrazo()+"</span></span><span           ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'><o:p></o:p></span></p>                              ");
      sb.append("  </td>                                                                                                           ");
      sb.append("  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span                                                 ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"+DateUtil.formatDataPadrao(demanda.getUltimaMovimentacao())+"<o:p></o:p></span></p>");
      sb.append("  </td>                                                                                                           ");
      
      sb.append("  <td style='border:solid #CCCCCC 1.0pt;mso-border-alt:solid #CCCCCC .75pt;                                       ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                                            ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span                                                 ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"+demanda.getDiasSemInteracao()+"<o:p></o:p></span></p>");
      sb.append("  </td>                                                                                                           ");
      
      sb.append(" </tr>                                                                                                            ");
    }
    sb.append("</table> ");

    return sb.toString();
  }

  private String criarTabelaQuestionamentoDemandante(List<PrazoQuestionamentoDemandasDTO> questionamentoDemandasList, String urlSistema) {

    StringBuilder sb = new StringBuilder();

    sb.append("<table class=MsoNormalTable border=0 cellpadding=0 style='mso-cellspacing:1.5pt;                     ");
    sb.append(" mso-yfti-tbllook:160;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>                                          ");
    sb.append(" <thead>                                                                                             ");
    sb.append("  <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes'>                                                 ");

    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Nº Demanda<o:p></o:p></span></b></p>                                    ");
    sb.append("   </td>                                                                                             ");
    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Título<o:p></o:p></span></b></p>                                        ");
    sb.append("   </td>                                                                                             ");
    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Assunto<o:p></o:p></span></b></p>                                       ");
    sb.append("   </td>                                                                                             ");
    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Data Demanda<o:p></o:p></span></b></p>                                  ");
    sb.append("   </td>                                                                                             ");
    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Data última Alteração Histórico<o:p></o:p></span></b></p>               ");
    sb.append("   </td>                                                                                             ");
    sb.append("   <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                   ");
    sb.append("   background:#EEEEEE;padding:.75pt .75pt .75pt .75pt'>                                              ");
    sb.append("   <p class=MsoNormal align=center style='text-align:center'><b><span                                ");
    sb.append("   style='font-family:Arial'>Dias sem iteração<o:p></o:p></span></b></p>                             ");
    sb.append("   </td>                                                                                             ");
    sb.append("  </tr>                                                                                              ");
    sb.append(" </thead>                                                                                            ");

    for (PrazoQuestionamentoDemandasDTO demanda : questionamentoDemandasList) {

      sb.append(" <tr style='mso-yfti-irow:1;mso-yfti-lastrow:yes'>                                                   ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><a                                       ");
      sb.append("  href='"+urlSistema+"'");
      sb.append("  target='_self'><span style='font-size:10.0pt;line-height:107%;font-family:                         ");
      sb.append("  Arial'>" + demanda.getDemanda().getId() + "</span></a><span style='font-size:10.0pt;line-height:107%;  ");
      sb.append("  font-family:Arial'><o:p></o:p></span></p>                                                          ");
      sb.append("  </td>                                                                                              ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal style='text-align:justify'><span style='font-size:10.0pt;                       ");
      sb.append("  line-height:107%;font-family:Arial'>" + demanda.getDemanda().getTitulo() + "<o:p></o:p></span></p>     ");
      sb.append("  </td>                                                                                              ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal style='text-align:justify'><span style='font-size:10.0pt;                       ");
      sb.append("  line-height:107%;font-family:Arial'>" + demanda.getAssuntoCompleto() + "<o:p></o:p></span></p>         ");
      sb.append("  </td>                                                                                              ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span                                    ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"
          + DateUtil.formatDataPadrao(demanda.getDataDemanda()) + "<o:p></o:p></span></p>");
      sb.append("  </td>                                                                                              ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span                                    ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>"
          + DateUtil.formatDataPadrao(demanda.getDataUltimaAlteracao()) + "<o:p></o:p></span></p>       ");
      sb.append("  </td>                                                                                              ");

      sb.append("  <td style='border:solid windowtext 1.0pt;mso-border-alt:solid windowtext .75pt;                    ");
      sb.append("  background:#F2DEDE;padding:.75pt .75pt .75pt .75pt'>                                               ");
      sb.append("  <p class=MsoNormal align=center style='text-align:center'><span class=GramE><span                  ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'>" + demanda.getDiasSemInteracao()
          + "</span></span><span  ");
      sb.append("  style='font-size:10.0pt;line-height:107%;font-family:Arial'><o:p></o:p></span></p>                 ");
      sb.append("  </td>                                                                                              ");

      sb.append(" </tr>                                                                                               ");
    }

    sb.append("</table>                                                                                             ");

    return sb.toString();
  }

}
