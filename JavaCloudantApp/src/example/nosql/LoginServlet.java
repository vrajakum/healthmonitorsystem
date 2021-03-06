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

@Path("/login")
/**
 * CRUD service of todo list table. It uses REST style.
 */
public class LoginServlet {

	public LoginServlet() {
	}

		
	@POST
	public Response create(@QueryParam("id") Long id,@FormParam("username")String username,@FormParam("password")String password)
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
		JsonObject resultObject = create(db, idString, username, password);

		System.out.println("Create Successful.");

		return Response.ok(resultObject.toString()).build();
	}

	protected JsonObject create(Database db, String id, String username, String password)
			throws IOException {
		
		//check document alredy exist
		HashMap<String, Object> obj = (id == null) ? null : db.find(HashMap.class, id);

		if (obj == null) {
			// if new document

			id=username+password;

			// create a new document
			System.out.println("Creating new document with id : " + id);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("username", username);
			data.put("_id", id);
			data.put("password", password);
			data.put("creation_date", new Date().toString());
		
			db.save(data);

		
		} else {
			// if existing document
			
			// update other fields in the document
			obj = db.find(HashMap.class, id);
			obj.put("username", username);
			obj.put("password", password);
			db.update(obj);
		}

		obj = db.find(HashMap.class, id);

		JsonObject resultObject = toJsonObject(obj);

		return resultObject;
	}
	
			

	@GET
	@Produces(MediaType.APPLICATION_JSON)

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

			/*	if (allDocs.size() == 0) {
					allDocs = initializeSampleData(db);
				}*/

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
		LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");
		if (attachments != null && attachments.size() > 0) {
			JsonArray attachmentList = getAttachmentList(attachments, obj.get("_id") + "");
			jsonObject.add("attachements", attachmentList);
		}
		jsonObject.addProperty("id", obj.get("_id") + "");
		jsonObject.addProperty("name", obj.get("name") + "");
		jsonObject.addProperty("value", obj.get("value") + "");
		return jsonObject;
	}

	

	private Database getDB() {
		return CloudantClientMgr.getDB();
	}

}

