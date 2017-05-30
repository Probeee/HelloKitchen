package tw.org.iii.hellokitchen;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.facebook.stetho.Stetho;


public class ActMain extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        InitialComponent();


        GetStetho();
    }

    private void InitialComponent()
    {
        fragMgr = getFragmentManager();
        frag_main = new Frag_Main();
        fragMgr.beginTransaction().add(R.id.frag_container,frag_main).commit();
    }

    private void GetStetho()
    {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    FragmentManager fragMgr;
    Frag_Main frag_main;
}
