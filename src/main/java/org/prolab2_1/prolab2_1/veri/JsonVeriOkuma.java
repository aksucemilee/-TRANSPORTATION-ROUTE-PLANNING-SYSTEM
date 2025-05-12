package org.prolab2_1.prolab2_1.veri;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.prolab2_1.prolab2_1.sistem.SehirVerisi;

import java.io.IOException;
import java.nio.file.Paths;

public class JsonVeriOkuma {
    private final ObjectMapper objectMapper;

    public JsonVeriOkuma() {
        this.objectMapper = new ObjectMapper();
    }

    public SehirVerisi sehirVerisiOku(String filePath) throws IOException {
        return objectMapper.readValue(Paths.get(filePath).toFile(), SehirVerisi.class);
    }
}