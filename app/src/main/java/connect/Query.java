package connect;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Query {

	private static Firebird conn = null;
	
	private static Query query=null;
	
	public static Query getQuery(Firebird conn)
	{
		
		if(query==null) query = new Query(conn);
		
		return query;
	}

	private Query(Firebird conn) {
		this.conn = conn;
	}

	public String getDataBaseVersion() {
		// conn.setNomen("execute procedure sys_connect(1,1)");
		ArrayList<ArrayList<Object>> obj2 = conn.getNomen("select * from sp_get_version");

		String name = "";

		if (obj2 != null) {
			if (obj2.size() > 0)

				name = (String) obj2.get(0).get(0);

		}

		return name;

	}

	public String getGoodNamebyBarcode(String barcode) {

		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select dg.name from dic_goods dg where dg.id = ( select dgb.goods_id "
						+ " from dic_goods_barcodes    dgb where dgb.barcode = '" + barcode + "' )");

		String name = "null!";
		if (obj != null)
			if (obj.size() > 0)
				name = (String) obj.get(0).get(0);

		return name;

	}

	public ArrayList<String> getGoodNameListbyBarcode(String barcode) {

		ArrayList<ArrayList<Object>> obj = conn.getNomen("select DG.NAME  from DIC_GOODS DG   "
				+ " left join dic_goods_barcodes db on db.goods_id = dg.id   where db.barcode ='" + barcode + "'");

		String name = "null!";

		ArrayList<String> list = new ArrayList<String>();
		if (obj != null) {
			if (obj.size() > 0)
				for (int i = 0; i < obj.size(); i++) {
					name = (String) obj.get(i).get(0);
					list.add(name);
				}
		}

		ArrayList<ArrayList<Object>> obj2 = conn
				.getNomen("select dg.name  from dic_goods dg  where dg.article='" + barcode + "'");

		name = "null!";

		// ArrayList<String> list = new ArrayList<String>();
		if (obj2 != null) {
			if (obj2.size() > 0)
				for (int i = 0; i < obj2.size(); i++) {
					name = (String) obj2.get(i).get(0);
					if (!list.contains(name))
						list.add(name);
				}
		}

		return list;

	}

	public ArrayList<ArrayList<Object>> getBillInList() {

		ArrayList<ArrayList<Object>> obj = conn.getNomen(
				" select jb.id ,jb.num , jb.date_time , (select  ds.name from  dic_subdivision ds where ds.id = jb.subdivision_id ) "
						+ "as subd from jor_bill_in jb where jb.doc_state = 0  order by jb.date_time");

		String name = "null!";
		if (obj != null)
			if (obj.size() > 0) {

				return obj;
			}

		return null;

	}

	public ArrayList<ArrayList<Object>> getBillInDtList(String id) {

		String query = "select jbt.id , " + "(select dg.article from dic_goods dg where dg.id=jbt.goods_id) as art , "
				+ "(select dg.name from dic_goods dg where dg.id=jbt.goods_id) as good , "
				+ "(select du.name from dic_unit du  where du.id = (select dgg.unit_id from dic_goods dgg where dgg.id = jbt.goods_id) ) as unit , "
				+ "(select first 1 db.barcode from dic_goods_barcodes db  where db.goods_id = jbt.goods_id ) as barc , "
				+ "jbt.cnt , jbt.price, jbt.sum_with_tax " + "from jor_bill_in_dt jbt " + "where jbt.hd_id= " + id;
		// System.out.println(query);
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		String name = "null!";
		if (obj != null)
			if (obj.size() >= 0) {

				// System.out.println("return "+obj);
				return obj;
			}
		// System.out.println("return null");
		return null;

	}

	public String sp_get_version() {
		ArrayList<ArrayList<Object>> obj = conn.getNomen("execute procedure sp_get_version");
		String version = "";
		if (obj != null)
			if (obj.size() > 0) {
				version = (String) obj.get(0).get(0);

			}
		return version;

	}

	public void addBillInDt(String hd_id, String name, String cnt, String price) {
		String str = name;
		if (str.contains("'")) {
			int a = str.indexOf("'");
			str = str.substring(0, a) + "'" + str.substring(a, str.length());
		}
		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select dg.id, dg.unit_id from dic_goods dg  where dg.name='" + str + "'");

		Double cntD = Double.parseDouble(cnt);
		Double priceD = Double.parseDouble(price);
		Double sumD = cntD * priceD;
		String sum = Double.toString(sumD);

		if (obj != null)
			if (obj.size() > 0) {
				String goodsId = ((Integer) obj.get(0).get(0)).toString();
				String unitId = ((Integer) obj.get(0).get(1)).toString();

				String query = "execute procedure z$jor_bill_in_dt_i(  null, " + hd_id + "," + goodsId + "," + unitId
						+ "," + cnt + "," + sum + "," + sum + ",0,0,0," + price + "," + price + ",null)";

				String version = sp_get_version();
				System.out.println("version db = " + version);

				if (version.equals("2.1.72.0")) {
					query = "execute procedure z$jor_bill_in_dt_i(  null, " + hd_id + "," + goodsId + "," + unitId + ","
							+ cnt + "," + sum + "," + sum + ",0,0,0," + price + "," + price + ")";
				}

				conn.setNomen("execute procedure sys_connect(1,1)");
				conn.setNomen(query);
			}

		// execute procedure z$jor_bill_in_dt_i( null, 'hd id','goods id','unit
		// id','cnt','cnt*price','cnt*price',0,0,0,'price','price',null)
		/*
		 * conn.setNomen("execute procedure sys_connect(1,1)"); //�������
		 * ����������� ���� conn.setNomen(
		 * "execute procedure z$jor_checks_dt_i(null,"+
		 * chekId+","+id+","+1+","+price1+", "+price1+","+subdivision_id+","+
		 * print_subdivision_id+",'"+""+"')");
		 */
	}

	// delete from jor_bill_in_dt jd where jd.id=:ID

	public void delBillInDt(String id) {
		conn.setNomen("delete from jor_bill_in_dt jd where  jd.id=" + id);
	}

	/*
	 * select JB.ID, JB.NUM, JB.date_time, (select DS.NAME from DIC_SUBDIVISION
	 * DS where DS.ID = JB.subdivision_id) as SUBD from jor_inventory_act JB
	 * where JB.DOC_STATE = 0 and JB.subdivision_id is not null order by
	 * JB.DATE_TIME
	 */

	public JSONArray getInventorynList() {

		String query = "select JB.ID, JB.NUM, JB.date_time, "
				+ " (select DS.NAME         from DIC_SUBDIVISION DS   where DS.ID = JB.subdivision_id) as SUBD "
				+ " from jor_inventory_act JB " + " 	where JB.DOC_STATE = 0   and JB.subdivision_id is not null "
				+ " order by JB.DATE_TIME ";

		// System.out.println(query);

		ArrayList<ArrayList<Object>> objList = conn.getNomen(query);

		JSONArray jsonArray = new JSONArray();

		if (objList != null)
			if (objList.size() > 0) {

				for (ArrayList<Object> obj : objList) {
					JSONObject jsonObject = new JSONObject();
					int id = (int) obj.get(0);
					String num = (String) obj.get(1);
					Timestamp datatime = (Timestamp) obj.get(2);
					String strDatatime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(datatime);

					String subdivision = (String) obj.get(3);

					try {
						jsonObject.put("id", id);
						jsonObject.put("num", num);
						jsonObject.put("datetime", strDatatime);
						jsonObject.put("subdivision", subdivision);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					jsonArray.put(jsonObject);
				}

				return jsonArray;
			}

		return jsonArray;

	}

	/**
	 * Get good's id by good's name
	 * 
	 * @param goodsName
	 */
	public String getgoodsIdByName(String goodsName) {
		String str = goodsName;
		if (str.contains("'")) {
			int a = str.indexOf("'");
			str = str.substring(0, a) + "'" + str.substring(a, str.length());
		}
		ArrayList<ArrayList<Object>> obj = conn.getNomen("select dg.id from dic_goods dg  where dg.name='" + str + "'");
		String goodsId = "-1";
		if (obj.size() > 0) {
			goodsId = ((Integer) obj.get(0).get(0)).toString();
		}
		return goodsId;

	}

	public String editInvDt(String id, String goodName, String cnt) {

		String ans = "";
		String str = goodName;
		if (str.contains("'")) {
			int a = str.indexOf("'");
			str = str.substring(0, a) + "'" + str.substring(a, str.length());
		}
		ArrayList<ArrayList<Object>> obj = conn.getNomen("select dg.id from dic_goods dg  where dg.name='" + str + "'");

		Double cntD = 1.0;
		try {
			cntD = Double.parseDouble(cnt);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}

		if (obj != null)
			if (obj.size() > 0) {
				String goodsId = ((Integer) obj.get(0).get(0)).toString();

				// execute procedure Z$INVENTORY_EDITOR_U(:HD_ID, :GOODS_ID,
				// :CNT, null, null,null);
				String query = "execute procedure Z$INVENTORY_EDITOR_U( " + id + "," + goodsId + "," + cnt
						+ ",0, null,null)";

				// System.out.println("before er 11");
				conn.setNomen("execute procedure sys_connect(1,1)");
				ans = conn.setNomen(query);
				System.out.println("ans inv=" + ans);
			}
		return ans;
	}

	public double getDtCNT(int invId, int goodId) {
		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select sum(ji.cnt_fact ) from jor_inventory_act_dt ji  where ji.hd_id=" + invId
						+ " and ji.goods_id=" + goodId);
		BigDecimal cnt = null;
		if (obj.size() > 0) {
			cnt = (BigDecimal) obj.get(0).get(0);
		}

		double d = 0;
		try {
			d = cnt.doubleValue();
		} catch (Exception e) {
		}

		return d;
	}

	public String getgoodsIdByBarcode(String barc) {
		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select dg.id from dic_goods dg where dg.id = ( select dgb.goods_id "
						+ " from dic_goods_barcodes    dgb where dgb.barcode = '" + barc + "' )");

		int id = -1;
		if (obj != null)
			if (obj.size() > 0)
				id = (Integer) obj.get(0).get(0);

		String name = Integer.toString(id);
		return name;
	}

	public String getGoodCnt() {
		ArrayList<ArrayList<Object>> obj = conn.getNomen("select count(id) from dic_goods");
		int cnt = -1;
		if (obj != null)
			if (obj.size() > 0)
				cnt = (Integer) obj.get(0).get(0);

		return Integer.toString(cnt);
	}

	public JSONArray getGoodList(int grp_id, int inventoryId, String barc, String art, String name, int offset,
			int limit) {

		int t=0;
		if(inventoryId<0) t=-1;
		
		String query0 = "select "+"DG.ID, DG.GRP_ID,DG.NAME,"
				+ " (select DU.NAME from DIC_UNIT DU where DU.ID = DG.UNIT_ID) as UNIT, DG.ARTICLE from DIC_GOODS DG ";

		String query = "select ";
		if(offset>=0&&limit>=0){
			query+=" first "+limit+" skip "+offset;
		}
		
				query+=" DG.ID, DG.GRP_ID, DG.NAME, " + "(select DU.NAME " + " from DIC_UNIT DU "
				+ " where DU.ID = DG.UNIT_ID) as UNIT, DG.ARTICLE, ";

		

		query += "( select first 1 PLD.PRICE " + "from DIC_PRICE_LIST PL, DIC_PRICE_LIST_DT PLD "
				+ " where PL.GOODS_ID = DG.ID " +

		"    and  PLD.HD_ID = PL.ID and " + "     PLD.DATE_FROM < current_timestamp "
				+ " order by PLD.DATE_FROM desc ) as PRC " +

		" , (select first 1 dgb.barcode from dic_goods_barcodes dgb" + " where dgb.goods_id = DG.id) as BARC ";
		
		
		if (inventoryId >= 0) {
	
			query += " , (select sum(JI.CNT_FACT) " + "from JOR_INVENTORY_ACT_DT JI " + " where JI.HD_ID = " + inventoryId
			+ " and " + "      JI.GOODS_ID = DG.ID) as KVO ";
		}
		

		query += " from DIC_GOODS DG    ";

		if (grp_id >= 0)
			query += " where DG.GRP_ID= " + grp_id;
		else {
			if (inventoryId >= 0) {

				if (barc.length() > 0)
					query += " left join dic_goods_barcodes db on db.goods_id = dg.id   where db.barcode ='" + barc
							+ "'";

				if (name.length() > 0) {
					if (barc.length() > 0)
						query += " or ";
					else
						query += " where ";
					query += " (DG.name containing '" + name + "')";
				}
				// else
				{

					if (art.length() > 0) {
						if (name.length() > 0)
							query += " or ";
						else
							query += " where ";
						query += " (DG.article='" + art + "')";
					}
				}

			}
		}

		if(inventoryId>=0) query += " order by DG.NAME";

		System.out.println("query= " + query);

		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		System.out.println("goods al = " + obj.size());
		JSONArray goodsArray = new JSONArray();

		if (obj.size() > 0) {
			for (ArrayList<Object> good : obj) {
				int goodId = (Integer) good.get(0);
				int goodGRP = (Integer) good.get(1);
				String goodName = (String) good.get(2);
				String unit = (String) good.get(3);
				String article = (String) good.get(4);

				
				

				double out_price = 0.0;
				if (good.get(5) != null) {
					BigDecimal price = (BigDecimal) good.get(5);
					out_price = Double.parseDouble(price.toString());

				}


				String barcode = "";
				if (good.get(6) != null) {
					barcode = (String) good.get(6);
				}
				
				BigDecimal cnt = null;
				double fcnt = 0;
				if(inventoryId>=0){
					if (good.get(7) != null) {
					cnt = (BigDecimal) good.get(7);
						}
					try {
						fcnt = cnt.doubleValue();
					} catch (Exception e) {
					}
					}
				
				

				JSONObject goodObj = new JSONObject();
				try {
					goodObj.put("out_price", out_price);
					goodObj.put("fcnt", fcnt);
					goodObj.put("article", article);
					goodObj.put("unit", unit);
					goodObj.put("name", goodName);
					goodObj.put("grp_id", goodGRP);
					goodObj.put("id", goodId);
					goodObj.put("barcode", barcode);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				goodsArray.put(goodObj);
			}

		}
		System.out.println("goods arr = " + goodsArray.length());
		return goodsArray;
	}

	public JSONObject getWeigthScanParameters() {
		JSONObject jsonObject = new JSONObject();

		String query = "select first 2 sw.param_value from sec_ws_options sw where (sw.param_name='WEIGTH_BARCODE'  or"
				+ " sw.param_name= 'WEIGTH_BARCODE_MASK') and  char_length(sw.param_value)>0";

		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);
		String WEIGTH_BARCODE = "";
		String WEIGTH_BARCODE_MASK = "";
		if (obj != null)
			if (obj.size() == 2) {
				WEIGTH_BARCODE = (String) obj.get(0).get(0);
				WEIGTH_BARCODE_MASK = (String) obj.get(1).get(0);
			}

		try {
			jsonObject.put("WEIGTH_BARCODE", WEIGTH_BARCODE);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			jsonObject.put("WEIGTH_BARCODE_MASK", WEIGTH_BARCODE_MASK);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;

	}

	public JSONArray getGoodsGRPList(int grp_id) {

		String query = "select dgp.id,  dgp.parent_id, dgp.name from dic_goods_grp dgp";

		if (grp_id >= 0)
			query += "  where dgp.parent_id= " + grp_id;

		query += " order by dgp.name";

		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		System.out.println("query = " + query);
		JSONArray goodsArray = new JSONArray();

		if (obj.size() > 0) {
			for (int o = 0; o < obj.size(); o++) {
				ArrayList<Object> good = obj.get(o);
				int goodId = (Integer) good.get(0);
				int goodGRP = (Integer) good.get(1);
				String goodName = (String) good.get(2);

				JSONObject goodObj = new JSONObject();
				try {

					goodObj.put("name", goodName);
					goodObj.put("grp_id", goodGRP);
					goodObj.put("id", goodId);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				goodsArray.put(goodObj);
			}

		}

		return goodsArray;
	}

	/**
	 * ���������� ������������ ������ � �� ������
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Object>> getGoodList2() {
		ArrayList<ArrayList<Object>> obj = conn.getNomen("select dg.name, dg.grp_id from dic_goods  dg");
		return obj;
	}

	public ArrayList<ArrayList<Object>> getGRPList2() {
		ArrayList<ArrayList<Object>> obj = conn.getNomen("select dg.id, dg.name, dg.parent_id from dic_goods_grp  dg");
		return obj;
	}

	/**
	 * �������� �������� ������ �� ��� ������������
	 * 
	 * @param nm
	 * @return
	 */
	public String getBarcByGoodId(String id) {
		String query = "select db.barcode from dic_goods_barcodes  db  where db.goods_id= " + id;
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);
		String barcode = "";
		if (obj.size() > 0)
			barcode = (String) obj.get(0).get(0);
		return barcode;
	}

	public ArrayList<String> getSubdivList() {
		String query = "select    ds.name from dic_subdivision ds  order by ds.name";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);
		ArrayList<String> list = new ArrayList<String>();

		for (ArrayList<Object> good : obj) {
			String ggd = (String) good.get(0);
			list.add(ggd);

		}
		return list;
	}

	public ArrayList<String> getContragentList() {
		String query = "select    dc.name  from dic_org dc  order by dc.name";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);
		ArrayList<String> list = new ArrayList<String>();

		for (ArrayList<Object> good : obj) {
			String ggd = (String) good.get(0);
			list.add(ggd);

		}
		return list;
	}

	/**
	 * �������� ������ �������� ����� ����� ���������
	 * 
	 * @return
	 */
	public ArrayList<String> getFoldertList() {
		String query = "select bg.id, bg.parent_id, bg.name from jor_bill_in_grp bg";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		ArrayList<Integer> listPARENT_ID = new ArrayList<Integer>();

		for (ArrayList<Object> good : obj) {
			int ggd = (Integer) good.get(1);
			listPARENT_ID.add(ggd);

		}

		ArrayList<String> listNAME = new ArrayList<String>();

		for (ArrayList<Object> good : obj) {
			int id = (Integer) good.get(0);
			if (!listPARENT_ID.contains(id)) {
				String name = (String) good.get(2);
				listNAME.add(name);
			}

		}

		return listNAME;
	}

	public String getBillInGrpIdByName(String name) {
		String stre = name;
		if (stre.contains("'")) {
			int a = stre.indexOf("'");
			stre = stre.substring(0, a) + "'" + stre.substring(a, stre.length());
		}
		String query = "select dc.id from jor_bill_in_grp dc where  dc.name= '" + stre + "'";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		String str = "";
		if (obj.size() > 0) {
			int sti = (Integer) obj.get(0).get(0);
			str = Integer.toString(sti);
		}
		return str;
	}

	public String getOrgIdByName(String name) {
		String stre = name;
		if (stre.contains("'")) {
			int a = stre.indexOf("'");
			stre = stre.substring(0, a) + "'" + stre.substring(a, stre.length());
		}
		String query = "select dc.id from dic_org dc where  dc.name= '" + stre + "'";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		String str = "";
		if (obj.size() > 0) {
			int sti = (Integer) obj.get(0).get(0);
			str = Integer.toString(sti);
		}
		return str;
	}

	public String getSubdivIdByName(String name) {
		String stre = name;
		if (stre.contains("'")) {
			int a = stre.indexOf("'");
			stre = stre.substring(0, a) + "'" + stre.substring(a, stre.length());
		}
		String query = "select dc.id from dic_subdivision dc where  dc.name= '" + stre + "'";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		String str = "";
		if (obj.size() > 0) {
			int sti = (Integer) obj.get(0).get(0);
			str = Integer.toString(sti);
		}
		return str;
	}

	// select first 1 de.id from dic_employee de
	public String getFirstIdByEmployee() {
		String query = "select first 1 de.id  from dic_employee de";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		String str = "";
		if (obj.size() > 0) {
			int sti = (Integer) obj.get(0).get(0);
			str = Integer.toString(sti);
		}
		return str;
	}

	// select max(id) from jor_bill_in
	public int getMaxIdByJorBillIn() {
		String query = "select max(id)  from jor_bill_in";
		ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

		int sti = -1;
		if (obj.size() > 0) {
			sti = (Integer) obj.get(0).get(0);

		}
		return sti;
	}

	/**
	 * ������� ���������
	 * 
	 * @param grp
	 *            ������ ���������
	 * @param org
	 *            ���������
	 * @param subdiv
	 *            �������������
	 * @return id of new bill in
	 */
	public String newBill(String grp, String org, String subdiv, String nds) {
		// select dc.id from jor_bill_in_grp dc where dc.name=:na

		String grp1 = getBillInGrpIdByName(grp);
		String subdiv1 = getSubdivIdByName(subdiv);
		String org1 = getOrgIdByName(org);
		String empl = getFirstIdByEmployee();

		int mnum = getMaxIdByJorBillIn() + 1;

		String query = "execute procedure z$jor_bill_in_i(null," + grp1 + ",current_timestamp," + mnum + ",0,null,"
				+ subdiv1 + "," + nds + ",null,0," + org1 + "," + empl
				+ ",null, 0,null,null, '��������� � BarcodeScanner' )";

		System.out.println(query);
		conn.setNomen("execute procedure sys_connect(1,1)");
		conn.setNomen(query);

		// �������� �� ������������� ���������
		int mnumNew = getMaxIdByJorBillIn();

		return Integer.toString(mnumNew);

	}

	public ArrayList<String> getgoodsNameByPartName(String nm) {
		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select DG.NAME  from DIC_GOODS DG where DG.NAME containing '" + nm + "'");

		String name = "null!";

		ArrayList<String> list = new ArrayList<String>();
		if (obj != null)
			if (obj.size() > 0)
				for (int i = 0; i < obj.size(); i++) {
					name = (String) obj.get(i).get(0);
					list.add(name);
				}

		return list;
	}

	public double getGoodPrice(int goodId) {
		// TODO Auto-generated method stub
		Double priced = 0.0;
		ArrayList<ArrayList<Object>> obj = conn
				.getNomen("select first 1 PLD.PRICE	 from DIC_PRICE_LIST PL, DIC_PRICE_LIST_DT PLD "
						+ "where PL.GOODS_ID = " + goodId + " and     PLD.HD_ID = PL.ID and "
						+ "PLD.DATE_FROM < current_timestamp	 order by PLD.DATE_FROM desc");

		String price1 = "";
		if (obj != null)
			if (obj.size() > 0) {
				BigDecimal price = (BigDecimal) obj.get(0).get(0);
				priced = Double.parseDouble(price.toString());
				price1 = Double.toString(priced);
			}
		return priced;
	}

	public String sendEditInvDt(int invId, int goodId, double cnt) {
		System.out.println("send edit cnt...");
		String query = "execute procedure Z$INVENTORY_EDITOR_U( " + invId + "," + goodId + "," + cnt + ",0, null,null)";

		// System.out.println("before er 11");
		conn.setNomen("execute procedure sys_connect(1,1)");
		String ans = conn.setNomen(query);
		System.out.println("ans inv=" + ans);
		return ans;
	}
	

	
	public int getCountGoods()
	{
	String query = "select count(dg.id) from dic_goods dg";
	ArrayList<ArrayList<Object>> obj = conn.getNomen(query);

	int sti = 0;
	if (obj.size() > 0) {
		sti = (Integer) obj.get(0).get(0);

	}
	return sti;
	}
	
	
	
	public JSONArray getShiftsList() {
		
		HashSet<Subdivision> subdivList = new HashSet<>();

		//������� �� �������������� ����
		String query1 = "select distinct ds.id,  ds.name "+
				" from  dic_subdivision ds  join  sec_workstations sw on sw.subdivision_id = ds.id  join sec_ws_options  so  on "+
				" sw.id = so.ws_id  and so.param_name = 'IS_RESTAURANT_FRONT' and so.param_value <> 0";

		
		ArrayList<ArrayList<Object>> obj1 = conn.getNomen(query1);
		
		if(obj1.size()>0){
			
			for(ArrayList<Object> subdiv: obj1)
			{
				int id = (int) subdiv.get(0);
				String name = (String) subdiv.get(1);
				Subdivision subdivision = new Subdivision(id, name);
				subdivision.setCash(true);
				subdivList.add(subdivision);
			}
		}
		
		//������� �� �������������� ��������
		String query2 = " select distinct ds.id, ds.name "+
				" from  dic_subdivision ds  join  dic_price_list_grp dg  on dg.subdivision_id = ds.id or (dg.fiscal_subdivision_id = ds.id)";

		ArrayList<ArrayList<Object>> obj2 = conn.getNomen(query2);
		
		if(obj2.size()>0){
			
			for(ArrayList<Object> subdiv: obj1)
			{
				int id = (int) subdiv.get(0);
				String name = (String) subdiv.get(1);
				Subdivision subdivision = new Subdivision(id, name);
				subdivList.add(subdivision);
			}
		}
		
		//�������� �������� �����
		String query3 = "select js.id, js.subdivision_id , js.dt_start , "+
				" (select de.code_name  from dic_employee de where de.id = js.starter_id) as emp from  jor_shifts js "+
				" where js.dt_stop is null";
		
		ArrayList<ArrayList<Object>> obj3 = conn.getNomen(query3);
		
		if(obj3.size()>0){
			
			for(ArrayList<Object> subdiv: obj3)
			{
				int shift_id = (int) subdiv.get(0);
				int subdiv_id= (int) subdiv.get(1);
				
				Object obj_dt =  subdiv.get(2);
				Object employee =  subdiv.get(3);
				
				if(obj_dt!=null) {
					String dt = (String) obj_dt;
					//String empl = (String) employee;
					for(Subdivision sub:subdivList)
					{
						if(sub.getId()==subdiv_id){ sub.setDt_start(dt);
						if(employee!=null) sub.setEmployee((String)employee);
						sub.setShift_id(shift_id);
						}
					}
				}
				
			}
		}

		System.out.println("list = " + subdivList);
		JSONArray subdivsArray = new JSONArray();

		
			for (Subdivision subdiv: subdivList) {
				
				int id = subdiv.getId();
				
				String name = subdiv.getName();
				String dateStart = subdiv.getDt_start();

				JSONObject subdivObj = new JSONObject();
				try {
					subdivObj.put("id", id);
					subdivObj.put("name", name);
					subdivObj.put("dateStart", dateStart);
					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				subdivsArray.put(subdivObj);
			}

		return subdivsArray;
	}
	

	
	private class Subdivision
	{
		int id=0;
		String name="";
		String dt_start="";
		boolean isCash = false;
		
		int shift_id;
		String employee;
		
		public Subdivision(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
		public String getDt_start() {
			return dt_start;
		}
		public void setDt_start(String dt_start) {
			this.dt_start = dt_start;
		}
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public boolean isCash() {
			return isCash;
		}
		public void setCash(boolean isCash) {
			this.isCash = isCash;
		}
		public int getShift_id() {
			return shift_id;
		}
		public void setShift_id(int shift_id) {
			this.shift_id = shift_id;
		}
		public String getEmployee() {
			return employee;
		}
		public void setEmployee(String employee) {
			this.employee = employee;
		}
		
		
		
	}
	

}
