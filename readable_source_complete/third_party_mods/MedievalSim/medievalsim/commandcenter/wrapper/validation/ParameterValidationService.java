/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter.wrapper.validation;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ParameterValidationService {
    public static ValidationResult validateParameter(ParameterMetadata paramMetadata, String value, Client client, Server server, ServerClient serverClient) {
        if (value == null || value.trim().isEmpty()) {
            if (paramMetadata.isOptional()) {
                return ValidationResult.valid();
            }
            return ValidationResult.invalid("This parameter is required");
        }
        try {
            CmdParameter tempParam = ParameterValidationService.createTempCmdParameter(paramMetadata);
            ParameterHandler<?> handler = paramMetadata.getHandler();
            boolean isValid = handler.tryParse(client, server, serverClient, value.trim(), tempParam);
            if (isValid) {
                return ValidationResult.valid();
            }
            return ValidationResult.invalid("Invalid value for parameter type");
        }
        catch (Exception e) {
            return ValidationResult.invalid("Validation error: " + e.getMessage());
        }
    }

    private static CmdParameter createTempCmdParameter(ParameterMetadata paramMetadata) {
        return new CmdParameter(paramMetadata.getName(), paramMetadata.getHandler(), paramMetadata.isOptional(), paramMetadata.isPartOfUsage(), new CmdParameter[0]);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        public boolean isValid() {
            return this.valid;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}

