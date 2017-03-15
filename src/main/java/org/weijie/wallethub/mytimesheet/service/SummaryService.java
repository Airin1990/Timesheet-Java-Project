package org.weijie.wallethub.mytimesheet.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.weijie.wallethub.mytimesheet.database.ConnectionManager;

public class SummaryService {

	private Connection con;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	//Clause associated with parameters
	//number of hours worked/week (last 6 weeks+average)
	private String sql_1_1 = "";
	private String sql_1_2 = "";
	//with or without weekends
	private String sql_2_1 = "";
	private String sql_2_2 = "";
	//number of weekend hours (total+average)
	private String sql_3 = "";
	//number of hours worked/month (last 3 months+average)
	private String sql_4 = "";
	//average hours/day, average breaks/day
	private String sql_5 = "SELECT AVG(worked_time) FROM time_record ";
	private String sql_6 = "SELECT AVG(break) FROM time_record ";
	//last date updated


	public SummaryService() {
	}

	public Response getSummaryReport(String stids,String rtids, long uid, Date start, Date end) throws SQLException {

		con = ConnectionManager.getConnection();
		// start must be equal or smaller than end
		if (start.getTime() > end.getTime()) {
			String json = Json.createObjectBuilder()
					.add("description", "Bad request")
					.add("status", ""+Response.Status.BAD_REQUEST)
					.build()
					.toString();
			return  Response.status(Response.Status.BAD_REQUEST)
					.entity(json)
					.type(MediaType.APPLICATION_JSON).build();
		}
		// if end date is last day of a week or a month
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		boolean isSunday = (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
		boolean isLastDayMonth = (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE));
		Date end_week = new Date(end.getTime());
		Date end_month = new Date(end.getTime());
		if (!isSunday) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(end_week);
			c1.add(Calendar.DAY_OF_WEEK, -(c1.get(Calendar.DAY_OF_WEEK)-1));
			end_week = new Date(c1.getTime().getTime()); 
		}
		if (!isLastDayMonth) {
			Calendar c2 = Calendar.getInstance();
			c2.setTime(end_month);
			c2.add(Calendar.MONTH, -1);
			c2.set(Calendar.DATE, c2.getActualMaximum(Calendar.DATE)); 
			end_month = new Date(c2.getTime().getTime()); 
		}
		
		//place to save result
		int[] week_total = new int[7];
		int[] weekday_total = new int[7];
		int weekend_total = -1;
		int week_count = 0;
		int weekend_average = -1;
		int[] month_total = new int[4];
		int aver_hrs = -1;
		int aver_brks = -1;
		String last_updated = "";
		int no_days = -1;
		
		JSONArray weekSummary = new JSONArray();
		JSONArray weekdaySummary = new JSONArray();
		JSONArray monthSummary = new JSONArray();
		
		// -1 is showing that user does not query for info for that week
		Arrays.fill(week_total,new Integer(-1));
		Arrays.fill(weekday_total,new Integer(-1));
		Arrays.fill(month_total, new Integer(-1));
		
        // Part 1	
		if (start.getTime() <= end_week.getTime()) {
			sql_1_1 ="SELECT DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date)) DAY), "
					+ "DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date))+6 DAY), "
					+ "(CASE WHEN temp.rid THEN sum(temp.work_time) ELSE 0 END) FROM dates d "
					+ "LEFT JOIN (select * from time_record  WHERE tid in ("+stids+") "
					+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
					+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
					+ "ON (date(temp.record_date) = d.record_date) "
					+ "WHERE d.record_date >= ? AND d.record_date <= ? "
					+ "GROUP BY DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date)) DAY) desc LIMIT 6";

			PreparedStatement stmt1 = con.prepareStatement(sql_1_1);
			stmt1.setLong(1, uid);
			stmt1.setDate(2, start);
			stmt1.setDate(3, end_week);

			ResultSet rs1 = stmt1.executeQuery();

			int i = 0;
			int sum = 0;
			while (rs1.next() && i < 6) {
				sum += rs1.getInt(3);
				
				JSONObject json = new JSONObject();
				try {
					json.put("start", rs1.getString(1));
					json.put("end", rs1.getString(2));
					json.put("work_time", rs1.getInt(3));
					weekSummary.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}

			if (i != 0) {
				sum /= i;
			}
			
			try {
				weekSummary.put(new JSONObject().put("Average", sum));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			sql_1_2 ="SELECT DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date)) DAY), "
					+ "DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date))+6 DAY), "
					+ "(CASE WHEN temp.rid THEN sum(temp.work_time) ELSE 0 END) FROM dates d "
					+ "LEFT JOIN (select * from time_record  WHERE tid in ("+stids+") "
					+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
					+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
					+ "ON (date(temp.record_date) = d.record_date) "
					+ "WHERE d.record_date >= ? AND d.record_date <= ? "
					+ " AND WEEKDAY(d.record_date) NOT IN (5,6)"
					+ "GROUP BY DATE_ADD(d.record_date, INTERVAL(-WEEKDAY(d.record_date)) DAY) desc LIMIT 6";

			PreparedStatement stmt2 = con.prepareStatement(sql_1_2);
			stmt2.setLong(1, uid);
			stmt2.setDate(2, start);
			stmt2.setDate(3, end_week);

			ResultSet rs2 = stmt2.executeQuery();

			i = 0;
			sum = 0;
			while (rs2.next() && i < 6) {
				sum += rs2.getInt(3);
				JSONObject json = new JSONObject();
				try {
					json.put("start", rs2.getString(1));
					json.put("end", rs2.getString(2));
					json.put("work_time", rs2.getInt(3));
					weekdaySummary.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}

			if (i != 0) {
				sum /= i;
			}
			
			try {
				weekdaySummary.put(new JSONObject().put("Average", sum));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Part 2 
		if (start.getTime() <= end_week.getTime()) {
			sql_2_1 = "SELECT sum(sub.total) from (SELECT (CASE WHEN temp.rid "
					+ "THEN sum(temp.work_time) ELSE 0 END) as total FROM dates d "
					+ "LEFT JOIN (select * from time_record  WHERE tid in ("+stids+") "
					+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time from revoked_record "
					+ "WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp ON (date(temp.record_date) = d.record_date) "
					+ "WHERE d.record_date >= ? AND d.record_date <= ? "
					+ "AND WEEKDAY(d.record_date) IN (5,6) "
					+ "GROUP BY YEARWEEK(d.record_date,3) desc) sub";

			PreparedStatement stmt = con.prepareStatement(sql_2_1);
			stmt.setLong(1, uid);
			stmt.setDate(2, start);
			stmt.setDate(3, end_week);

			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				weekend_total = rs.getInt(1);
					
			sql_2_2 = "SELECT count(*) FROM (SELECT count(*) FROM dates "
					+ "WHERE record_date >= ? AND record_date <= ? "
					+ "GROUP BY YEARWEEK(record_date,3) desc) wk;";
			
			stmt = con.prepareStatement(sql_2_2);
			stmt.setDate(1, start);
			stmt.setDate(2, end_week);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				week_count = rs.getInt(1);
			}
			
			if (week_count > 0) {
				weekend_average = weekend_total/week_count;
			}
			
		}

		// Part 3
		if (start.getTime() <= end_month.getTime()) {
			sql_3 = "SELECT date_format(d.record_date, '%Y-%b'),"
					+ "(CASE WHEN temp.rid THEN sum(temp.work_time) "
					+ "ELSE 0 END) FROM dates d LEFT JOIN (select * from time_record WHERE tid in ("+stids+") "
					+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
					+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
					+ "ON (date(temp.record_date) = d.record_date) "
					+ "WHERE d.record_date >= ? AND d.record_date <= ? "
					+ "GROUP BY year(d.record_date) desc, month(d.record_date) desc;";

			PreparedStatement stmt = con.prepareStatement(sql_3);
			stmt.setLong(1, uid);
			stmt.setDate(2, start);
			stmt.setDate(3, end_month);

			ResultSet rs = stmt.executeQuery();

			int i = 0;
			int sum = 0;
			while (rs.next() && i < 3) {
				sum += rs.getInt(2);
				JSONObject json = new JSONObject();
				try {
					json.put("month", rs.getString(1));
					json.put("work_time", rs.getInt(2));
					monthSummary.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}

			if (i != 0) {
				sum/= i;
			}

			try {
				monthSummary.put(new JSONObject().put("Average",sum));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//Part 4
		
		sql_4 = "SELECT ROUND(avg(work_time)) FROM (select * from time_record WHERE tid in ("+stids+") "
				+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
				+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
				+ "WHERE record_date >= ? AND record_date <= ?";
		
		PreparedStatement stmt = con.prepareStatement(sql_4);
		stmt.setLong(1, uid);
		stmt.setDate(2, start);
		stmt.setDate(3, end);
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next())
			aver_hrs = rs.getInt(1);
		
		sql_5 = "SELECT ROUND(avg(break_time)) FROM (select * from time_record WHERE tid in ("+stids+") "
				+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
				+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
				+ "WHERE record_date >= ? AND record_date <= ?";
		
		stmt = con.prepareStatement(sql_5);
		stmt.setLong(1, uid);
		stmt.setDate(2, start);
		stmt.setDate(3, end);
		
		rs = stmt.executeQuery();
		if (rs.next())
			aver_brks = rs.getInt(1);
		
		sql_6 = "SELECT date(MAX(updated_time)) FROM (select * from time_record WHERE tid in ("+stids+") "
				+ "UNION select rid, record_date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created_time, updated_time "
				+ "from revoked_record WHERE tid in ("+rtids+") AND revoked_uid = ?) AS temp "
				+ "WHERE record_date >= ? AND record_date <= ?";
		
		stmt = con.prepareStatement(sql_6);
		stmt.setLong(1, uid);
		stmt.setDate(2, start);
		stmt.setDate(3, end);
		
		rs = stmt.executeQuery();
		if (rs.next()) {
			if (rs.getDate(1) != null) {
				last_updated = df.format(rs.getDate(1));
				Calendar cal = Calendar.getInstance();
				Date today = new Date(cal.getTime().getTime());
				no_days = Math.max((int) ((today.getTime() - rs.getDate(1).getTime())/(24 * 60 * 60 * 1000)), 0);
			}
		}
		
		
		
		// Forming the return response value
		JSONObject result = new JSONObject();
		try {
			result.put("week_summary", weekSummary);
			result.put("weekday_summary", weekdaySummary);
			result.put("weekend_total", weekend_total);
			result.put("weekend_average", weekend_average);
			result.put("month_summary", monthSummary);
			result.put("average_hours_per_day", aver_hrs);
			result.put("average_break_per_day", aver_brks);
			result.put("last_day_updated", last_updated);
			result.put("number_of_days_since_last_update", no_days);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = result.toString();
		return Response.status(Response.Status.OK)
				.entity(json)
				.type(MediaType.APPLICATION_JSON).build();
	}


}
