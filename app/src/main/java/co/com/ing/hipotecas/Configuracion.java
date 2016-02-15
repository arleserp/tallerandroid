package co.com.ing.hipotecas;

/**
 * Created by INVESTIGADOR on 12/02/2016.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Configuracion extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.configuracion);
    }
}