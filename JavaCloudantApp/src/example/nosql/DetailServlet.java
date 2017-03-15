package example.nosql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Part;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

@Path("/detail")
/**
 * CRUD service of todo list table. It uses REST style.
 */
public class DetailServlet {

	public DetailServlet() {
	}

		
	@POST
		
	public Response create(@QueryParam("id") Long id,@FormParam("appname")String appname,@FormParam("anstname")String anstname,
			@FormParam("batdate")String batdate,@FormParam("compdate")String compdate,@FormParam("comptime")String comptime,
			@FormParam("appstatus")String appstatus,@FormParam("prob")String prob)
			throws Exception {
				
				//System.out.println(username);

				Database db = null;
				try {
					db = getDB();
				} catch (Exception re) {
					re.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}

				String idString = id == null ? null : id.toString();
				JsonObject resultObject = create(db, idString, appname, anstname,batdate,compdate,comptime,appstatus,prob);

				System.out.println("Created detail record Successfully.");

				return Response.ok(resultObject.toString()).build();
			}

		protected JsonObject create(Database db, String id, String appname, String anstname,String batdate,
					String compdate,String comptime,String appstatus,String prob)
					throws IOException {

		// check if document exist
		HashMap<String, Object> obj = (id == null) ? null : db.find(HashMap.class, id);

		if (obj == null) {
			// if new document

			//id = String.valueOf(System.currentTimeMillis());
			
			id=batdate+appname;
			

			// create a new document
			System.out.println("Creating new document with id : " + id);
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			data.put("Application_Name:", appname);
			data.put("_id", id);
			data.put("Analyst_Name:", anstname);
			data.put("Batch_Cycle_date:", batdate);
			data.put("Application_Cycle_Completion_Date:", compdate);
			data.put("Application_Cycle_Completion_Time:", comptime);
			data.put("Application_Status:", appstatus);
			data.put("problem/Issues:", prob);
			db.save(data);

			
		} 
		else {
			// if existing document
			
			// update other fields in the document
			obj = db.find(HashMap.class, id);
			
			obj.put("Application_Name:", appname);
			obj.put("Analyst_Name:", anstname);
			obj.put("Batch_Cycle_date:", batdate);
			obj.put("Completion_Date:", compdate);
			obj.put("Completion_Time:", comptime);
			obj.put("Application_Status:", appstatus);
			obj.put("problem/Issues:", prob);
			db.update(obj);
		}

		obj = db.find(HashMap.class, id);

		JsonObject resultObject = toJsonObject(obj);

		return resultObject;
	}
	
			

	@GET
	@Produces(MediaType.APPLICATION_JSON)
//	public Response get(@QueryParam("id") Long id, @QueryParam("cmd") String cmd) throws Exception {
	public Response get(@QueryParam("id") String id) throws Exception {
		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		JsonObject resultObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		if (id == null) {
			try {
				// get all the document present in database
				List<HashMap> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
						.getDocsAs(HashMap.class);

				if (allDocs.size() == 0) {
					allDocs = initializeSampleData(db);
				}

				for (HashMap doc : allDocs) {
					HashMap<String, Object> obj = db.find(HashMap.class, doc.get("_id") + "");
					JsonObject jsonObject = new JsonObject();
					LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");

					if (attachments != null && attachments.size() > 0) {
						JsonArray attachmentList = getAttachmentList(attachments, obj.get("_id") + "");
						jsonObject.addProperty("id", obj.get("_id") + "");
						jsonObject.addProperty("name", obj.get("name") + "");
						jsonObject.addProperty("value", obj.get("value") + "");
						jsonObject.add("attachements", attachmentList);
					} else {
						jsonObject.addProperty("id", obj.get("_id") + "");
						jsonObject.addProperty("name", obj.get("name") + "");
						jsonObject.addProperty("value", obj.get("value") + "");
					}

					jsonArray.add(jsonObject);
				}
			} catch (Exception dnfe) {
				System.out.println("Exception thrown : " + dnfe.getMessage());
			}

			resultObject.addProperty("id", "all");
			resultObject.add("body", jsonArray);

			return Response.ok(resultObject.toString()).build();
		}

		// check if document exists
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");
		if (obj != null) {
			JsonObject jsonObject = toJsonObject(obj);
			return Response.ok(jsonObject.toString()).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	public Response delete(@QueryParam("id") String id) {

		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		// check if document exist
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		if (obj == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			db.remove(obj);

			System.out.println("Delete Successful.");

			return Response.ok("OK").build();
		}
	}

	@PUT
	public Response update(@QueryParam("id") String id, @QueryParam("appname") String appname,@QueryParam("anstname") String anstname,
			@QueryParam("batdatename") String batdate,@QueryParam("compdate") String compdate,@QueryParam("comptime") String comptime,
			@QueryParam("appstatus") String appstatus,@QueryParam("prob") String prob) {
		
		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		System.out.println(id);

		// check if document exist
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		if (obj == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			obj.put("Application_Name:", appname);
			obj.put("Analyst_Name:", anstname);
			obj.put("Batch_Cycle_date:", batdate);
			obj.put("Completion_Date:", compdate);
			obj.put("Completion_Time:", comptime);
			obj.put("Application_Status:", appstatus);
			obj.put("problem/Issues:", prob);

			db.update(obj);

			System.out.println("Update Successful.");

			return Response.ok("OK").build();
		}
	}

	private JsonArray getAttachmentList(LinkedTreeMap<String, Object> attachmentList, String docID) {
		JsonArray attachmentArray = new JsonArray();

		for (Object key : attachmentList.keySet()) {
			LinkedTreeMap<String, Object> attach = (LinkedTreeMap<String, Object>) attachmentList.get(key);

			JsonObject attachedObject = new JsonObject();
			// set the content type of the attachment
			attachedObject.addProperty("content_type", attach.get("content_type").toString());
			// append the document id and attachment key to the URL
			attachedObject.addProperty("url", "attach?id=" + docID + "&key=" + key);
			// set the key of the attachment
			attachedObject.addProperty("key", key + "");

			// add the attachment object to the array
			attachmentArray.add(attachedObject);
		}

		return attachmentArray;
	}

	private JsonObject toJsonObject(HashMap<String, Object> obj) {
		JsonObject jsonObject = new JsonObject();
	/*	LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");
		if (attachments != null && attachments.size() > 0) {
			JsonArray attachmentList = getAttachmentList(attachments, obj.get("_id") + "");
			jsonObject.add("attachements", attachmentList);
		}*/
		jsonObject.addProperty("id", obj.get("_id") + "");
		jsonObject.addProperty("appname", obj.get("Application_Name:") + "");
		jsonObject.addProperty("anstname", obj.get("Analyst_Name:") + "");
		jsonObject.addProperty("compdate", obj.get("Application_Cycle_Completion_Date:") + "");
		jsonObject.addProperty("comptime", obj.get("Application_Cycle_Completion_Time:") + "");
		jsonObject.addProperty("appstatus", obj.get("Application_Status:") + "");
		jsonObject.addProperty("prob", obj.get("problem/Issues:") + "");
		return jsonObject;
	}

	private void saveAttachment(Database db, String id, Part part, String fileName, HashMap<String, Object> obj)
			throws IOException {
		if (part != null) {
			InputStream inputStream = part.getInputStream();
			try {
				db.saveAttachment(inputStream, URLEncoder.encode(fileName,"UTF-8"), part.getContentType(), id, (String) obj.get("_rev"));
			} finally {
				inputStream.close();
			}
		}
	}

	/*
	 * Create a document and Initialize with sample data/attachments
	 */
	private List<HashMap> initializeSampleData(Database db) throws Exception {

		long id = System.currentTimeMillis();
		String name = "Sample category";
		String value = "List of sample files";

		// create a new document
		System.out.println("Creating new document with id : " + id);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("_id", id + "");
		data.put("value", value);
		data.put("creation_date", new Date().toString());
		db.save(data);

		// attach the object
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		// attachment#1
		File file = new File("Sample.txt");
		file.createNewFile();
		PrintWriter writer = new PrintWriter(file);
		writer.write("This is a sample file...");
		writer.flush();
		writer.close();
		FileInputStream fileInputStream = new FileInputStream(file);
		db.saveAttachment(fileInputStream, file.getName(), "text/plain", id + "", (String) obj.get("_rev"));
		fileInputStream.close();

		return db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(HashMap.class);

	}

	private Database getDB() {
		return CloudantClientMgr.getDB();
	}

}
