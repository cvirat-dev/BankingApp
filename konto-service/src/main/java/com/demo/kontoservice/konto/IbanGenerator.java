package com.demo.kontoservice.konto;

public final class IbanGenerator {

    private IbanGenerator() {}

    /// % → start of a formatting instruction
    /// d → format an integer as a decimal number
    /// 6 → minimum width of 6 characters
    /// 0 → pad with leading zeros if necessary
    /// Bewusste Vereinfachung:  In einem echten System würde man den MOD-97-Algorithmus nach ISO 13616 verwenden,
    public static String generate(Long id) {
        return String.format("DE00370400440000%06d", id);
    }
}
