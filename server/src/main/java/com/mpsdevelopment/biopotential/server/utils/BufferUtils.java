package com.mpsdevelopment.biopotential.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class BufferUtils {
	
	private static final Logger LOGGER = LoggerUtil.getLogger(BufferUtils.class);

	public static byte[] readBytes(String filename) {
		try {
			return FileUtils.readFileToByteArray(new File(filename));
		} catch (IOException e) {
			LOGGER.error("Cannot read heightmap file %s", filename);
			LOGGER.printStackTrace(e);
		}
		return null;
	}

	/**
	 * Writes buffer to file
	 * 
	 * @param buffer
	 * @param filename
	 */
	public static void writeBuffer(ByteBuffer buffer, String filename) {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(new File(filename));
			stream.write(buffer.array());
			LOGGER.info("Height buffer successfully saved to file %s with size ", filename, new File(filename).length());
		} catch (IOException e) {
			LOGGER.info("Cannot save height buffer to file %s", filename);
			LOGGER.printStackTrace(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static ByteBuffer readBuffer(String filename) {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filename, "r");
			FileChannel inChannel = file.getChannel();
			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			buffer = buffer.load();
			return buffer;
		} catch (IOException e) {
			LOGGER.info("Cannot read height buffer from file %s", filename);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					LOGGER.printStackTrace(e);
				}
			}
		}
		return null;
	}

	/**
	 * Converts bytes array to float array
	 * 
	 * @param bytes
	 * @return
	 */
	public static float[] toFloatArray(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		FloatBuffer fb = buffer.asFloatBuffer();

		float[] floatArray = new float[fb.capacity()];
		fb.get(floatArray);

		return floatArray;
	}
	
	public static double[] toDoubleArray(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		DoubleBuffer fb = buffer.asDoubleBuffer();

		double[] array = new double[fb.capacity()];
		fb.get(array);

		return array;
	}

	/**
	 * Converts ByteBuffer to float array
	 * 
	 * @param buffer
	 * @return
	 */
	public static float[] toFloatArray(ByteBuffer buffer) {
		buffer = ByteBuffer.wrap(buffer.array());
		FloatBuffer fb = buffer.asFloatBuffer();

		float[] floatArray = new float[fb.capacity()];
		fb.get(floatArray);

		return floatArray;
	}

	/**
	 * Converts ByteBuffer to FloatBuffer
	 * 
	 * @param buffer
	 * @return
	 */
	public static FloatBuffer toFloatBuffer(ByteBuffer buffer) {
		buffer = ByteBuffer.wrap(buffer.array());
		return buffer.asFloatBuffer();
	}
}
