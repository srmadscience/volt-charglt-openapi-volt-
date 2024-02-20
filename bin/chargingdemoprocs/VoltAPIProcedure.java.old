package chargingdemoprocs;

import java.lang.reflect.Type;

import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

import com.google.gson.Gson;

public abstract class VoltAPIProcedure extends VoltProcedure {

    public static final String RESPONSE_200 = "200";
    public static final String RESPONSE_400 = "400";

    public static final int RESPONSE_CREATED = 200;
    public static final int RESPONSE_BAD_REQUEST = 400;
    public static final int RESPONSE_UNAUTHORIZED = 401;
    public static final int RESPONSE_FORBIDDEN = 403;
    public static final int RESPONSE_METHOD_NOT_ALLOWED = 405;
    public static final int RESPONSE_CONFLICT = 409;
    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 500;
    public static final int RESPONSE_UNKNOWN = 0;

    public static final byte RESPONSE_VOLT_PROC_OK = 9;
    public static final String RESPONSE_VOLT_PROC_OK_STRING = "9";
    public static final byte RESPONSE_VOLT_PROC_FK_NOT_FOUND = 10;
    public static final byte RESPONSE_VOLT_PROC_NOT_OK = 11;
    public static final String RESPONSE_VOLT_PROC_NOT_OK_STRING = "11";

    public static final byte RESPONSE_VOLT_ALREADY_LOCKED = 12;
    public static final String RESPONSE_VOLT_ALREADY_LOCKED_STRING = "12";

    public static final byte RESPONSE_VOLT_NOT_FOUND = 14;
    public static final String RESPONSE_VOLT_NOT_FOUND_STRING = "14";

    Gson json = null;

    @SuppressWarnings("unused")
    protected String toJson(Object thingsToMakeIntoJson) {
        if (thingsToMakeIntoJson == null) {
            return "";
        }
        initJson();
        return json.toJson(thingsToMakeIntoJson);
    }

    @SuppressWarnings("unused")
    protected Object fromJson(String objectThatIsJson, Type type) throws Exception {
        initJson();
        Object theObject = null;
        try {
            theObject = json.fromJson(objectThatIsJson, type);
        } catch (Exception e) {
            throw new Exception("Unable to parse JSON:" + e.getMessage());
        }

        return theObject;
    }

    private void initJson() {
        if (json == null) {
            json = new Gson();
        }
    }

    @SuppressWarnings("unused")
    protected VoltTable[] castObjectToVoltTableArray(Object thingsToCast, int statusCode, String optionalMessage) {

        VoltTable[] returnArray = new VoltTable[1];
        returnArray[0] = new VoltTable(new VoltTable.ColumnInfo("PAYLOAD", VoltType.STRING),
                new VoltTable.ColumnInfo("STATUS_CODE", VoltType.INTEGER),
                new VoltTable.ColumnInfo("OPTIONAL_MESSAGE", VoltType.STRING));
        returnArray[0].addRow(toJson(thingsToCast), statusCode, optionalMessage);
        this.setAppStatusCode(RESPONSE_VOLT_PROC_OK);
        return returnArray;
    }

    @SuppressWarnings("unused")
    protected VoltTable[] castObjectArrayToVoltTableArray(Object[] thingsToCast, int statusCode,
            String optionalMessage) {

        VoltTable[] returnArray = new VoltTable[thingsToCast.length];
        returnArray[0] = new VoltTable(new VoltTable.ColumnInfo("PAYLOAD", VoltType.STRING),
                new VoltTable.ColumnInfo("STATUS_CODE", VoltType.INTEGER),
                new VoltTable.ColumnInfo("OPTIONAL_MESSAGE", VoltType.STRING));

        for (Object element : thingsToCast) {
            returnArray[0].addRow(toJson(element), statusCode, optionalMessage);
        }

        this.setAppStatusCode(RESPONSE_VOLT_PROC_OK);
        return returnArray;
    }

}
