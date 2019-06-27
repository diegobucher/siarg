package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {

  /**
   * WriteFile.
   */
  public static void writeFile(final byte[] file, final String path, final String fileName) throws IOException {

    FileOutputStream fos = new FileOutputStream(new File(path + fileName));
    try {
      fos.write(file);
      fos.flush();
    } finally {
      fos.close();
    }
  }

  /**
   * ReadFile.
   */
  public static byte[] readFile(final String path, final String fileName) throws IOException {

    File file = new File(path + fileName);

    if (file.exists()) {
      return getFileBytes(file);
    }

    return null;
  }

  /**
   * DeleteFile.
   */
  public static void deleteFile(final String path, final String fileName) throws IOException {

    File file = new File(path + fileName);
    file.delete();
  }

  private static byte[] getFileBytes(File file) throws IOException {
    int len = (int) file.length();
    byte[] sendBuf = new byte[len];

    FileInputStream inFile = new FileInputStream(file);
    try {
      inFile.read(sendBuf, 0, len);
    } finally {
      inFile.close();
    }

    return sendBuf;
  }
}
