package br.gov.caixa.gitecsa.siarg.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exporter.AbstractExcelDataExporter;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoDemandaDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;

public class ExportarMigracaoDemandaXLS extends AbstractExcelDataExporter<ExportacaoDemandaDTO> {

  public static final String TITULO_RELATORIO = "PLANILHA DE MIGRAÇÃO DE DEMANDAS DO SIARG";

  private static final String FONT_FAMILY = "Arial";

  private int maxNumberOfColumns = 0;
  
  public static final Logger LOGGER = Logger.getLogger(ExportarMigracaoDemandaXLS.class);
  
  public String LINE_SEPARATOR = System.getProperty("line.separator");
  
  private String listaConcatString = "";

  @Override
  public void createHeader() {
    
    try {
      this.getBook().setSheetName(0, "Planilha1");

      HSSFRow rowReportName = this.addRowAt(0);
      rowReportName.createCell(5).setCellValue(new HSSFRichTextString("Legenda"));
      CellRangeAddress regionLegenda = new CellRangeAddress(0, 0, 5, 6);
      rowReportName.getSheet().addMergedRegion(regionLegenda);
      
      HSSFRow rowLegenda = this.addRowAt(1);
      rowLegenda.createCell(5).setCellValue(new HSSFRichTextString("Caracter de separação"));
      rowLegenda.createCell(6).setCellValue(new HSSFRichTextString(">"));
      
      HSSFCell cellReportName = rowReportName.createCell(0);
      cellReportName.setCellValue(new HSSFRichTextString(TITULO_RELATORIO));
      cellReportName.setCellStyle(this.getHeaderStyleTituloPlanilha());
      CellRangeAddress regionReportName = new CellRangeAddress(0, 1, 0, 4);
      rowReportName.getSheet().addMergedRegion(regionReportName);
      
    } catch (Exception e) {
      LOGGER.error(e);
    } 
    
  }

  @Override
  public void createDetail() throws AppException {
    
    HSSFCellStyle headerStyle = this.getHeaderStyle();
    
    int rowNumber = 2;
    
    int ultimaColunaUtilizadaHeader = 0;
    HSSFRow rowHeader = this.addRowAt(rowNumber);
    rowHeader.setHeight((short) 800);

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("ÁRVORE DE ASSUNTO ATUAL");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("NÚMERO\n DEMANDA");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("FLUXO DEMANDA");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("PRAZO FLUXO\n DEMANDA");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("RESPONSÁVEL\n ATUAL");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("CAIXA POSTAL\n DEMANDANTE");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;

    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("OBSERVADORES DA\n DEMANDA");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    
    this.maxNumberOfColumns = ultimaColunaUtilizadaHeader;
    
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
      
      ExportacaoDemandaDTO dto = this.getData().get(index);
      
      HSSFRow rowDetail = this.addRowAt(rowNumber);
      rowDetail.setHeight((short)1000);

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getArvoreAssuntoAtual().replaceAll(AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO, AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO + LINE_SEPARATOR));
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getNumeroDemanda());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getFluxoDemanda());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getPrazoFluxoDemanda());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getResponsavelAtual());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getCaixaPostalDemandante());
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalhe);
      ultimaColunaUtilizada++;
      
      listaConcatString = "";
      String colunaObservadoresDemanda = dto.getObservadoresDemanda();
      formatarSiglas(colunaObservadoresDemanda);
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(listaConcatString);
      rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(styleDetalheEsquerda);
      ultimaColunaUtilizada++;
      
    }
    
  }
  
  private String formatarSiglas(String stringCompleta) {
    String[] listaString = stringCompleta.split(AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO);
    int contador = 1;

    for (String listaFormatada : listaString) {
      if (!listaFormatada.isEmpty() && StringUtils.isNotBlank(listaFormatada)) {
        if (contador % 3 == 0 && contador < listaString.length) {
          listaConcatString += listaFormatada + AssuntoService.SEPARADOR_CATEGORIA_ASSUNTO + LINE_SEPARATOR;
        } else if(contador % 3 != 0 && contador < listaString.length){
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
    font.setFontHeightInPoints((short) 16);
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
