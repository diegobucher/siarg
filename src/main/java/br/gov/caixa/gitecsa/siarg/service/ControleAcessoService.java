/**
 * ControleAcessoService.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exception.CaixaPostalException;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.dao.AbrangenciaDAO;
import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UsuarioSistemaDTO;
import br.gov.caixa.gitecsa.siico.service.FuncaoEmpregadoGerenteService;

/**
 * Classe de Serviços - Chamado.
 * @author f520296
 */
@Stateless
public class ControleAcessoService {

  private static final String MENSAGEM_EXCEPTION_CAIXA_POSTAL = "Usuário lotado em unidade sem Caixa Postal Cadastrada.";

  /** Injeção de Dependência. */
  @Inject
  private UsuarioDAO usuarioDAO;

  /** Injeção de Dependência. */
  @Inject
  private AbrangenciaDAO abrangenciaDAO;

  /** Injeção de Dependência. */
  @Inject
  private PerfilDAO perfilDAO;

  /** Injeção de Dependência. */
  @Inject
  private UnidadeDAO unidadeDAO;

  /** Injeção de Dependência. */
  @Inject
  private FuncaoEmpregadoGerenteService funcaoEmpregadoGerenteService;

  /**
   * Obter o usuário completo do sistema.
   * @param usuario **UsuarioLdap**
   * @return UsuarioSistemaDTO
   * @throws AppException
   */
  public UsuarioSistemaDTO obterPerfilCompletoDoUsuarioLogado(final UsuarioLdap usuarioLdap) throws CaixaPostalException {

    UsuarioSistemaDTO usuarioSistemaDTO = new UsuarioSistemaDTO();
    Usuario usuarioExcessao = this.usuarioDAO.obterUsuarioExcessaoPorMatricula(usuarioLdap.getNuMatricula());
    usuarioSistemaDTO.setUsuario(usuarioExcessao);
    usuarioSistemaDTO.setUsuarioLdap(usuarioLdap);

    /* Usuário possui registro de excessão - Perfil Excessão. */
    if ((usuarioExcessao != null) && (usuarioExcessao.getId() != null)) {

      /*
       * Perfil de excessão - FA1 - Usuário cadastrado como excessão. Validar Usuário master.
       */
      List<Unidade> listaUnidades;
      if (usuarioExcessao.getPerfil().getId() == 1) {
        listaUnidades = this.obterListaUnidadesUsuarioMaster(usuarioExcessao.getId());
      } else {
        listaUnidades = this.unidadeDAO.obterListaUnidadesUsuarioExcessao(usuarioExcessao.getId());
      }

      /* Inserir a unidade de lotação do usuário na lista de unidades. */
      List<Unidade> unidadeUsuarioList = this.unidadeDAO.obterListaUnidadeUsuarioLogado(usuarioLdap.getCoUnidade());
      if (unidadeUsuarioList != null){
        for (Unidade unidade : unidadeUsuarioList) {
          
          if (unidade.getCaixasPostaisList() != null
              && !unidade.getCaixasPostaisAtivasList().isEmpty()
              && !listaUnidades.contains(unidade)) {
            
              listaUnidades.add(unidade);
              Collections.sort(listaUnidades);
          }
        }
      }

      Unidade unidadeLotacao = this.unidadeDAO.obterUnidadeUsuarioLogado(listaUnidades.get(0).getCgcUnidade());
      usuarioSistemaDTO.setUnidade(unidadeLotacao);

      /* Validar se existe pelo menos uma unidade com caixa postal. */
      if ((listaUnidades == null) || listaUnidades.isEmpty() || !this.existeCaixaPostalListaUnidades(listaUnidades)) {
        throw new CaixaPostalException(MENSAGEM_EXCEPTION_CAIXA_POSTAL);
      }
      usuarioSistemaDTO.setListaUnidadesExcessao(this.montarListaUnidadesDTO(listaUnidades));

      /* Verificar se é master: 1 - Administrador. */
      usuarioSistemaDTO.setPerfil(this.perfilDAO.obterPerfilPorId(usuarioExcessao.getPerfil().getId()));

      /* Verificar no perfil se o mesmo é Gerente. */
      usuarioSistemaDTO.setPerfilGerencial(this.obterPerfilGerencialSiicoPorMatricula(usuarioLdap.getNuFuncao()));

      /* Usuário não possui registro de excessão - Perfil padrão. */
    } else {
      List<Unidade> unidadeLotacaoList = this.unidadeDAO.obterListaUnidadeUsuarioLogado(usuarioLdap.getCoUnidade());
      if (unidadeLotacaoList != null && !unidadeLotacaoList.isEmpty()) {
        
        List<Unidade> listaUnidades = new ArrayList<>();

        for (Unidade unidade : unidadeLotacaoList) {
          
          if (unidade.getCaixasPostaisList() != null
              && !unidade.getCaixasPostaisAtivasList().isEmpty()
              && !listaUnidades.contains(unidade)) {
            
              listaUnidades.add(unidade);
              Collections.sort(listaUnidades);
          }
        }

        /* Buscar as caixa postais cadastradas */
        if (listaUnidades == null || listaUnidades.isEmpty()) {
          throw new CaixaPostalException(MENSAGEM_EXCEPTION_CAIXA_POSTAL);
        }
        
        Unidade unidadeLotacao = listaUnidades.get(0);
        
        usuarioSistemaDTO.setUnidade(unidadeLotacao);

        /* Inserir Lista de unidades. */
        usuarioSistemaDTO.setListaUnidadesExcessao(this.montarListaUnidadesDTO(unidadeLotacaoList));
        /*
         * Inserindo perfil Padrão no usuário. Padrão 2L - Usuário Padrão.
         */
        usuarioSistemaDTO.setPerfil(this.perfilDAO.obterPerfilPorId(2L));

        /* Verificar no perfil se o mesmo é Gerente. */
        usuarioSistemaDTO.setPerfilGerencial(this.obterPerfilGerencialSiicoPorMatricula(usuarioLdap.getNuFuncao()));
      } else {
        throw new CaixaPostalException("Unidade do usuário não cadastrada.");
      }
    }

    return usuarioSistemaDTO;
  }

  /**
   * Obter Lista Unidades Usuario Master.
   * @param id **id Do Usuário**
   * @return list
   */
  private List<Unidade> obterListaUnidadesUsuarioMaster(Long idUsuario) {
    List<Abrangencia> listaAbrangencia = this.abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(idUsuario);
    List<Unidade> listaUnidadesMaster = new ArrayList<>();
    for (Abrangencia abrangencia : listaAbrangencia) {
      listaUnidadesMaster.addAll(this.unidadeDAO.obterListaUnidadesPorAbrangencia(abrangencia.getId()));
    }
    Collections.sort(listaUnidadesMaster);
    return listaUnidadesMaster;
  }

  /**
   * Monta uma lista com todas as unidades apenas com Id, Nome e Sigla.
   * @param listaUnidades **list**
   * @return list
   */
  private List<UnidadeDTO> montarListaUnidadesDTO(List<Unidade> listaUnidades) {
    List<UnidadeDTO> unidadesDTOList = new ArrayList<>();
    UnidadeDTO unidadeDTO = new UnidadeDTO();
    for (Unidade unidade : listaUnidades) {
      unidadeDTO.setId(unidade.getId());
      unidadeDTO.setNome(unidade.getNome());
      unidadeDTO.setSigla(unidade.getSigla());
      unidadeDTO.setAbrangencia(unidade.getAbrangencia().getId());
      unidadesDTOList.add(unidadeDTO);
      unidadeDTO = new UnidadeDTO();
    }
    return unidadesDTOList;
  }

  /**
   * Valida se existe caixas postais para as respectivas unidades.
   * @param listaUnidades **list**
   * @return boolean
   */
  private Boolean existeCaixaPostalListaUnidades(List<Unidade> listaUnidades) {
    Boolean validar = Boolean.FALSE;
    /* Validar se a lista de unidades está vazia. */
    if (listaUnidades.isEmpty()) {
      return Boolean.FALSE;
    }
    for (Unidade unidade : listaUnidades) {
      if (!unidade.getCaixasPostaisList().isEmpty()) {
        validar = Boolean.TRUE;
        break;
      }
    }
    return validar;
  }

  /**
   * Obter o perfil no SISRH se o usuário é Gerente.
   * @param numerofuncao **Numero da Função**
   * @return boolean
   */
  private Boolean obterPerfilGerencialSiicoPorMatricula(final Integer numeroFuncao) {
    return this.funcaoEmpregadoGerenteService.obterFuncaoGerentePorCodigo(numeroFuncao);
  }

}
