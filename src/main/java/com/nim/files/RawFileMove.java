package com.nim.files;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RawFileMove {
	private static final Logger logger = LogManager.getLogger(RawFileMove.class);


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final File rootFolder = new File("D:\\");
		final File rawFileDestinationFolder = new File("E:\\allRemainingRaws2");
		buildIndex(rootFolder, rawFileDestinationFolder);
		
	}

	private static void buildIndex(final File rootFolder, final File rawFileDestinationFolder) throws IOException {
		logger.info("Scaning folder Index...");
		int i = 0;
		for (final File fileEntry : rootFolder.listFiles()) {

			logger.info("File:  {}", fileEntry.getPath());

			if (fileEntry.isDirectory()) {
				buildIndex(fileEntry, rawFileDestinationFolder);
			} else {
				String name = fileEntry.getName().toLowerCase();
				if (name.endsWith(".arw") && !fileEntry.getAbsolutePath().contains( "$RECYCLE.BIN")) {
					
					File locationToCopy = new File(
							rawFileDestinationFolder.getAbsolutePath() + "\\" + fileEntry.getParentFile().getName());
					if (!locationToCopy.exists()) {
						locationToCopy.mkdirs();
					}

					logger.info("Moving file from: {} to: {}",fileEntry.getAbsolutePath(), locationToCopy);
					FileUtils.moveFileToDirectory(fileEntry, locationToCopy, true);
					logger.info("Number of files moved: {}", ++i);
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}



		logger.info("Building Index Completed...");

	}
}
