package br.gov.caixa.gitecsa.arquitetura.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

  public static final String STRING_VAZIA = "";

  private static final String WILDCARD = "%";

  public static final String LETRAS_COM_ACENTUACAO = "ÁÀÃÂÄÉÈÊËÍÌÏÎÓÒÕÔÖÚÙÛÜÇÑÝŸáàãâäéèêëíìïîóòõôöúùûüçñýÿ";
  public static final String LETRAS_SEM_ACENTUACAO = "AAAAAEEEEIIIIOOOOOUUUUCNYYaaaaaeeeeiiiiooooouuuucnyy";

  public static final String EDITAR = "EDITAR";
  public static final String DETALHAR = "DETALHAR";
  public static final String ACAO = "ACAO";
  public static final String OBJETO = "OBJETO";

  /**
   * Verifica se o objeto informado é nulo ou vazio.
   */
  @SuppressWarnings("rawtypes")
  public static boolean isNullOuVazio(Object objeto) {

    if (objeto == null) {

      return true;

    } else if (objeto instanceof Collection) {

      return ((Collection) objeto).isEmpty();

    } else if (objeto instanceof String) {

      return ((String) objeto).trim().equals(STRING_VAZIA);

    } else if (objeto instanceof Integer) {

      return ((Integer) objeto).intValue() == 0;
    }

    return false;
  }

  public static String removeAcentos(String string) {
    return Normalizer.normalize(string.trim(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
  }

  public static String pesquisaPorNome(String nomeParametroPesquisa, String campoBanco) {
    StringBuilder hql = new StringBuilder();

    String nomeModificado = WILDCARD + removeAcentos(nomeParametroPesquisa.trim().toUpperCase()) + WILDCARD;

    hql.append(" AND TRANSLATE(UPPER(" + campoBanco + "), '" + LETRAS_COM_ACENTUACAO + "', '" + LETRAS_SEM_ACENTUACAO
        + "') like '" + nomeModificado + "'");

    return hql.toString();
  }

  public static String lpad(String input, char padding, int length) {
    String output = "";

    if (input != null) {

      output = input;

    }

    if (output.length() >= length) {

      return output;

    } else {

      StringBuilder result = new StringBuilder();

      int numChars = length - output.length();

      for (int i = 0; i < numChars; i++) {

        result.append(padding);

      }

      result.append(output);

      return result.toString();

    }

  }

  public static String rpad(String input, String padding, int length) {

    String output = "";

    if (input != null) {
      output = input;
    }

    if (output.length() >= length) {
      return output.substring(0, length);
    } else {

      StringBuilder result = new StringBuilder();

      int numChars = length - output.length();
      result.append(output);

      for (int i = 0; i < numChars; i++) {
        result.append(padding);
      }

      return result.toString();

    }

  }

  public static String formataCPF(Long cpf) {
    String cpfString = lpad(Long.toString(cpf), '0', 11);

    if (cpfString != null) {

      cpfString =
          cpfString.substring(0, 3) + "." + cpfString.substring(3, 6) + "." + cpfString.substring(6, 9) + "-"
              + cpfString.substring(9, 11);

    }

    return cpfString;
  }

  public static String formataCnpj(Long cnpj) {
    String cnpjString = lpad(Long.toString(cnpj), '0', 14);

    if (!isNullOuVazio(cnpjString)) {
      cnpjString =
          cnpjString.substring(0, 2) + "." + cnpjString.substring(2, 5) + "." + cnpjString.substring(5, 8) + "/"
              + cnpjString.substring(8, 12) + "-" + cnpjString.substring(12, 14);
    }

    return cnpjString;
  }

  /**
   * Valida o parametro nome pela expressão regular informada.
   */
  public static boolean validarNomePorExpressaoRegular(String expressaoRegular, String nome) {
    Pattern pattern = Pattern.compile(expressaoRegular);

    Matcher matcher = pattern.matcher(nome);

    if (matcher.find()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Formata o codigo informado, para String com LDAP com zeros e tamanho 4.
   */
  public static String formataCodigo(Integer codigo) {
    String codigoFormatado = lpad(Integer.toString(codigo), '0', 4);
    if (codigoFormatado != null) {
      return codigoFormatado;
    } else {
      return "";
    }

  }

  /**
   * Valida se é um CNPJ.
   */
  public static Boolean validaCNPJ(String cnpj) {

    if (cnpj == null || cnpj.length() != 14) {
      return false;
    }

    try {
      Long.parseLong(cnpj);
    } catch (NumberFormatException e) {
      return false;
    }

    int soma = 0;
    String cnpjCalc = cnpj.substring(0, 12);

    char[] chrCnpj = cnpj.toCharArray();

    for (int i = 0; i < 4; i++) {
      if (chrCnpj[i] - 48 >= 0 && chrCnpj[i] - 48 <= 9) {
        soma += (chrCnpj[i] - 48) * (6 - (i + 1));
      }
    }
    for (int i = 0; i < 8; i++) {
      if (chrCnpj[i + 4] - 48 >= 0 && chrCnpj[i + 4] - 48 <= 9) {
        soma += (chrCnpj[i + 4] - 48) * (10 - (i + 1));
      }
    }
    int dig = 11 - soma % 11;
    cnpjCalc =
        (new StringBuilder(String.valueOf(cnpjCalc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();
    soma = 0;

    for (int i = 0; i < 5; i++) {
      if (chrCnpj[i] - 48 >= 0 && chrCnpj[i] - 48 <= 9) {
        soma += (chrCnpj[i] - 48) * (7 - (i + 1));
      }
    }
    for (int i = 0; i < 8; i++) {
      if (chrCnpj[i + 5] - 48 >= 0 && chrCnpj[i + 5] - 48 <= 9) {
        soma += (chrCnpj[i + 5] - 48) * (10 - (i + 1));
      }
    }
    dig = 11 - soma % 11;
    cnpjCalc =
        (new StringBuilder(String.valueOf(cnpjCalc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();

    if (!cnpj.equals(cnpjCalc) || cnpj.equals("00000000000000")) {
      return false;
    }

    return true;
  }

  /**
   * Converte o parametro para MD5.
   */
  public static String convertStringToMD5(String input) {

    String md5 = null;

    if (null == input) {
      return null;
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");

      digest.update(input.getBytes(), 0, input.length());

      md5 = new BigInteger(1, digest.digest()).toString(64);

    } catch (NoSuchAlgorithmException e) {

      e.printStackTrace();
    }
    return md5;
  }

  public static String formataLista(List<String> lista) {
    String r = "";
    if (lista.size() >= 1) {
      r += lista.get(0);
      if (lista.size() > 1) {
        for (int i = 1; i < (lista.size() - 1); i++) {
          r += ", " + lista.get(i);
        }
        r += " e " + lista.get(lista.size() - 1);
      }
    }
    return r;
  }


  public static String acrescentaZeroEsquerda(String valor, int tamanho) {
    while (valor.length() < tamanho) {
      valor = "0" + valor;
    }
    return valor;
  }

  /**
   * Valida CPF do usuario. Não aceita cpfs padrão, como 11111111111 ou 22222222222.
   * 
   * @param cpf
   *          String valor com 11 dígitos
   */
  public static boolean validaCPF(String cpf) {
    if (cpf == null || cpf.length() != 11 || isCPFPadrao(cpf)) {
      return false;
    }
    try {
      Long.parseLong(cpf);
    } catch (NumberFormatException e) { // CPF não possui somente n�meros
      return false;
    }

    if (!calcDigVerif(cpf.substring(0, 9)).equals(cpf.substring(9, 11))) {
      return false;
    }
    return true;
  }

  /**
   * 
   * @param cpf
   *          String valor a ser testado.
   * @return boolean indicando se o usuário entrou com um CPF padrão
   */
  private static boolean isCPFPadrao(String cpf) {
    if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
        || cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666") || cpf.equals("77777777777")
        || cpf.equals("88888888888") || cpf.equals("99999999999")) {

      return true;
    }

    return false;
  }

  private static String calcDigVerif(String num) {
    Integer primDig;
    Integer segDig;

    int soma = 0;
    int peso = 10;

    for (int i = 0; i < num.length(); i++) {
      soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;
    }
    if (soma % 11 == 0 | soma % 11 == 1) {
      primDig = new Integer(0);
    } else {
      primDig = new Integer(11 - (soma % 11));
    }
    soma = 0;
    peso = 11;
    for (int i = 0; i < num.length(); i++) {
      soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;
    }
    soma += primDig.intValue() * 2;
    if (soma % 11 == 0 | soma % 11 == 1) {
      segDig = new Integer(0);
    } else {
      segDig = new Integer(11 - (soma % 11));
    }
    return primDig.toString() + segDig.toString();
  }

  /**
   * Valida o digito verificador da caixa.
   */
  public static boolean validaDvCaixa(String conta, String dv) {

    int peso = 8;
    int soma = 0;

    for (char c : conta.toCharArray()) {
      int n = Integer.parseInt(String.valueOf(c));
      soma += n * peso;
      peso--;
      if (peso < 2) {
        peso = 9;
      }
    }

    int modulo = (soma * 10) % 11;

    if (modulo == 10) {
      modulo = 0;
    }

    return (modulo == Integer.parseInt(dv)) ? Boolean.TRUE : Boolean.FALSE;
  }
  
  	/**Calcula e formata o percentual de dois numeros*/
	public static String converterDecimalFormatado(Double divisor, Double dividendo) {
		String str = "0%";
		if(!(divisor==1 && dividendo==0)) {

			
				DecimalFormat df = new DecimalFormat("0.##"); 
				Double result = (divisor/dividendo)* 100;
				str = df.format(result)+"%";
				
			
		}
		
		return str;
	}
	public static boolean validarEmail(String email) { 
		boolean isEmailIdValid = false; 
		if (email != null && email.length() > 0) { 
			String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"; 
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE); 
			Matcher matcher = pattern.matcher(email); 
			if (matcher.matches()) { 
				isEmailIdValid = true; 
				} 
		} 
		return isEmailIdValid; 
	}
	

}