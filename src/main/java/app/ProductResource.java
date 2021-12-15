package app;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "products", description = "Operations on products resource.")
public class ProductResource {

    @GET
    @Operation(summary = "Get all products")
    public List<Product> get() {
        return Product.listAll();
    }

    @GET
    @Path("{id}")
    @APIResponse(responseCode = "200")
    @APIResponse(responseCode = "404", description = "Product not found")
    @Operation(summary = "Find product by ID")
    public Product getSingle(@PathParam("id") UUID id) {
        return Product.findByIdOptional(id).orElseThrow(NotFoundException::new);
    }

    @POST
    @Transactional
    @APIResponse(responseCode = "201",
            description = "Product created",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "406", description = "Invalid data")
    @APIResponse(responseCode = "409", description = "Product already exists")
    @Operation(summary = "Create new product")
    public Response create(@Valid Product entity) {
        if (Product.exists(entity)) {
            return Response.status(Status.CONFLICT).build();
        }

        entity.persist();
        return Response.ok(entity).status(Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @APIResponse(responseCode = "200",
            description = "Product created",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "404", description = "Product not found")
    @APIResponse(responseCode = "409", description = "Product already exists")
    @Operation(summary = "Edit product by ID")
    public Response update(@PathParam("id") UUID id, @Valid Product newEntity) {
        Product entity = Product.findByIdOptional(id).orElseThrow(NotFoundException::new);

        if (Product.exists(newEntity, id)) {
            return Response.status(Status.CONFLICT).build();
        }

        entity.title = newEntity.title;


        entity.persist();
        return Response.ok(entity).status(Status.OK).build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    @APIResponse(responseCode = "204", description = "Product deleted")
    @APIResponse(responseCode = "404", description = "Product not found")
    @Operation(summary = "Delete product by ID")
    public Response delete(@PathParam("id") UUID id) {
        Product entity = Product.findByIdOptional(id).orElseThrow(NotFoundException::new);
        entity.delete();
        return Response.status(Status.NO_CONTENT).build();
    }
}
