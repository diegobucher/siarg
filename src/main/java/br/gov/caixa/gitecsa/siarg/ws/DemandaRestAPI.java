package br.gov.caixa.gitecsa.siarg.ws;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ejb.ConcurrentAccessException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.siarg.service.DemandaRestService;
import br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException;
import br.gov.caixa.gitecsa.siarg.ws.exception.RequiredParamException;
import br.gov.caixa.gitecsa.siarg.ws.model.DemandaAbertaDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;
import br.gov.caixa.gitecsa.siarg.ws.model.ResponseJson;

@Path("/demanda")
@DenyAll
public class DemandaRestAPI {

  @Inject
  private Logger logger;

  @Inject
  private DemandaRestService demandaService;

  @GET
  @Path("/listar")
  @PermitAll
  @Produces("application/json")
  public Response listarDemandas(
      @QueryParam("data_inicial") String dataInicial, @QueryParam("data_final") String dataFinal,
      @QueryParam("aberta") String aberta, @QueryParam("reaberta") String reaberta, @QueryParam("externa") String externa,
      @QueryParam("consulta") String consulta, @QueryParam("conteudo") String conteudo,
      
      @QueryParam("co_assunto") String coAssunto,
      @QueryParam("cp_demandante") String cpDemandante,
      @QueryParam("cp_demandada") String cpDemandada,
      
      @QueryParam("cp_responsavel_assunto") String cpResponsavelAssunto,
      @QueryParam("cp_responsavel_atual") String cpResponsavelAtual,
      @QueryParam("co_unidade_demandante") String coUnidadeDemandante,
      @QueryParam("co_unidade_demandada") String coUnidadeDemandada,
      @QueryParam("co_unidade_responsavel_assunto") String coUnidadeRespAssunto,
      
      @QueryParam("co_unidade_responsavel_atual") String coUnidadeRespAtual,
      @QueryParam("nu_contrato") String nuContrato,
      @QueryParam("prazo_demanda") String prazoDemanda,
      @QueryParam("prazo_responsavel_atual") String prazoResponsavel,
      @QueryParam("tipo_iteracao") String tipoIteracao
      ) {

    ObjectMapper objectMapper = new ObjectMapper();

    try {

      validarCamposObrigatorios(dataInicial, aberta, reaberta, externa, consulta, conteudo);

      FiltrosConsultaDemandas filtro = parsearParametrosObrigatorios(dataInicial, dataFinal, aberta, reaberta, externa, consulta, conteudo);
      filtro.setIdAbrangencia(1l);
      
      parsearParametrosOpcionais(coAssunto, cpDemandante, cpDemandada, cpResponsavelAssunto, cpResponsavelAtual,
          coUnidadeDemandante, coUnidadeDemandada, coUnidadeRespAssunto, coUnidadeRespAtual, nuContrato, prazoDemanda,
          prazoResponsavel, tipoIteracao, filtro);

      List<DemandaAbertaDTO> demandaList =
          demandaService.obterDemandasWSConsulta(filtro);

      return Response.status(Status.OK).entity(objectMapper.writeValueAsString(demandaList)).build();
    } catch (
        ParseParamException
        | RequiredParamException e) {
      try {
        return Response.status(Status.BAD_REQUEST)
            .entity(objectMapper.writeValueAsString(new ResponseJson(Status.BAD_REQUEST.getStatusCode(), e.getMessage())))
            .build();
      } catch (IOException e1) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
      }
    } catch (ConcurrentAccessException e) {
      logger.info(ExceptionUtils.getStackTrace(e));
      try {
        return Response.status(Status.BAD_REQUEST)
            .entity(objectMapper.writeValueAsString(new ResponseJson(Status.BAD_REQUEST.getStatusCode(), "Atenção! A consulta não foi executada em razão de outra consulta estar em andamento!")))
            .build();
      } catch (IOException e1) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
      }
    } catch (Exception e) {
      logger.info(ExceptionUtils.getStackTrace(e));
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  private void parsearParametrosOpcionais(String coAssunto, String cpDemandante, String cpDemandada, String cpResponsavelAssunto,
      String cpResponsavelAtual, String coUnidadeDemandante, String coUnidadeDemandada, String coUnidadeRespAssunto,
      String coUnidadeRespAtual, String nuContrato, String prazoDemanda, String prazoResponsavel, String tipoIteracao,
      FiltrosConsultaDemandas filtro)  throws br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException {
    
    try {
      if(StringUtils.isNotBlank(coAssunto)) {
        filtro.setCoAssunto(Long.parseLong(coAssunto));
      }
      filtro.setCpDemandante(cpDemandante);
      filtro.setCpDemandada(cpDemandada);
      
      filtro.setCpResponsavelAssunto(cpResponsavelAssunto);
      filtro.setCpResponsavelAtual(cpResponsavelAtual);
      if(StringUtils.isNotBlank(coUnidadeDemandante)) {
        filtro.setCoUnidadeDemandante(Integer.parseInt(coUnidadeDemandante));
      }
      if(StringUtils.isNotBlank(coUnidadeDemandada)) {
        filtro.setCoUnidadeDemandada(Integer.parseInt(coUnidadeDemandada));
      }
      if(StringUtils.isNotBlank(coUnidadeRespAssunto)) {
        filtro.setCoUnidadeRespAssunto(Integer.parseInt(coUnidadeRespAssunto));
      }
      if(StringUtils.isNotBlank(coUnidadeRespAtual)) {
        filtro.setCoUnidadeRespAtual(Integer.parseInt(coUnidadeRespAtual));
      }
      if(StringUtils.isNotBlank(nuContrato)) {
        filtro.setNuContrato(nuContrato);
      }
      if(StringUtils.isNotBlank(prazoDemanda)) {
        filtro.setPrazoDemanda(Integer.parseInt(prazoDemanda));
      }
      if(StringUtils.isNotBlank(prazoResponsavel)) {
        filtro.setPrazoResponsavel(Integer.parseInt(prazoResponsavel));
      }
      if(StringUtils.isNotBlank(tipoIteracao)) {
        filtro.setTipoIteracao(Integer.parseInt(tipoIteracao));
      }
    } catch (NumberFormatException e) {
      throw new ParseParamException("Parâmetro inválido!");
    }
    
  }

  private FiltrosConsultaDemandas parsearParametrosObrigatorios(String dataInicial, String dataFinal, String aberta,
      String reaberta, String externa, String consulta, String conteudo) 
          throws br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException {

    List<String> valoresPossiveis = Arrays.asList("0","1","2");
    FiltrosConsultaDemandas filtro = new FiltrosConsultaDemandas();
    try {
      filtro.setDataInicial(DateUtil.parseDateThrowsException(dataInicial, DateUtil.FORMATO_DATA));
    } catch (ParseException e) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: data_inicial");
    }
    if (StringUtils.isNotBlank(dataFinal)) {
      try {
        filtro.setDataFinal(DateUtil.parseDateThrowsException(dataFinal, DateUtil.FORMATO_DATA));
      } catch (ParseException e) {
        throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: data_final");
      }
    } else {
      filtro.setDataFinal(new Date());
    }
    
    if(!valoresPossiveis.contains(aberta)) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: aberta");
    }
    if(!valoresPossiveis.contains(reaberta)) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: reaberta");
    }
    if(!valoresPossiveis.contains(externa)) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: externa");
    }
    if(!valoresPossiveis.contains(consulta)) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: consulta");
    }
    if(!conteudo.equals("0") && !conteudo.equals("1") ) {
      throw new br.gov.caixa.gitecsa.siarg.ws.exception.ParseParamException("Parametro inválido: conteudo");
    }
    filtro.setConteudo(Integer.parseInt(conteudo));

    filtro.setAberta(Integer.parseInt(aberta));
    filtro.setReaberta(Integer.parseInt(reaberta));
    filtro.setExterna(Integer.parseInt(externa));
    filtro.setTipoConsulta(Integer.parseInt(consulta));

    return filtro;
  }

  private void validarCamposObrigatorios(String dataInicial, String aberta, String reaberta, String externa,
      String consulta, String conteudo) throws RequiredParamException {

    if (StringUtils.isBlank(dataInicial)) {
      throw new RequiredParamException("Parametro obrigatório: data_inicial");
    }
    if (StringUtils.isBlank(aberta)) {
      throw new RequiredParamException("Parametro obrigatório: aberta");
    }
    if (StringUtils.isBlank(reaberta)) {
      throw new RequiredParamException("Parametro obrigatório: reaberta");
    }
    if (StringUtils.isBlank(externa)) {
      throw new RequiredParamException("Parametro obrigatório: externa");
    }
    if (StringUtils.isBlank(consulta)) {
      throw new RequiredParamException("Parametro obrigatório: consulta");
    }
    if (StringUtils.isBlank(conteudo)) {
      throw new RequiredParamException("Parametro obrigatório: conteudo");
    }
  }

}
