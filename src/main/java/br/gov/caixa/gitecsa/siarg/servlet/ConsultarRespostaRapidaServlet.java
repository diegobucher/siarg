package br.gov.caixa.gitecsa.siarg.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;

import com.google.gson.Gson;

import br.gov.caixa.gitecsa.siarg.service.AtendimentoService;

@WebServlet("/ConsultarRespostaRapidaServlet")
public class ConsultarRespostaRapidaServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  @EJB
  private AtendimentoService atendimentoService;

  public ConsultarRespostaRapidaServlet() {
    super();
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    Gson gson = new Gson();
    
    // Request
    String textoPesquisado = request.getParameter("phrase");
    String assunto = request.getParameter("idAssunto");

    List<RespostaRapidaDTO> listaRespostas = new ArrayList<>();
    if(textoPesquisado != null && textoPesquisado.length() > 4) {
      
      String htmlEscape;
      try {
        htmlEscape =  StringEscapeUtils.escapeHtml4(textoPesquisado);
      } catch (Exception e) {
        htmlEscape = textoPesquisado;
      }
      Set<String> ultimosAtendimentos =  new HashSet<String>(atendimentoService.obterUltimasRespostasPorAssuntoCaixa(Long.parseLong(assunto), htmlEscape));
      for (String atendimento : ultimosAtendimentos) {
        listaRespostas.add(new RespostaRapidaDTO(atendimento));
      }
    }
    
    // Response
    response.setContentType("application/json");
    response.getWriter().write(gson.toJson(listaRespostas));
  }
}

class RespostaRapidaDTO {

  private String abreviacao;
  
  private String resposta;
  
  public RespostaRapidaDTO(String resposta) {
    
    String textoParseado = Jsoup.parse(resposta).text();

    this.resposta = resposta;
    this.abreviacao = textoParseado;
    if(textoParseado.length() > 200) {
      this.abreviacao = textoParseado.substring(0, 200);
    } 
  }

  public String getAbreviacao() {
    return abreviacao;
  }

  public void setAbreviacao(String abreviacao) {
    this.abreviacao = abreviacao;
  }

  public String getResposta() {
    return resposta;
  }

  public void setResposta(String resposta) {
    this.resposta = resposta;
  }
}
