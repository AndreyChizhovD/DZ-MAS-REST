package setup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class Restaurant {

    public static Map<String, JsonObject> products = new HashMap<>();
    public static Map<String, JsonObject> productTypes = new HashMap<>();
    public static Map<String, JsonArray> receipts = new HashMap<>();
    public static Map<String, jade.core.AID> processes = new HashMap<>();
    public static Map<String, String> customers = new HashMap<>();
    public static Map<String, String> equipments = new HashMap<>();
    public static Map<String, String> cookers = new HashMap<>();
    public static Map<String, JsonObject> dishes = new HashMap<>();
}
