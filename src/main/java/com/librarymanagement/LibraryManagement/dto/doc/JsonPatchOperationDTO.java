package com.librarymanagement.LibraryManagement.dto.doc;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "JsonPatchOperation",
        description = "A single operation inside an RFC 6902 JSON Patch document.")
public record JsonPatchOperationDTO(

        @Schema(
                description = "Which operation to perform.",
                example = "replace",
                allowableValues = {"add", "remove", "replace", "move", "copy", "test"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        String op,

        @Schema(
                description = "JSON Pointer to the target location, e.g. /nationality.",
                example = "/nationality",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String path,

        @Schema(
                description = "The value to write. Required for add, replace and test; "
                        + "ignored by remove, move and copy.",
                example = "Syrian")
        Object value,

        @Schema(
                description = "JSON Pointer to the source location. Only used by move and copy.",
                example = "/authorName")
        String from) {
}