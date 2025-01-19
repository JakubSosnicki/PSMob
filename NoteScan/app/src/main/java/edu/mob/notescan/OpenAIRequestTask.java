package edu.mob.notescan;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;


public class OpenAIRequestTask extends AsyncTask<String, Void, String> {
    private final Context context;
    private final OpenAIResponseCallback callback;

    public OpenAIRequestTask(Context context, OpenAIResponseCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... prompts) {
        String apiKey = context.getString(R.string.openai_api_key);
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");  // gpt-4o-mini
            jsonObject.put("prompt", prompts[0]);
            jsonObject.put("max_tokens", 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating JSON: " + e.getMessage();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                return "Error: " + response.code() + " - " + response.message();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Request failed: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null && !result.startsWith("Error")) {
            callback.onSuccess(result);
        } else {
            callback.onFailure(result != null ? result : "Unknown error");
        }
    }

    public interface OpenAIResponseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
