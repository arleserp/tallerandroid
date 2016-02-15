package co.com.ing.hipotecas;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;




public class MainActivity extends ListActivity {

    public static final String C_MODO  = "modo" ;
    public static final int C_VISUALIZAR = 551 ;
    public static final int C_CREAR = 552 ;
    public static final int C_EDITAR = 553 ;
    public static final int C_ELIMINAR = 554 ;
    public static final int C_CONFIGURAR = 555 ;


    private HipotecaDbAdapter dbAdapter;
    private Cursor cursor;
    private HipotecasAdapter hipotecaAdapter ;
    private ListView lista;
    private boolean mHasMenu = true;
    private Toolbar mToolbar;
    private String filtro ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPreferencias();
       /* dbAdapter = new HipotecaDbAdapter(this);
        try {
            dbAdapter.abrir();
            consultar();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        AppCompatCallback callback = new AppCompatCallback() {
            @Override
            public void onSupportActionModeStarted(ActionMode actionMode) {
            }

            @Override
            public void onSupportActionModeFinished(ActionMode actionMode) {
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                return null;
            }

        };

        AppCompatDelegate delegate = AppCompatDelegate.create(this,callback);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_main);

        Toolbar toolbar= (Toolbar) findViewById(R.id.menu_crear);
        delegate.setSupportActionBar(toolbar);


        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        lista = (ListView) findViewById(android.R.id.list);
        dbAdapter = new HipotecaDbAdapter(this);
        dbAdapter.abrir();
        consultar();

        registerForContextMenu(this.getListView());
    }

    private void consultar() throws SQLException {
        hipotecaAdapter = new HipotecasAdapter(this, dbAdapter.getHipotecas(filtro));
        lista.setAdapter(hipotecaAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       Log.i(this.getClass().toString(),"Entra!");
        // Inflate the menu; this adds items to the action bar if it is present.
        new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(hipotecaAdapter.getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position).getNombre());
        menu.add(Menu.NONE, C_VISUALIZAR, Menu.NONE, R.string.menu_visualizar);
        menu.add(Menu.NONE, C_EDITAR, Menu.NONE, R.string.menu_editar);
        menu.add(Menu.NONE, C_ELIMINAR, Menu.NONE, R.string.menu_eliminar);
    }


    private void borrar(final long id)
    {
        //
        // Borramos el registro y refrescamos la lista
        //
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(R.string.hipoteca_eliminar_titulo));
        dialogEliminar.setMessage(getResources().getString(R.string.hipoteca_eliminar_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int boton) {
                dbAdapter.delete(id);
                Toast.makeText(MainActivity.this, R.string.hipoteca_eliminar_confirmacion, Toast.LENGTH_SHORT).show();
                consultar();
            }
        });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.string.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void visualizar(long id)
    {
        // Llamamos a la Actividad HipotecaFormulario indicando el modo visualización y el identificador del registro
        Intent i = new Intent(this, HipotecaFormulario.class);
        i.putExtra(C_MODO, C_VISUALIZAR);
        i.putExtra(HipotecaDbAdapter.C_COLUMNA_ID, id);

        startActivityForResult(i, C_VISUALIZAR);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        visualizar(id);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        Intent i;

        switch (item.getItemId())
        {
            case R.id.menu_crear:
                i = new Intent(this, HipotecaFormulario.class);
                i.putExtra(C_MODO, C_CREAR);
                startActivityForResult(i, C_CREAR);
                return true;
            case R.id.menu_preferencias:
                i = new Intent(MainActivity.this, Configuracion.class);
                startActivityForResult(i, C_CONFIGURAR);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //
        // Nos aseguramos que es la petición que hemos realizado
        //
// Nos aseguramos que es la petición que hemos realizado
        //
        switch(requestCode)
        {
            case C_CREAR:
                if (resultCode == RESULT_OK)
                    consultar();

            case C_VISUALIZAR:
                if (resultCode == RESULT_OK)
                    consultar();

            case C_CONFIGURAR:
                // en la PreferenceActivity no hemos definido ningún resultado por lo que recargamos
                // siempre las preferencias
                getPreferencias();
                consultar();

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Intent i;

        switch(item.getItemId())
        {
            case C_ELIMINAR:
                borrar(info.id);
                return true;

            case C_VISUALIZAR:
                visualizar(info.id);
                return true;

            case C_EDITAR:
                i = new Intent(MainActivity.this, HipotecaFormulario.class);
                i.putExtra(C_MODO, C_EDITAR);
                i.putExtra(HipotecaDbAdapter.C_COLUMNA_ID, info.id);

                startActivityForResult(i, C_EDITAR);
                return true;
        }
        return super.onContextItemSelected(item);
    }


    private void getPreferencias()
    {
        //
        // Recuperamos las preferencias
        //
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferencias.getBoolean("ocultar_registros_pasivos", false))
        {
            // si se ocultan registros pasivos filtramos solamente los que tengan el valor 'N'
            this.filtro = HipotecaDbAdapter.C_COLUMNA_PASIVO + " = 'N' " ;
        }
        else
        {
            // si no se ocultan registros pasivos no filtramos
            this.filtro = null ;
        }
    }
}
