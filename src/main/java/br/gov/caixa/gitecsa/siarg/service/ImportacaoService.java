package br.gov.caixa.gitecsa.siarg.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessRollbackException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dao.FluxoAssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.FluxoDemandaDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoDemandaDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.exporter.ExportarMigracaoAssuntoXLS;
import br.gov.caixa.gitecsa.siarg.exporter.ExportarMigracaoDemandaXLS;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

@Stateless
public class ImportacaoService {

  private static final String ABRANGENCIA = "abrangencia";

  private static final String NAO = "NÃO";

  private static final String SIM = "SIM";

  private static final String EXTERNA = "@externa";

  private static final Integer PRAZO_INVALIDO = -9;
  
  private static final String EOL = "\r\n";

  @Inject
  private AssuntoDAO assuntoDao;

  @Inject
  private CaixaPostalDAO caixaPostalDao;

  @Inject
  private UnidadeDAO unidadeDao;

  @Inject
  private DemandaDAO demandaDao;

  @Inject
  private FluxoDemandaDAO fluxoDemandaDao;

  @Inject
  private FluxoAssuntoDAO fluxoAssuntoDAO;
  
  @Inject
  private AtendimentoDAO atendimentoDAO;
  
  public static final String SEPARADOR_DADOS_PLANILHA = ">";


  /** Construtor Padrão. */
  public ImportacaoService() {
    super();
  }

  public Integer importarAssuntos(InputStream inputStream) throws BusinessRollbackException {

    List<ExportacaoAssuntoDTO> listRegistros;

    try {

      List<String> erros = new ArrayList<String>();
      Map<Long, Assunto> mapAssuntos = new HashMap<Long, Assunto>();
      Map<String, Unidade> mapUnidades = new HashMap<String, Unidade>();
      Map<String, CaixaPostal> mapCaixasPostais = new HashMap<String, CaixaPostal>();
      Map<Long, ExportacaoAssuntoDTO> mapQualificados = new HashMap<Long, ExportacaoAssuntoDTO>();

      // -- Abrangência
      Abrangencia abrangencia = (Abrangencia) JSFUtil.getSessionMapValue(ABRANGENCIA);

      // -- Põe todos os assuntos ativos em memória
      List<Assunto> listAssunto = this.assuntoDao.findAllByAbrangencia(abrangencia);
      for (Assunto assunto : listAssunto) {
        mapAssuntos.put(assunto.getId(), assunto);
      }

      // -- Põe todas as unidades em memória
      List<Unidade> listUnidade =
          this.unidadeDao.obterUnidadesAtivas(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);

      for (Unidade unidade : listUnidade) {
        mapUnidades.put(unidade.getSigla().trim().toUpperCase(), unidade);
      }

      List<CaixaPostal> listCaixaPostal =
          this.caixaPostalDao.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.FILIAL, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.ARROBA_EXTERNA);
      for (CaixaPostal caixaPostal : listCaixaPostal) {
        mapCaixasPostais.put(caixaPostal.getSigla().toUpperCase(), caixaPostal);
      }

      listRegistros = this.extrairDadosPlanilhaAssunto(inputStream);

      int numLinha = 1;

      for (ExportacaoAssuntoDTO registro : listRegistros) {
        try {
          this.validarAssunto(registro, numLinha, mapQualificados, mapAssuntos, mapCaixasPostais, mapUnidades);
          mapQualificados.put(registro.getNumeroAssunto(), registro);
        } catch (BusinessException e) {
          erros.add(e.getMessage());
        }

        numLinha++;
      }

      if (!erros.isEmpty()) {
        throw new BusinessRollbackException(erros, gerarMensagemLogErro(erros, listRegistros.size()));
      } else {
        if( listRegistros.size() > 0) {
          efetuarMigracaoAssuntos(mapQualificados, listRegistros, mapAssuntos, mapCaixasPostais, mapUnidades);
        }
        return listRegistros.size();
      }

      
    } catch (IOException e) {
      throw new BusinessRollbackException(e.getMessage());
    }
  }
  
  public String gerarMensagemLogErro(List<String> errorList, Integer qtdRegistrosNoArquivo) {
    
    StringBuilder sb = new StringBuilder();

    sb.append("Foi(ram) encontrado(s) "+errorList.size()+" inconsistência(s) no arquivo, conforme abaixo: "+EOL+EOL);

    for (String error : errorList) {
      sb.append(error);
      sb.append(EOL);
    }
    sb.append(EOL);
    sb.append("Foi(ram) identificado(s) "+qtdRegistrosNoArquivo+" registro(s) no arquivo.");

    return sb.toString();
  }

  private void efetuarMigracaoDemandas(Map<Long, ExportacaoDemandaDTO> mapDemandasValido, List<ExportacaoDemandaDTO> exportacaoDemandaList,
      Map<Long, Demanda> mapDemanda, Map<String, CaixaPostal> mapCaixasPostais, String matriculaUsuarioLogado, String nomeUsuarioLogado) throws BusinessRollbackException {
    
    // Percorre a lista que está com a ordenação de importação
    for (ExportacaoDemandaDTO exportacaoDemandaDTO : exportacaoDemandaList) {

      // Pega o objeto do map de demandas validos
      ExportacaoDemandaDTO dto = mapDemandasValido.get(exportacaoDemandaDTO.getNumeroDemanda());

      Demanda demandaNaBase = mapDemanda.get(dto.getNumeroDemanda());
      
      /*
       * Inicio do Fluxo demanda
       */
      List<FluxoDemanda> fluxoDemandaList = demandaNaBase.getFluxosDemandasList();
      
      String [] fluxoTokens = dto.getFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      String [] prazoTokens = dto.getPrazoFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);

      List<String> fluxoDemandaPlanilhaList = new ArrayList<String>();
      List<String> prazoFluxoDemandaPlanilhaList =  new ArrayList<String>();
      
      for (String string : fluxoTokens) {
        fluxoDemandaPlanilhaList.add(string.trim());
      }
      
      for (String string : prazoTokens) {
        prazoFluxoDemandaPlanilhaList.add(string.trim());
      }
      
      //Guardando o BKP do FluxosDemanda
      List<FluxoDemanda> fluxoDemandaListBKP = new ArrayList<FluxoDemanda>();
      for (FluxoDemanda fluxo : fluxoDemandaList) {
        if(fluxo.isAtivo() && !fluxo.getOrdem().equals(0)) {
          fluxoDemandaListBKP.add(fluxo);
        }
      }
      //Ordenando o fluxo BKP antes das alterações
      Collections.sort(fluxoDemandaListBKP);
      
      // Desativar Fluxos Ativos que estão na Base e não estão na Planilha
      //Ignora o Fluxo Ordem 0 
      for (FluxoDemanda fluxo : fluxoDemandaList) {
        if(fluxo.isAtivo() && !fluxo.getOrdem().equals(0) &&
            !fluxoDemandaPlanilhaList.contains(fluxo.getCaixaPostal().getSigla().trim())) {
          fluxo.setAtivo(false);
        }
      }
      
      // Verifica o que tem na Planilha e não tem na Base.
      for (int i = 0; i < fluxoDemandaPlanilhaList.size(); i++) {
        String caixa = fluxoDemandaPlanilhaList.get(i).trim();
        
        List<FluxoDemanda> fluxosList = demandaNaBase.getFluxosDemandasList();
        
        //Guarda o tipoFluxo atual para ser utilizado para os novos fluxos demanda
        TipoFluxoEnum tipoFluxoDemandaUtilizado = null;
        
        boolean fluxoCaixaPostalAtivaNaBase = false;
        for (FluxoDemanda fluxoDemanda : fluxosList) {
          tipoFluxoDemandaUtilizado = fluxoDemanda.getTipoFluxoDemanda();
          
          if(fluxoDemanda.isAtivo() && fluxoDemanda.getCaixaPostal().getSigla().equals(caixa)) {
            fluxoCaixaPostalAtivaNaBase = true;
            break;
          }
        }
        
        // ADICIONAR OS NOVOS FLUXOS na Demanda
        if(!fluxoCaixaPostalAtivaNaBase) {
          FluxoDemanda novoFluxoDemanda = new FluxoDemanda();
          novoFluxoDemanda.setAtivo(true);
          novoFluxoDemanda.setDemanda(demandaNaBase);
          novoFluxoDemanda.setTipoFluxoDemanda(tipoFluxoDemandaUtilizado);
          novoFluxoDemanda.setCaixaPostal(mapCaixasPostais.get(caixa));
          novoFluxoDemanda.setPrazo(Integer.parseInt(prazoFluxoDemandaPlanilhaList.get(i)));
          demandaNaBase.getFluxosDemandasList().add(novoFluxoDemanda); // VERIFICAR JPA
        }
      }
      
     // REORDENAR AS ORDENS DO FLUXO DEMANDA ATIVOS PELA ORDEM DA PLANILHA
     for (int i = 0; i < fluxoDemandaPlanilhaList.size(); i++) {
       
       String caixa = fluxoDemandaPlanilhaList.get(i).trim();
       Integer prazo = Integer.parseInt(prazoFluxoDemandaPlanilhaList.get(i).trim());
       Integer ordem = i+1;
       
       // Procura o FluxoDemanda relacionado e seta o prazo e ordem
       for (FluxoDemanda fluxo : fluxoDemandaList) {
         if(fluxo.isAtivo() && fluxo.getCaixaPostal().getSigla().equals(caixa)) {
           fluxo.setPrazo(prazo);
           fluxo.setOrdem(ordem);
           if(fluxo.getId() == null) {
             fluxoDemandaDao.save(fluxo);
           }

         }
       }
     }

     /*
      * Final do fluxo Demanda
      */
        
     List<Atendimento> atendimentoList = demandaNaBase.getAtendimentosList();
     
     Atendimento ultimoAtendimento = null;
     
     if(atendimentoList != null && !atendimentoList.isEmpty()) {
       Collections.sort(atendimentoList);
       Collections.reverse(atendimentoList);
       ultimoAtendimento = atendimentoList.iterator().next();
     }
     
     String descricaoMigracao = "Migração de Caixa Postal envolvida";
     
     // Caso exista mudança de Demandante
     if(!dto.getCaixaPostalDemandante().trim().equals(demandaNaBase.getCaixaPostalDemandante().getSigla())) {
       //ALTERANDO O Demandante
       demandaNaBase.setCaixaPostalDemandante(mapCaixasPostais.get(dto.getCaixaPostalDemandante().trim()));
     }
     
     boolean houveMudancaDeResponsavel = false;
     
     //A DEMANDA ESTÁ NA CAIXA EXTERNA?
     if(demandaNaBase.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA) ||
         demandaNaBase.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA) ) {
       
       //TEM QUE TER 2 CAIXAS QUANDO EXISTE @EXTERNA
       String penultimaCaixaPlanilha = fluxoDemandaPlanilhaList.get(fluxoDemandaPlanilhaList.size()-2);
       
       //E ESTAR NA PENULTIMA CAIXA
       //SE O (RESPONSAVEL ATUAL PLANILHA) NÃO É O (PENULTIMO FLUXO DEMANDA DA PLANILHA), ENTAO HOUVE MUDANCA
       if(!(dto.getResponsavelAtual().trim().equals(penultimaCaixaPlanilha))) {
         houveMudancaDeResponsavel = true;
       } else {
         
         //PONTO DE ALTERACAO "demandaNaBase" está sendo atualizado pelo codigo anterior.
//         FluxoDemanda penultimoFluxoBase = demandaNaBase.getPenultimoFluxoDemanda();
         FluxoDemanda penultimoFluxoBase = null;
         
         for (int i = 0; i < fluxoDemandaListBKP.size(); i++) {
          if(i == fluxoDemandaListBKP.size()-2) {
            penultimoFluxoBase = fluxoDemandaListBKP.get(i);
          }
         }
         //FIM PEGANDO O PENULTIMO FLUXO DEMANDA
         
         //Compara o penultimo fluxo da base com o responsável atual da planilha
         if(!dto.getResponsavelAtual().trim().equals(penultimoFluxoBase.getCaixaPostal().getSigla())) {
           
           FluxoDemanda novoFluxoDemandaResponsavelAtual = null;
           for (FluxoDemanda fluxoDem : demandaNaBase.getFluxosDemandasList()) {
             //Faz a referencia do fluxo ao novo fluxo demanda
             if(fluxoDem.getCaixaPostal().getSigla().equals(dto.getResponsavelAtual().trim())
                 && fluxoDem.isAtivo()) {
               novoFluxoDemandaResponsavelAtual = fluxoDem;
               break;
             }
           }
           
           //ATUALIZA ATENDIMENTO ANTIGO
           ultimoAtendimento.setAcaoEnum(AcaoAtendimentoEnum.MIGRAR);
           ultimoAtendimento.setDescricao(descricaoMigracao);
           ultimoAtendimento.setMatricula(matriculaUsuarioLogado);
           ultimoAtendimento.setNomeUsuarioAtendimento(nomeUsuarioLogado);
           ultimoAtendimento.setDataHoraAtendimento(new Date());
           
           //Cria atendimento novo 
           Atendimento novoAtendimento = new Atendimento();
           novoAtendimento.setDemanda(demandaNaBase);
           novoAtendimento.setDataHoraRecebimento(new Date());
           novoAtendimento.setFluxoDemanda(novoFluxoDemandaResponsavelAtual);
           demandaNaBase.getAtendimentosList().add(novoAtendimento); // Chamar JPA
           atendimentoDAO.save(novoAtendimento);
         }
         
       }
       
     } else {
       if(!dto.getResponsavelAtual().trim().equals(demandaNaBase.getCaixaPostalResponsavel().getSigla())) {
         houveMudancaDeResponsavel = true;
       }
     }
     
     
     // CASO EXISTA MUDANCA DE RESPONSAVEL ATUAL
     if(houveMudancaDeResponsavel) {
       
       //ALTERANDO O RESPONSAVEL ATUAL
       demandaNaBase.setCaixaPostalResponsavel(mapCaixasPostais.get(dto.getResponsavelAtual().trim()));
       
       FluxoDemanda novoFluxoDemandaResponsavelAtual = null;
       for (FluxoDemanda fluxoDem : demandaNaBase.getFluxosDemandasList()) {
         if(fluxoDem.getCaixaPostal().getSigla().equals(dto.getResponsavelAtual().trim())) {
           novoFluxoDemandaResponsavelAtual = fluxoDem;
           break;
         }
       }
       
       //TESTAR SE O RESPONSAVEL ATUAL É O DEMANDANTE
       if(demandaNaBase.getCaixaPostalResponsavel().getId().equals(demandaNaBase.getCaixaPostalDemandante().getId())) {
         
         if(demandaNaBase.getSituacao().equals(SituacaoEnum.FECHADA) || demandaNaBase.getSituacao().equals(SituacaoEnum.CANCELADA)) {
           
           //CRIAR ATENDIMENTO
           Atendimento novoAtendimento = new Atendimento();
           novoAtendimento.setDemanda(demandaNaBase);
           novoAtendimento.setAcaoEnum(AcaoAtendimentoEnum.MIGRAR);
           novoAtendimento.setDescricao(descricaoMigracao);
           novoAtendimento.setMatricula(matriculaUsuarioLogado);
           novoAtendimento.setNomeUsuarioAtendimento(nomeUsuarioLogado);
           novoAtendimento.setDataHoraAtendimento(new Date());

           novoAtendimento.setAnexoAtendimento(ultimoAtendimento.getAnexoAtendimento());
           novoAtendimento.setDataHoraRecebimento(ultimoAtendimento.getDataHoraRecebimento());
           novoAtendimento.setFluxoDemanda(ultimoAtendimento.getFluxoDemanda());
           novoAtendimento.setMotivoReabertura(ultimoAtendimento.getMotivoReabertura());
           novoAtendimento.setUnidadeExterna(ultimoAtendimento.getUnidadeExterna());
           
           demandaNaBase.getAtendimentosList().add(novoAtendimento);
           atendimentoDAO.save(novoAtendimento);

         }
         
       } else {
         
         if(novoFluxoDemandaResponsavelAtual == null) {
           throw new BusinessRollbackException("Erro ao encontrar o fluxo demanda");
         }
         
         //ATUALIZA ATENDIMENTO ANTIGO
         ultimoAtendimento.setAcaoEnum(AcaoAtendimentoEnum.MIGRAR);
         ultimoAtendimento.setDescricao(descricaoMigracao);
         ultimoAtendimento.setMatricula(matriculaUsuarioLogado);
         ultimoAtendimento.setNomeUsuarioAtendimento(nomeUsuarioLogado);
         ultimoAtendimento.setDataHoraAtendimento(new Date());
         
         //Cria atendimento novo 
         Atendimento novoAtendimento = new Atendimento();
         novoAtendimento.setDemanda(demandaNaBase);
         novoAtendimento.setDataHoraRecebimento(new Date());
         novoAtendimento.setFluxoDemanda(novoFluxoDemandaResponsavelAtual);
         demandaNaBase.getAtendimentosList().add(novoAtendimento); // Chamar JPA
         atendimentoDAO.save(novoAtendimento);
       }
       
     }
     
     //Tratar Observadores
     
     // Observadores
     // Limpa a lista atual e preenche com a lista nova
     demandaNaBase.getCaixasPostaisObservadorList().clear();

     List<CaixaPostal> caixaPostalObservadoresList = new ArrayList<CaixaPostal>();

     if (StringUtils.isNotBlank(dto.getObservadoresDemanda())) {
       List<String> listaSiglas =
           Arrays.asList(dto.getObservadoresDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA));

       for (String sigla : listaSiglas) {
         CaixaPostal observador = mapCaixasPostais.get(sigla.trim());
         if (observador == null) {
           throw new BusinessRollbackException("Erro! Caixa Postal Inexistente: " + sigla);
         }
         caixaPostalObservadoresList.add(observador);
       }

     }

     demandaNaBase.setCaixasPostaisObservadorList(caixaPostalObservadoresList);
     
    } //Fim do loop de registros
    
    
  }
  
  /**
   * Realiza a migração
   * 
   * @param mapAssuntosValidosImportacao
   *          - Map com os registros validos
   * @param exportacaoAssuntoDTOList
   *          - List com a ordenação da importação
   * @param mapAssuntosAtivosBase
   *          - Map com os assuntos ativos na base
   * @param mapCaixasPostais
   * @throws BusinessException
   * @throws BusinessRollbackException
   */
  private void efetuarMigracaoAssuntos(Map<Long, ExportacaoAssuntoDTO> mapAssuntosValidosImportacao,
      List<ExportacaoAssuntoDTO> exportacaoAssuntoDTOList, Map<Long, Assunto> mapAssuntosAtivosBase,
      Map<String, CaixaPostal> mapCaixasPostais, Map<String, Unidade> mapUnidades) throws BusinessRollbackException {

    List<String> errosEncontrados = new ArrayList<String>();

    // Contador para registrar os erros as linhas
    Integer contadorLinha = 1;

    // Percorre a lista que está com a ordenação de importação
    for (ExportacaoAssuntoDTO exportacaoAssuntoDTO : exportacaoAssuntoDTOList) {

      // Pega o objeto do map de assuntos validos
      ExportacaoAssuntoDTO dto = mapAssuntosValidosImportacao.get(exportacaoAssuntoDTO.getNumeroAssunto());

      Assunto assuntoNaBase = mapAssuntosAtivosBase.get(exportacaoAssuntoDTO.getNumeroAssunto());

      // # Colunas que não alteram #
      // Numero Assunto
      // Arvore de Assunto Atual
      // Categoria 1

      try {

        validarCategoria2(contadorLinha, exportacaoAssuntoDTO, assuntoNaBase);
        validarCategoria3(contadorLinha, exportacaoAssuntoDTO, assuntoNaBase);

        // Nome Assunto
        assuntoNaBase.setNome(dto.getNomeAssunto());

        // Fluxo Outros Demandantes
        // Prazo dias assunto
        assuntoNaBase.setPrazo(dto.getPrazoResponsavel());

        // Caixa Postal Responsável
        if (!assuntoNaBase.getCaixaPostal().getSigla().equals(dto.getResponsavel())) {
          CaixaPostal novaCaixaResponsavel = mapCaixasPostais.get(dto.getResponsavel().trim());
          if (novaCaixaResponsavel == null) {
            throw new BusinessRollbackException("Erro! Caixa Postal Inexistente: " + dto.getResponsavel());
          }
          assuntoNaBase.setCaixaPostal(novaCaixaResponsavel);

          // BUSCAR E ATUALIZAR a CAIXA DO FLUXO DE TIPO 3
          List<FluxoAssunto> fluxos = assuntoNaBase.getFluxosAssuntosList();
          for (FluxoAssunto fluxoAssunto : fluxos) {
            if (fluxoAssunto.getTipoFluxo().equals(TipoFluxoEnum.OUTROS_DEMANDANTES)) {
              fluxoAssunto.setCaixaPostal(novaCaixaResponsavel);
            }
          }

        }

        // Fluxos Demandantes Pré-Definidos
        // TODO FLUXO ASSUNTO
        Iterator<FluxoAssunto> itFluxos = assuntoNaBase.getFluxosAssuntosList().iterator();

        // Remove todos os fluxos do tipo demandante responsavel
        while (itFluxos.hasNext()) {
          FluxoAssunto fluxo = itFluxos.next();
          if (fluxo.getTipoFluxo().equals(TipoFluxoEnum.DEMANDANTE_DEFINIDO)) {
            fluxo.setAssunto(null);
            itFluxos.remove();

            fluxoAssuntoDAO.delete(fluxo);
          }
        }

        if (StringUtils.isNotBlank(dto.getFluxoAssunto())) {
          // Adicionar os Novos
          List<String> listaSiglasCaixaFluxoAssuntos =
              Arrays.asList(dto.getFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA));
          List<String> listaPrazosFluxoAssuntos =
              Arrays.asList(dto.getPrazoFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA));

          if (listaSiglasCaixaFluxoAssuntos.size() != listaPrazosFluxoAssuntos.size()) {
            throw new BusinessRollbackException("Quantidade de Caixas diferente de quantidade de prazos");
          }

          for (int i = 0; i < listaSiglasCaixaFluxoAssuntos.size(); i++) {
            String siglaCaixa = listaSiglasCaixaFluxoAssuntos.get(i).trim();
            String prazo = listaPrazosFluxoAssuntos.get(i).trim();

            CaixaPostal caixaFluxo = mapCaixasPostais.get(siglaCaixa.trim());

            FluxoAssunto fluxoAssunto = new FluxoAssunto();
            fluxoAssunto.setAssunto(assuntoNaBase);
            fluxoAssunto.setCaixaPostal(caixaFluxo);
            fluxoAssunto.setOrdem(i + 1);
            fluxoAssunto.setPrazo(Integer.parseInt(prazo.trim()));
            fluxoAssunto.setTipoFluxo(TipoFluxoEnum.DEMANDANTE_DEFINIDO);

            assuntoNaBase.getFluxosAssuntosList().add(fluxoAssunto);
          }

        }
        // TODO PRAZO FLUXO ASSUNTO

        // Demandantes Pré-Definidos
        assuntoNaBase.getAssuntoUnidadeDemandanteList().clear();

        List<Unidade> unidadeDemandantesList = new ArrayList<Unidade>();

        if (StringUtils.isNotBlank(dto.getDemandantesPreDefinidos())) {
          List<String> listaSiglas =
              Arrays.asList(dto.getDemandantesPreDefinidos().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA));

          for (String sigla : listaSiglas) {
            if (StringUtils.isNotBlank(sigla)) {
              Unidade unidade = mapUnidades.get(sigla.trim());
              if (unidade == null) {
                throw new BusinessRollbackException("Erro! Unidade Inexistente: " + sigla);
              }
              unidadeDemandantesList.add(unidade);
            }
          }

        }
        assuntoNaBase.setAssuntoUnidadeDemandanteList(unidadeDemandantesList);

        // Observadores
        // Limpa a lista atual e preenche com a lista nova
        assuntoNaBase.getCaixasPostaisObservadorList().clear();

        List<CaixaPostal> caixaPostalObservadoresList = new ArrayList<CaixaPostal>();

        if (StringUtils.isNotBlank(dto.getObservadoresAssunto())) {
          List<String> listaSiglas =
              Arrays.asList(dto.getObservadoresAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA));

          for (String sigla : listaSiglas) {
            CaixaPostal observador = mapCaixasPostais.get(sigla.trim());
            if (observador == null) {
              throw new BusinessRollbackException("Erro! Caixa Postal Inexistente: " + sigla);
            }
            caixaPostalObservadoresList.add(observador);
          }

        }

        assuntoNaBase.setCaixasPostaisObservadorList(caixaPostalObservadoresList);

        // Ativo
        // FA 014 e FA 015

        boolean ativoNaPlanilha = false;
        
        if (StringUtils.isBlank(dto.getAtivo()) || dto.getAtivo().equals("SIM")) {
          ativoNaPlanilha = true;
//          assuntoNaBase.setAtivo(true);
        } else if (dto.getAtivo().equals("NÃO")) {
          ativoNaPlanilha = false;
//          assuntoNaBase.setAtivo(false);
        }
        
        if(ativoNaPlanilha == false) {
          assuntoNaBase.setAtivo(ativoNaPlanilha);
          inativarAtivarCategoriasPaiRecursivamenteSemAuditoria(assuntoNaBase);
        }

      } catch (BusinessException e) {
        errosEncontrados.addAll(e.getErroList());
      }

    } // Final do Loop

    if (!errosEncontrados.isEmpty()) {
      throw new BusinessRollbackException(errosEncontrados,  gerarMensagemLogErro(errosEncontrados, exportacaoAssuntoDTOList.size()));
    }


  }
  
  public void inativarAtivarCategoriasPaiRecursivamenteSemAuditoria(Assunto assunto) throws BusinessRollbackException{

    // Pega o assunto atualizado com Fetch
    Assunto assuntoFetch;
    try {
      assuntoFetch = assuntoDao.findByIdEager(assunto.getId());
    } catch (DataBaseException e) {
      throw new BusinessRollbackException(e.toString());
    }

    // Se tem pai faz as checagens
    Assunto assuntoPai =  assuntoFetch.getAssuntoPai();
    
    if (assuntoPai != null) {

      // Busca os filhos do pai
      List<Assunto> assuntosFilhoList = assuntoDao.findAssuntosByPai(assuntoPai);

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

      assuntoDao.update(assuntoPai);

      inativarAtivarCategoriasPaiRecursivamenteSemAuditoria(assuntoPai);
    }

  }

  private void validarCategoria2(Integer contador, ExportacaoAssuntoDTO exportacaoAssuntoDTO,
      Assunto assuntoNaBase) throws BusinessException {
    
    OperacaoPlanilha operacaoPlanilha = null;
    
    List<String> errosEncontrados = new ArrayList<String>();

    // IDENTIFICAR QUEM É A CATEGORIA 2 EM RELACAO AO ASSUNTO EM ANALISE

    // DESCOBRIR QUANTAS CATEGORIAS ESSE ASSUNTO TEM
    boolean temAssuntoPai = true;

    // Armazena a categoria 1 do assunto na base
    Assunto categoria1DoAssuntoNaBase = null;

    // Armazena a categoria 2 do assunto na base
    Assunto categoria2DoAssuntoNaBase = null;

    // Armazena a categoria 3 do assunto na base
    Assunto categoria3DoAssuntoNaBase = null;

    int quantidadeDeCategoriasNaBase = 0;

    Assunto assuntoLoop = assuntoNaBase;

    // Sobe a arvore até a primeira categoria
    while (temAssuntoPai) {
      assuntoLoop = assuntoLoop.getAssuntoPai();
      quantidadeDeCategoriasNaBase++;
      if (assuntoLoop.getAssuntoPai() == null) {
        temAssuntoPai = false;
      }
    }

    if (quantidadeDeCategoriasNaBase == 1) {
      categoria2DoAssuntoNaBase = null;
      categoria1DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
    } else if (quantidadeDeCategoriasNaBase == 2) {
      categoria2DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
      categoria1DoAssuntoNaBase = categoria2DoAssuntoNaBase.getAssuntoPai();
    } else if (quantidadeDeCategoriasNaBase == 3) {
      categoria3DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
      categoria2DoAssuntoNaBase = assuntoNaBase.getAssuntoPai().getAssuntoPai();
      categoria1DoAssuntoNaBase = assuntoNaBase.getAssuntoPai().getAssuntoPai().getAssuntoPai();
    }

    // Categoria 2 identificada na base se existir
    String nomeCategoria2Planilha = exportacaoAssuntoDTO.getCategoria2();
    String nomeCategoria3Planilha = exportacaoAssuntoDTO.getCategoria3();
    
    /* 
     * DEFINIR A OPERACAO DA PLANILHA 
     */
    /** 01/03 (INSERCAO) - Existe no Arquivo e NÃO Existe no Banco para o Assunto em questão */
    if (StringUtils.isNotBlank(nomeCategoria2Planilha) && categoria2DoAssuntoNaBase == null) {
      operacaoPlanilha = OperacaoPlanilha.INSERIR_FIM;
      
    /** 02/03 (EXCLUSAO) - NÃO Existe no Arquivo e Existe no Banco para o Assunto em questão */
    } else if (StringUtils.isBlank(nomeCategoria2Planilha) && categoria2DoAssuntoNaBase != null) {
      
      // EXCLUI ULTIMA
      if (quantidadeDeCategoriasNaBase == 2) {
        operacaoPlanilha = OperacaoPlanilha.EXCLUIR_ULTIMA;
      }
      // EXCLUI MEIO
      else if (quantidadeDeCategoriasNaBase == 3) {
        operacaoPlanilha = OperacaoPlanilha.EXCLUIR_MEIO;
      }
      
    /** 03/03 (ATUALIZACAO) - Existe no Arquivo e Existe no Banco para o Assunto em questão */
    } else if (StringUtils.isNotBlank(nomeCategoria2Planilha) && categoria2DoAssuntoNaBase != null
        && !nomeCategoria2Planilha.trim().equals(categoria2DoAssuntoNaBase.getNome().trim())) {
      
      //SE A NOVA CAT 2 da planilha é IGUAL A CAT 3 DA BASE É UMA EXCLUSAO MEIO
      if(categoria3DoAssuntoNaBase != null && StringUtils.isBlank(nomeCategoria3Planilha)
          && nomeCategoria2Planilha.equals(categoria3DoAssuntoNaBase.getNome())) {
        operacaoPlanilha = OperacaoPlanilha.EXCLUIR_MEIO;

      // ALTERA NO FIM
      } else if (quantidadeDeCategoriasNaBase == 2) {
        operacaoPlanilha = OperacaoPlanilha.ALTERAR_FIM;

      // ALTERA NO MEIO
      } else if (quantidadeDeCategoriasNaBase == 3) {
        operacaoPlanilha = OperacaoPlanilha.ALTERAR_MEIO;
      }
      
    }
    /* 
     * DEFINIR A OPERACAO DA PLANILHA 
     */

    // Houve operação na planilha
    if (operacaoPlanilha != null) {
      
      /** 01/03 (INSERCAO) - Existe no Arquivo e NÃO Existe no Banco para o Assunto em questão */
      if (operacaoPlanilha.equals(OperacaoPlanilha.INSERIR_FIM)) {

        // FA06 - INSERE NO FIM

        // BUSCAR A REUTILIZACAO DE CATEGORIA EXISTENTE?
        // SENÃO CRIA UMA NOVA

        Assunto novaCategoria2 = null;
        List<Assunto> categorias2DisponiveisPelaCategoria1 = categoria1DoAssuntoNaBase.getAssuntosList();

        for (Assunto cat2 : categorias2DisponiveisPelaCategoria1) {
          if (cat2.getAtivo() && !cat2.getFlagAssunto() && cat2.getNome().trim().equals(nomeCategoria2Planilha.trim())) {
            novaCategoria2 = cat2;
          }
        }

        // Cria a nova Categoria 2 e relaciona com a Categoria 1 e com o Assunto em questão
        // Assunto novaCategoria2 = new Assunto();

        if (novaCategoria2 == null) {
          novaCategoria2 = new Assunto();
          novaCategoria2.setAbrangencia(categoria1DoAssuntoNaBase.getAbrangencia());
          novaCategoria2.setAssuntoPai(categoria1DoAssuntoNaBase);
          novaCategoria2.setNome(nomeCategoria2Planilha);
          novaCategoria2.setPermissaoAbertura(false);
          novaCategoria2.setAtivo(true);
          novaCategoria2.setFlagAssunto(false);
          novaCategoria2.setExcluido(false);
          novaCategoria2.setContrato(false);

          // "PERSISTIR" essa categoria quando se cria uma nova
          categoria1DoAssuntoNaBase.getAssuntosList().add(novaCategoria2);

          assuntoDao.save(novaCategoria2);
        }

        // "ATUALIZAR" esse assunto
        assuntoNaBase.setAssuntoPai(novaCategoria2);

      }

      /** 02/03 (EXCLUSAO) - NÃO Existe no Arquivo e Existe no Banco para o Assunto em questão */
      else if ((operacaoPlanilha.equals(OperacaoPlanilha.EXCLUIR_MEIO)
          || operacaoPlanilha.equals(OperacaoPlanilha.EXCLUIR_ULTIMA))
          && categoria2DoAssuntoNaBase != null
          ) {

        // EXCLUI ULTIMA
        if (operacaoPlanilha.equals(OperacaoPlanilha.EXCLUIR_ULTIMA)) {
          Boolean existeAssuntosIrmaos = false;
          // Percorre a lista de Assuntos dessa Categoria 2 a ser excluída
          for (Assunto assunto : categoria2DoAssuntoNaBase.getAssuntosList()) {
            // Verifica se é ativo e diferente do assunto em Questão
            if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
              existeAssuntosIrmaos = true;
              break;
            }
          }

          // SEM IRMAOS // FA08
          if (!existeAssuntosIrmaos) {
            // Inativa e limpa a lista de assuntos
            categoria2DoAssuntoNaBase.setAtivo(false);
            categoria2DoAssuntoNaBase.setExcluido(true);
            categoria2DoAssuntoNaBase.setAssuntosList(new ArrayList<Assunto>());

            // Altera o assunto para a categoria anterior
            assuntoNaBase.setAssuntoPai(categoria1DoAssuntoNaBase);

            // Adiciona na lista da categoria 1 o assunto
            categoria1DoAssuntoNaBase.getAssuntosList().add(assuntoNaBase);

            // COM IRMAOS // FA09
          } else {

            // Altera o assunto para a categoria anterior
            assuntoNaBase.setAssuntoPai(categoria1DoAssuntoNaBase);

            // Adiciona na lista da categoria 1 o assunto
            categoria1DoAssuntoNaBase.getAssuntosList().add(assuntoNaBase);

          }
        }

        // EXCLUI MEIO
        else if (operacaoPlanilha.equals(OperacaoPlanilha.EXCLUIR_MEIO) && categoria3DoAssuntoNaBase != null) {

          Boolean existeCategoriasIrmas = false;

          // Percorre a lista de Assuntos dessa Categoria 2 a ser excluída
          for (Assunto categoria3 : categoria2DoAssuntoNaBase.getAssuntosList()) {

            // Verifica se é ativo e diferente do assunto em Questão
            if (categoria3.getAtivo() && !categoria3.getId().equals(assuntoNaBase.getAssuntoPai().getId())) {
              existeCategoriasIrmas = true;
              break;
            }
          }

          Boolean existeAssuntosIrmaos = false;
          // Percorre a lista de Assuntos dessa Categoria 3 para verificar se o assunto em questão tem irmãos
          for (Assunto assunto : categoria3DoAssuntoNaBase.getAssuntosList()) {

            // Verifica se é ativo e diferente do assunto em Questão
            if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
              existeAssuntosIrmaos = true;
              break;
            }
          }

          // SEM IRMAOS // FA10
          if (!existeCategoriasIrmas && !existeAssuntosIrmaos) {

            // Inativa e limpa a lista de assuntos
            categoria2DoAssuntoNaBase.setAtivo(false);
            categoria2DoAssuntoNaBase.setExcluido(true);

            categoria2DoAssuntoNaBase.setAssuntosList(new ArrayList<Assunto>());

            // Altera o assunto para a categoria anterior
            categoria3DoAssuntoNaBase.setAssuntoPai(categoria1DoAssuntoNaBase);

            // Adiciona na lista da categoria 1 o assunto
            categoria1DoAssuntoNaBase.getAssuntosList().add(categoria3DoAssuntoNaBase);
          } else {
            // COM IRMAOS // FA11

            // Validar o mesmo Responsável de todos os assuntos

            Boolean existeAssuntoOutroResponsavel = false;

            // Percorre a lista de Assuntos dessa Categoria 2 a ser excluída
            for (Assunto categoria3 : categoria2DoAssuntoNaBase.getAssuntosList()) {

              if (categoria3.getAtivo()) {
                if (categoria3.getFlagAssunto()
                    && !categoria3.getCaixaPostal().getId().equals(assuntoNaBase.getCaixaPostal().getId())) {
                  existeAssuntoOutroResponsavel = true;
                  break;
                } else {
                  for (Assunto assunto : categoria3.getAssuntosList()) {
                    if (assunto.getAtivo() && assunto.getFlagAssunto()
                        && !assunto.getCaixaPostal().getId().equals(assuntoNaBase.getCaixaPostal().getId())) {
                      existeAssuntoOutroResponsavel = true;
                      break;
                    }
                  }
                }

              }
              // Verifica se é ativo e diferente do assunto em Questão
              if (categoria3.getAtivo() && !categoria3.getId().equals(assuntoNaBase.getAssuntoPai().getId())) {
                existeCategoriasIrmas = true;
                break;
              }
            }

            if (existeAssuntoOutroResponsavel) {
              // FA 12
              errosEncontrados.add(
                  "A categoria Alterada pertence a outros assuntos, que são de responsabilidade de outra Unidade. A categoria NÃO poderá ser criada ou alterada. Linha: "
                      + contador + " , coluna: " + "Categoria 2");
            } else {

              // Inativa e exclui a categoria
              categoria2DoAssuntoNaBase.setAtivo(false);
              categoria2DoAssuntoNaBase.setExcluido(true);

              // Relaciona os "filhos da categoria a ser excluida" com a categoria 1
              for (Assunto assunto : categoria2DoAssuntoNaBase.getAssuntosList()) {
                if (assunto.getAtivo()) {
                  assunto.setAssuntoPai(categoria1DoAssuntoNaBase);

                  // Adiciona na lista da categoria 1 o assunto
                  categoria1DoAssuntoNaBase.getAssuntosList().add(assunto);
                }
              }

            }

          }

        }
      }

      /** 03/03 (ATUALIZACAO) - Existe no Arquivo e Existe no Banco para o Assunto em questão */
      else if (operacaoPlanilha.equals(OperacaoPlanilha.ALTERAR_MEIO) || operacaoPlanilha.equals(OperacaoPlanilha.ALTERAR_FIM)) {

        // ALTERA NO FIM
        if (operacaoPlanilha.equals(OperacaoPlanilha.ALTERAR_FIM) && categoria2DoAssuntoNaBase != null) {

          Boolean existemAssuntosIrmaos = false;

          for (Assunto assunto : categoria2DoAssuntoNaBase.getAssuntosList()) {
            if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
              existemAssuntosIrmaos = true;
              break;
            }
          }

          // FA 025
          if (!existemAssuntosIrmaos) {
            categoria2DoAssuntoNaBase.setNome(nomeCategoria2Planilha.trim());

            // FA 026
          } else {

            // BUSCAR A REUTILIZACAO DE CATEGORIA EXISTENTE?
            // SENÃO CRIA UMA NOVA

            Assunto novaCategoria2 = null;

            // Cria a nova Categoria 2 e relaciona com a Categoria 1 e com o Assunto em questão
            // Assunto novaCategoria2 = new Assunto();

            if (novaCategoria2 == null) {
              novaCategoria2 = new Assunto();
              novaCategoria2.setAbrangencia(categoria1DoAssuntoNaBase.getAbrangencia());
              novaCategoria2.setAssuntoPai(categoria1DoAssuntoNaBase);
              novaCategoria2.setNome(nomeCategoria2Planilha);
              novaCategoria2.setPermissaoAbertura(false);
              novaCategoria2.setAtivo(true);
              novaCategoria2.setFlagAssunto(false);
              novaCategoria2.setExcluido(false);
              novaCategoria2.setContrato(false);

              // "PERSISTIR" essa categoria quando se cria uma nova
              categoria1DoAssuntoNaBase.getAssuntosList().add(novaCategoria2);

              assuntoDao.save(novaCategoria2);
            }

            // "ATUALIZAR" esse assunto
            assuntoNaBase.setAssuntoPai(novaCategoria2);
          }
        }

        // ALTERA NO MEIO
        else if (operacaoPlanilha.equals(OperacaoPlanilha.ALTERAR_MEIO) && categoria3DoAssuntoNaBase != null) {

          Boolean existeAssuntosIrmaos = false;
          // Percorre a lista de Assuntos dessa Categoria 3 para verificar se o assunto em questão tem irmãos
          for (Assunto assunto : categoria3DoAssuntoNaBase.getAssuntosList()) {

            // Verifica se é ativo e diferente do assunto em Questão
            if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
              existeAssuntosIrmaos = true;
              break;
            }
          }

          Boolean existemCategoriasIrmas = false;

          // Percorre a lista de Assuntos dessa Categoria 2 a ser alterada
          for (Assunto categoria3 : categoria2DoAssuntoNaBase.getAssuntosList()) {

            // Verifica se é ativo e diferente do assunto em Questão
            if (categoria3.getAtivo() && !categoria3.getId().equals(assuntoNaBase.getAssuntoPai().getId())) {
              existemCategoriasIrmas = true;
              break;
            }
          }

          // FA 027
          if (!existeAssuntosIrmaos && !existemCategoriasIrmas) {

            categoria2DoAssuntoNaBase.setNome(nomeCategoria2Planilha.trim());

            // FA 028
          } else {

            // Validar o mesmo Responsável de todos os assuntos
            Boolean existeAssuntoOutroResponsavel = false;

            // Percorre a lista de Assuntos dessa Categoria 2 a ser altera
            for (Assunto categoria3 : categoria2DoAssuntoNaBase.getAssuntosList()) {
              if (categoria3.getAtivo()) {
                if (categoria3.getFlagAssunto()
                    && !categoria3.getCaixaPostal().getId().equals(assuntoNaBase.getCaixaPostal().getId())) {
                  existeAssuntoOutroResponsavel = true;
                  break;
                } else {
                  for (Assunto assunto : categoria3.getAssuntosList()) {
                    if (assunto.getAtivo() && assunto.getFlagAssunto()
                        && !assunto.getCaixaPostal().getId().equals(assuntoNaBase.getCaixaPostal().getId())) {
                      existeAssuntoOutroResponsavel = true;
                      break;
                    }
                  }
                }

              }
            }

            if (existeAssuntoOutroResponsavel) {
              // FA 12
              errosEncontrados.add(
                  "A categoria Alterada pertence a outros assuntos, que são de responsabilidade de outra Unidade. A categoria NÃO poderá ser criada ou alterada. Linha: "
                      + contador + " , coluna: " + "Categoria 2");
            } else {
              categoria2DoAssuntoNaBase.setNome(nomeCategoria2Planilha.trim());
            }

          }
        }

      } 
    }
    

    /**
     * 
     * FIM: Categoria 2
     * 
     */

    if (!errosEncontrados.isEmpty()) {
      throw new BusinessException(errosEncontrados);
    }
    
    
  }
  
  enum OperacaoPlanilha {
    
    INSERIR_FIM,
    
    EXCLUIR_ULTIMA,
    EXCLUIR_MEIO,
    
    ALTERAR_MEIO,
    ALTERAR_FIM
    
  }

  private void validarCategoria3(Integer contador, ExportacaoAssuntoDTO exportacaoAssuntoDTO,
      Assunto assuntoNaBase) throws BusinessException {
    
    List<String> errosEncontrados = new ArrayList<String>();

    // DESCOBRIR QUANTAS CATEGORIAS ESSE ASSUNTO TEM
    boolean temAssuntoPai = true;

    // Armazena a categoria 1 do assunto na base
    Assunto categoria1DoAssuntoNaBase = null;

    // Armazena a categoria 2 do assunto na base
    Assunto categoria2DoAssuntoNaBase = null;

    // Armazena a categoria 3 do assunto na base
    Assunto categoria3DoAssuntoNaBase = null;

    int quantidadeDeCategoriasNaBase = 0;

    Assunto assuntoLoop = assuntoNaBase;

    // Sobe a arvore até a primeira categoria
    while (temAssuntoPai) {
      assuntoLoop = assuntoLoop.getAssuntoPai();
      quantidadeDeCategoriasNaBase++;
      if (assuntoLoop.getAssuntoPai() == null) {
        temAssuntoPai = false;
      }
    }

    if (quantidadeDeCategoriasNaBase == 1) {
      categoria2DoAssuntoNaBase = null;
      categoria1DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
    } else if (quantidadeDeCategoriasNaBase == 2) {
      categoria2DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
      categoria1DoAssuntoNaBase = categoria2DoAssuntoNaBase.getAssuntoPai();
    } else if (quantidadeDeCategoriasNaBase == 3) {
      categoria3DoAssuntoNaBase = assuntoNaBase.getAssuntoPai();
      categoria2DoAssuntoNaBase = assuntoNaBase.getAssuntoPai().getAssuntoPai();
      categoria1DoAssuntoNaBase = assuntoNaBase.getAssuntoPai().getAssuntoPai().getAssuntoPai();
    }

    // Categoria 3 identificada na base se existir
    String nomeCategoria3Planilha = exportacaoAssuntoDTO.getCategoria3();
    
    /*
     * DEFINIR A OPERACAO
     */
    OperacaoPlanilha operacaoPlanilha = null;
    
    /**
     * 01/03 (INSERCAO) - Existe no Arquivo e NÃO Existe no Banco para o Assunto em questão
     */
    if (StringUtils.isNotBlank(nomeCategoria3Planilha) && categoria3DoAssuntoNaBase == null) {
      operacaoPlanilha = OperacaoPlanilha.INSERIR_FIM;
    }
    /** 
     * 02/03 (EXCLUSAO) - NÃO Existe no Arquivo e Existe no Banco para o Assunto em questão 
     */
    else if (StringUtils.isBlank(nomeCategoria3Planilha) && categoria3DoAssuntoNaBase != null) {
      operacaoPlanilha = OperacaoPlanilha.EXCLUIR_ULTIMA;
    }
    /** 
     * 03/03 (ATUALIZACAO) - Existe no Arquivo e Existe no Banco para o Assunto em questão 
     */
    else if (StringUtils.isNotBlank(nomeCategoria3Planilha) && categoria3DoAssuntoNaBase != null
        && !nomeCategoria3Planilha.trim().equals(categoria3DoAssuntoNaBase.getNome().trim())) {
      operacaoPlanilha = OperacaoPlanilha.ALTERAR_FIM;
    }
    
    
    /*
     * DEFINIR A OPERACAO
     */


    
    
    /**
     * 01/03 (INSERCAO) - Existe no Arquivo e NÃO Existe no Banco para o Assunto em questão
     */
    if (operacaoPlanilha != null && operacaoPlanilha.equals(OperacaoPlanilha.INSERIR_FIM)
        && categoria2DoAssuntoNaBase != null) {

      // FA06 - INSERE NO FIM

      // BUSCAR A REUTILIZACAO DE CATEGORIA EXISTENTE?
      // SENÃO CRIA UMA NOVA

      if(categoria2DoAssuntoNaBase.getAssuntosList() == null) {
        categoria2DoAssuntoNaBase.setAssuntosList(new ArrayList<Assunto>());
      }

      Assunto novaCategoria3 = null;
      List<Assunto> categorias3DisponiveisPelaCategoria2 = categoria2DoAssuntoNaBase.getAssuntosList();
      

      for (Assunto cat3 : categorias3DisponiveisPelaCategoria2) {
        if (cat3.getAtivo() && !cat3.getFlagAssunto() && cat3.getNome().trim().equals(nomeCategoria3Planilha.trim())) {
          novaCategoria3 = cat3;
        }
      }

      // Cria a nova Categoria 2 e relaciona com a Categoria 1 e com o Assunto em questão
      // Assunto novaCategoria2 = new Assunto();

      if (novaCategoria3 == null) {
        novaCategoria3 = new Assunto();
        novaCategoria3.setAbrangencia(categoria2DoAssuntoNaBase.getAbrangencia());
        novaCategoria3.setAssuntoPai(categoria2DoAssuntoNaBase);
        novaCategoria3.setNome(nomeCategoria3Planilha);
        novaCategoria3.setPermissaoAbertura(false);
        novaCategoria3.setAtivo(true);
        novaCategoria3.setFlagAssunto(false);
        novaCategoria3.setExcluido(false);
        novaCategoria3.setContrato(false);

        // "PERSISTIR" essa categoria quando se cria uma nova
        categoria2DoAssuntoNaBase.getAssuntosList().add(novaCategoria3);

        assuntoDao.save(novaCategoria3);
      }

      // "ATUALIZAR" esse assunto
      assuntoNaBase.setAssuntoPai(novaCategoria3);

    }

    /** 02/03 (EXCLUSAO) - NÃO Existe no Arquivo e Existe no Banco para o Assunto em questão */
    /* */
    else if (operacaoPlanilha != null && operacaoPlanilha.equals(OperacaoPlanilha.EXCLUIR_ULTIMA)
        && categoria2DoAssuntoNaBase != null && categoria3DoAssuntoNaBase != null) {

      // EXCLUI ULTIMA
      Boolean existeAssuntosIrmaos = false;

      // Percorre a lista de Assuntos dessa Categoria 3 a ser excluída
      for (Assunto assunto : categoria3DoAssuntoNaBase.getAssuntosList()) {
        // Verifica se é ativo e diferente do assunto em Questão
        if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
          existeAssuntosIrmaos = true;
          break;
        }
      }

      // SEM IRMAOS // FA08
      if (!existeAssuntosIrmaos) {
        // Inativa e limpa a lista de assuntos
        categoria3DoAssuntoNaBase.setAtivo(false);
        categoria3DoAssuntoNaBase.setExcluido(true);
        categoria3DoAssuntoNaBase.setAssuntosList(new ArrayList<Assunto>());

        // Altera o assunto para a categoria anterior
        assuntoNaBase.setAssuntoPai(categoria2DoAssuntoNaBase);

        // Adiciona na lista da categoria 2 o assunto
        categoria2DoAssuntoNaBase.getAssuntosList().add(assuntoNaBase);

        // COM IRMAOS // FA09
      } else {

        // Altera o assunto para a categoria anterior
        assuntoNaBase.setAssuntoPai(categoria2DoAssuntoNaBase);

        // Adiciona na lista da categoria 2 o assunto
        categoria2DoAssuntoNaBase.getAssuntosList().add(assuntoNaBase);
      }

    }

    /** 03/03 (ATUALIZACAO) - Existe no Arquivo e Existe no Banco para o Assunto em questão */
    else if (operacaoPlanilha != null && operacaoPlanilha.equals(OperacaoPlanilha.ALTERAR_FIM)
        && categoria3DoAssuntoNaBase != null) {

      // ALTERA NO FIM

      Boolean existemAssuntosIrmaos = false;

      for (Assunto assunto : categoria3DoAssuntoNaBase.getAssuntosList()) {
        if (assunto.getAtivo() && !assunto.getId().equals(assuntoNaBase.getId())) {
          existemAssuntosIrmaos = true;
          break;
        }
      }

      // FA 025
      if (!existemAssuntosIrmaos) {
        categoria3DoAssuntoNaBase.setNome(nomeCategoria3Planilha.trim());

        // FA 026
      } else {

        // BUSCAR A REUTILIZACAO DE CATEGORIA EXISTENTE?
        // SENÃO CRIA UMA NOVA

        Assunto novaCategoria3 = null;

        // Cria a nova Categoria 2 e relaciona com a Categoria 1 e com o Assunto em questão
        // Assunto novaCategoria2 = new Assunto();

        if (novaCategoria3 == null && categoria2DoAssuntoNaBase != null) {
          novaCategoria3 = new Assunto();
          novaCategoria3.setAbrangencia(categoria2DoAssuntoNaBase.getAbrangencia());
          novaCategoria3.setAssuntoPai(categoria2DoAssuntoNaBase);
          novaCategoria3.setNome(nomeCategoria3Planilha);
          novaCategoria3.setPermissaoAbertura(false);
          novaCategoria3.setAtivo(true);
          novaCategoria3.setFlagAssunto(false);
          novaCategoria3.setExcluido(false);
          novaCategoria3.setContrato(false);

          // "PERSISTIR" essa categoria quando se cria uma nova
          categoria2DoAssuntoNaBase.getAssuntosList().add(novaCategoria3);

          assuntoDao.save(novaCategoria3);
        }

        // "ATUALIZAR" esse assunto
        assuntoNaBase.setAssuntoPai(novaCategoria3);
      }

    }

    /**
     * 
     * FIM: Categoria 2
     * 
     */

    if (!errosEncontrados.isEmpty()) {
      throw new BusinessException(errosEncontrados);
    }
  }

  public Integer importarDemandas(InputStream inputStream, String matriculaUsuarioLogado, String nomeUsuarioLogado) throws BusinessRollbackException {

    List<ExportacaoDemandaDTO> listRegistros;

    try {

      // -- Abrangência
      Abrangencia abrangencia = (Abrangencia) JSFUtil.getSessionMapValue(ABRANGENCIA);

      List<String> erros = new ArrayList<String>();
      Map<Long, Demanda> mapDemanda = new HashMap<Long, Demanda>();
      Map<String, CaixaPostal> mapCaixasPostais = new HashMap<String, CaixaPostal>();
      Map<Long, ExportacaoDemandaDTO> mapQualificados = new HashMap<Long, ExportacaoDemandaDTO>();

      // Põe todas as Caixas Postais em memória
      List<CaixaPostal> listCaixaPostal =
          this.caixaPostalDao.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.FILIAL, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.ARROBA_EXTERNA);
      for (CaixaPostal caixaPostal : listCaixaPostal) {
        mapCaixasPostais.put(caixaPostal.getSigla().toUpperCase(), caixaPostal);
      }

      listRegistros = this.extrairDadosPlanilhaDemanda(inputStream);

      List<Long> listaIdDemandaArquivo = new ArrayList<Long>();
      for (ExportacaoDemandaDTO lista : listRegistros) {
        if(lista.getNumeroDemanda() != null) {
          listaIdDemandaArquivo.add(lista.getNumeroDemanda());
        }
      }

      List<Demanda> listDemanda = new ArrayList<Demanda>();
      if (listaIdDemandaArquivo.size() > 0) {
        // Pesquisa somente as demandas inseridas no arquivo
        listDemanda = this.demandaDao.findByNumeroDemanda(listaIdDemandaArquivo);
      }
      
        for (Demanda demanda : listDemanda) {
          mapDemanda.put(demanda.getId(), demanda);
        }
        
        int numLinha = 1;
        for (ExportacaoDemandaDTO registro : listRegistros) {
          try {
            this.validarDemanda(registro, numLinha, mapQualificados, mapCaixasPostais, mapDemanda);
            mapQualificados.put(registro.getNumeroDemanda(), registro);
          } catch (BusinessException e) {
            erros.add(e.getMessage());
          }

          numLinha++;
        } 
        
      if (!erros.isEmpty()) {
        throw new BusinessRollbackException(erros,  gerarMensagemLogErro(erros, listRegistros.size()));
      } else {
        if( listRegistros.size() > 0) {
          efetuarMigracaoDemandas(mapQualificados, listRegistros, mapDemanda, mapCaixasPostais, matriculaUsuarioLogado, nomeUsuarioLogado);
        }
        return listRegistros.size();
      }
      
    } catch (IOException e) {
      throw new BusinessRollbackException(e.getMessage());
    }
  }
  
  class DemandaNumeroColuna{
    public static final String NUMERO_DEMANDA = "2";
    public static final String FLUXO_DEMANDA = "3";
    public static final String PRAZO_FLUXO_DEMANDA = "4";
    public static final String RESPONSAVEL_ATUAL = "5";
    public static final String CAIXA_POSTAL_DEMANDANTE = "6";
    public static final String OBSERVADORES_DEMANDA= "7";
  }

  private void validarDemanda(final ExportacaoDemandaDTO registro, int linha, final Map<Long, ExportacaoDemandaDTO> mapValidos,
      final Map<String, CaixaPostal> mapCaixasPostais, final Map<Long, Demanda> mapDemanda) throws BusinessException {
    
    //Pulando a contagem das linhas de cabeçalho
    linha = linha + 3;

    // -- FE4 obrigatoriedade
    if (registro.getNumeroDemanda() == null || registro.getNumeroDemanda() == 0) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), DemandaNumeroColuna.NUMERO_DEMANDA));
    }

    if (StringUtils.isBlank(registro.getFluxoDemanda())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), DemandaNumeroColuna.FLUXO_DEMANDA));
    }

    if (StringUtils.isBlank(registro.getPrazoFluxoDemanda())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), DemandaNumeroColuna.PRAZO_FLUXO_DEMANDA));
    }

    if (StringUtils.isBlank(registro.getResponsavelAtual())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), DemandaNumeroColuna.RESPONSAVEL_ATUAL));
    }

    if (StringUtils.isBlank(registro.getCaixaPostalDemandante())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), DemandaNumeroColuna.CAIXA_POSTAL_DEMANDANTE));
    }
    
    // -- FE12 Duplicidade de número de demanda
    if (registro.getNumeroDemanda() != null && mapValidos.containsKey(registro.getNumeroDemanda())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA059", String.valueOf(linha), DemandaNumeroColuna.NUMERO_DEMANDA));
    }

    // -- FE6: Responsável, Fluxo Demanda, Demandante pré-definido e observadores do assunto
    if (!mapCaixasPostais.containsKey(registro.getResponsavelAtual().trim().toUpperCase())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA051", String.valueOf(linha),  DemandaNumeroColuna.RESPONSAVEL_ATUAL));
    }

    if (!StringUtils.isBlank(registro.getFluxoDemanda())) {
      String[] demandas = registro.getFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < demandas.length; i++) {
        String strDemanda = this.removeEnterAndSpaces(demandas[i]).trim().toUpperCase();
        if (!mapCaixasPostais.containsKey(strDemanda) && !strDemanda.toLowerCase().contains(EXTERNA)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA051", String.valueOf(linha), DemandaNumeroColuna.FLUXO_DEMANDA));
        }
      }
    }

    if (!StringUtils.isBlank(registro.getObservadoresDemanda())) {
      String[] demandas = registro.getObservadoresDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < demandas.length; i++) {
        String strDemanda = this.removeEnterAndSpaces(demandas[i]).trim().toUpperCase();
        if (!mapCaixasPostais.containsKey(strDemanda)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA066", String.valueOf(linha),  DemandaNumeroColuna.OBSERVADORES_DEMANDA, strDemanda));
        }
      }
    }

    // -- FE7 Apenas inteiros: Prazo responsável e prazo fluxo assuntos
    if (!StringUtils.isBlank(registro.getPrazoFluxoDemanda())) {
      String[] prazos = registro.getPrazoFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < prazos.length; i++) {
        String strPrazo = this.removeEnterAndSpaces(prazos[i]).trim();

        if (!NumberUtils.isDigits(strPrazo)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA052", String.valueOf(linha), DemandaNumeroColuna.PRAZO_FLUXO_DEMANDA));
        } else {
          // -- FE8 Apenas inteiros superiores à zero: Prazo fluxo assuntos
          int prazo = Integer.valueOf(strPrazo);
          if (prazo <= 0) {
            throw new BusinessException(MensagemUtil.obterMensagem("MA053", String.valueOf(linha), DemandaNumeroColuna.PRAZO_FLUXO_DEMANDA));
          }
        }
      }
    }

    // -- FE8 Apenas inteiros superiores à zero: Prazo responsável e prazo fluxo demanda
    if (registro.getPrazoFluxoDemanda().trim().equals("0")) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA053", String.valueOf(linha), DemandaNumeroColuna.PRAZO_FLUXO_DEMANDA));
    }

    // -- FE9 @externa no início ou meio do fluxo
    if (!StringUtils.isBlank(registro.getFluxoDemanda()) && registro.getFluxoDemanda().toLowerCase().contains(EXTERNA)) {
      String[] fluxo = registro.getFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      if ((fluxo.length == 1) || !(this.removeEnterAndSpaces(fluxo[fluxo.length - 1]).trim().equalsIgnoreCase(EXTERNA))) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA054", String.valueOf(linha), DemandaNumeroColuna.FLUXO_DEMANDA));
      }
    }

    // -- FE10 qtd de caixas postais e prazos
    if (!StringUtils.isBlank(registro.getFluxoDemanda()) && !StringUtils.isBlank(registro.getPrazoFluxoDemanda())) {

      int numCaixasPostais = registro.getFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA).length;
      int numPrazos = registro.getPrazoFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA).length;

      if (numCaixasPostais != numPrazos) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA055", String.valueOf(linha), DemandaNumeroColuna.FLUXO_DEMANDA));
      }
    }

    // -- FE13 Caixa postal informada não faz parte do fluxo da demanda
    if (!StringUtils.isBlank(registro.getResponsavelAtual())) {

      String responsavelAtual = registro.getResponsavelAtual().trim();
      String[] fluxo = registro.getFluxoDemanda().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      
      List<String> fluxoList = new ArrayList<String>();
      for (String fx : fluxo) {
        fluxoList.add(fx.trim());
      }
      
      if(responsavelAtual.toUpperCase().equals(EXTERNA.toUpperCase())) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA068", String.valueOf(linha),  DemandaNumeroColuna.RESPONSAVEL_ATUAL));
      }
      
      if (!fluxoList.contains(responsavelAtual)
          && !responsavelAtual.equals(registro.getCaixaPostalDemandante().trim())) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA060", String.valueOf(linha),  DemandaNumeroColuna.RESPONSAVEL_ATUAL));
      }

    }

    // -- FE15 número assunto inexistente
    if (!mapDemanda.containsKey(registro.getNumeroDemanda())) {
      throw new BusinessException(
          MensagemUtil.obterMensagem("MA062", String.valueOf(registro.getNumeroDemanda()), String.valueOf(linha), DemandaNumeroColuna.NUMERO_DEMANDA));
    }

    // -- FE16 caixa postal demandante inexistente
    if (!StringUtils.isBlank(registro.getCaixaPostalDemandante())) {
      String[] caixasPostais = registro.getCaixaPostalDemandante().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < caixasPostais.length; i++) {
        String caixa = this.removeEnterAndSpaces(caixasPostais[i].trim().toUpperCase());
        if (!StringUtils.isBlank(caixa) && !mapCaixasPostais.containsKey(caixa)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA051", String.valueOf(linha),  DemandaNumeroColuna.CAIXA_POSTAL_DEMANDANTE));
        }
      }
    }
    
    Demanda demandaNaBase = mapDemanda.get(registro.getNumeroDemanda());
    
    if(demandaNaBase.getCaixaPostalDemandante().getId().equals(demandaNaBase.getCaixaPostalResponsavel().getId())) {
      if(!demandaNaBase.getCaixaPostalResponsavel().getSigla().equals(registro.getResponsavelAtual().trim())) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA050", String.valueOf(linha),  DemandaNumeroColuna.RESPONSAVEL_ATUAL));
      }
      if(!demandaNaBase.getCaixaPostalDemandante().getSigla().equals(registro.getCaixaPostalDemandante().trim())) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA050", String.valueOf(linha),  DemandaNumeroColuna.CAIXA_POSTAL_DEMANDANTE));
      }
    } else {
      if(!demandaNaBase.getCaixaPostalDemandante().getSigla().equals(registro.getCaixaPostalDemandante().trim())) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA047", String.valueOf(linha),  DemandaNumeroColuna.CAIXA_POSTAL_DEMANDANTE));
      }
    }
    
  }

  private void validarAssunto(final ExportacaoAssuntoDTO registro, int linha, final Map<Long, ExportacaoAssuntoDTO> mapValidos,
      final Map<Long, Assunto> mapAssuntos, final Map<String, CaixaPostal> mapCaixasPostais,
      final Map<String, Unidade> mapUnidades) throws BusinessException {

    linha = linha + 4;
    
    // -- FE4 obrigatoriedade
    if (registro.getNumeroAssunto() == null || registro.getNumeroAssunto() == 0) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "1"));
    }

    // -- FE15 número assunto inexistente
    if (!mapAssuntos.containsKey(registro.getNumeroAssunto())) {
      throw new BusinessException(
          MensagemUtil.obterMensagem("MA062", String.valueOf(registro.getNumeroAssunto()), String.valueOf(linha), "1"));
    }

    // -- FE3 duplicidade
    if (registro.getNumeroAssunto() != null && mapValidos.containsKey(registro.getNumeroAssunto())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA044", String.valueOf(linha), "1"));
    }
    
    Assunto assuntoNaBase = mapAssuntos.get(registro.getNumeroAssunto());
    
    if(assuntoNaBase != null && !assuntoNaBase.getAtivo()) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA063", String.valueOf(linha), "1"));
    }

    if (StringUtils.isBlank(registro.getCategoria1())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "3"));
    }

    if (StringUtils.isBlank(registro.getNomeAssunto())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "6"));
    }

    if (StringUtils.isBlank(registro.getResponsavel())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "7"));
    }

    if (registro.getPrazoResponsavel() == null) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "8"));
    }

    if (StringUtils.isBlank(registro.getFluxoAssunto()) && (!StringUtils.isBlank(registro.getPrazoFluxoAssunto())
        || !StringUtils.isBlank(registro.getDemandantesPreDefinidos()))) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "9"));
    }

    if (StringUtils.isBlank(registro.getPrazoFluxoAssunto())
        && (!StringUtils.isBlank(registro.getFluxoAssunto()) || !StringUtils.isBlank(registro.getDemandantesPreDefinidos()))) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "10"));
    }

    if (StringUtils.isBlank(registro.getDemandantesPreDefinidos())
        && (!StringUtils.isBlank(registro.getFluxoAssunto()) || !StringUtils.isBlank(registro.getPrazoFluxoAssunto()))) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "11"));
    }

    if (StringUtils.isBlank(registro.getAtivo())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA048", String.valueOf(linha), "13"));
    }
    
    if(StringUtils.isBlank(registro.getCategoria2())
        && StringUtils.isNotBlank(registro.getCategoria3())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA067", String.valueOf(linha), "4"));
    }
    

    // -- FE5 alteração de categoria
    Assunto categoria1 = null;
    Assunto assunto = mapAssuntos.get(registro.getNumeroAssunto());

    if (assunto.getAssuntoPai() != null && assunto.getAssuntoPai().getAssuntoPai() != null
        && assunto.getAssuntoPai().getAssuntoPai().getAssuntoPai() != null) {
      categoria1 = assunto.getAssuntoPai().getAssuntoPai().getAssuntoPai();
    } else if (assunto.getAssuntoPai() != null && assunto.getAssuntoPai().getAssuntoPai() != null) {
      categoria1 = assunto.getAssuntoPai().getAssuntoPai();
    } else if (assunto.getAssuntoPai() != null) {
      categoria1 = assunto.getAssuntoPai();
    }

    if (!registro.getCategoria1().trim().equalsIgnoreCase(categoria1.getNome().trim())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA049", String.valueOf(linha), "3"));
    }

    // -- FE6: Responsável, Fluxo Assunto, Demandante pré-definido e observadores do assunto
    if (!mapCaixasPostais.containsKey(registro.getResponsavel().trim().toUpperCase())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA051", String.valueOf(linha), "7"));
    }

    if (!StringUtils.isBlank(registro.getFluxoAssunto())) {
      String[] assuntos = registro.getFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < assuntos.length; i++) {
        String strAssunto = this.removeEnterAndSpaces(assuntos[i]).trim().toUpperCase();
        if (!mapCaixasPostais.containsKey(strAssunto)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA051", String.valueOf(linha), "9"));
        }
      }
    }

    if (!StringUtils.isBlank(registro.getObservadoresAssunto())) {
      String[] assuntos = registro.getObservadoresAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < assuntos.length; i++) {
        String strAssunto = this.removeEnterAndSpaces(assuntos[i]).trim().toUpperCase();
        if (!mapCaixasPostais.containsKey(strAssunto)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA066", String.valueOf(linha), "12", strAssunto));
        }
      }
    }

    // -- FE7 Apenas inteiros: Prazo responsável e prazo fluxo assuntos
    if (PRAZO_INVALIDO.equals(registro.getPrazoResponsavel())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA052", String.valueOf(linha), "8"));
    }

    if (!StringUtils.isBlank(registro.getPrazoFluxoAssunto())) {
      String[] prazos = registro.getPrazoFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < prazos.length; i++) {
        String strPrazo = this.removeEnterAndSpaces(prazos[i]).trim();

        if (!NumberUtils.isDigits(strPrazo)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA052", String.valueOf(linha), "10"));
        } else {
          // -- FE8 Apenas inteiros superiores à zero: Prazo fluxo assuntos
          int prazo = Integer.valueOf(strPrazo);
          if (prazo <= 0) {
            throw new BusinessException(MensagemUtil.obterMensagem("MA053", String.valueOf(linha), "10"));
          }
        }
      }
    }

    // -- FE8 Apenas inteiros superiores à zero: Prazo responsável e prazo fluxo assuntos
    if (registro.getPrazoResponsavel() <= 0) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA053", String.valueOf(linha), "8"));
    }

    // -- FE9 @externa no início ou meio do fluxo
    if (!StringUtils.isBlank(registro.getFluxoAssunto()) && registro.getFluxoAssunto().toLowerCase().contains(EXTERNA)) {
      String[] fluxo = registro.getFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      if ((fluxo.length == 1) || !(this.removeEnterAndSpaces(fluxo[fluxo.length - 1]).trim().equalsIgnoreCase(EXTERNA))) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA054", String.valueOf(linha), "9"));
      }
    }

    // -- FE10 qtd de caixas postais e prazos
    if (!StringUtils.isBlank(registro.getFluxoAssunto()) && !StringUtils.isBlank(registro.getPrazoFluxoAssunto())) {

      int numCaixasPostais = registro.getFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA).length;
      int numPrazos = registro.getPrazoFluxoAssunto().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA).length;

      if (numCaixasPostais != numPrazos) {
        throw new BusinessException(MensagemUtil.obterMensagem("MA055", String.valueOf(linha), "9"));
      }
    }

    // -- FE11 campo ativo diferente de vazio, Sim e Não
    if (!StringUtils.isBlank(registro.getAtivo()) && !SIM.equalsIgnoreCase(registro.getAtivo())
        && !NAO.equalsIgnoreCase(registro.getAtivo())) {
      throw new BusinessException(MensagemUtil.obterMensagem("MA056", String.valueOf(linha), "13"));
    }

    // -- FE16 unidade inexistente
    if (!StringUtils.isBlank(registro.getDemandantesPreDefinidos())) {
      String[] unidades = registro.getDemandantesPreDefinidos().split(ImportacaoService.SEPARADOR_DADOS_PLANILHA);
      for (int i = 0; i < unidades.length; i++) {
        String unidade = this.removeEnterAndSpaces(unidades[i].toUpperCase()).trim();
        if (!StringUtils.isBlank(unidade) && !mapUnidades.containsKey(unidade)) {
          throw new BusinessException(MensagemUtil.obterMensagem("MA061", String.valueOf(linha), "11", unidade));
        }
      }
    }
  }

  private List<ExportacaoAssuntoDTO> extrairDadosPlanilhaAssunto(InputStream inputStream) throws IOException, BusinessRollbackException {
    List<ExportacaoAssuntoDTO> exportacaoDtoList = new ArrayList<ExportacaoAssuntoDTO>();

    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
    Sheet datatypeSheet = workbook.getSheetAt(0);

    Iterator<Row> iterator = datatypeSheet.iterator();
    
    // Percorre as linhas
    while (iterator.hasNext()) {
      Row currentRow = iterator.next();
      
      if(currentRow.getRowNum() == 0) {
        Iterator<Cell> cellIterator = currentRow.iterator();
        
        while (cellIterator.hasNext()) {
          Cell currentCell = cellIterator.next();
          if(currentCell.getColumnIndex() == 0) {
            String titulo = this.getCellValueAsString(currentCell);
            if(!ExportarMigracaoAssuntoXLS.TITULO_RELATORIO.equals(titulo)) {
              throw new BusinessRollbackException(MensagemUtil.obterMensagem("MA065"));
            }
          }
        }
      }
      

      if (currentRow.getRowNum() > 3) {

        ExportacaoAssuntoDTO assuntoDTO = new ExportacaoAssuntoDTO();

        Iterator<Cell> cellIterator = currentRow.iterator();

        while (cellIterator.hasNext()) {
          Cell currentCell = cellIterator.next();

          Integer cellIndex = currentCell.getColumnIndex();

          switch (cellIndex) {
          case 0:
            assuntoDTO.setNumeroAssunto(this.getCellValueAsNumeric(currentCell));
            break;
          case 1:
            String arvoreAssunto = currentCell.getStringCellValue();
            assuntoDTO.setArvoreAssuntoAtual(arvoreAssunto);
            break;

          case 2:
            String categoria1 = currentCell.getStringCellValue();
            assuntoDTO.setCategoria1(categoria1);
            break;

          case 3:
            String categoria2 = currentCell.getStringCellValue();
            assuntoDTO.setCategoria2(categoria2);
            break;

          case 4:
            String categoria3 = currentCell.getStringCellValue();
            assuntoDTO.setCategoria3(categoria3);
            break;

          case 5:
            String nomeAssunto = currentCell.getStringCellValue();
            assuntoDTO.setNomeAssunto(nomeAssunto);
            break;

          case 6:
            String responsavel = currentCell.getStringCellValue();
            assuntoDTO.setResponsavel(responsavel);
            break;

          case 7:
            try {
              Double prazo = currentCell.getNumericCellValue();
              assuntoDTO.setPrazoResponsavel(prazo.intValue());
              
              if(prazo.toString().contains(".0") || prazo.toString().contains(",0") ) {
                assuntoDTO.setPrazoResponsavel(prazo.intValue());
              } else {
                assuntoDTO.setPrazoResponsavel(Integer.parseInt(prazo.toString()));
              }

            } catch (
                IllegalStateException
                | NumberFormatException e) {
              assuntoDTO.setPrazoResponsavel(PRAZO_INVALIDO);
            }
            break;

          case 8:
            String fluxoAssunto = currentCell.getStringCellValue();
            assuntoDTO.setFluxoAssunto(fluxoAssunto);
            break;
          case 9:
            assuntoDTO.setPrazoFluxoAssunto(this.getCellValueAsString(currentCell));
            break;

          case 10:
            String demandantes = currentCell.getStringCellValue();
            assuntoDTO.setDemandantesPreDefinidos(demandantes);
            break;

          case 11:
            String observadores = currentCell.getStringCellValue();
            assuntoDTO.setObservadoresAssunto(observadores);
            break;

          case 12:
            String ativo = currentCell.getStringCellValue();
            assuntoDTO.setAtivo(ativo);
            break;

          default:
            break;
          }
        }
        exportacaoDtoList.add(assuntoDTO);

      }

    }

    return exportacaoDtoList;
  }

  private List<ExportacaoDemandaDTO> extrairDadosPlanilhaDemanda(InputStream inputStream) throws IOException, BusinessRollbackException {
    List<ExportacaoDemandaDTO> exportacaoDtoList = new ArrayList<ExportacaoDemandaDTO>();

    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
    Sheet datatypeSheet = workbook.getSheetAt(0);

    Iterator<Row> iterator = datatypeSheet.iterator();

    // Percorre as linhas
    while (iterator.hasNext()) {
      Row currentRow = iterator.next();
      
      if(currentRow.getRowNum() == 0) {
        Iterator<Cell> cellIterator = currentRow.iterator();
        
        while (cellIterator.hasNext()) {
          Cell currentCell = cellIterator.next();
          if(currentCell.getColumnIndex() == 0) {
            String titulo = this.getCellValueAsString(currentCell);
            if(!ExportarMigracaoDemandaXLS.TITULO_RELATORIO.equals(titulo)) {
              throw new BusinessRollbackException(MensagemUtil.obterMensagem("MA065"));
            }
          }
        }
      }

      if (currentRow.getRowNum() > 2) {

        ExportacaoDemandaDTO demandaDTO = new ExportacaoDemandaDTO();

        Iterator<Cell> cellIterator = currentRow.iterator();

        if (isRowEmpty(currentRow)) {
          break;
        }

        while (cellIterator.hasNext()) {
          Cell currentCell = cellIterator.next();

          Integer cellIndex = currentCell.getColumnIndex();

          switch (cellIndex) {
          case 1:
            demandaDTO.setNumeroDemanda(this.getCellValueAsNumeric(currentCell));
            break;
          case 2:
            String fluxoDemanda = currentCell.getStringCellValue();
            demandaDTO.setFluxoDemanda(fluxoDemanda);
            break;

          case 3:
            String prazo = this.getCellValueAsString(currentCell);
            demandaDTO.setPrazoFluxoDemanda(prazo);
            break;

          case 4:
            String responsavelAtual = currentCell.getStringCellValue();
            demandaDTO.setResponsavelAtual(responsavelAtual);
            break;

          case 5:
            String caixaPostalDemandante = currentCell.getStringCellValue();
            demandaDTO.setCaixaPostalDemandante(caixaPostalDemandante);
            break;

          case 6:
            String observadoresDemanda = currentCell.getStringCellValue();
            demandaDTO.setObservadoresDemanda(observadoresDemanda);
            break;

          default:
            break;
          }
        }
        exportacaoDtoList.add(demandaDTO);
      }

    }

    return exportacaoDtoList;
  }

  public static boolean isRowEmpty(Row row) {
    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
      Cell cell = row.getCell(c);
      if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
        return false;
    }
    return true;
  }

  private String removeEnterAndSpaces(String value) {
    final String EOL = System.getProperty("line.separator");

    if (!StringUtils.isBlank(value)) {
      return value.trim().replaceAll(EOL, StringUtils.EMPTY);
    }

    return StringUtils.EMPTY;
  }

  /**
   * Retorna o valor da celula como String. Consegue ler o tipo das Celulas String e Numeric.
   */
  private String getCellValueAsString(final Cell cell) {
    String value = StringUtils.EMPTY;

    switch (cell.getCellType()) {
    case 0:
      Double d = cell.getNumericCellValue();
      
      value = String.valueOf(d);
      if(d.toString().contains(".0") || d.toString().contains(",0")) {
        value = String.valueOf(d.intValue());
      }
      
      break;
    case 1:
      value = cell.getStringCellValue();
      break;
    }

    return value;
  }

  /**
   * Retorna o valor da celula como um Long.
   */
  private Long getCellValueAsNumeric(final Cell cell) {
    Long value = null;

    switch (cell.getCellType()) {
    case 0:
      Double d = cell.getNumericCellValue();
      value = Long.valueOf(d.intValue());
      break;
    case 1:
      String s = cell.getStringCellValue();
      value = (NumberUtils.isDigits(s)) ? Long.valueOf(s) : null;
      break;
    }

    return value;
  }

}
