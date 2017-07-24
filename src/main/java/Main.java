import com.google.gson.*;
import org.apache.commons.lang3.ObjectUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) throws IOException {


        File folder = new File("/CachedData/Weatherdatatest");

        if (folder != null && folder.isDirectory()) {

            for (final File file : folder.listFiles((dir, name) -> name.endsWith(".json"))) {

                JsonStreamParser parser = new JsonStreamParser(new FileReader(file));

                while (parser.hasNext()) {

                    JsonElement object = parser.next();
                    String filename = file.getName();
                    String location = filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."));
                    System.out.println(location);


                    JsonObject jsonObject = object.getAsJsonObject();

                    try{
                        jsonObject.getAsJsonObject("history").remove("dailysummary");
                        jsonObject.getAsJsonObject("history").remove("stadt");
                        jsonObject.getAsJsonObject("history").remove("date");
                        jsonObject.getAsJsonObject("history").remove("utcdate");
                        jsonObject.remove("response");
                        JsonArray array = jsonObject.getAsJsonObject("history").getAsJsonArray("observations");
                        Iterator<JsonElement> iterator = array.iterator();
                        while(iterator.hasNext()){
                            JsonElement element = iterator.next();
                            JsonObject object1 = element.getAsJsonObject();
                            object1.remove("utcdate");

                            JsonObject dateObject = object1.getAsJsonObject("date");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, dateObject.get("year").getAsInt());
                            calendar.set(Calendar.MONTH, dateObject.get("mon").getAsInt()-1);
                            calendar.set(Calendar.DAY_OF_MONTH, dateObject.get("mday").getAsInt());
                            calendar.set(Calendar.HOUR_OF_DAY, dateObject.get("hour").getAsInt());
                            calendar.set(Calendar.MINUTE, dateObject.get("min").getAsInt());
                            calendar.set(Calendar.SECOND, 0);

                            Date date = calendar.getTime();

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            object1.addProperty("zeitpunkt", format.format(date));

                            object1.addProperty("stadt", location);

                            object1.remove("date");
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }


                    Gson gson = new Gson();

                    // convert java object to JSON format,
                    // and returned as JSON formatted string
                    String json = gson.toJson(object);

                    //write converted json data to a file named "CountryGSON.json"
                    FileWriter writer = new FileWriter(file.getAbsolutePath());
                    writer.write(json);
                    writer.close();
                }
            }
        }

    }
}
