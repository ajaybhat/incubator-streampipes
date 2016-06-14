package de.fzi.cep.sepa.rest.v2;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.client.deployment.ElementType;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.streampipes.codegeneration.api.CodeGenerator;


@Path("/v2/users/{username}/deploy")
public class DeploymentImpl extends AbstractRestInterface {

	@POST
	@Path("/implementation")
	@Produces("application/zip")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getFile(@FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
	    
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);

		NamedSEPAElement element = getElement(config, model);

		if (element == null) {
			throw new WebApplicationException(500);
		}

		File f = CodeGenerator.getCodeGenerator(config, element).getGeneratedFile();

	    if (!f.exists()) {
	        throw new WebApplicationException(404);
	    }

	    return Response.ok(f)
	            .header("Content-Disposition",
	                    "attachment; filename=" +f.getName()).build();
	}

	public static NamedSEPAElement getElement(DeploymentConfiguration config, String model) {

		if (config.getElementType() == ElementType.SEP) {
			return GsonSerializer.getGsonWithIds().fromJson(model, SepDescription.class);
		} else if (config.getElementType() == ElementType.SEPA) {
			return GsonSerializer.getGsonWithIds().fromJson(model, SepaDescription.class);
		} else if (config.getElementType() == ElementType.SEC) {
			return GsonSerializer.getGsonWithIds().fromJson(model, SecDescription.class);
		} else {
			return null;
		}
	}
	
	@POST
	@Path("/import")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String directImport(@PathParam("username") String username, @FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
	    
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);
		
		SepDescription sep = new SepDescription(GsonSerializer.getGsonWithIds().fromJson(model, SepDescription.class));
		try {
			Message message = Operations.verifyAndAddElement(Utils.asString(new JsonLdTransformer().toJsonLd(sep)), username, true);
			 return toJson(message);
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException
				| SepaParseException | InvalidRdfException e) {
			e.printStackTrace();
			return toJson(Notifications.error("Error: Could not store source definition."));
		}	   
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String directUpdate(@PathParam("username") String username, @FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
		
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);
		boolean success;
		
		if (config.getElementType().equals("Sepa")) {
			SepaDescription sepa = GsonSerializer.getGsonWithIds().fromJson(model, SepaDescription.class);
			success = StorageManager.INSTANCE.getStorageAPI().deleteSEPA(sepa.getElementId());
			StorageManager.INSTANCE.getStorageAPI().storeSEPA(sepa);
		}
		
		else {
			SecDescription sec = new SecDescription(GsonSerializer.getGsonWithIds().fromJson(model, SecDescription.class));
			success = StorageManager.INSTANCE.getStorageAPI().update(sec);
		}
		
		if (success) return toJson(Notifications.success("Element description updated."));
		else return toJson(Notifications.error("Could not update element description."));
		
	}
	
	@POST
	@Path("/description/java")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getDescription(@FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
		
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);

		NamedSEPAElement element = getElement(config, model);

		String java = CodeGenerator.getCodeGenerator(config, element).getDeclareModel();
		
		try {
			
			return Response.ok(java).build();
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}	
	}
	
	@POST
	@Path("/description/jsonld")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getDescriptionAsJsonLd(@FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
	
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);

		NamedSEPAElement element = getElement(config, model);
		
		try {
			return Response.ok(Utils.asString(new JsonLdTransformer().toJsonLd(element))).build();
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException
				| InvalidRdfException e) {
			return Response.serverError().build();
		}
		
	}
	
	private String makeName(String name)
	{
		return name.replaceAll(" ", "_").toLowerCase();
	}
}