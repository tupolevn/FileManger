package com.nim.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileManagement {
	private static final Logger logger = LogManager.getLogger(FileManagement.class);
	// static int i = 0;
	static List<File> filesToDelete = new ArrayList<>();
	static List<File> jpgFiles = new ArrayList<>();
	static List<File> arwFiles = new ArrayList<>();
	// static List<String> sourceOfTruthFileNames = new ArrayList<>();
	static Set<String> sourceOfTruthFileNameSet = new HashSet<>();
	
	static Map<String, File> sourceOfTruthFileNameMap = new HashMap<>();
	static List<File> sourceOfTruthFileNameList = new ArrayList<>();  
	static List<String> matchedFileNameList = new ArrayList<>();

	private static final String JPG_FILE_EXTENTION = ".JPG";
	private static final String ARW_FILE_EXTENTION = ".ARW";
	public static void main(String[] args) {

		logger.info("Test");
		// The list of files should only exist in this directory.
		// Similar files in other locations can be considered as duplicates.
		// This should be in a different drive
		// this is source of truth
		final File sourceOfTruthFolder = new File("E:\\a7iii-originals");

		// locations to scan
		// final File rootFolderForScaningDuplicates = new File("D:\\Photos\\a7iii");
		final File rootFolderForScaningDuplicates = new File("E:\\allRemainingRaws2");
		
		// location to copy duplicate files
		final File destinationFolderForDuplicateJpgFiles = new File("D:\\FileManagement");
		
		// Destination to copy raw files for jpeg images in sourceOfTruthFolder
		final File rawFileDestinationFolder = new File("E:\\a7iii-originals-raw-files-2");

		buildSourceOfTruth(sourceOfTruthFolder);

		buildIndex(rootFolderForScaningDuplicates);

		// printList(filesToDelete);

		findAndMoveDuplicateJpg(jpgFiles, destinationFolderForDuplicateJpgFiles);

		deleteFiles(filesToDelete);

		//findAndMoveArwFile(jpgFiles, arwFiles, rawFileDestinationFolder);
		findAndMoveArwFile(sourceOfTruthFileNameList, arwFiles, rawFileDestinationFolder);
		
		deleteEmptyDirectories(rootFolderForScaningDuplicates);

		// To-Do
		// if files are not identical but the name is same, move them to a separate
		// folder
	}

	private static void buildSourceOfTruth(File sourceOfTruthFolder) {
		logger.info("Building SourceOfTruth...");
		for (final File folder : sourceOfTruthFolder.listFiles()) {
			logger.info("Scaning folder: {}", folder.getAbsolutePath());
			for (final File jpgFile : folder.listFiles()) {
				logger.info("File: {}", jpgFile.getAbsolutePath());
				// System.out.println("File >>> " + jpgFile.getPath() + " >>>>> " +
				// jpgFile.getName());
				// String _jpgFileName = jpgFile.getName().substring(0,
				// jpgFile.getName().length() - JPG_FILE_EXTENTION.length());
				sourceOfTruthFileNameSet.add(jpgFile.getName());
				sourceOfTruthFileNameMap.put(jpgFile.getName(), jpgFile);
				sourceOfTruthFileNameList.add(jpgFile);
			}
		}

		logger.info("Size of source of truth is: {}", sourceOfTruthFileNameSet.size());
	}

	private static void buildIndex(final File rootFolder) {
		logger.info("Building Index...");
		int i = 0;
		for (final File fileEntry : rootFolder.listFiles()) {

			logger.info("File entry: {}, File: {}", i++, fileEntry.getPath());
			// System.out.println(i++ + " file >>> " + fileEntry.getPath() + " >>>>> " +
			// fileEntry.getName());

			if (fileEntry.isDirectory()) {
				buildIndex(fileEntry);
			} else {

				if (fileEntry.getName().startsWith(".")) {
					filesToDelete.add(fileEntry);
				}

				if (fileEntry.getName().toUpperCase().endsWith(JPG_FILE_EXTENTION)) {
					jpgFiles.add(fileEntry);
				}

				if (fileEntry.getName().toUpperCase().endsWith(ARW_FILE_EXTENTION)) {
					arwFiles.add(fileEntry);
				}

			}
		}

		logger.info("filesToDelete SIZE: {}", filesToDelete.size());
		logger.info("jpgFiles SIZE: {}", jpgFiles.size());
		logger.info("arwFiles SIZE: {}", arwFiles.size());

		logger.info("Building Index Completed...");

	}

	private static void deleteFiles(List<File> list) {
		logger.info("deleteFiles list size: {}", list.size());

		for (File file : list) {

			try {
				file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				return;
			}
			logger.info("Delete: {}", file.getAbsolutePath());
		}
	}

	private static void findAndMoveDuplicateJpg(List<File> jpgFiles, File destinationFolder) {
		logger.info("Start findAndMoveDuplicateJpg destinationFolder: {}", destinationFolder);
		int i = 0;
		int numberOfFilesMoved = 0;
		for (File jpgFile : jpgFiles) {
			++i;
			// System.out.println(" " + jpgFile.getAbsolutePath());

			if (sourceOfTruthFileNameSet.contains(jpgFile.getName())) {
				try {
					++numberOfFilesMoved;

					// System.out.println("D >" + jpgFile.getAbsolutePath());
					logger.info("Found duplicate file: {}", jpgFile.getAbsolutePath());
					String _jpgFileName = jpgFile.getName().substring(0,
							jpgFile.getName().length() - JPG_FILE_EXTENTION.length());

//					File destinateionFolderforDuplicate = new File(destinationFolder.getAbsolutePath()+"\\"+ _jpgFileName);
//					if (!destinateionFolderforDuplicate.exists()){
//						destinateionFolderforDuplicate.mkdirs();
//					}

					File newLocation = new File(
							destinationFolder.getAbsolutePath() + "\\" + _jpgFileName + "__" + i + JPG_FILE_EXTENTION);
					// logger.info("New location for file: {} is: {}", _jpgFileName,
					// newLocation.getAbsolutePath());
					// Create in new folder
					// File newLocation = new File(destinationFolder.getAbsolutePath()+"\\"+
					// _jpgFileName + "\\"+_jpgFileName +"__"+ i + JPG_FILE_EXTENTION);
					logger.info("Comparing files with equal names ...");
					logger.info("Original {}", sourceOfTruthFileNameMap.get(jpgFile.getName()).getAbsolutePath());
					logger.info("Duplicate {}", jpgFile.getAbsolutePath());
					logger.info("Coparing Files");
					boolean isEqualFile = ImageDuplicateFinder.isEqualImage(jpgFile, sourceOfTruthFileNameMap.get(jpgFile.getName()));
					if(!isEqualFile){
						File destFile = new File(
								destinationFolder.getAbsolutePath() + "\\unequal\\" + _jpgFileName + "__" + i + JPG_FILE_EXTENTION);
						logger.info("Coping un equal file to: {}", destFile.getAbsolutePath());
						logger.info("Original un equal file at: {}", sourceOfTruthFileNameMap.get(jpgFile.getName()).getAbsolutePath());
						FileUtils.copyFile(jpgFile, destFile, true);
						//logger.info("Coping un equal file to: {}", destFile.getAbsolutePath());
					}
					FileUtils.moveFile(jpgFile, newLocation);
					logger.info("Moved to: {}", newLocation.getAbsolutePath());
					logger.info("Number of files moved: {}", numberOfFilesMoved);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// String _jpgFileName = jpgFile.getName().substring(0,
			// jpgFile.getName().length() - JPG_FILE_EXTENTION.length());
			// System.out.println(jpgFile.getName() + " >>>> " + _jpgFileName);
		}
		logger.info("Total Number of files moved: {}", numberOfFilesMoved);
	}

	private static void findAndMoveArwFile(List<File> jpgFiles, List<File> arwFiles, File rawDestinationFolder) {
		logger.info("findAndMoveArwFile {}", rawDestinationFolder);
		int i = 0;

		for (File jpgFile : jpgFiles) {

			String _jpgFileName = jpgFile.getName().substring(0,
					jpgFile.getName().length() - JPG_FILE_EXTENTION.length());
			//System.out.println(jpgFile.getName() + " >>>> " + _jpgFileName);

			for (File arwFile : arwFiles) {
				String _arwFileName = arwFile.getName().substring(0,
						arwFile.getName().length() - ARW_FILE_EXTENTION.length());

				if (_jpgFileName.toUpperCase().equals(_arwFileName.toUpperCase())) {
					logger.info("Match Found JPEG: {}" , jpgFile.getAbsolutePath());
					logger.info("Match Found ARW: {}" , arwFile.getAbsolutePath());
					logger.info("Match Found parent: {}" , jpgFile.getParentFile());
					logger.info("Match Found parent: {}" , jpgFile.getParentFile().getName());

					try {

						File locationToCopy = new File(
								rawDestinationFolder.getAbsolutePath() + "\\" + jpgFile.getParentFile().getName());
						if (!locationToCopy.exists()) {
							locationToCopy.mkdirs();
						}

						//copy jpg file to compare if the same image raw file is found.
						//go to folder and compare manually
						FileUtils.copyFileToDirectory(jpgFile, locationToCopy);


						if (matchedFileNameList.contains(_jpgFileName)) {
							//FileUtils.copyFileToDirectory(jpgFile, FileUtils.getFile(
									//rawDestinationFolder.getAbsolutePath() + _jpgFileName + i + JPG_FILE_EXTENTION));
							logger.info("*** Copy do nothing to: {}" , jpgFile.getAbsolutePath());
						} else {
							logger.info("Copy raw file from: {} to: {}" , arwFile.getAbsolutePath(), locationToCopy.getAbsolutePath());
							//FileUtils.copyFileToDirectory(arwFile, locationToCopy);
							FileUtils.moveFileToDirectory(arwFile, locationToCopy, true);
							Thread.sleep(3000);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block IO
						e.printStackTrace();
					}

					matchedFileNameList.add(_jpgFileName);
				}

				// if(jpgFile.getName().replace(JPG_FILE_EXTENTION, 0))

			}

		}
	}

	private static void printList(List<File> fileList) {
		System.out.println("SIZE: " + fileList.size());

		for (File file : fileList) {
			System.out.println(file.getAbsolutePath());
		}
	}

	private static void deleteEmptyDirectories(File directory) {
		// List all files and directories in the current directory
		//logger.info("deleteEmptyDirectories directory: {}" , directory);
		File[] files = directory.listFiles();

		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					// Recursive call to handle subdirectories
					deleteEmptyDirectories(file);
				}
			}
		}

		// After processing subdirectories, check if the current directory is empty
		if (directory.isDirectory() && directory.list().length == 0) {
			// Delete the empty directory
			if (directory.delete()) {
				//System.out.println("Deleted empty directory: " + directory.getAbsolutePath());
				logger.info("Deleted empty directory: {}", directory.getAbsolutePath());
			} else {
				logger.info("Failed to delete directory: {}", directory.getAbsolutePath());
				//System.out.println("Failed to delete directory: " + directory.getAbsolutePath());
			}
		}
	}

	private static void listFilesForFolder(final File folder) {
		String regexPattern = "^DSC\\d{5}\\.jpg$";
		Pattern pattern = Pattern.compile(regexPattern);

		// Replace this with your actual list of file names
		String[] fileNames = { "DSC04523.jpg", "DSC12345.jpg", "image.jpg" };

		for (String fileName : fileNames) {
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()) {
				System.out.println("Found matching file: " + fileName);
			}
		}
	}

}
