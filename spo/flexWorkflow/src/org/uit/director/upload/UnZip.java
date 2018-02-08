package org.uit.director.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.crimson.tree.XmlDocument;
import org.xml.sax.SAXException;

/**
 * UnZip -- print or unzip a JAR or PKZIP file using JDK1.1 java.util.zip.
 * Final command-line version: extracts files.
 * @author Ian Darwin, Ian@DarwinSys.com
 * $Id: UnZip.java,v 1.1.1.3 2005/11/02 06:17:38 pd190390 Exp $
 */

/**
 * Класс содержит методы для извлечения файлов из архива *.zip, созданного pkzip
 * для DOS. Вызов метода unZip происходит из класса UploadAction, после загрузки
 * архива с клиентской машины на сервер
 */

public class UnZip {
	/**
	 * Constants for mode listing or mode extracting.
	 */
	public static final int LIST = 0, EXTRACT = 1;

	/**
	 * Whether we are extracting or just printing TOC
	 */
	protected int mode = EXTRACT;

	/**
	 * The ZipFile that is used to read an archive
	 */
	protected ZipFile zippy;

	/**
	 * The buffer for reading/writing the ZipFile data
	 */
	protected byte[] b;
	
	protected static int SIZE_BYTES = 8092;

	/**
	 * Simple main program, construct an UnZipper, process each .ZIP file from
	 * argv[] through that object.
	 */
	public static void main(String[] argv) {
		UnZip u = new UnZip();

		// for (int i=0; i<argv.length; i++)
		// {
		// if ("-x".equals(argv[i]))
		// {
		u.setMode(EXTRACT);
		// continue;
		// }
		// String candidate = argv[i];
		// System.err.println("FlexWorkflow: Trying path " + candidate);
		// if (candidate.endsWith(".zip") ||
		// candidate.endsWith(".jar"))
		// u.unZip(candidate);
		// else System.err.println("FlexWorkflow: Not a zip file? " +
		// candidate);
		// }
		System.err.println("FlexWorkflow: All done!");
	}

	/**
	 * Construct an UnZip object. Just allocate the buffer
	 */
	public UnZip() {
		b = new byte[SIZE_BYTES];
	}

	/**
	 * Set the Mode (list, extract).
	 */
	protected void setMode(int m) {
		if (m == LIST || m == EXTRACT)
			mode = m;
	}

	/**
	 * For a given Zip file, process each entry.
	 * 
	 * @param fileName -
	 *            содержит имя архива
	 * @param documents -
	 *            ссписок иксЭмЭль документов
	 */
	public String unZip(String fileName, Map documents) {

		String name = "";
		try {
			zippy = new ZipFile(fileName);

			Enumeration all = zippy.entries();

			boolean[] checkComplete = { false, false, false, false, false };

			while (all.hasMoreElements()) {

				ZipEntry next = (ZipEntry) all.nextElement();
				name = next.getName();

				if (name.endsWith("Definition.xml")) {

					/*
					 * try { BufferedReader br = new BufferedReader (new
					 * InputStreamReader(zippy.getInputStream(next)));
					 * System.out.print(br.read()); System.out.print(br.read());
					 * System.out.print(br.read());
					 * 
					 * 
					 * }catch (Exception e) { }
					 */

					if (name.equalsIgnoreCase("imageDefinition.xml")) {

						InputStream inputStream =  zippy
								.getInputStream(next);						
						
						StringWriter stringWriter = new StringWriter();						
						
						int n = 0;						
						while ((n = inputStream.read()) > 0) {													
							stringWriter.write(n);							
						}
						stringWriter.toString();
						
						String imageStr = stringWriter.toString();
						documents.put("image-definition", imageStr);
						checkComplete[4] = true;

					} else {
						XmlDocument doc = XmlDocument.createXmlDocument(zippy
								.getInputStream(next), false);

						if (name.equalsIgnoreCase("processDefinition.xml")) {
							documents.put("process-definition", doc);
							checkComplete[0] = true;
						} else if (name
								.equalsIgnoreCase("attributesDefinition.xml")) {
							documents.put("attributes-definition", doc);
							checkComplete[1] = true;
						} else if (name
								.equalsIgnoreCase("stagesDefinition.xml")) {
							documents.put("stages-definition", doc);
							checkComplete[2] = true;
						} else if (name.equalsIgnoreCase("rolesDefinition.xml")) {
							documents.put("roles-definition", doc);
							checkComplete[3] = true;
						}

					}
				}

			}

			if (!checkComplete[0])
				return "Отсутствует XML файл processDefinition.xml";
			if (!checkComplete[1])
				return "Отсутствует XML файл attributesDefinition.xml";
			if (!checkComplete[2])
				return "Отсутствует XML файл stagesDefinition.xml";
			if (!checkComplete[3])
				return "Отсутствует XML файл rolesDefinition.xml";
			if (!checkComplete[4])
				return "Отсутствует XML файл imageDefinition.xml";

		} catch (IOException err) {
			System.err.println("FlexWorkflow: IO Error: " + err);
			return "Ошибка ввода вывода";
		} catch (SAXException e) {
			e.printStackTrace();
			return "Ошибка преобразования XML документа " + name
					+ ".<br><br><strong> " + e.getMessage() + "</strong>";
		}

		return "ok";
	}

	/**
	 * Process one file from the zip, given its name. Either print the name, or
	 * create the file on disk.
	 * 
	 * @param e -
	 *            указатель на файл в архиве, который необходимо извлечь
	 * @param path -
	 *            содержит каталог, куда будут извлекаться файлы из архива
	 */
	protected void getFile(ZipEntry e, String path) throws IOException {
		String zipName = e.getName();
		if (mode == EXTRACT) {

			if (zipName.endsWith("/")) {
				new File(zipName).mkdirs();
				return;
			}
			// Else must be a file; open the file for output
			System.err.println("FlexWorkflow: Creating " + path + "\\"
					+ zipName);
			FileOutputStream os = new FileOutputStream(path + "\\" + zipName);
			InputStream is = zippy.getInputStream(e);
			int n = 0;
			while ((n = is.read(b)) > 0)
				os.write(b, 0, n);
			is.close();
			os.close();
		} else
		// Not extracting, just list
		if (e.isDirectory()) {
			System.out.println("FlexWorkflow: Directory " + zipName);
		} else {
			System.out.println("FlexWorkflow: File " + zipName);
		}
	}

}
