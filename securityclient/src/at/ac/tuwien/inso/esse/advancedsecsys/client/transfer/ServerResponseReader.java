package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;

public class ServerResponseReader
{
  public static List<Entry> interpretQuery(BufferedReader paramBufferedReader)
    throws IOException
  {
    List<Entry> localArrayList = new ArrayList<Entry>();
    int i = 0;
    while (true)
    {
      String localObject = paramBufferedReader.readLine();
      if (localObject == null)
        break;
      if (!localObject.equals("<query"))
      {
        if (!localObject.startsWith("<query:")) {
          continue;
        }
        String[] arr = localObject.substring("<query".length()).split(" ");
        Entry entry = new Entry(Utility.b64d(arr[0]), Utility.b64d(arr[1]));
        localArrayList.add(entry);
        System.out.println(entry.getName() + " " + entry.getTelnr());
        continue;
      }
      i = 1;
    }
    if (i == 0)
      localArrayList = null;
    return (List<Entry>)localArrayList;
  }
}
