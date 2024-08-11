package dev.greenhouseteam.enchiridion.client;

import dev.greenhouseteam.enchiridion.client.platform.EnchiridionClientPlatformHelper;

public class EnchiridionClient {
    private static EnchiridionClientPlatformHelper helper;

    public static void init(EnchiridionClientPlatformHelper helper)  {
        if (EnchiridionClient.helper != null)
            return;

        EnchiridionClient.helper = helper;
    }

    public static EnchiridionClientPlatformHelper getHelper() {
        return helper;
    }

}
