package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

public class Utility {
	public static String b64d(String paramString) {
		String str = new String(Base64.decode(paramString.getBytes(),
				Base64.NO_WRAP));
		if (str.endsWith("\n"))
			str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String b64e(String paramString) {
		String str = new String(Base64.encode(paramString.getBytes(),
				Base64.NO_WRAP));
		if (str.endsWith("\n"))
			str = str.substring(0, str.length() - 1);
		return str;
	}
}
