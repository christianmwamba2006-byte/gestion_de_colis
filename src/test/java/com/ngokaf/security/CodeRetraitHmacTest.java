package com.ngokaf.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngokaf.entity.Colis;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeRetraitHmacTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private final CodeRetraitHmac codeRetraitHmac = new CodeRetraitHmac(SECRET);

    @Test
    void genereEtVerifieUnVerificateurSansConserverLeCodeBrut() {
        String code = "481205";

        String verificateur = codeRetraitHmac.genererVerificateur(code);

        assertThat(verificateur).startsWith("hmac-sha256$").isNotEqualTo(code);
        assertThat(codeRetraitHmac.verifier(code, verificateur)).isTrue();
        assertThat(codeRetraitHmac.verifier("481206", verificateur)).isFalse();
    }

    @Test
    void refuseLesAnciennesValeursBrutesCommeVerificateurs() {
        assertThat(codeRetraitHmac.verifier("481205", "481205")).isFalse();
    }

    @Test
    void echoueClairementLorsqueLaCleExterneEstAbsente() {
        CodeRetraitHmac sansCle = new CodeRetraitHmac("");

        assertThatThrownBy(() -> sansCle.verifier("481205", "hmac-sha256$" + "0".repeat(64)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("NGOKAF_HMAC_SECRET");
    }

    @Test
    void neSerialisePasLeVerificateurDansLeJsonDuColis() throws Exception {
        Colis colis = new Colis();
        String verificateur = codeRetraitHmac.genererVerificateur("481205");
        colis.setVerificateurCodeRetrait(verificateur);

        String json = new ObjectMapper().writeValueAsString(colis);

        assertThat(json)
                .doesNotContain("verificateurCodeRetrait")
                .doesNotContain(verificateur);
    }

    @Test
    void refuseLeStockageDUnCodeBrutParLeSetterDuColis() {
        Colis colis = new Colis();

        assertThatThrownBy(() -> colis.setVerificateurCodeRetrait("481205"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
