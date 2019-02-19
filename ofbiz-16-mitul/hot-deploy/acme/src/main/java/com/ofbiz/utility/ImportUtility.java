/**
 * 
 */
package com.ofbiz.utility;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.ofbiz.base.util.Debug;

/**
 * @author Gaurav Rai
 *
 */
public class ImportUtility {
	
	public static final String module = ImportUtility.class.getName();
	
	public static void moveXMLFilesFromDir(String dirPath, String destDirPath) throws IOException {

		Debug.logInfo(" Method moveXMLFilesFromDir starts", module);
		File dir = new File(dirPath);
		File destDir = new File(destDirPath);
		for (File file : dir.listFiles()) {
			FileUtils.copyFileToDirectory(file, destDir);
			//file.delete();
		}
		Debug.logInfo(" Method moveXMLFilesFromDir ends", module);
	}


}
