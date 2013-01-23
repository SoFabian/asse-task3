package at.ac.tuwien.inso.esse.advancedsecsys.client;

import android.app.Application;
import android.content.Context;

public class EsseClient extends Application
{
  private static Context context;

  public static Context getAppContext()
  {
    return context;
  }

  public void onCreate()
  {
    super.onCreate();
    context = getApplicationContext();
  }
}
