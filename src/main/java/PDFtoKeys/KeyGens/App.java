package PDFtoKeys.KeyGens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import PDFtoKeys.KeyGens.PDFExtractor;
import PDFtoKeys.KeyGens.PDF;
import PDFtoKeys.KeyGens.WordOcc;
import PDFtoKeys.KeyGens.Words;

import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Hello world!
 * 
 */
public class App {
	static boolean debug = false;
	static String title = "";

	public App() {

	}
//TODO all lower CASE!!! IMPORTANT
	public static void main(String[] args) {
		// BasicConfigurator.configure();
		App app = new App();
		// Text2Image image = new Text2Image();
		// image.generateImage("INEC");
		if (!debug) {

			try {
				app.parsePDFtoKey();
			} catch (LangDetectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void parsePDFtoKey() throws LangDetectException, IOException {
		PDFExtractor extractor = new PDFExtractor();
		File hack = new File(".");
		String home = hack.getAbsolutePath();
		String img = home + "/export/gen_img/";
		String key = home + "/export/gen_key/";
		String export = home + "/export/gen_svg/";
		URL url = getClass().getResource("/data/pdf/");
		File folder = new File(url.getPath());
		boolean first = true;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {

				System.out.println("File= " + folder.getAbsolutePath() + "\\"
						+ fileEntry.getName());
				title = fileEntry.getName();
				int pos = title.lastIndexOf(".");
				if (pos >= 0) {
					title = title.substring(0, pos);
				}
				Text2Image image = new Text2Image();
				image.generateImage(title, img);

				ArrayList<Words> words = extractor.parsePDFtoKey(fileEntry,
						first);
				if (first) {
					first = false;
				}
				ArrayList<WordOcc> occ = extractor.keyOcc(words);
				// createTextExport(occ);
				createTextExport(occ, key, title);
				if ((title
						.substring(title.lastIndexOf('.') + 1, title.length())
						.toLowerCase()).equals("txt"))
					System.out.println("File= " + folder.getAbsolutePath()
							+ "\\" + fileEntry.getName());
				WordCramGen wcg = new WordCramGen();

				wcg.generate(home + "/export/", export, title);
			}
		}
		// ArrayList<Words> words = extractor.parsePDFtoKey();
		//
		// ArrayList<WordOcc> occ = extractor.keyOcc(words);
		// // createTextExport(occ);
		// createTextExport(occ);

		// PDF pdf = new PDF(occ, extractor.getLang());

	}

	private static void createTextExport(ArrayList<WordOcc> keyOcc,
			String path, String title) {
		Writer writer = null;
		
		//Use external txt file
		//File stopList = getClass().getResourceAsStream("/stopList/stopList.txt");
		
		//here create the stopList
		ArrayList<String> stopList =new ArrayList<String>();
		stopList.add("ggf");
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + title + ".txt"), "utf-8"));
			for (int ii = 0; ii < keyOcc.size(); ii++) {
				WordOcc current = keyOcc.get(ii);
				if(!stopList.contains(current.getWord())){
				writer.write(current.getWord().getWord() + ";"
						+ current.getOcc() + ";");
				}
			}
		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}

	}

}
