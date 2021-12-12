package org.unclesniper.oauth2;

import static org.unclesniper.util.ArgUtils.notNull;

public final class AzureUtils {

	private AzureUtils() {}

	public static String blobResource(String storageAccount) {
		StringBuilder builder = new StringBuilder();
		builder.append("https://");
		builder.append(notNull(storageAccount, "storageAccount"));
		builder.append(".blob.core.windows.net");
		return builder.toString();
	}

}
