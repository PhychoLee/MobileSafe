package com.llf.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String MD5Digest(String password) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			byte[] digest = instance.digest(password.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);

				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取文件的MD5
	 * @param sourceDir
     */
	public static String getFileMD5(String sourceDir) {
		File file = new File(sourceDir);

		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			MessageDigest instance = MessageDigest.getInstance("MD5");
			while ((len = fis.read(buffer))!= -1){
				instance.digest(buffer, 0, len);
			}

			byte[] digest = instance.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);

				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
