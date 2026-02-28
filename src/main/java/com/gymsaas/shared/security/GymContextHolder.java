package com.gymsaas.shared.security;

import java.util.UUID;

public final class GymContextHolder {

    private static final ThreadLocal<UUID> CONTEXT = new ThreadLocal<>();

    private GymContextHolder() {}

    public static void set(UUID gymId) {
        CONTEXT.set(gymId);
    }

    public static UUID get() {
        return CONTEXT.get();
    }

    // Úsalo en los Services. Explota claro si no hay contexto.
    public static UUID getRequired() {
        UUID gymId = CONTEXT.get();
        if (gymId == null) {
            throw new IllegalStateException("No hay gymId en el contexto actual");
        }
        return gymId;
    }

    // MUY IMPORTANTE: limpiar al terminar el request
    public static void clear() {
        CONTEXT.remove();
    }
}