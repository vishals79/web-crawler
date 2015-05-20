package com.pramati.webcrawler.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.nodes.Document;

public class DownloaderImpl implements Downloader {

	private static int emailCount = 1;
	
	public void downloadEmail(String path,Document doc) {
		File file = null;
		FileWriter fileWriter;
		BufferedWriter bufferedWriter = null;
		
		try {
			if(doc != null){
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				file = new File(path + "email" + DownloaderImpl.emailCount);
				DownloaderImpl.emailCount++;
				if (!file.exists()) {
					file.createNewFile();
				}
				
				fileWriter = new FileWriter(file.getAbsoluteFile());
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(doc.text());
			}
		} catch (IOException e) {
		} finally {
			try {
				if(bufferedWriter != null){
					bufferedWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*private static boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}*/
	
	/*private boolean isValidEmail(Document doc,FilterCriteria filterCriteria, int criteriaNo){
		WebCrawlerFilter filter = null;
		if(doc != null && filterCriteria != null){
			if(criteriaNo == 1){
				filter = new WebCrawlerFilter();
				return filter.matchForYear(doc, filterCriteria.getForYear());
			}
		}
		return false;
	}
	private static String filterContent(String fileContent) {

		if (isNotEmpty(fileContent)) {
			fileContent = fileContent.replaceAll("&lt;", "<");
			fileContent = fileContent.replaceAll("&gt;", ">");
			fileContent = fileContent.replaceAll("&#010;", "\n");
		}

		return fileContent;
	}*/
	
	/*public void downloadEmails(String path, String url,FilterCriteria filterCriteria,int criteriaNo) {
		Document emailDoc = null;
		String from;
		String subject;
		String date;
		String contents;
		StringBuilder fileName = new StringBuilder();
		StringBuilder fileContent = new StringBuilder();
		FileWriter fileWriter;
		BufferedWriter bufferedWriter = null;
		int emailCount = 1;

		if (isNotEmpty(path) && isNotEmpty(url)) {
			try {
				emailDoc = Jsoup.connect(url).get();
				if (emailDoc != null) {
					from = emailDoc.select("from").text();
					subject = emailDoc.select("subject").text();
					date = emailDoc.select("date").text();
					contents = emailDoc.select("contents").text();
					fileName = fileName.append(date.replaceAll("\\s+", "")
							.replace(",", ""));
					
					if(!isValidEmail(emailDoc, filterCriteria, criteriaNo)){
						return ;
					}
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdir();
					}

					File file = new File(path + "email" + this.emailCount);
					this.emailCount++;
					if (!file.exists()) {
						file.createNewFile();
					} else {
						fileName = fileName
								.append(date.replaceAll("\\s+", "").replace(
										",", "")).append("(")
								.append(emailCount++).append(")");
					}

					fileContent = fileContent.append(from).append("\n")
							.append(subject).append("\n").append(date)
							.append("\n").append(contents).append("\n");

					fileWriter = new FileWriter(file.getAbsoluteFile());
					bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(emailDoc.html());
				}

			} catch (IOException e) {
			} finally {
				try {
					if(bufferedWriter != null){
						bufferedWriter.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
}
