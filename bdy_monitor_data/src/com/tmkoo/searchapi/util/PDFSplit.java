package com.tmkoo.searchapi.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;

public final class PDFSplit
{
  private static final String PASSWORD = "-password";
  private static final String SPLIT = "-split";
  private static final String START_PAGE = "-startPage";
  private static final String END_PAGE = "-endPage";
  private static final String OUTPUT_PREFIX = "-outputPrefix";

  public static void main(String[] args)
    throws IOException
  {
    System.setProperty("apple.awt.UIElement", "true");

    PDFSplit split = new PDFSplit();
    split.split(args);
  }

  private void split(String[] args) throws IOException
  {
    String password = "";
    String split = null;
    String startPage = null;
    String endPage = null;
    Splitter splitter = new Splitter();
    String pdfFile = null;
    String outputPrefix = null;
    for (int i = 0; i < args.length; ++i)
    {
      if (args[i].equals("-password"))
      {
        ++i;
        if (i >= args.length)
        {
          usage();
        }
        password = args[i];
      }
      else if (args[i].equals("-split"))
      {
        ++i;
        if (i >= args.length)
        {
          usage();
        }
        split = args[i];
      }
      else if (args[i].equals("-startPage"))
      {
        ++i;
        if (i >= args.length)
        {
          usage();
        }
        startPage = args[i];
      }
      else if (args[i].equals("-endPage"))
      {
        ++i;
        if (i >= args.length)
        {
          usage();
        }
        endPage = args[i];
      }
      else if (args[i].equals("-outputPrefix"))
      {
        ++i;
        outputPrefix = args[i];
      }
      else
      {
        if (pdfFile != null)
          continue;
        pdfFile = args[i];
      }

    }

    if (pdfFile == null)
    {
      usage();
    }
    else
    {
      if (outputPrefix == null)
      {
        outputPrefix = pdfFile.substring(0, pdfFile.lastIndexOf(46));
      }
      PDDocument document = null;
      List documents = null;
      try
      {
        document = PDDocument.load(new File(pdfFile), password);

        int numberOfPages = document.getNumberOfPages();
        boolean startEndPageSet = false;
        if (startPage != null)
        {
          splitter.setStartPage(Integer.parseInt(startPage));
          startEndPageSet = true;
          if (split == null)
          {
            splitter.setSplitAtPage(numberOfPages);
          }
        }
        if (endPage != null)
        {
          splitter.setEndPage(Integer.parseInt(endPage));
          startEndPageSet = true;
          if (split == null)
          {
            splitter.setSplitAtPage(Integer.parseInt(endPage));
          }
        }
        if (split != null)
        {
          splitter.setSplitAtPage(Integer.parseInt(split));
        }
        else if (!(startEndPageSet))
        {
          splitter.setSplitAtPage(1);
        }

        documents = splitter.split(document);
        for (int i = 0; i < documents.size(); ++i)
        {
          PDDocument doc = (PDDocument)documents.get(i);
          String fileName = outputPrefix + "-" + (i + 1) + ".pdf";
          writeDocument(doc, fileName);
          doc.close();
        }
      }
      finally
      {        
      
        if (document != null)
        {
          document.close();
        }
        for (int i = 0; (documents != null) && (i < documents.size()); ++i)
        {
          PDDocument doc = (PDDocument)documents.get(i);
          doc.close();
        }
      }
    }
  }

  private static void writeDocument(PDDocument doc, String fileName) throws IOException
  {
    FileOutputStream output = null;
    COSWriter writer = null;
    try
    {
      output = new FileOutputStream(fileName);
      writer = new COSWriter(output);
      writer.write(doc);
    }
    finally
    {
      if (output != null)
      {
        output.close();
      }
      if (writer != null)
      {
        writer.close();
      }
    }
  }

  private static void usage()
  {
    String message = "Usage: java -jar pdfbox-app-x.y.z.jar PDFSplit [options] <inputfile>\n\nOptions:\n  -password  <password>  : Password to decrypt document\n  -split     <integer>   : split after this many pages (default 1, if startPage and endPage are unset)\n  -startPage <integer>   : start page\n  -endPage   <integer>   : end page\n  -outputPrefix <prefix> : Filename prefix for splitted files\n  <inputfile>            : The PDF document to use\n";

    System.err.println(message);
    System.exit(1);
  }
}