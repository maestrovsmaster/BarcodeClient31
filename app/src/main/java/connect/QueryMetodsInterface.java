package connect;

import java.util.ArrayList;

/**
 * ��������� ������������� ������  ���������� �������� ��� ���� ����� �����������
 * @author v.gorodetskiy
 *
 */
public interface QueryMetodsInterface {
	
	/**
	 * �������� ��������� ������ 
	 * @param query ������
	 * @return
	 */
	public ArrayList<ArrayList<Object>> getNomen(String query);
	
	/**
	 *��������� ������ 
	 * @param query ������
	 * @return ���������
	 */
	public String setNomen(String query);

}
