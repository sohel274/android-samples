/*
Copyright 2009-2015 Urban Airship Inc. All rights reserved.


Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE URBAN AIRSHIP INC ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL URBAN AIRSHIP INC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.urbanairship.richpush.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.urbanairship.richpush.sample.preference.PushPreferencesActivity;

/**
 * An activity that creates and launches a task stack from a deep link uri.
 * <p/>
 * The activity will operate on any URI that it is started with, only parsing
 * the URI's path and query parameters. URI filtering should be defined in the
 * AndroidManifest.xml for the ParseDeepLinkActivity entry by defining an intent
 * filter.
 * <p/>
 * Handles URLs of the following syntax:
 * <url> := vnd.urbanairship.richpush://deeplink/<deep-link>
 * <deep-link> := home | preferences | inbox | inbox?<message_id>
 * <message-id> := A rich push message ID
 * <p/>
 * Examples:
 * <p/>
 * // Deep link to inbox
 * vnd.urbanairship.richpush://deeplink/inbox
 * <p/>
 * // Deep link to home
 * vnd.urbanairship.richpush://deeplink/home
 * <p/>
 * // Deep link to preferences
 * vnd.urbanairship.richpush://deeplink/preferences
 * <p/>
 * // Deep link to the message 'VJO7DpCEQ2i7LdqOxLbFxw'
 * vnd.urbanairship.richpush://deeplink/inbox?message_id=VJO7DpCEQ2i7LdqOxLbFxw
 */
public class ParseDeepLinkActivity extends Activity {

    private static final String TAG = "ParseDeepLinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Parse the deep link
        String deepLink = getDeepLink();

        if (deepLink == null) {
            // Fall back to main activity
            startActivity(new Intent(this, MainActivity.class));
        }

        switch (deepLink) {
            case "/preferences":
                startActivity(new Intent(getApplicationContext(), PushPreferencesActivity.class));
                break;
            case "/home":
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_NAVIGATE_ITEM, MainActivity.HOME_ITEM));

                break;
            case "/inbox":
                Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_NAVIGATE_ITEM, MainActivity.INBOX_ITEM);

                // Check for an optional Message ID
                String messageId = getDeepLinkQueryParameter("message_id");
                if (messageId != null && messageId.length() > 0) {
                    launchIntent.putExtra(MainActivity.EXTRA_MESSAGE_ID, messageId);
                }
                startActivity(launchIntent);
                break;
            default:
                Log.e(TAG, "Unknown deep link: " + deepLink + ". Falling back to main activity.");
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

        finish();
    }

    private String getDeepLink() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            return intent.getData().getPath();
        }

        return null;
    }

    private String getDeepLinkQueryParameter(String key) {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            return intent.getData().getQueryParameter(key);
        }

        return null;
    }
}
