package com.thredim.regserver.utils;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Created by wk on 2017/2/14.
 */

public class RSAUtils {
	private static String RSA = "RSA";
	/** *//**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 128;
	/**
	 * 随机生成RSA密钥对(默认密钥长度为1024)
	 *
	 * @return
	 */
	public static KeyPair generateRSAKeyPair()
	{
		return generateRSAKeyPair(1024);
	}

	/**
	 * 随机生成RSA密钥对
	 *
	 * @param keyLength
	 *            密钥长度，范围：512～2048<br>
	 *            一般1024
	 * @return
	 */
	public static KeyPair generateRSAKeyPair(int keyLength)
	{
		try
		{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
			kpg.initialize(keyLength);
			return kpg.genKeyPair();
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用公钥加密 <br>
	 * 每次加密的字节数，不能超过密钥的长度值减去11
	 *
	 * @param data
	 *            需加密数据的byte数据
	 * @param publicKey 公钥
	 * @return 加密后的byte型数据
	 */
	public static byte[] encryptData(byte[] data, PublicKey publicKey)
	{

		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// 编码前设定编码方式及密钥
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			// 传入编码数据并返回编码结果
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用私钥解密
	 *
	 * @param encryptedData
	 *            经过encryptedData()加密返回的byte数据
	 * @param privateKey
	 *            私钥
	 * @return
	 */
	public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			// 传入编码数据并返回编码结果
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
	 *
	 * @param keyBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException,
			InvalidKeySpecException
	{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 通过私钥byte[]将公钥还原，适用于RSA算法
	 *
	 * @param keyBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException,
			InvalidKeySpecException
	{
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 使用N、e值还原公钥
	 *
	 * @param modulus
	 * @param publicExponent
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKey(String modulus, String publicExponent)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		BigInteger bigIntModulus = new BigInteger(modulus);
		BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 使用N、d值还原私钥
	 *
	 * @param modulus
	 * @param privateExponent
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKey(String modulus, String privateExponent)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		BigInteger bigIntModulus = new BigInteger(modulus);
		BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 从字符串中加载公钥
	 *
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static PublicKey loadPublicKey(String publicKeyStr) throws Exception
	{
		try
		{
			byte[] buffer = Base64Utils.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e)
		{
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e)
		{
			throw new Exception("公钥非法");
		} catch (NullPointerException e)
		{
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从字符串中加载私钥<br>
	 * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
	 *
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception
	{
		try
		{
			byte[] buffer = Base64Utils.decode(privateKeyStr);
			// X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e)
		{
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e)
		{
			throw new Exception("私钥非法");
		} catch (NullPointerException e)
		{
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 从文件中输入流中加载公钥
	 *
	 * @param in
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static PublicKey loadPublicKey(InputStream in) throws Exception
	{
		try
		{
			return loadPublicKey(readKey(in));
		} catch (IOException e)
		{
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e)
		{
			throw new Exception("公钥输入流为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 *
	 * @param in
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(InputStream in) throws Exception
	{
		try
		{
			return loadPrivateKey(readKey(in));
		} catch (IOException e)
		{
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e)
		{
			throw new Exception("私钥输入流为空");
		}
	}

	/**
	 * 读取密钥信息
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String readKey(InputStream in) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String readLine = null;
		StringBuilder sb = new StringBuilder();
		while ((readLine = br.readLine()) != null)
		{
			if (readLine.charAt(0) == '-')
			{
				continue;
			} else
			{
				sb.append(readLine);
				sb.append('\r');
			}
		}

		return sb.toString();
	}

	/**
	 * 打印公钥信息
	 *
	 * @param publicKey
	 */
	public static void printPublicKeyInfo(PublicKey publicKey)
	{
		RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
		System.out.println("----------RSAPublicKey----------");
		System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
		System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
		System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
		System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
	}

	public static void printPrivateKeyInfo(PrivateKey privateKey)
	{
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
		System.out.println("----------RSAPrivateKey ----------");
		System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
		System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
		System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
		System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());

	}



	private  static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9FN1w8gfXSBP1/" + "\r"
			+ "fWtC4gicvB7t+XZ20Qn3eBOaMT1zYf6QtUQ1aAQKIlVDmyidA1/BOgwp07Rvc6V/" + "\r"
			+ "imAEp4tOGtrP8vedgliVuqMcLeNONSdlzSW66alcayjHrb4+5IYGV9vzMk7qGLHg" + "\r"
			+ "ZX++HJBUKkb1piqATvPJNFlhf1vJAgMBAAECgYA736xhG0oL3EkN9yhx8zG/5RP/" + "\r"
			+ "WJzoQOByq7pTPCr4m/Ch30qVerJAmoKvpPumN+h1zdEBk5PHiAJkm96sG/PTndEf" + "\r"
			+ "kZrAJ2hwSBqptcABYk6ED70gRTQ1S53tyQXIOSjRBcugY/21qeswS3nMyq3xDEPK" + "\r"
			+ "XpdyKPeaTyuK86AEkQJBAM1M7p1lfzEKjNw17SDMLnca/8pBcA0EEcyvtaQpRvaL" + "\r"
			+ "n61eQQnnPdpvHamkRBcOvgCAkfwa1uboru0QdXii/gUCQQDGmkP+KJPX9JVCrbRt" + "\r"
			+ "7wKyIemyNM+J6y1ZBZ2bVCf9jacCQaSkIWnIR1S9UM+1CFE30So2CA0CfCDmQy+y" + "\r"
			+ "7A31AkB8cGFB7j+GTkrLP7SX6KtRboAU7E0q1oijdO24r3xf/Imw4Cy0AAIx4KAu" + "\r"
			+ "L29GOp1YWJYkJXCVTfyZnRxXHxSxAkEAvO0zkSv4uI8rDmtAIPQllF8+eRBT/deD" + "\r"
			+ "JBR7ga/k+wctwK/Bd4Fxp9xzeETP0l8/I+IOTagK+Dos8d8oGQUFoQJBAI4Nwpfo" + "\r"
			+ "MFaLJXGY9ok45wXrcqkJgM+SN6i8hQeujXESVHYatAIL/1DgLi+u46EFD69fw0w+" + "\r" + "c7o0HLlMsYPAzJw="
			+ "\r";
	public static String getDecryptShader(String encrypt){
		try {
			PrivateKey privateKey = loadPrivateKey(PRIVATE_KEY);
		    byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(encrypt), privateKey);
			String decryptStr = new String(decryptByte);
//            Log.d("RSA","decryptStr:"+decryptStr);
            return decryptStr;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public static void main (String[] args) throws Exception {
//        Map<String, String> keyMap = RSAUtils.createKeys(1024);
//        String  publicKey = keyMap.get("publicKey");
//        String  privateKey = keyMap.get("privateKey");

		String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaUGFIbOzTN5yTK89jDtjldyhK" + "\r" +
				"nicFLMCTsmSRmyY8LbPvfJ4Y04KgDgwuEY_puaHYCEi4A1iuC0AMC2NRbdfmV9YT" + "\r" +
				"Hc842zu5ERt2iHlhi2jMoAOC7FSESc_Yqz2R8RYnM5kbo1kCKTtr-HgMOS4N0gD3" + "\r" +
				"8-8mRtge_gT1d9Ry3QIDAQAB" + "\r";
		String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJpQYUhs7NM3nJMr" + "\r" +
				"z2MO2OV3KEqeJwUswJOyZJGbJjwts-98nhjTgqAODC4Rj-m5odgISLgDWK4LQAwL" + "\r" +
				"Y1Ft1-ZX1hMdzzjbO7kRG3aIeWGLaMygA4LsVIRJz9irPZHxFiczmRujWQIpO2v4" + "\r" +
				"eAw5Lg3SAPfz7yZG2B7-BPV31HLdAgMBAAECgYBuE_Sxk3IE4eDBYzkXMpoqZivJ" + "\r" +
				"vrObD2d4dIMlEwXdW8TFi6HDl-3QevTZUBHW6mK3FoG5MjnqPYA8l0sL5U1imajP" + "\r" +
				"gyNK902rywdXLOx5aLOi4KIyKovb99HdWIelNbZlUurYuxg3qWgX-Z0QpiuJRHyc" + "\r" +
				"WFQvrIFXufa8hy5tCQJBAMcKR2OJMpQjgTvc0wOn-CKQzsl-MHkypV4OMZ6-pxku" + "\r" +
				"2OAm5ZdwY1Ey_skl156kH2b__hEU1TwY3f0z_qXKs0MCQQDGeXQGe7L2q-i2fFGN" + "\r" +
				"tn1_8koPv9gwnxd3mhDrG-ny5u2MS97b5sP2OoNGwJ7tumsYDhS5Wm56ETo3geOR" + "\r" +
				"SQ9fAkBrZMdqFF_fAF09kIOmqF3BQMLigxzieAoz-pKZZaNBej54i2Q71t8vs8U3" + "\r" +
				"gRybM1DiQWOzkbsQJxa4BPL0P6-DAkA0ARbZrAfUgm7TgfeMfkicAcxOHCJ1evoX" + "\r" +
				"KJNOR14he0i6_hZC4FBwcBx9qXL7L2FoSCg9-OgBrAnpCWurYc1ZAkEAn3RvmkXR" + "\r" +
				"wNHGm3jHUxxTEZvVMjtEWOajx1ndP8FSKtoNIjBv6lOhHP69gtgweqNQK7MpdTKG" + "\r" +
				"cb72CsPoPrU3iQ=" + "\r";
//		String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRTdcPIH10gT9f31rQuIInLwe" + "\r"
//				+ "7fl2dtEJ93gTmjE9c2H+kLVENWgECiJVQ5sonQNfwToMKdO0b3Olf4pgBKeLThra" + "\r"
//				+ "z/L3nYJYlbqjHC3jTjUnZc0luumpXGsox62+PuSGBlfb8zJO6hix4GV/vhyQVCpG" + "\r"
//				+ "9aYqgE7zyTRZYX9byQIDAQAB" + "\r";
//		String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9FN1w8gfXSBP1/" + "\r"
//				+ "fWtC4gicvB7t+XZ20Qn3eBOaMT1zYf6QtUQ1aAQKIlVDmyidA1/BOgwp07Rvc6V/" + "\r"
//				+ "imAEp4tOGtrP8vedgliVuqMcLeNONSdlzSW66alcayjHrb4+5IYGV9vzMk7qGLHg" + "\r"
//				+ "ZX++HJBUKkb1piqATvPJNFlhf1vJAgMBAAECgYA736xhG0oL3EkN9yhx8zG/5RP/" + "\r"
//				+ "WJzoQOByq7pTPCr4m/Ch30qVerJAmoKvpPumN+h1zdEBk5PHiAJkm96sG/PTndEf" + "\r"
//				+ "kZrAJ2hwSBqptcABYk6ED70gRTQ1S53tyQXIOSjRBcugY/21qeswS3nMyq3xDEPK" + "\r"
//				+ "XpdyKPeaTyuK86AEkQJBAM1M7p1lfzEKjNw17SDMLnca/8pBcA0EEcyvtaQpRvaL" + "\r"
//				+ "n61eQQnnPdpvHamkRBcOvgCAkfwa1uboru0QdXii/gUCQQDGmkP+KJPX9JVCrbRt" + "\r"
//				+ "7wKyIemyNM+J6y1ZBZ2bVCf9jacCQaSkIWnIR1S9UM+1CFE30So2CA0CfCDmQy+y" + "\r"
//				+ "7A31AkB8cGFB7j+GTkrLP7SX6KtRboAU7E0q1oijdO24r3xf/Imw4Cy0AAIx4KAu" + "\r"
//				+ "L29GOp1YWJYkJXCVTfyZnRxXHxSxAkEAvO0zkSv4uI8rDmtAIPQllF8+eRBT/deD" + "\r"
//				+ "JBR7ga/k+wctwK/Bd4Fxp9xzeETP0l8/I+IOTagK+Dos8d8oGQUFoQJBAI4Nwpfo" + "\r"
//				+ "MFaLJXGY9ok45wXrcqkJgM+SN6i8hQeujXESVHYatAIL/1DgLi+u46EFD69fw0w+" + "\r"
//				+ "c7o0HLlMsYPAzJw="+ "\r";
		System.out.println("公钥： \n\r" + PUCLIC_KEY);
		System.out.println("私钥： \n\r" + PRIVATE_KEY);

		String customerNo = "ThreDim";                                  //客户号
		String equipmentId = "b56a48887c434d75b97912ba4a6d0b0d";        //设备唯一标识
		String pollCode = "700A-12358-D2500-E9DC5-AEB99";               //批量注册号
		Map<String, String> content = new HashMap<>();
		content.put("customerNo", customerNo);
		content.put("equipmentId", equipmentId);
		content.put("pollCode", pollCode);
		String str = JSON.toJSONString(content);

		System.out.println("\r明文：\r\n" + str);
		System.out.println("\r明文大小：\r\n" + str.getBytes().length);
		PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
		byte[] encryptByte = RSAUtils.encryptData(str.getBytes(), publicKey);
		String encodedData = Base64Utils.encode(encryptByte);

//		String encodedData = RSAUtils2.publicEncrypt(str, RSAUtils2.getPublicKey(publicKey));
		System.out.println("密文：\r\n" + encodedData);
		PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
		byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(encodedData), privateKey);
		String decodedData = new String(decryptByte);

//		String decodedData = RSAUtils2.privateDecrypt(encodedData, RSAUtils2.getPrivateKey(privateKey));
		System.out.println("解密后文字: \r\n" + decodedData);
	}
}