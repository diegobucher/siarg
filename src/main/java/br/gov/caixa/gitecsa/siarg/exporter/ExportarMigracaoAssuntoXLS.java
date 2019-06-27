package br.gov.caixa.gitecsa.siarg.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exporter.AbstractExcelDataExporter;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;

public class ExportarMigracaoAssuntoXLS extends AbstractExcelDataExporter<ExportacaoAssuntoDTO> {

  public static final String TITULO_RELATORIO = "PLANILHA DE MIGRAÇÃO DE ASSUNTOS DO SIARG";

  private static final String FONT_FAMILY = "Arial";

  private int maxNumberOfColumns = 0;
  
  public static final Logger LOGGER = Logger.getLogger(ExportarMigracaoAssuntoXLS.class);
  
  public String LINE_SEPARATOR = System.getProperty("line.separator");
  
  public static final String[] VALORES_POSSIVEIS_REFLETIR_DEMANDA = new String[] {"NÃO","TODAS","ABERTAS","FECHADAS","CANCELADAS","RASCUNHADAS/MINUTADAS"};
  public static final String[] VALORES_POSSIVEIS_ATIVO = new String[] {"SIM","NÃO"};

private String listaConcatString = "";

  @Override
  public void createHeader() {
    
    try {
      this.getBook().setSheetName(0, "Planilha1");

      HSSFRow rowReportName = this.addRowAt(0);
      
      rowReportName.createCell(11).setCellValue(new HSSFRichTextString("Legenda"));
      CellRangeAddress regionLegenda = new CellRangeAddress(0, 0, 11, 13);
      rowReportName.getSheet().addMergedRegion(regionLegenda);
      
      HSSFRow rowLegenda = this.addRowAt(1);
      rowLegenda.createCell(11).setCellValue(new HSSFRichTextString("Caracter de separação"));
      rowLegenda.createCell(12).setCellValue(new HSSFRichTextString(">"));
      
      /*Primeira Linha com o titulo do relatório*/
      HSSFCell cellReportName = rowReportName.createCell(0);
      cellReportName.setCellValue(new HSSFRichTextString(TITULO_RELATORIO));
      cellReportName.setCellStyle(this.getHeaderStyleTituloPlanilha());
      CellRangeAddress regionReportName = new CellRangeAddress(0, 1, 0, 10);
      rowReportName.getSheet().addMergedRegion(regionReportName);

      
      /*Segunda Linha com as mesclas*/
      Integer rowNumber = 2;
      HSSFRow rowAgrupamentoColunas = this.addRowAt(rowNumber);
      
      //Celula vazia 1
      rowAgrupamentoColunas.createCell(0).setCellStyle(this.getHeaderStyle());
      
      //Celula vazia 2
      rowAgrupamentoColunas.createCell(1).setCellStyle(this.getHeaderStyle());

      //Celula Agrupadora 1
      rowAgrupamentoColunas.createCell(2).setCellValue(new HSSFRichTextString("ÁRVORE DE ASSUNTO PROPOSTA"));
      rowAgrupamentoColunas.getCell(2).setCellStyle(this.getHeaderStyle());
      
      CellRangeAddress regionAgrupada1 = new CellRangeAddress(rowNumber, rowNumber, 2, 5);
      this.addStyleToRegion(regionAgrupada1, this.getHeaderStyle());
      rowAgrupamentoColunas.getSheet().addMergedRegion(regionAgrupada1);
      
      //Celula Agrupadora 2
      rowAgrupamentoColunas.createCell(6).setCellValue(new HSSFRichTextString("FLUXO OUTROS DEMANDANTES"));
      rowAgrupamentoColunas.getCell(6).setCellStyle(this.getHeaderStyle());
      
      CellRangeAddress regionAgrupada2 = new CellRangeAddress(rowNumber, rowNumber, 6, 7);
      this.addStyleToRegion(regionAgrupada2, this.getHeaderStyle());
      rowAgrupamentoColunas.getSheet().addMergedRegion(regionAgrupada2);
      
      //Celula Agrupadora 3
      rowAgrupamentoColunas.createCell(8).setCellValue(new HSSFRichTextString("FLUXO DEMANDANTES PRÉ-DEFINIDOS"));
      rowAgrupamentoColunas.getCell(8).setCellStyle(this.getHeaderStyle());
      
      CellRangeAddress regionAgrupada3 = new CellRangeAddress(rowNumber, rowNumber, 8, 10);
      this.addStyleToRegion(regionAgrupada3, this.getHeaderStyle());
      rowAgrupamentoColunas.getSheet().addMergedRegion(regionAgrupada3);
      
      //Celula Agrupadora 4
      rowAgrupamentoColunas.createCell(11);
      rowAgrupamentoColunas.getCell(11).setCellStyle(this.getHeaderStyle());
      
      CellRangeAddress regionAgrupada4 = new CellRangeAddress(rowNumber, rowNumber, 11, 12);
      this.addStyleToRegion(regionAgrupada4, this.getHeaderStyle());
      rowAgrupamentoColunas.getSheet().addMergedRegion(regionAgrupada4);
      
    } catch (Exception e) {
      LOGGER.error(e);
    } 
    
  }

  @Override
  public void createDetail() throws AppException {
    
    HSSFCellStyle headerStyle = this.getHeaderStyle();
    
    int rowNumber = 3;
    
    int ultimaColunaUtilizadaHeader = 0;
    HSSFRow rowHeader = this.addRowAt(rowNumber);
    rowHeader.setHeight((short) 800);
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("NÚMERO"+LINE_SEPARATOR+"ASSUNTO");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("ÁRVORE DE ASSUNTO ATUAL");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("CATEGORIA 1");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("CATEGORIA 2");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("CATEGORIA 3");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("NOME ASSUNTO");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("RESPONSÁVEL");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("PRAZO"+LINE_SEPARATOR+"RESPONSÁVEL");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("FLUXO ASSUNTO");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("PRAZO"+LINE_SEPARATOR+"FLUXO"+LINE_SEPARATOR+"ASSUNTO ");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("DEMANDANTES"+LINE_SEPARATOR+"PRÉ-DEFINIDOS");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("OBSERVADORES"+LINE_SEPARATOR+"DO ASSUNTO");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("ATIVO");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    HSSFCellStyle detailStyleCinza = this.getDetailStyle(true);
    HSSFCellStyle detailStyleBranco = this.getDetailStyle(false);
    HSSFCellStyle detailStyleBrancoEsquerda = this.getDetailStyleLeft(false);
    HSSFCellStyle detailStyleCinzaEsquerda = this.getDetailStyleLeft(true);
    
    for (int index = 0; index < this.getData().size(); index++) {
      
      HSSFCellStyle styleDetalhe;
      HSSFCellStyle styleDetalheEsquerda;
      
      if (index % 2 == 0) {
        styleDetalhe = detailStyleBranco;
        styleDetalheEsquerda = detailStyleBrancoEsquerda;
      } else {
        styleDetalhe = detailStyleCinza;
        styleDetalheEsquerda = detailStyleCinzaEsquerda;
      }

      rowNumber++;
      int ultimaColunaUtilizada = 0;
      
      ExportacaoAssuntoDTO dto = this.getData().get(index);
      
      HSSFRow rowDetail = this.addRowAt(rowNumber);
      rowDetail.setHeight((short)1000);

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getNumeroAssunto());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getArvoreAssuntoAtual().replaceAll(AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO, AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO + LINE_SEPARATOR));
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getCategoria1());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getCategoria2());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getCategoria3());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getNomeAssunto());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getResponsavel());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getPrazoResponsavel());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getFluxoAssunto());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getPrazoFluxoAssunto());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      listaConcatString = "";
      String valorDemandantesPreDefinidos = dto.getDemandantesPreDefinidos();
      formatarSiglas(valorDemandantesPreDefinidos);
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(listaConcatString);
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      
      ultimaColunaUtilizada++;
      
      listaConcatString = "";
      String valorObservadoresAssunto = dto.getObservadoresAssunto();
      formatarSiglas(valorObservadoresAssunto);
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(listaConcatString);
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getAtivo());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
//      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getRefleteDemandas());
//      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
//      ultimaColunaUtilizada++;

    }

    this.maxNumberOfColumns = Math.max(this.maxNumberOfColumns, ultimaColunaUtilizadaHeader);
    
//    CellRangeAddressList rangeRefleteList = new CellRangeAddressList(4, getSheet().getLastRowNum(), 13, 13);
    CellRangeAddressList rangeAtivoList = new CellRangeAddressList(4, getSheet().getLastRowNum(), 12, 12);
    
//    DVConstraint dvConstraintReflete = DVConstraint.createExplicitListConstraint(ExportarMigracaoAssuntoXLS.VALORES_POSSIVEIS_REFLETIR_DEMANDA);
    DVConstraint dvConstraintAtivo = DVConstraint.createExplicitListConstraint(ExportarMigracaoAssuntoXLS.VALORES_POSSIVEIS_ATIVO);

//    DataValidation dataValidationReflete = new HSSFDataValidation(rangeRefleteList, dvConstraintReflete);
    DataValidation dataValidationAtivo = new HSSFDataValidation(rangeAtivoList, dvConstraintAtivo);
    
//    dataValidationReflete.setSuppressDropDownArrow(false);
    dataValidationAtivo.setSuppressDropDownArrow(false);

//    getSheet().addValidationData(dataValidationReflete);
    getSheet().addValidationData(dataValidationAtivo);
    
  }

  private String formatarSiglas(String stringCompleta) {
    String[] listaString = stringCompleta.split(AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO);
    int contador = 1;

    for (String listaFormatada : listaString) {
      if (!listaFormatada.isEmpty() && StringUtils.isNotBlank(listaFormatada)) {
        if (contador % 2 == 0 && contador < listaString.length) {
          listaConcatString += listaFormatada + AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO + LINE_SEPARATOR;
        } else if(contador % 2 != 0 && contador < listaString.length) {
          listaConcatString += listaFormatada + AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO;
        }else if(contador == listaString.length) {
          listaConcatString += listaFormatada;
        }
        contador++;
      }
    }
    return listaConcatString;
  }

  @Override
  public void createFooter() {
    throw new NotImplementedException();
  }

  @Override
  public File export(String filename) throws FileNotFoundException, IOException, AppException {

    this.createDetail();
    this.createHeader();

    if (!ObjectUtils.isNullOrEmpty(this.maxNumberOfColumns)) {
      for (int column = 0; column < this.maxNumberOfColumns; column++) {
        this.getSheet().autoSizeColumn(column);
      }
    }

    File file = new File(filename);
    OutputStream out = new FileOutputStream(file);
    this.getBook().write(out);

    return file; 
  }
  
  private void configurarBordasPadrao(HSSFCellStyle style) {

    style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    style.setTopBorderColor(HSSFColor.BLACK.index);
    
    style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    
    style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    style.setLeftBorderColor(HSSFColor.BLACK.index);
    
    style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    
  }

  private HSSFCellStyle getHeaderStyleTituloPlanilha() {

    HSSFCellStyle style = this.getBook().createCellStyle();
    
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    
    this.configurarBordasPadrao(style);

    //Configuração de Fonte
    HSSFFont font = this.getBook().createFont();
    font.setFontHeightInPoints((short) 18);
    font.setFontName(FONT_FAMILY);
    style.setFont(font);

    return style;
  }


  private HSSFCellStyle getHeaderStyle() {

    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte) 211, (byte) 211, (byte) 211);

    style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    style.setWrapText(true);
//    style.setAlignment(CellStyle.ALIGN_CENTER);
    
    this.configurarBordasPadrao(style);
    
    HSSFFont font = this.getBook().createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeightInPoints((short) 10);
    font.setFontName(FONT_FAMILY);
    style.setFont(font);

    return style;
  }
  
  private HSSFCellStyle getDetailStyle(Boolean cinza) {
    
    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    
    if (cinza) {
      palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte) 242, (byte) 242, (byte) 242);
      style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
    } else {
      palette.setColorAtIndex(HSSFColor.WHITE.index, (byte) 255, (byte) 255, (byte) 255);
      style.setFillForegroundColor(HSSFColor.WHITE.index);
    }
    
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);
    
    style.setWrapText(true);

    return style;
  }
  
  private HSSFCellStyle getDetailStyleLeft(Boolean cinza) {
    
    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    
    if (cinza) {
      palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte) 242, (byte) 242, (byte) 242);
      style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
    } else {
      palette.setColorAtIndex(HSSFColor.WHITE.index, (byte) 255, (byte) 255, (byte) 255);
      style.setFillForegroundColor(HSSFColor.WHITE.index);
    }
    
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);
    style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
    
    style.setWrapText(true);

    return style;
  }

}
