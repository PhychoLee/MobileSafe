package com.llf.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	/**
	 * 工具方法：将Stream转换为String
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream in) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			bos.write(buffer, 0, len);
		}
		
		String result = bos.toString();
		in.close();
		bos.close();
		return result;
	}
}
