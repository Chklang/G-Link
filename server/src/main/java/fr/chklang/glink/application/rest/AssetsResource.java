package fr.chklang.glink.application.rest;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import fr.chklang.glink.application.Main;

@Path("")
public class AssetsResource {
	private static String main = "/";

	@Path("/{file: .*}")
	@GET
	public Response getAsset(@PathParam("file") String pFile) throws FileNotFoundException {
		if (pFile.isEmpty()) {
			pFile = "index.html";
		}
		String lResource = main + pFile;
		System.out.println("Get " + lResource);
		InputStream lStream = Main.class.getResourceAsStream(lResource);
		if (lStream == null) {
			return Response.status(404).build();
		}
		return Response.ok(lStream).build();
	}
}
