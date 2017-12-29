package json_process;

/**
 * Created by MaestroVS on 21.12.2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoodJSON {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("article")
    @Expose
    private String article;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("barcode")
    @Expose
    private String barcode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}


