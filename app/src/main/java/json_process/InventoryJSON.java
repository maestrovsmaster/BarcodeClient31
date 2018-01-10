package json_process;

/**
 * Created by MaestroVS on 21.12.2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InventoryJSON {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("num")
    @Expose
    private String num;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("subdivision_id")
    @Expose
    private Integer subdivisionId;
    @SerializedName("subdivision_name")
    @Expose
    private String subdivisionName;
    @SerializedName("doc_state")
    @Expose
    private int doc_state=0;

    public InventoryJSON(){

    }

    /**
     *
     * @param id
     * @param num
     * @param dateTime
     * @param subdivisionId
     * @param subdivisionName
     * @param doc_state
     */
    public InventoryJSON(Integer id, String num, String dateTime, Integer subdivisionId, String subdivisionName, int doc_state) {
        this.id = id;
        this.num = num;
        this.dateTime = dateTime;
        this.subdivisionId = subdivisionId;
        this.subdivisionName = subdivisionName;
        this.doc_state = doc_state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getSubdivisionId() {
        return subdivisionId;
    }

    public void setSubdivisionId(Integer subdivisionId) {
        this.subdivisionId = subdivisionId;
    }

    public String getSubdivisionName() {
        return subdivisionName;
    }

    public void setSubdivisionName(String subdivisionName) {
        this.subdivisionName = subdivisionName;
    }

    public int getDoc_state() {
        return doc_state;
    }

    public void setDoc_state(int doc_state) {
        this.doc_state = doc_state;
    }
}


