package kidnox.particles;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import kidnox.particles.sample.ParticleSystemConfig;

public class App extends Application {

    private static final String FILE_CONFIG = "ps_config.json";

    private static Gson gson;
    private static Context context;

    @Override public void onCreate() {
        super.onCreate();
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        context = this;
    }

    public static String loadConfigAsString() {
        return gson.toJson(loadConfig());
    }

    public static ParticleSystemConfig loadConfig() {
        ParticleSystemConfig result;
        File file = new File(context.getCacheDir(), FILE_CONFIG);
        if(file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                result = gson.fromJson(fileReader, ParticleSystemConfig.class);
                fileReader.close();
                if(result == null) {
                    file.delete();
                    result = new ParticleSystemConfig();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                result = new ParticleSystemConfig();
                printError(ex.getMessage());
            }
        } else {
            result = new ParticleSystemConfig();
            saveConfig(gson.toJson(result));
        }
        return result;
    }

    public static boolean saveConfig(String json) {
        File file = new File(context.getCacheDir(), FILE_CONFIG);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            try {
                gson.fromJson(json, ParticleSystemConfig.class);
            } catch (Exception ex) {
                printError(ex.getMessage());
                return false;
            }
            fileWriter.write(json);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reset() {
        saveConfig(gson.toJson(new ParticleSystemConfig()));
    }

    private static void printError(String message) {
        Toast.makeText(context, String.valueOf(message), Toast.LENGTH_LONG).show();
    }


}
