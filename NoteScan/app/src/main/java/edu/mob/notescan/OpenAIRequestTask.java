package edu.mob.notescan;

import android.os.AsyncTask;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class OpenAIRequestTask extends AsyncTask<String, Void, String> {
    private static final String API_KEY = "API_KEY";
    private static final String API_URL = "https://api.openai.com/v1/completions";

    @Override
    protected String doInBackground(String... prompts) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", prompts[0]);
            jsonObject.put("max_tokens", 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Zapytanie HTTP POST
        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Odpowiedź z API
                return response.body().string();
            } else {
                return "Error: " + response.code();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Request failed: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("OpenAIResponse", result);
    }
}
