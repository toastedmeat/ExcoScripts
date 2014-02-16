/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Eric
 */

public final class rsItem {

	private static String BASE_URL = "http://forums.zybez.net/runescape-2007-prices/api/";
	public String price = "";
	private static String Json = "";

	public rsItem(String itemName) {
		try {
			Json = readUrl(BASE_URL + itemName);
		} catch (Exception e) {
			Json = "";
			e.printStackTrace();
		}
	}

	public int getAveragePrice() {

		Pattern pattern = Pattern.compile("(?<=\"average\":\")[0-9]+");
		Matcher matcher = pattern.matcher(Json);
		while (matcher.find()){
			return (Integer.parseInt(matcher.group()));
                }
		return 0;
	}
        public int getAveragePriceDragonBones(){
            Pattern pattern = Pattern.compile("(?<=\"average\":\")[0-9]+");
		Matcher matcher = pattern.matcher(Json);
		while (matcher.find(300)){
			return (Integer.parseInt(matcher.group()));
                }
		return 0;
        }

	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		URLConnection uc = null;
		try {
			URL url = new URL(urlString);
			uc = url.openConnection();
			uc.addRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			uc.connect();
			reader = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}

	}
	
}


