package com.example.unitokyodemo0911;


import android.content.Context;
import android.util.Log;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Dialogflow の detectIntent に関するクラス
 */
public class DetectIntent {


    private String TAG = "DetectIntent";
    String PROJECT_ID = "universityoftokyo"; // TODO: GoogleCloudで作成したプロジェクトIDを指定（全員共通で同じ名前で作成？）
    String LANGUAGE_CODE = "ja";

    public static final List<String> SCOPE =
            Collections.singletonList("https://www.googleapis.com/auth/cloud-platform");

    /**
     * セッションを取得する。
     * TODO : Dialogflow のセッションはクライアント毎にユニークとなるよう処理を記述する。
     */


    private String getSession() {
        return "hogehoge";
    }

    private SessionsClient sessionsClient;
    private ContextsClient contextClient;

    DetectIntent(Context context) {
        final InputStream stream = context.getResources().openRawResource(R.raw.credential);
        try {
            final GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(SCOPE);

            sessionsClient = createSessions(credentials);
            contextClient = createContexts(credentials);
        } catch (IOException e) {
            Log.e(TAG, "Failed to obtain access token.", e);
        }

    }

    /**
     * SessionClient を作成する。
     */
    private SessionsClient createSessions(GoogleCredentials credentials) {
        SessionsSettings sessionsSetting = null;
        SessionsClient wSessionsClient = null;
        try {
            sessionsSetting = SessionsSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            wSessionsClient = SessionsClient.create(sessionsSetting);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wSessionsClient;
    }

    /**
     * ContextsClient を作成する。
     */
    private ContextsClient createContexts(GoogleCredentials credentials) {
        ContextsClient wContextsClient = null;

        ContextsSettings contextsSettings =
                null;
        try {
            contextsSettings = ContextsSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            wContextsClient = ContextsClient.create(contextsSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wContextsClient;
    }

    /**
     * detectIntent を実行し、その結果を返却
     * 指定されたテキストを送信するだけ。
     */
    String send(String text) {
        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setQueryInput(
                        QueryInput.newBuilder()
                                .setText(
                                        TextInput.newBuilder()
                                                .setText(text)
                                                .setLanguageCode(LANGUAGE_CODE)
                                )
                                .build())
                .setSession(SessionName.format(PROJECT_ID, getSession()))
                .build();

        DetectIntentResponse res = sessionsClient.detectIntent(request);
        Log.d(TAG, "response result : ${res.queryResult}");

        return res.getQueryResult().getFulfillmentText();
    }

    /**
     * detectIntent を実行し、その結果を返却
     * context 指定可能
     */
    String send(String text, List<String> contexts) {
        QueryParameters.Builder queryParametersBuilder = QueryParameters.newBuilder();
        for (String it : contexts) {
            queryParametersBuilder.addContexts
                    (
                            com.google.cloud.dialogflow.v2.Context.newBuilder()
                                    .setName(ContextName.format(PROJECT_ID, getSession(), it))
                                    .setLifespanCount(5) // TODO: context の Lifespan を動的にする。
                                    .build()
                    );
        }

        // Dialogflow に投げるテキスト、コンテキストなどセット
        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setQueryParams(queryParametersBuilder.build())
                .setQueryInput(
                        QueryInput.newBuilder()
                                .setText(
                                        TextInput.newBuilder()
                                                .setText(text)
                                                .setLanguageCode(LANGUAGE_CODE)
                                )
                                .build())
                .setSession(SessionName.format(PROJECT_ID, getSession()))
                .build();

        DetectIntentResponse res = sessionsClient.detectIntent(request);
        Log.d(TAG, "response result : ${res.queryResult}");
        return res.getQueryResult().getFulfillmentText();
    }

    /**
     * context をリセットする。
     */
    void resetContexts() {
        contextClient.deleteAllContexts(SessionName.format(PROJECT_ID, getSession()));
    }
}
