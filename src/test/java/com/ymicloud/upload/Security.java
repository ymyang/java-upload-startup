package com.ymicloud.upload;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 服务器端实现加密算法。
 * 
 * @author 郭天良
 */
public class Security {

	private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	// 用户密码的截取点和偏移位数，从 point 位置，截取 offset 个字符，这样密码无法用 SHA256 字典进行猜测
	private final static int point = 25;
	private final static int offset = 4;

	// 服务器默认私钥
	final static String SERVER_KEY = "C2D5605BYMD8M0J00TW19UMCW17A0WSK12J4LS00MYYE9J0EKQNLAUU1RZ";

	private static int TIME_LENGTH = 16;

	// token 中密钥的长度
	final static int KEY_LENGTH = 6;
	// token 默认长度
	final static int TOKEN_LENGTH = 80;

	// token 过期时间
	private static int HOLD_TIME = 24 * 60 * 60 * 1000;

	private final static int SIZE = 256;
	// 加密用字符数组，做字符映射
	private final byte sbox[] = new byte[SIZE];
	private int i;
	private int j;

	/**
	 * 转换字符串为十六进制字符串
	 * 
	 * @param s
	 *            String to be converted
	 * @return Hex equivalent of the input string
	 */
	public static String byteStringToHexString(final String s) {
		StringBuilder hexString = new StringBuilder(65);
		for (int i = 0; i < s.length(); i++) {
			hexString.append(byteToHexChars(s.charAt(i)));
		}
		return hexString.toString();
	}

	/**
	 * 字符为十六进制字符
	 * 
	 * @param i
	 *            Number to be converted
	 * @return Hex equivalent, in two characters.
	 */
	private static String byteToHexChars(final int i) {
		final String s = "0" + Integer.toHexString(i);
		return s.substring(s.length() - 2);
	}

	/**
	 * 十六进制字符转换为字符
	 * 
	 * @param s
	 * @return Original string
	 */
	public static String hexStringToByteString(final String s) {
		StringBuilder byteString = new StringBuilder(65);
		for (int i = 0, len = s.length(); i < len; i += 2) {
			byteString.append((char) Integer.parseInt(s.substring(i, i + 2), 16));
		}
		return byteString.toString();
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String SHA256(final String text) {
		MessageDigest messageDigesSHA256 = null;
		try {
			messageDigesSHA256 = MessageDigest.getInstance("SHA-256");
		} catch (final NoSuchAlgorithmException ex) {
//			Logs.getLogger().error("ServerKey config error!", ex);
		}
		messageDigesSHA256.update(text.getBytes());

		byte byteData[] = messageDigesSHA256.digest();

		StringBuilder hexString = new StringBuilder(65);
		for (int i = 0, len = byteData.length; i < len; i++) {
			hexString.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return hexString.toString();
	}

	/**
	 * 构造数字和字符的随机数
	 * 
	 * @return
	 */
	public static String randomCharString() {
		return randomCharString(TOKEN_LENGTH);
	}

	/**
	 * 得到一个指定长度的随机字符
	 * 
	 * @param length
	 * @return
	 */
	public static String randomCharString(int length) {
		Random random = new Random();
		char[] radStr = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] randBuffer = new char[length];
		for (int i = 0; i < length; i++) {
			randBuffer[i] = radStr[random.nextInt(36)];
		}

		return new String(randBuffer);
	}

	/**
	 * 解密
	 * 
	 * @param plaintext
	 * @return
	 */
	private String codeDecode(final String plaintext) {
		byte x;
		StringBuilder mString = new StringBuilder(65);
		final int pl = plaintext.length();
		for (int k = 0; k < pl; k++) {
			i = i + 1 & 0xff;
			j = j + sbox[i] & 0xff;

			x = sbox[i];
			sbox[i] = sbox[j];
			sbox[j] = x;
			mString.append((char) (plaintext.charAt(k) ^ sbox[sbox[i] + sbox[j] & 0xff] & 0xff));
		}
		return mString.toString();
	}

	/**
	 * 用一个 Key，加密文本，对称加密
	 * 
	 * @param key
	 * @param plaintext
	 * @return
	 */
	public String codeDecode(final String key, final String plaintext) {

		setUp(key);
		return codeDecode(plaintext);
	}

	/**
	 * 设定 Key，用于加密
	 * 
	 * @param key
	 */
	private void setUp(final String key) {
		int k;
		byte x;

		for (i = 0; i < SIZE; i++) {
			sbox[i] = (byte) i;
		}

		final int kl = key.length();
		for (i = 0, j = 0, k = 0; i < SIZE; i++) {
			j = j + sbox[i] + key.charAt(k) & 0xff;
			k = (k + 1) % kl;

			x = sbox[i];
			sbox[i] = sbox[j];
			sbox[j] = x;
		}

		i = 0;
		j = 0;
	}

	/**
	 * 生成用户的令牌
	 * 
	 * @param userKey
	 * @return
	 */
	public static String CreateToken(final long userId, final long entId) {
		// 生成一个密钥
		String key = randomCharString(KEY_LENGTH);
		// 令牌产生时间
		long date = System.currentTimeMillis();
		// 令牌产生时间
		String timestamp = reverse(toHexadecimal(byteArrayFromLong(date)));
		// 企业ID
		String eid = reverse(toHexadecimal(byteArrayFromLong(entId)));
		// 用户ID
		String uid = cutLeftZero(toHexadecimal(byteArrayFromLong(userId)));

		String sha = SHA256(entId + key + SERVER_KEY).substring(0, TOKEN_LENGTH - KEY_LENGTH - 2 * TIME_LENGTH);
		StringBuilder token = new StringBuilder();

		// 拼接 Timestamp
		token.append(timestamp);
		// 拼接验证码
		token.append(sha);
		// 拼接随机的密钥
		token.append(key);
		// 拼接用户ID
		token.append(uid);
		// 拼接企业ID
		token.append(eid);

		return token.toString();
	}

	/**
	 * 验证用户令牌
	 * 
	 * @param userToken
	 * @return
	 */
	public static boolean CheckToken(String userToken) {

		if (isEmpty(userToken)) {
			return false;
		}

		if (userToken.length() < TOKEN_LENGTH) {
			return false;
		}

		// 检查令牌是否过期
		long tokenTimestamp = getTimestamp(userToken);
		long entId = getEnterpriseID(userToken);
		// 检查令牌是否过期
		long currentTimestamp = System.currentTimeMillis();
		if ((currentTimestamp - tokenTimestamp) > HOLD_TIME) {
			print("token expired, userToken:" + userToken);
			return false;
		}

		// 截取验证码
		String sha = userToken.substring(TIME_LENGTH, TOKEN_LENGTH - TIME_LENGTH - KEY_LENGTH);

		String key = userToken.substring(TOKEN_LENGTH - TIME_LENGTH - KEY_LENGTH, TOKEN_LENGTH - TIME_LENGTH);

		// 计算验证码
		String sha2 = SHA256(entId + key + SERVER_KEY).substring(0, TOKEN_LENGTH - KEY_LENGTH - 2 * TIME_LENGTH);

		// 验证令牌是否合法
		boolean check = sha.equals(sha2);
//		if (!check) {
//			Logs.getLogger().debug("check token error, userToken:" + userToken + ", mdKey:" + sha + ", userId:" + entId
//					+ ", key:" + key + ", newKey:" + sha2 + ", token:" + userToken);
//		}

		return check;
	}

	private static String cutLeftZero(String s) {
		int len = s.length();
		if (len > 0) {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) != '0') {
					return s.substring(i);
				}
			}
		}
		return s;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	private static byte[] byteArrayFromLong(final long number) {
		final byte b0 = (byte) (0xff & number);
		final byte b1 = (byte) (0xff & (number >> 8));
		final byte b2 = (byte) (0xff & (number >> 16));
		final byte b3 = (byte) (0xff & (number >> 24));
		final byte b4 = (byte) (0xff & (number >> 32));
		final byte b5 = (byte) (0xff & (number >> 40));
		final byte b6 = (byte) (0xff & (number >> 48));
		final byte b7 = (byte) (0xff & (number >> 56));

		return new byte[] { b7, b6, b5, b4, b3, b2, b1, b0 };
	}

	/**
	 * 
	 * @param byteArray
	 * @return
	 */
	private static long longFromByteArray(final byte[] byteArray) {

		if (byteArray == null || byteArray.length == 0) {
			throw new IllegalArgumentException("Cannot convert an empty array into an long");
		}
		long result = (0xff & byteArray[0]);
		for (int i = 1; i < byteArray.length; i++) {
			result = (result << 8) | (0xff & byteArray[i]);
		}

		return result;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private static String toHexadecimal(final byte[] message) {
		if (message == null) {
			return null;
		}
		final StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < message.length; i++) {
			int curByte = message[i] & 0xff;
			buffer.append(hexDigits[(curByte >> 4)]);
			buffer.append(hexDigits[curByte & 0xf]);
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private static byte[] fromHexadecimal(final String message) {
		if (message == null) {
			return null;
		}
		if ((message.length() % 2) != 0) {
			return null;
		}
		try {
			final byte[] result = new byte[message.length() / 2];
			for (int i = 0; i < message.length(); i = i + 2) {
				final int first = Integer.parseInt("" + message.charAt(i), 16);
				final int second = Integer.parseInt("" + message.charAt(i + 1), 16);
				result[i / 2] = (byte) (0x0 + ((first & 0xff) << 4) + (second & 0xff));
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private static String reverse(String s) {
		StringBuilder result = new StringBuilder(s);
		result.reverse();
		return result.toString();
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private static boolean isEmpty(final String string) {
		if (string == null || string.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private static String insertLeftZero(String s) {
		if (s.length() > 0 && s.length() <= TIME_LENGTH) {
			int count = TIME_LENGTH - s.length();

			StringBuilder sb = new StringBuilder(s);
			for (int i = 0; i < count; i++) {
				sb.insert(0, '0');
			}

			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 生成用户密码验证串， 在用户的密码的密文中截取子串，使用用户无法通过 SHA,MD5 数据库字典猜出用户密码
	 * 
	 * @param mdPassword
	 * @return
	 */
	public static String CreatePassword(String mdPassword) {
		// 构造用户密码的验证串
		return mdPassword.substring(0, point) + mdPassword.substring(point + offset);
	}

	/**
	 * 从用户的Token 中取得用户的id
	 * @param userToken
	 * @return
	 */
	public static long getUserID(final String userToken) {
		if (isEmpty(userToken)) {
			return -1;
		}

		return longFromByteArray(fromHexadecimal(insertLeftZero(userToken.substring(TOKEN_LENGTH - TIME_LENGTH,
				userToken.length() - TIME_LENGTH))));
	}

	/**
	 * 获取用户Token的时间戳
	 * @param userToken
	 * @return
	 */
	public static long getTimestamp(final String userToken) {
		if (isEmpty(userToken)) {
			return -1;
		}

		return longFromByteArray(fromHexadecimal(reverse(userToken.substring(0, TIME_LENGTH))));
	}

	/**
	 * 获取用户企业ID
	 * @param userToken
	 * @return
	 */
	public static long getEnterpriseID(final String userToken) {
		if (isEmpty(userToken)) {
			return -1;
		}
		return longFromByteArray(fromHexadecimal(reverse(userToken.substring(userToken.length() - TIME_LENGTH))));
	}

	public static void main(String[] args) {

//		String token = CreateToken(10246, 10191);
//		System.out.println("token=" + token);
//
//		System.out.println(getTimestamp(token));
//
//		System.out.println(getEnterpriseID(token));
//
//		System.out.println(getUserID(token));
//
//		boolean auth = CheckToken(token);
//
//		System.out.println(auth);
		
		System.out.println(CreatePassword(SHA256("yliyun")));

	}

	private static void print(String msg) {
//		Logs.getLogger().debug(msg);
	}
}
